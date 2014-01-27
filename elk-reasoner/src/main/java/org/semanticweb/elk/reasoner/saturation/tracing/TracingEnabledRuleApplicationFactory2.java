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
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState.TracedContext;
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState.TracingWriter;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;
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
 * and B => (A, B) => A and B). TODO explain
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TracingEnabledRuleApplicationFactory2 extends RuleApplicationFactory {

	private static final boolean CYCLE_AVOIDANCE = true;
	// logger for this class
	protected static final Logger LOGGER_ = LoggerFactory	.getLogger(TracingEnabledRuleApplicationFactory2.class);
	
	private final LocalTracingSaturationState tracingState_;
	
	private final TraceStore.Writer inferenceWriter_;
	
	private final TraceStore.Reader inferenceReader_;

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

			ExtendedSaturationStateWriter localContextWriter = getSaturationStateWriter();
			// inserts to the local context and writes inferences.
			// the inference writer should go first so we capture alternative
			// derivations.
			ConclusionVisitor<Integer, Context> inserter = new TracedConclusionInserter(new ConclusionInsertionVisitor());
			// applies rules when a conclusion is inserted the first time or the second time
			ConclusionVisitor<Boolean, Context> applicator = new ApplicationVisitor(
					new SmartWriter(localContextWriter),
					SaturationState.DEFAULT_INIT_RULE_APP_VISITOR);
			// combines the inserter and the applicator
			conclusionProcessor_ = new InserterApplicatorCombinator(inserter, applicator);
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

		private final BasicSaturationStateWriter localWriter_;
		private final CompositionRuleApplicationVisitor ruleAppVisitor_;
		private final DecompositionRuleApplicationVisitor mainDecompRuleAppVisitor_;

		public ApplicationVisitor(BasicSaturationStateWriter iterationWriter,
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
			//write the inference
			conclusion.accept(inferenceInserter_, mainContext);
			//insert the conclusion into context
			if (conclusion.accept(contextInserter_, cxt)) {
				//the first insertion
				return 1;
			}
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
	 * Applies rules if the conclusion has been derived no more than two times.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private static class InserterApplicatorCombinator extends BaseConclusionVisitor<Boolean, Context> {

		private final ConclusionVisitor<Integer, Context> inserter_;
		
		private final ConclusionVisitor<Boolean, Context> applicator_;
		
		InserterApplicatorCombinator(ConclusionVisitor<Integer, Context> ins, ConclusionVisitor<Boolean, Context> applicator) {
			inserter_ = ins;
			applicator_ = applicator;
		}
		
		@Override
		protected Boolean defaultVisit(Conclusion conclusion, Context cxt) {
			Integer cnt = conclusion.accept(inserter_, cxt);
			
			if (cnt.intValue() <= (CYCLE_AVOIDANCE ? 2 : 1)) {
				return conclusion.accept(applicator_, cxt);
			}
			else {
				return false;
			}
		}
		
	}
	
	/**
	 * TODO: explain the method produce *very* carefully
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private class SmartWriter extends DelegatingBasicSaturationStateWriter {
		
		private final ConclusionEqualityChecker conclusionEqualityChecker_ = new ConclusionEqualityChecker();
		
		public SmartWriter(BasicSaturationStateWriter writer) {
			super(writer);
		}

		// TODO: this is spaghetti code, too many nested visitors, need to
		// create higher level constructs to iterate over premises and
		// inferences rather than using visitors
		@Override
		public void produce(final Context context, final Conclusion conclusionBeingProduced) {
			if (!CYCLE_AVOIDANCE) {
				super.produce(context, conclusionBeingProduced);
				return;
			}
			
			TracedConclusion inference = (TracedConclusion) conclusionBeingProduced;
			// first, check if this inference has been applied before
			/*if (!isNew(inference, context)) {
				return;
			}*/			
			// second, check if all premises have been produced by at least one
			// inference in which all premises are different from the expression being produced now
			Context inferenceContext = inference.getInferenceContext(context).getRoot().getContext();
			
			final MutableBoolean allPremisesHaveAlternativeInference = new MutableBoolean(true);
			
			//going through all premises
			inference.acceptTraced(new PremiseVisitor<Void, Context>(){

				@Override
				protected Void defaultVisit(Conclusion premise, final Context contextWherePremiseStored) {
					// go through all inferences which produced this premise and
					// compare their premises with the conclusion being produced
					final MutableBoolean hasAnyInference = new MutableBoolean(false);
					final MutableBoolean hasAlternativeInference = new MutableBoolean(false);

					inferenceReader_.accept(contextWherePremiseStored, premise, new BaseTracedConclusionVisitor<Void, Void>(){

						@Override
						public Void visit(InitializationSubsumer premiseInference, Void ignored) {
							//initialization inference is obviously not cyclic
							hasAnyInference.set(true);
							hasAlternativeInference.set(true);
							
							return null;
						}

						@Override
						protected Void defaultTracedVisit(final TracedConclusion premiseInference, Void ignored) {
							hasAnyInference.set(true);
							
							boolean isAlternative = allPremisesDifferentFrom(premiseInference,
									conclusionBeingProduced,
									premiseInference.getInferenceContext(contextWherePremiseStored),
									context); 
							
							hasAlternativeInference.or(isAlternative);
							
							//FIXME
							/*for (TracedConclusion equi : equiMap.get(premiseInference)) {
								boolean equiIsAlternative = allPremisesDifferentFrom(equi, conclusionBeingProduced, equi.getInferenceContext(contextWherePremiseStored), context);
									
								if (equiIsAlternative != isAlternative) {
									throw new RuntimeException();
								}
							}*/
							
							return null;
						}
						
					});
					//if the premise doesn't have any inference yet, it can't be a part of a cycle
					allPremisesHaveAlternativeInference.and(hasAlternativeInference.get() || !hasAnyInference.get());
					
					return null;
				}
				
			}, inferenceContext);
			
			if (allPremisesHaveAlternativeInference.get()) {
				//all checks passed
				super.produce(context, conclusionBeingProduced);
			}
			else {
				System.err.println(conclusionBeingProduced);
				//FIXME
				//blocked.add(inference);
			}
		}
		
		/**
		 * Returns true if all premises of the inference are not equivalent to
		 * the given conclusion.
		 */
		private boolean allPremisesDifferentFrom(final TracedConclusion inference,
				final Conclusion conclusion,
				final Context whereInferenceMade,
				final Context whereConclusionStored) {
			final MutableBoolean allPremisesDifferentFromCurrent = new MutableBoolean(true);
			
			inference.acceptTraced(new PremiseVisitor<Void, Context>() {

				@Override
				protected Void defaultVisit(Conclusion premise, Context wherePremiseStored) {
					
					if (wherePremiseStored.getRoot() == whereConclusionStored.getRoot() &&
							premise.accept(conclusionEqualityChecker_, conclusion)) {
						allPremisesDifferentFromCurrent.set(false);
					}
					
					return null;
				}
				
			}, whereInferenceMade);
			
			return allPremisesDifferentFromCurrent.get();
		}

		/**
		 * Returns true if the conclusion has been derived via the same inference 
		 */
		private boolean isNew(final TracedConclusion conclusion, final Context context) {
			final MutableBoolean inferenceFound = new MutableBoolean(false);
			
			inferenceReader_.accept(context.getRoot().getContext(), conclusion, new BaseTracedConclusionVisitor<Void, Void>(){

				@Override
				protected Void defaultTracedVisit(TracedConclusion inference, Void ignored) {
					
					if (TracedConclusionEqualityChecker.equal(inference, conclusion, context)) {
						//FIXME
						//LOGGER_.info("duplicate inferences detected:\n{}\n{}", InferencePrinter.print(conclusion), InferencePrinter.print(inference));
						equiMap.add(inference, conclusion);
						equiMap.add(conclusion, inference);
						
						inferenceFound.set(true);
					}
					
					return null;
				}
				
			});
			
			return !inferenceFound.get();
		}
	}
	
	//FIXME
	private final Multimap<TracedConclusion, TracedConclusion> equiMap = new HashListMultimap<TracedConclusion, TracedConclusion>();
	//static Set<TracedConclusion> blocked = new HashSet<TracedConclusion>();
	//static Multimap<IndexedClassExpression, TracedConclusion> allBlocked = new HashSetMultimap<IndexedClassExpression, TracedConclusion>();
	
}
