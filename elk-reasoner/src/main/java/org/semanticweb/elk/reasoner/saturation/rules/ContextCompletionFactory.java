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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextImpl;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
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
 * Applies rules to all conclusions of partially completed contexts. Uses a
 * local saturation state to iterate over all conclusions, adds previously
 * non-existent conclusions to the ToDo queues in the main saturation state.
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
			return new BasicDecompositionRuleApplicationVisitor() {
				
				ExtendedSaturationStateWriter writer_ = ContextCompletionEngine.this.getSaturationStateWriter();
						
				@Override
				public void visit(IndexedObjectSomeValuesFrom ice, Context context) {
					Context fillerContext = ice.getFiller().getContext();
					
					// create the local context, if the main context exists and
					// was modified
					if (fillerContext != null) {
						Context mainContext = context.getRoot().getContext();
						
						if (!fillerContext.isSaturated()) {
							writer_.getCreateContext(ice.getFiller());
						}
						
						writer_.produce(fillerContext, new BackwardLink(mainContext, ice.getRelation()));
					}
				}
				
				@Override
				protected BasicSaturationStateWriter getSaturationStateWriter() {
					return writer_;
				}
			};
		}

		@Override
		protected ConclusionVisitor<Boolean> getBaseConclusionProcessor(
				BasicSaturationStateWriter saturationStateWriter,
				SaturationStatistics localStatistics) {

			return new CombinedConclusionVisitor(
					// this checks for existence in the main context and inserts
					// the conclusion either into the main context's ToDo or
					// into the local context
					new ConditionalInsertionVisitor(
							saturationState.getWriter(ConclusionVisitor.DUMMY)),
					// this applies rules in the latter case
					filterRuleConclusionProcessor(
							// this visitor applies rules
							new ConclusionGapFillingVisitor(
									saturationStateWriter,
									getEngineCompositionRuleApplicationVisitor(localStatistics
											.getRuleStatistics()),
									getEngineDecompositionRuleApplicationVisitor(
											getDecompositionRuleApplicationVisitor(),
											localStatistics.getRuleStatistics())),
							localStatistics));
		}

	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private static class LocalSaturationState implements SaturationState {

		private static final RuleApplicationVisitor DEFAULT_INIT_RULE_APP_VISITOR_ = new BasicCompositionRuleApplicationVisitor();

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

		private LocalSaturationStateWriter getDefaultWriter(
				ConclusionVisitor<?> conclusionVisitor) {
			return new LocalSaturationStateWriter(ontologyIndex_,
					conclusionVisitor);
		}

		/**
		 * 
		 * @author Pavel Klinov
		 * 
		 *         pavel.klinov@uni-ulm.de
		 */
		private class LocalSaturationStateWriter implements
				ExtendedSaturationStateWriter {

			private final OntologyIndex ontologyIndex_;
			// needed for statistics
			private final ConclusionVisitor<?> conclusionVisitor_;

			LocalSaturationStateWriter(OntologyIndex index,
					ConclusionVisitor<?> visitor) {
				ontologyIndex_ = index;
				conclusionVisitor_ = visitor;
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

			@Override
			public void produce(Context context, Conclusion conclusion) {
				Context localContext = getContext(context.getRoot());
				
				if (localContext == null) {
					localContext = getCreateContext(context.getRoot());
				}
				
				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace(localContext + ": produced local conclusion "
							+ conclusion);
				}
				
				conclusion.accept(conclusionVisitor_, localContext);

				if (localContext.addToDo(conclusion)) {
					// context was activated
					activeContexts_.add(localContext);
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
				produce(context, new PositiveSubsumer(context.getRoot()));
				// apply all context initialization rules
				LinkRule<Context> initRule = ontologyIndex_
						.getContextInitRuleHead();

				while (initRule != null) {
					initRule.accept(DEFAULT_INIT_RULE_APP_VISITOR_, this,
							context);
					initRule = initRule.next();
				}
			}

			@Override
			public void removeContext(Context context) {
				contextMap_.remove(context.getRoot());
			}
		}

	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private class ConditionalInsertionVisitor implements
			ConclusionVisitor<Boolean> {

		private final ConclusionVisitor<Boolean> checker_;

		private final ConclusionVisitor<Boolean> inserter_;

		private final BasicSaturationStateWriter mainStateWriter_;

		ConditionalInsertionVisitor(BasicSaturationStateWriter writer) {
			checker_ = new ConclusionOccurranceCheckingVisitor();
			inserter_ = new ConclusionInsertionVisitor();
			mainStateWriter_ = writer;
		}

		private Boolean defaultVisit(Conclusion conclusion, Context context) {
			Context mainContext = context.getRoot().getContext();

			if (conclusion.accept(checker_, mainContext)) {
				
				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace(context + ": conclusion " + conclusion
							+ " exists in the main context, trying to insert locally");
				}
				// insert locally
				return conclusion.accept(inserter_, context);
			} else {
				// insert to the main context's ToDo
				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace(context
							+ ": conclusion "
							+ conclusion
							+ " does NOT exist in the main context, insert into TODO");
				}

				mainStateWriter_.produce(mainContext, conclusion);

				return false;
			}
		}

		@Override
		public Boolean visit(NegativeSubsumer negSCE, Context context) {
			return defaultVisit(negSCE, context);
		}

		@Override
		public Boolean visit(PositiveSubsumer posSCE, Context context) {
			return defaultVisit(posSCE, context);
		}

		@Override
		public Boolean visit(BackwardLink link, Context context) {
			return defaultVisit(link, context);
		}

		@Override
		public Boolean visit(ForwardLink link, Context context) {
			return defaultVisit(link, context);
		}

		@Override
		public Boolean visit(Contradiction bot, Context context) {
			return defaultVisit(bot, context);
		}

		@Override
		public Boolean visit(Propagation propagation, Context context) {
			return defaultVisit(propagation, context);
		}

		@Override
		public Boolean visit(DisjointnessAxiom disjointnessAxiom,
				Context context) {
			return defaultVisit(disjointnessAxiom, context);
		}

	}

	
	/**
	 * Applies conclusion rules to the main context of the class expression (not
	 * the local context)
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private static class ConclusionGapFillingVisitor implements ConclusionVisitor<Boolean> {

		private final BasicSaturationStateWriter writer_;
		private final RuleApplicationVisitor ruleAppVisitor_;
		private final DecompositionRuleApplicationVisitor decompRuleAppVisitor_;

		public ConclusionGapFillingVisitor(BasicSaturationStateWriter writer, RuleApplicationVisitor ruleAppVisitor, DecompositionRuleApplicationVisitor decompVisitor) {
			this.writer_ = writer;
			this.ruleAppVisitor_ = ruleAppVisitor;
			this.decompRuleAppVisitor_ = decompVisitor;
		}

		@Override
		public Boolean visit(NegativeSubsumer negSCE, Context context) {
			negSCE.apply(writer_, context.getRoot().getContext(), ruleAppVisitor_);
			negSCE.applyDecompositionRules(context.getRoot().getContext(), decompRuleAppVisitor_);
			return true;
		}

		@Override
		public Boolean visit(PositiveSubsumer posSCE, Context context) {
			posSCE.apply(writer_, context.getRoot().getContext(), ruleAppVisitor_, decompRuleAppVisitor_);
			return true;
		}

		@Override
		public Boolean visit(BackwardLink link, Context context) {
			link.apply(writer_, context.getRoot().getContext(), ruleAppVisitor_);
			return true;
		}

		@Override
		public Boolean visit(ForwardLink link, Context context) {
			link.deapply(writer_, context.getRoot().getContext());
			return true;
		}

		@Override
		public Boolean visit(Contradiction bot, Context context) {
			bot.deapply(writer_, context.getRoot().getContext());
			return true;
		}

		@Override
		public Boolean visit(Propagation propagation, Context context) {
			propagation.deapply(writer_, context.getRoot().getContext());
			return true;
		}

		@Override
		public Boolean visit(DisjointnessAxiom disjointnessAxiom, Context context) {
			disjointnessAxiom.deapply(writer_, context.getRoot().getContext());
			return true;
		}		

	}

}
