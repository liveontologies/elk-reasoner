package org.semanticweb.elk.reasoner.saturation;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
			return new LocalWriter(conclusionVisitor, initRuleAppVisitor, new SimpleConclusionFactory());
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
			
			private final ConclusionFactory conclusionFactory_;
			
			private final ContextCreationListener newContextListener_;

			public LocalWriter(ConclusionVisitor<?, Context> visitor,
					CompositionRuleApplicationVisitor ruleAppVisitor, ConclusionFactory conclusionFactory) {
				this(visitor, ruleAppVisitor, conclusionFactory, ContextCreationListener.DUMMY);
			}
			
			public LocalWriter(ConclusionVisitor<?, Context> visitor,
					CompositionRuleApplicationVisitor ruleAppVisitor,
					ConclusionFactory conclusionFactory,
					ContextCreationListener newCxtListener) {
				conclusionVisitor_ = visitor;
				checker_ = new ConclusionOccurranceCheckingVisitor();
				initRuleAppVisitor_ = ruleAppVisitor;
				conclusionFactory_ = conclusionFactory;
				newContextListener_ = newCxtListener;
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
				// important: we explicitly pass the main (not local) context to the checker
				return conclusion.accept(checker_, context.getRoot().getContext());
			}

			protected void produceLocally(Context context, Conclusion conclusion) {
				LOGGER_.trace("{}: conclusion {} exists in the main context, producing locally", context, conclusion);
				
				Context localContext = getContext(context.getRoot());

				if (localContext == null) {
					localContext = getCreateContext(context.getRoot());
				}

				// used for stats
				conclusion.accept(conclusionVisitor_, localContext);

				if (localContext.addToDo(conclusion)) {
					// context was activated
					activate(localContext);
				}
			}
			
			protected void activate(Context context) {
				activeContexts_.add(context);
			}

			@Override
			public void produce(Context context, Conclusion conclusion) {
				if (existsGlobally(context, conclusion)) {
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
				Context context = newContext(root);
				Context oldContext = contextMap_.putIfAbsent(root, context);

				if (oldContext == null) {
					if (LOGGER_.isTraceEnabled()) {
						LOGGER_.trace(context.getRoot()
								+ ": local context created");
					}

					newContextListener_.notifyContextCreation(context);
					
					return context;
				}
				return oldContext;
			}
			
			protected Context newContext(IndexedClassExpression root) {
				return new ContextImpl(root);
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
			
		}		
}