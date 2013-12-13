package org.semanticweb.elk.reasoner.saturation;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionInsertionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionOccurranceCheckingVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.SimpleConclusionFactory;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.BasicCompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is supposed to be used by computations that need to maintain a
 * local, auxiliary saturation store, for example, to iterate over conclusions
 * which belong to (partially closed) contexts.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class LocalSaturationState implements SaturationState {
	
		// logger for this class
		protected static final Logger LOGGER_ = LoggerFactory	.getLogger(LocalSaturationState.class);	

		private final ConcurrentHashMap<IndexedClassExpression, Context> contextMap_;
		private final OntologyIndex ontologyIndex_;
		private final Queue<Context> activeContexts_ = new ConcurrentLinkedQueue<Context>();
		private final ConclusionFactory conclusionFactory_ = new SimpleConclusionFactory();

		public LocalSaturationState(OntologyIndex index) {
			contextMap_ = new ConcurrentHashMap<IndexedClassExpression, Context>();
			ontologyIndex_ = index;
		}

		@Override
		public OntologyIndex getOntologyIndex() {
			return ontologyIndex_;
		}

		@Override
		public Context getContext(IndexedClassExpression ice) {
			return contextMap_.get(ice);
		}

		@Override
		public Collection<Context> getContexts() {
			return contextMap_.values();
		}

		@Override
		public BasicSaturationStateWriter getWriter(
				ConclusionVisitor<?, Context> conclusionVisitor) {
			return getExtendedWriter(conclusionVisitor, new BasicCompositionRuleApplicationVisitor());
		}

		@Override
		public ExtendedSaturationStateWriter getExtendedWriter(
				ConclusionVisitor<?, Context> conclusionVisitor, CompositionRuleApplicationVisitor initRuleAppVisitor) {
			return new LocalWriter(conclusionVisitor, initRuleAppVisitor);
		}

		
		/**
		 * TODO
		 * 
		 * @author Pavel Klinov
		 * 
		 *         pavel.klinov@uni-ulm.de
		 */
		protected class LocalWriter implements ExtendedSaturationStateWriter {

			private final CompositionRuleApplicationVisitor initRuleAppVisitor_;
			// needed for statistics
			private final ConclusionVisitor<?, Context> conclusionVisitor_;

			private final ConclusionVisitor<Boolean, Context> checker_;

			LocalWriter(ConclusionVisitor<?, Context> visitor,
					CompositionRuleApplicationVisitor ruleAppVisitor) {
				conclusionVisitor_ = visitor;
				checker_ = new ConclusionOccurranceCheckingVisitor();
				initRuleAppVisitor_ = ruleAppVisitor;
			}

			@Override
			public IndexedClassExpression getOwlThing() {
				return ontologyIndex_.getIndexedOwlThing();
			}

			@Override
			public IndexedClassExpression getOwlNothing() {
				return ontologyIndex_.getIndexedOwlNothing();
			}

			@Override
			public Context pollForActiveContext() {
				return activeContexts_.poll();
			}
			
			protected boolean existsGlobally(Context context, Conclusion conclusion) {
				return conclusion.accept(checker_, context.getRoot()
						.getContext());
			}

			protected void produceLocally(Context context, Conclusion conclusion) {
				Context localContext = getContext(context.getRoot());

				if (localContext == null) {
					localContext = getCreateContext(context.getRoot());
				}

				// used for stats
				conclusion.accept(conclusionVisitor_, localContext);

				if (localContext.addToDo(conclusion)) {
					// context was activated
					activeContexts_.add(localContext);
				}
			}

			@Override
			public void produce(Context context, Conclusion conclusion) {

				if (existsGlobally(context, conclusion)) {
					LOGGER_.trace(
							"{}: conclusion {} exists in the main context, producing locally",
							context, conclusion);
					// produce the conclusion for the local copy of the context
					produceLocally(context, conclusion);
				}
			}

			@Override
			public boolean markAsNotSaturated(Context context) {
				return false;
			}

			@Override
			public void clearNotSaturatedContexts() {
				// this state doesn't maintain unsaturated contexts
			}

			@Override
			public void resetContexts() {
				contextMap_.clear();
			}
			
			@Override
			public Context getCreateContext(IndexedClassExpression root) {
				Context context = new ContextImpl(root);
				Context oldContext = contextMap_.putIfAbsent(root, context);

				if (oldContext == null) {
					initContext(context);

					if (LOGGER_.isTraceEnabled()) {
						LOGGER_.trace(context.getRoot()
								+ ": local context created");
					}

					return context;
				}
				return oldContext;
			}

			@Override
			public void initContext(Context context) {
				SaturationUtils.initContext(context, this,
						ontologyIndex_.getContextInitRuleHead(),
						initRuleAppVisitor_);
			}

			@Override
			public ConclusionFactory getConclusionFactory() {
				return conclusionFactory_;
			}

			@Override
			public ConclusionVisitor<Boolean, Context> getConclusionInserter() {
				return new ConclusionInsertionVisitor();
			}

			@Override
			public Context getCreateContext(IndexedClassExpression root, ConclusionFactory factory) {
				return getCreateContext(root);
			}
			
		}		
}