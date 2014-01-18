/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

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

import org.semanticweb.elk.MutableBoolean;
import org.semanticweb.elk.MutableInteger;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.DelegatingBasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationState;
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.BaseConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionEqualityChecker;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionInsertionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.BasicDecompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.DecompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.tracing.TracingSaturationState.TracedContext;
import org.semanticweb.elk.reasoner.saturation.tracing.TracingSaturationState.TracingWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Applies rule and records inferences. Traces only conclusions which logically
 * belong to the context submitted for tracing.
 * 
 * Implements a special trick to avoid trivial one-step inference cycles (e.g. A
 * and B => (A, B) => A and B). When a conclusion is first produced, only rules
 * which do NOT derive any of its premises are applied. When it is derived the
 * second time, only rules which haven't been applied the first time are applied
 * ( because now there is a new acycic proof of its premises). This logic is
 * implemented via two special context writers.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TracingEnabledRuleApplicationFactory extends RuleApplicationFactory {

	// logger for this class
	protected static final Logger LOGGER_ = LoggerFactory	.getLogger(TracingEnabledRuleApplicationFactory.class);
	
	private final TracingSaturationState tracingState_;
	
	private final TraceStore.Writer inferenceWriter_;
	
	private final TraceStore.Reader inferenceReader_;

	public TracingEnabledRuleApplicationFactory(ExtendedSaturationState mainSaturationState,
			TracingSaturationState traceState, TraceStore traceStore) {
		super(mainSaturationState);
		tracingState_ = traceState;
		inferenceWriter_ = traceStore.getWriter();
		inferenceReader_ = traceStore.getReader();
	}

	@Override
	public BaseEngine getDefaultEngine(ContextCreationListener listener,
			ContextModificationListener modListener) {
		return new TracingEngine();
	}

	@Override
	public TracingSaturationState getSaturationState() {
		return tracingState_;
	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	class TracingEngine extends RuleApplicationFactory.BaseEngine {

		private final CompositionRuleApplicationVisitor initRuleAppVisitor_;
		
		// used to count produced conclusions
		private final ConclusionVisitor<?, Context> conclusionStatsVisitor_;
		
		// processes conclusions taken from the ToDo queue
		private final ConclusionVisitor<Boolean, Context> conclusionProcessor_;

		protected TracingEngine() {
			super(new SaturationStatistics());
			
			ConclusionStatistics stats = localStatistics.getConclusionStatistics();
			
			initRuleAppVisitor_ = SaturationUtils.getStatsAwareCompositionRuleAppVisitor(localStatistics.getRuleStatistics());
			conclusionStatsVisitor_ = SaturationUtils.addStatsToConclusionVisitor(stats);

			ExtendedSaturationStateWriter localContextWriter = getSaturationStateWriter();
			// inserts to the local context and writes inferences.
			// the inference writer should go first so we capture alternative
			// derivations.
			//ConclusionVisitor<Boolean, Context> inserter = new CombinedConclusionVisitor<Context>(getTraceInserter(), new ConclusionInsertionVisitor());
			ConclusionVisitor<Integer, Context> inserter = new TracedConclusionInserter(new ConclusionInsertionVisitor());
			// applies rules when a conclusion is inserted the first time
			ConclusionVisitor<Boolean, Context> firstApplicator = new ApplicationVisitor(
					new FirstTimeWriter(localContextWriter),
					SaturationState.DEFAULT_INIT_RULE_APP_VISITOR);
			// applies rules when a conclusion is inserted the second time
			ConclusionVisitor<Boolean, Context> secondApplicator = new ApplicationVisitor(
					new SecondTimeWriter(localContextWriter),
					SaturationState.DEFAULT_INIT_RULE_APP_VISITOR);
			// combines the inserter and the applicator
			conclusionProcessor_ = new InserterApplicatorCombinator(inserter, firstApplicator, secondApplicator);
		}
		
		@Override
		public void submit(IndexedClassExpression root) {
			TracingWriter tracingWriter = getSaturationState().getTracingWriter(conclusionStatsVisitor_, initRuleAppVisitor_); 
			TracedContext cxt = tracingWriter.getCreateContext(root);
			//trace if it's not been traced
			if (!cxt.isSaturated()) {
				localStatistics.getContextStatistics().countModifiedContexts++;
				
				tracingWriter.initContext(cxt);
			}
		}

		@Override
		protected void process(Context context) {
			super.process(context);
		}

		@Override
		protected ConclusionVisitor<Boolean, Context> getBaseConclusionProcessor() {
			return conclusionProcessor_;
		}

		@Override
		protected TracingWriter getSaturationStateWriter() {
			return getSaturationState().getTracingWriter(conclusionStatsVisitor_, initRuleAppVisitor_);
		}

	}

	/**
	 * Applies unoptimized rules on main contexts.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private class ApplicationVisitor implements ConclusionVisitor<Boolean, Context> {

		private final PremiseBasedWriter localWriter_;
		private final CompositionRuleApplicationVisitor ruleAppVisitor_;
		private final DecompositionRuleApplicationVisitor mainDecompRuleAppVisitor_;

		public ApplicationVisitor(PremiseBasedWriter iterationWriter,
				CompositionRuleApplicationVisitor ruleAppVisitor) {
			this.localWriter_ = iterationWriter;
			this.ruleAppVisitor_ = ruleAppVisitor;
			this.mainDecompRuleAppVisitor_ = new LocalDecompositionVisitor(saturationState);

		}

		Context getContext(Conclusion conclusion, Context context) {
			IndexedClassExpression root = context.getRoot();
			
			if (context == conclusion.getSourceContext(context)) {
				// this will be the traced context which will return all local
				// conclusions except of the backward links which belong to
				// other contexts. Those will be retrieved from the main
				// context.
				return getSaturationState().getContext(root);
			} else {
				return root.getContext();
			}
		}

		@Override
		public Boolean visit(ComposedSubsumer negSCE, Context context) {
			Context cxt = getContext(negSCE, context);
			
			localWriter_.setPremise((TracedConclusion) negSCE);
			negSCE.apply(localWriter_, cxt, ruleAppVisitor_);
			negSCE.applyDecompositionRules(cxt, mainDecompRuleAppVisitor_);

			return true;
		}

		@Override
		public Boolean visit(DecomposedSubsumer posSCE, Context context) {
			localWriter_.setPremise((TracedConclusion) posSCE);
			posSCE.apply(localWriter_, getContext(posSCE, context),
					ruleAppVisitor_, mainDecompRuleAppVisitor_);
			return true;
		}

		@Override
		public Boolean visit(BackwardLink link, Context inferenceContext) {
			localWriter_.setPremise((TracedConclusion) link);
			link.applyLocally(localWriter_, getContext(link, inferenceContext), ruleAppVisitor_);

			return true;
		}

		@Override
		public Boolean visit(ForwardLink link, Context inferenceContext) {
			localWriter_.setPremise((TracedConclusion) link);
			link.applyLocally(localWriter_, getContext(link, inferenceContext));

			return true;
		}

		@Override
		public Boolean visit(Contradiction bot, Context context) {
			localWriter_.setPremise(null);
			bot.deapply(localWriter_, getContext(bot, context));
			return true;
		}

		@Override
		public Boolean visit(Propagation propagation, final Context inferenceContext) {
			localWriter_.setPremise((TracedConclusion) propagation);
			propagation.applyLocally(localWriter_,
					getContext(propagation, inferenceContext));
			return true;
		}

		@Override
		public Boolean visit(DisjointnessAxiom disjointnessAxiom,
				Context context) {
			localWriter_.setPremise(null);
			disjointnessAxiom.apply(localWriter_,
					getContext(disjointnessAxiom, context));

			return true;
		}

		/**
		 * A decomposition visitor which look ups contexts in the main
		 * saturation state and doesn't create local contexts.
		 * 
		 * @author Pavel Klinov
		 * 
		 *         pavel.klinov@uni-ulm.de
		 */
		private class LocalDecompositionVisitor extends BasicDecompositionRuleApplicationVisitor {

			private final SaturationState mainSaturationState_;

			LocalDecompositionVisitor(SaturationState mainState) {
				mainSaturationState_ = mainState;
			}

			@Override
			public void visit(IndexedObjectSomeValuesFrom ice, Context context) {
				//this call won't ever create a context in the main saturation state
				Context fillerContext = mainSaturationState_.getContext(ice.getFiller());

				if (fillerContext != null) {
					// the passed context is hybrid but we really need to point
					// the backward link to the main context.
					Context mainContext = context.getRoot().getContext();

					localWriter_.produce(fillerContext,
							localWriter_.getConclusionFactory()
									.createBackwardLink(ice, mainContext));
				}
			}

			@Override
			protected BasicSaturationStateWriter getSaturationStateWriter() {
				return localWriter_;
			}

		}

	}
	
	/**
	 * Returns the number of times the conclusion has been inserted. Also makes
	 * sure that all inferences, when stored, are indexed by the main contexts.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private class TracedConclusionInserter extends BaseConclusionVisitor<Integer, Context> {
		
		private final TracingConclusionInsertionVisitor inferenceInserter_;
		private final ConclusionInsertionVisitor contextInserter_;
		
		public TracedConclusionInserter(ConclusionInsertionVisitor inserter) {
			inferenceInserter_ = new TracingConclusionInsertionVisitor(inferenceWriter_);
			contextInserter_ = inserter;
		}

		@Override
		protected Integer defaultVisit(Conclusion conclusion, Context cxt) {
			Context mainContext = cxt.getRoot().getContext();
			
			conclusion.accept(inferenceInserter_, mainContext);
			conclusion.accept(contextInserter_, cxt);
			//counting the number of inferences for the just inserted conclusion
			final MutableInteger inferenceCount = new MutableInteger(0);
			
			inferenceReader_.accept(mainContext, conclusion, new BaseTracedConclusionVisitor<Void, Void>() {

				@Override
				protected Void defaultTracedVisit(TracedConclusion conclusion, Void ignored) {
					inferenceCount.increment();
					
					return null;
				}
				
			});

			return inferenceCount.get();
		}

	}
	
	/**
	 * Applies the first applicator if the conclusion was not derived
	 * before (according to the inserter). Applies the second applicator if
	 * the conclusion was derived the second time. Otherwise doesn't apply
	 * rules (this guarantees termination).
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private static class InserterApplicatorCombinator extends BaseConclusionVisitor<Boolean, Context> {

		private final ConclusionVisitor<Integer, Context> inserter_;
		
		private final ConclusionVisitor<Boolean, Context> firstTimeApplicator_;
		
		private final ConclusionVisitor<Boolean, Context> secondTimeApplicator_;
		
		InserterApplicatorCombinator(ConclusionVisitor<Integer, Context> ins, ConclusionVisitor<Boolean, Context> first, ConclusionVisitor<Boolean, Context> second) {
			inserter_ = ins;
			firstTimeApplicator_ = first;
			secondTimeApplicator_ = second;
		}
		
		@Override
		protected Boolean defaultVisit(Conclusion conclusion, Context cxt) {
			Integer cnt = conclusion.accept(inserter_, cxt);
			
			if (cnt.intValue() == 1) {
				return conclusion.accept(firstTimeApplicator_, cxt);
			}
			else if (cnt.intValue() == 2) {
				return conclusion.accept(secondTimeApplicator_, cxt);
			}
			else {
				return false;
			}
		}
		
	}
	
	/**
	 * The base class for writers which produce a conclusion depending on
	 * whether it's the same as one of the premises of its premise.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private abstract class PremiseBasedWriter extends DelegatingBasicSaturationStateWriter {
		
		private TracedConclusion premise;
		private final ConclusionEqualityChecker equalityChecker_ = new ConclusionEqualityChecker();
		
		public PremiseBasedWriter(BasicSaturationStateWriter writer) {
			super(writer);
		}
		
		/*
		 * returns true if the conclusion is equal to one of the premises of the premise
		 */
		boolean sameAsPremise(final Conclusion conclusion, final Context context) {
			if (premise == null || premise.getInferenceContext(context).getRoot() != context.getRoot()) {
				return false;
			}
			// compare the conclusion with all premises of the conclusion
			// which produced it
			final MutableBoolean same = new MutableBoolean(false);
			
			premise.acceptTraced(new PremiseVisitor<Void, Void>() {

				@Override
				protected Void defaultVisit(Conclusion premiseOfPremise, Void ignored) {
					if (premiseOfPremise.accept(equalityChecker_, conclusion)) {
						same.set(true);
					}
					
					return null;
				}
				
			}, null);
			
			return same.get();
		}
		
		void setPremise(TracedConclusion premise) {
			this.premise = premise;
		}
	}
	
	/**
	 * Used when rules are applied for a conclusion inserted the first time.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private class FirstTimeWriter extends PremiseBasedWriter {

		public FirstTimeWriter(BasicSaturationStateWriter writer) {
			super(writer);
		}

		@Override
		public void produce(Context context, Conclusion conclusion) {
			if (!sameAsPremise(conclusion, context)) {
				super.produce(context, conclusion);
			}
		}
		
	}
	
	/**
	 * Used when rules are applied for a conclusion inserted the second time.
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private class SecondTimeWriter extends PremiseBasedWriter {

		public SecondTimeWriter(BasicSaturationStateWriter writer) {
			super(writer);
		}
		
		
		@Override
		public void produce(Context context, Conclusion conclusion) {
			if (sameAsPremise(conclusion, context)) {
				super.produce(context, conclusion);
			}
		}
	}

}
