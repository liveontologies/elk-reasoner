/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules;

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

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextImpl;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.CombinedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionInsertionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionOccurranceCheckingVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.NegativeSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Applies rules to all conclusions of partially completed contexts to close
 * them deductively. Uses a local saturation state to iterate over all
 * conclusions, adds previously non-existent conclusions to the ToDo queues in
 * the main saturation state.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ContextCompletionFactory extends RuleApplicationFactory {

	// logger for this class
	protected static final Logger LOGGER_ = Logger
			.getLogger(ContextCompletionFactory.class);

	private final LocalSaturationState localState_;

	public ContextCompletionFactory(SaturationState saturationState) {
		super(saturationState);
		localState_ = new LocalSaturationState(
				saturationState.getOntologyIndex());
	}

	@Override
	public BaseEngine getDefaultEngine(ContextCreationListener listener,
			ContextModificationListener modListener) {
		return new ContextCompletionEngine();
	}

	@Override
	public SaturationState getSaturationState() {
		return localState_;
	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private class ContextCompletionEngine extends
			RuleApplicationFactory.BaseEngine {

		private ExtendedSaturationStateWriter writer_;

		protected ContextCompletionEngine() {
			super(new SaturationStatistics());
		}

		@Override
		public void submit(IndexedClassExpression root) {
			// create a local context for this ICE
			getSaturationStateWriter().getCreateContext(root);
		}

		@Override
		protected ExtendedSaturationStateWriter getSaturationStateWriter() {
			if (writer_ == null) {
				ConclusionStatistics stats = localStatistics
						.getConclusionStatistics();
				ConclusionVisitor<?> visitor = RuleApplicationFactory
						.getEngineConclusionVisitor(stats);

				writer_ = localState_.getDefaultWriter(visitor);
			}

			return writer_;
		}

		@Override
		protected DecompositionRuleApplicationVisitor getDecompositionRuleApplicationVisitor() {
			return null;
		}

		@Override
		protected ConclusionVisitor<Boolean> getBaseConclusionProcessor(
				BasicSaturationStateWriter saturationStateWriter,
				SaturationStatistics localStatistics) {

			ConclusionStatistics conclusionStats = localStatistics
					.getConclusionStatistics();
			RuleStatistics ruleStats = localStatistics.getRuleStatistics();
			ConclusionVisitor<?> statsVisitor = RuleApplicationFactory
					.getEngineConclusionVisitor(conclusionStats);
			// create two decomposition rule app visitors: one produces
			// conclusions for both local and the main contexts, the other only
			// for local contexts
			DecompositionRuleApplicationVisitor iterationVisitor = getEngineDecompositionRuleApplicationVisitor(
					new ForwardDecompositionRuleApplicationVisitor(writer_),
					ruleStats);
			DecompositionRuleApplicationVisitor produceVisitor = getEngineDecompositionRuleApplicationVisitor(
					new ForwardDecompositionRuleApplicationVisitor(localState_
							.getWriterForDecompositionVisitor(statsVisitor)),
					ruleStats);
			// this visitor applies rules to fill all gaps in the
			// deductive closure
			ConclusionGapFillingVisitor gapFiller = new ConclusionGapFillingVisitor(
					saturationStateWriter,
					getEngineCompositionRuleApplicationVisitor(localStatistics
							.getRuleStatistics()), iterationVisitor, produceVisitor);

			return new CombinedConclusionVisitor(
					new ConclusionInsertionVisitor(),
					filterRuleConclusionProcessor(gapFiller, localStatistics));
		}

	}

	/**
	 * Maintains a map of "local" contexts for class expressions, used as a
	 * cache to avoid infinite looping when iterating over all conclusions which
	 * belong to a certain context.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private class LocalSaturationState implements SaturationState {

		private final RuleApplicationVisitor initRuleAppVisitor_ = new BasicCompositionRuleApplicationVisitor();
		private final ConcurrentHashMap<IndexedClassExpression, Context> contextMap_;
		private final OntologyIndex ontologyIndex_;
		private final Queue<Context> activeContexts_ = new ConcurrentLinkedQueue<Context>();

		LocalSaturationState(OntologyIndex index) {
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
		public Collection<IndexedClassExpression> getNotSaturatedContexts() {
			return contextMap_.keySet();
		}

		@Override
		public ExtendedSaturationStateWriter getExtendedWriter(
				ContextCreationListener contextCreationListener,
				ContextModificationListener contextModificationListener,
				RuleApplicationVisitor ruleAppVisitor,
				ConclusionVisitor<?> conclusionVisitor,
				boolean trackNewContextsAsUnsaturated) {
			return getDefaultWriter(conclusionVisitor);
		}

		@Override
		public BasicSaturationStateWriter getWriter(
				ContextModificationListener contextModificationListener,
				ConclusionVisitor<?> conclusionVisitor) {
			return getDefaultWriter(conclusionVisitor);
		}

		@Override
		public BasicSaturationStateWriter getWriter(
				ConclusionVisitor<?> conclusionVisitor) {
			return getDefaultWriter(conclusionVisitor);
		}

		@Override
		public ExtendedSaturationStateWriter getExtendedWriter(
				ConclusionVisitor<?> conclusionVisitor) {
			return getDefaultWriter(conclusionVisitor);
		}

		private TracingSaturationStateWriter getDefaultWriter(
				ConclusionVisitor<?> conclusionVisitor) {
			return new TracingSaturationStateWriter(ontologyIndex_,
					conclusionVisitor,
					saturationState.getWriter(conclusionVisitor));
		}

		private ExtendedSaturationStateWriter getWriterForDecompositionVisitor(
				ConclusionVisitor<?> conclusionVisitor) {
			return new OptimizedLocalSaturationStateWriter(ontologyIndex_,
					conclusionVisitor);
		}
		
		/**
		 * Only produces conclusions for the local contexts. Used by the
		 * decomposition rule application visitor which should not produce the
		 * results of decomposition of negative subsumers to the main context.
		 * 
		 * @author Pavel Klinov
		 * 
		 *         pavel.klinov@uni-ulm.de
		 */
		private class OptimizedLocalSaturationStateWriter implements
				ExtendedSaturationStateWriter {

			private final OntologyIndex ontologyIndex_;
			// needed for statistics
			private final ConclusionVisitor<?> conclusionVisitor_;

			private final ConclusionVisitor<Boolean> checker_;

			OptimizedLocalSaturationStateWriter(OntologyIndex index,
					ConclusionVisitor<?> visitor) {
				ontologyIndex_ = index;
				conclusionVisitor_ = visitor;
				checker_ = new ConclusionOccurranceCheckingVisitor();
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

			boolean existsGlobally(Context context, Conclusion conclusion) {
				return conclusion.accept(checker_, context.getRoot()
						.getContext());
			}

			void produceLocally(Context context, Conclusion conclusion) {
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
					if (LOGGER_.isTraceEnabled()) {
						LOGGER_.trace(context + ": conclusion " + conclusion
								+ " exists in the main context, producing locally");
					}
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
				} else {
					return oldContext;
				}
			}

			@Override
			public void initContext(Context context) {
				SaturationUtils.initContext(context, this, ontologyIndex_, initRuleAppVisitor_);
			}

			@Override
			public void removeContext(Context context) {
				contextMap_.remove(context.getRoot());
			}
		}
		
		
		/**
		 * This writer is used for iterating over the conclusions whuch belong
		 * to a certain context in the main saturation state.
		 * 
		 * It produces conclusions to two contexts: the local copy (if the
		 * conclusion exists in the main context) and the main context
		 * otherwise.
		 * 
		 * @author Pavel Klinov
		 * 
		 *         pavel.klinov@uni-ulm.de
		 */
		private class TracingSaturationStateWriter extends OptimizedLocalSaturationStateWriter {

			private final BasicSaturationStateWriter mainStateWriter_;

			TracingSaturationStateWriter(OntologyIndex index,
					ConclusionVisitor<?> visitor,
					BasicSaturationStateWriter writer) {
				super(index, visitor);
				mainStateWriter_ = writer;
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

			void produceGlobally(Context context, Conclusion conclusion) {
				// insert to the main context's ToDo
				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace(context
							+ ": conclusion "
							+ conclusion
							+ " does NOT exist in the main context, insert into TODO");
				}

				mainStateWriter_.produce(context.getRoot().getContext(),
						conclusion);
			}

			@Override
			public void produce(Context context, Conclusion conclusion) {
				if (existsGlobally(context, conclusion)) {
					// produce the conclusion for the local copy of the context
					// but only if the main context is not modified
					// (the same logic as was previously used for cleaning)
					Context sourceContext = conclusion.getSourceContext(context);
					
					if (sourceContext == null || !sourceContext.isSaturated()) {
						produceLocally(context, conclusion);
					}
					
				} else {
					// produce the conclusion for the main context
					produceGlobally(context, conclusion);
				}
			}
		}

	}

	/**
	 * Applies conclusion rules to the main context of the class expression (not
	 * the local context).
	 * 
	 * This visitor uses two different decomposition rule application visitors:
	 * One is used for iterating over the set of conclusions for a context (this
	 * requires decomposition of all subsumers, positive and negative, to not
	 * miss any conclusion regardless of the order of rule applications). The
	 * other is used for "gap filling", i.e. adding missing conclusions to the
	 * main context's ToDo. It does not require decomposition of negative
	 * subsumers.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private static class ConclusionGapFillingVisitor implements
			ConclusionVisitor<Boolean> {

		private final BasicSaturationStateWriter iterationWriter_;
		private final RuleApplicationVisitor ruleAppVisitor_;
		private final DecompositionRuleApplicationVisitor iterateDecompRuleAppVisitor_;
		private final DecompositionRuleApplicationVisitor produceDecompRuleAppVisitor_;

		public ConclusionGapFillingVisitor(
				BasicSaturationStateWriter enumWriter,
				RuleApplicationVisitor ruleAppVisitor,
				DecompositionRuleApplicationVisitor enumVisitor,
				DecompositionRuleApplicationVisitor produceVisitor) {
			this.iterationWriter_ = enumWriter;
			this.ruleAppVisitor_ = ruleAppVisitor;
			this.iterateDecompRuleAppVisitor_ = enumVisitor;
			this.produceDecompRuleAppVisitor_ = produceVisitor;

		}

		@Override
		public Boolean visit(NegativeSubsumer negSCE, Context context) {
			negSCE.apply(iterationWriter_, context.getRoot().getContext(),
					ruleAppVisitor_);
			negSCE.applyDecompositionRules(context.getRoot().getContext(),
					produceDecompRuleAppVisitor_);
			return true;
		}

		@Override
		public Boolean visit(PositiveSubsumer posSCE, Context context) {
			posSCE.apply(iterationWriter_, context.getRoot().getContext(),
					ruleAppVisitor_, iterateDecompRuleAppVisitor_);
			return true;
		}

		@Override
		public Boolean visit(BackwardLink link, Context context) {
			link.apply(iterationWriter_, context.getRoot().getContext(),
					ruleAppVisitor_);
			return true;
		}

		@Override
		public Boolean visit(ForwardLink link, Context context) {
			link.apply(iterationWriter_, context.getRoot().getContext());
			return true;
		}

		@Override
		public Boolean visit(Contradiction bot, Context context) {
			bot.deapply(iterationWriter_, context.getRoot().getContext());
			return true;
		}

		@Override
		public Boolean visit(Propagation propagation, Context context) {
			propagation.apply(iterationWriter_, context.getRoot()
					.getContext());
			return true;
		}

		@Override
		public Boolean visit(DisjointnessAxiom disjointnessAxiom,
				Context context) {
			disjointnessAxiom.apply(iterationWriter_, context.getRoot()
					.getContext());
			return true;
		}

	}

}
