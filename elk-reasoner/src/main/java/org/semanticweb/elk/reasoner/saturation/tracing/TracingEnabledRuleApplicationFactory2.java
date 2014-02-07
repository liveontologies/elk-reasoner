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

import java.util.Collection;
import java.util.Iterator;

import org.semanticweb.elk.MutableBoolean;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.DelegatingExtendedSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.BaseConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.CombinedConclusionVisitor;
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
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState.TracedContext;
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState.TracingWriter;
import org.semanticweb.elk.util.collections.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This factory should be used for tracing already made inferences whose
 * conclusions are stored in the main contexts.
 * 
 * Applies rule and records inferences. Traces only conclusions which logically
 * belong to the context submitted for tracing.
 * 
 * Implements a special trick to avoid trivial one-step inference cycles (e.g. A
 * and B => (A, B) => A and B). Every time a conclusion is produced, a special
 * writer checks if all premises of the inference have been derived by at least
 * one inference not using the current conclusion. If that's not the case, i.e.,
 * if there's a premise that is derived only through our conclusion, the
 * inference is blocked and saved for that premise. Then, when/if that premise
 * is derived by some other (alternative) inference, we check if the blocked
 * inference should now be applied or there's another blocking premise. In the
 * latter case we save it for that other premise.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TracingEnabledRuleApplicationFactory2 extends RuleApplicationFactory {
	
	//private final static boolean CYCLE_AVOIDANCE = true;
	// logger for this class
	protected static final Logger LOGGER_ = LoggerFactory	.getLogger(TracingEnabledRuleApplicationFactory2.class);
	
	private final LocalTracingSaturationState tracingState_;
	
	private final TraceStore.Writer inferenceWriter_;
	
	private final TraceStore.Reader inferenceReader_;
	
	private final ConclusionEqualityChecker conclusionEqualityChecker_ = new ConclusionEqualityChecker();

	public TracingEnabledRuleApplicationFactory2(ExtendedSaturationState mainSaturationState,
			LocalTracingSaturationState traceState, TraceStore traceStore) {
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
	public LocalTracingSaturationState getSaturationState() {
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
		
		// used to count produced conclusions before they go into the ToDo queue
		private final ConclusionVisitor<?, Context> conclusionStatsVisitor_;
		
		// processes conclusions taken from the ToDo queue
		private final ConclusionVisitor<Boolean, Context> conclusionProcessor_;

		protected TracingEngine() {
			super(new SaturationStatistics());
			
			ConclusionStatistics stats = localStatistics.getConclusionStatistics();
			
			initRuleAppVisitor_ = SaturationUtils.getStatsAwareCompositionRuleAppVisitor(localStatistics.getRuleStatistics());
			conclusionStatsVisitor_ = SaturationUtils.addStatsToConclusionVisitor(stats);

			TracingWriter localContextWriter = getSaturationStateWriter();
			// inserts to the local context and writes inferences.
			// the inference writer should go first so we capture alternative
			// derivations.
			ConclusionVisitor<Boolean, Context> inserter = new TracedConclusionInserter(new ConclusionInsertionVisitor(), localStatistics);
			// applies rules when a conclusion is inserted the first time or the second time
			ConclusionVisitor<Boolean, Context> applicator = new ApplicationVisitor(
					new CycleAvoidingWriter(localContextWriter),
					SaturationState.DEFAULT_INIT_RULE_APP_VISITOR);
			// combines the inserter and the applicator
			conclusionProcessor_ = new CombinedConclusionVisitor<Context>(inserter, applicator);
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
		protected ConclusionVisitor<Boolean, Context> getBaseConclusionProcessor() {
			return conclusionProcessor_;
		}

		@Override
		protected TracingWriter getSaturationStateWriter() {
			return tracingState_.getTracingWriter(conclusionStatsVisitor_, initRuleAppVisitor_);
		}

		@Override
		public void finish() {
			super.finish();
			// removing the blocked inferences, they won't ever be made
			for (Context cxt : tracingState_.getContexts()) {
				TracedContext context = (TracedContext) cxt;

				context.cleanBlockedInferences();
			}
		}
		
	}

	/**
	 * Checks for cycles
	 */
	private class CycleAvoidingWriter extends DelegatingExtendedSaturationStateWriter {

		public CycleAvoidingWriter(TracingWriter writer) {
			super(writer);
		}

		@Override
		public void produce(Context context, Conclusion conclusion) {
			// no need to check for duplicates since rules for all conclusions
			// are applied only once.			
			final TracedConclusion inference = (TracedConclusion) conclusion;
			//the inference is applied in this context
			//final Context inferenceContext = inference.getInferenceContext(context);
			final TracedContext thisContext = tracingState_.getContext(context.getRoot());//inferenceContext.getRoot());
			
			if (thisContext == null) {
				super.produce(context, conclusion);
				return;
			}
			// get the premise which blocks this inference, if any
			Conclusion cyclicPremise = isCyclic(inference, thisContext);
			
			if (cyclicPremise == null) {
				super.produce(context, conclusion);
				//see if this inference can unblock previously blocked inferences
				unblockInferences(inference, thisContext);
			}
			else {
				//block the inference
				LOGGER_.trace("Inference {} is cyclic through {}", inference, cyclicPremise);
				
				thisContext.getBlockedInferences().add(cyclicPremise, inference);
			}
		}
		
		private void unblockInferences(final TracedConclusion premiseInference, final TracedContext cxt) {
			Collection<TracedConclusion> blocked = cxt.getBlockedInferences().get(premiseInference);
			
			if (blocked != null && !blocked.isEmpty()) {
				Iterator<TracedConclusion> inferenceIter = blocked.iterator();
				
				for (; inferenceIter.hasNext(); ) {
					final TracedConclusion blockedInference = inferenceIter.next();
					//deciding if the new inference should unblock the previously blocked one.
					//i.e. if the new inference derives its conclusion NOT via the conclusion of the blocked inference.
					if (isAlternative(premiseInference, blockedInference)) {
						inferenceIter.remove();
						// now let's see if some other premise so far is derived
						// only via the blocked inference's conclusion
						Conclusion otherBlockingPremise = PremiseUtils.find(blockedInference, new Condition<Conclusion>() {

							@Override
							public boolean holds(Conclusion premise) {
								if (ConclusionEqualityChecker.equal(premise, premiseInference, cxt)) {
									return false;
								}
								
								return derivedOnlyViaGivenConclusion(premise, blockedInference, blockedInference.getInferenceContext(cxt), cxt);
							}
							
						});
						
						if (otherBlockingPremise != null) {
							//now block the same inference for the other premise
							LOGGER_.trace("Inference {} is now blocked through {}", blockedInference, otherBlockingPremise);
							
							cxt.getBlockedInferences().add(otherBlockingPremise, blockedInference);
						}
						else {
							//unblock the inference
							LOGGER_.trace("Inference {} is unblocked", blockedInference);
							
							super.produce(cxt, blockedInference);
						}
					}
				}
			}
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

		private final CycleAvoidingWriter localWriter_;
		private final CompositionRuleApplicationVisitor ruleAppVisitor_;
		private final DecompositionRuleApplicationVisitor mainDecompRuleAppVisitor_;

		public ApplicationVisitor(CycleAvoidingWriter iterationWriter,
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
			
			negSCE.apply(localWriter_, cxt, ruleAppVisitor_);
			negSCE.applyDecompositionRules(cxt, mainDecompRuleAppVisitor_);

			return true;
		}

		@Override
		public Boolean visit(DecomposedSubsumer posSCE, Context context) {
			posSCE.apply(localWriter_, getContext(posSCE, context),
					ruleAppVisitor_, mainDecompRuleAppVisitor_);
			return true;
		}

		@Override
		public Boolean visit(BackwardLink link, Context inferenceContext) {
			link.applyLocally(localWriter_, getContext(link, inferenceContext), ruleAppVisitor_);

			return true;
		}

		@Override
		public Boolean visit(ForwardLink link, Context inferenceContext) {
			link.applyLocally(localWriter_, getContext(link, inferenceContext));

			return true;
		}

		@Override
		public Boolean visit(Contradiction bot, Context context) {
			bot.deapply(localWriter_, getContext(bot, context));
			return true;
		}

		@Override
		public Boolean visit(Propagation propagation, final Context inferenceContext) {
			propagation.applyLocally(localWriter_,
					getContext(propagation, inferenceContext));
			return true;
		}

		@Override
		public Boolean visit(DisjointnessAxiom disjointnessAxiom,
				Context context) {
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
					// the passed context is local but we really need to point
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
	 * First, writes the new inference for the conclusion, second, inserts that
	 * conclusion into the context.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private class TracedConclusionInserter extends BaseConclusionVisitor<Boolean, Context> {
		
		private final ConclusionInsertionVisitor contextInserter_;
		private final ConclusionVisitor<Boolean, Context> inferenceInserter_;
		
		public TracedConclusionInserter(ConclusionInsertionVisitor inserter, SaturationStatistics localStats) {
			contextInserter_ = inserter;
			inferenceInserter_ = SaturationUtils.getUsedConclusionCountingProcessor(new TracingConclusionInsertionVisitor(inferenceWriter_), localStats);
		}

		@Override
		protected Boolean defaultVisit(final Conclusion conclusion, final Context cxt) {
			//write the inference and count it as used conclusion (if needed)
			conclusion.accept(inferenceInserter_, cxt);
			//insert into context
			return conclusion.accept(contextInserter_, cxt);
		}
		
	}
	
	Conclusion isCyclic(final TracedConclusion inference, final TracedContext storeContext) {
		// the inference is cyclic if at least one of the premises has been
		// derived only through this inference's conclusion
		final Context inferenceContext = inference.getInferenceContext(storeContext);
		
		Conclusion cyclicPremise = PremiseUtils.find(inference, new Condition<Conclusion>(){

			@Override
			public boolean holds(Conclusion premise) {
				return derivedOnlyViaGivenConclusion(premise, inference, inferenceContext, storeContext);
			}
			
		});
		
		return cyclicPremise;
	}
	
	/**
	 * Returns true if all inferences of the given premise use the given conclusion.
	 * 
	 * @param premise
	 * @param conclusion
	 * @param premiseContext
	 * @param conclusionContext 
	 * @return
	 */
	boolean derivedOnlyViaGivenConclusion(final Conclusion premise, final Conclusion conclusion, final Context premiseContext, final Context conclusionContext) {
		final MutableBoolean foundAlternative = new MutableBoolean(false);
		final MutableBoolean anyInference = new MutableBoolean(false);

		inferenceReader_.accept(premiseContext.getRoot(), premise, new BaseTracedConclusionVisitor<Void, Void>(){

			@Override
			protected Void defaultTracedVisit(TracedConclusion premiseInference, Void ignored) {
				anyInference.set(true);
				// if the premise is produced in a context different
				// from where the conclusion is stored, then it must be
				// produced by an alternative inference.
				if (premiseInference.getInferenceContext(conclusionContext).getRoot() != conclusionContext.getRoot()
					|| isAlternative(premiseInference, conclusion)) {
					foundAlternative.set(true);
				}
				
				return null;
			}
			
		});
		
		return anyInference.get() && !foundAlternative.get();
	}
	
	/**
	 * Returns true if all premises of the inference are not equivalent to the
	 * given conclusion (i.e. if the inference derives its conclusion NOT via
	 * the given conclusion). It is assumed that the premises are stored in the
	 * same context as the conclusion.
	 */
	boolean isAlternative(final TracedConclusion inference, final Conclusion conclusion) {
		Conclusion equivalentPremise = PremiseUtils.find(inference, new Condition<Conclusion>(){

			@Override
			public boolean holds(Conclusion premise) {
				return premise.accept(conclusionEqualityChecker_, conclusion);
			}
			
		});
		
		return equivalentPremise == null;
	}
	
}
