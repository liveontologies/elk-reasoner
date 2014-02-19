/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.factories;

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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriterWrap;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionEntry;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.AbstractConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ComposedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionInsertionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionOccurrenceCheckingVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.HybridLocalRuleApplicationConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.LocalizedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.factories.AbstractRuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState;
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState.TracedContext;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.util.IsInferenceCyclic;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.GetInferenceTarget;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.InferenceInsertionVisitor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
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
public class CycleBlockingRuleApplicationFactory extends AbstractRuleApplicationFactory {
	
	private final static boolean CYCLE_AVOIDANCE = true;
	// logger for this class
	protected static final Logger LOGGER_ = LoggerFactory	.getLogger(CycleBlockingRuleApplicationFactory.class);
	
	private final LocalTracingSaturationState tracingState_;
	
	private final TraceStore.Writer inferenceWriter_;
	
	private final TraceStore.Reader inferenceReader_;

	public CycleBlockingRuleApplicationFactory(SaturationState mainSaturationState,
			LocalTracingSaturationState traceState, TraceStore traceStore) {
		super(mainSaturationState);
		tracingState_ = traceState;
		inferenceWriter_ = traceStore.getWriter();
		inferenceReader_ = traceStore.getReader();
	}
	
	@Override
	protected InputProcessor<IndexedClassExpression> getEngine(RuleVisitor ruleVisitor,
			SaturationStateWriter writer, SaturationStatistics localStatistics) {
		SaturationStateWriter localWriter = tracingState_.getTracingWriter();
		// use the cycle blocking writer in the engine instead of the main one
		return super.getEngine(
				getConclusionProcessor(ruleVisitor, writer, new CycleBlockingProducer(localWriter)),
				localWriter, localStatistics);
	}

	@SuppressWarnings("unchecked")
	ConclusionVisitor<Context, Boolean> getConclusionProcessor(
			RuleVisitor ruleVisitor, ConclusionProducer mainProducer,
			ConclusionProducer tracingProducer/*this producer is responsible for blocking cyclic inferences*/) {
		
		return new ComposedConclusionVisitor<Context>(
				// checking the conclusion against the main saturation state
				new LocalizedConclusionVisitor(
						// conclusion already occurs there
						new ConclusionOccurrenceCheckingVisitor(),
						super.getSaturationState()),
				// if all fine, insert the conclusion to the local context copy and write the inference 
				new InferenceInserter(new ConclusionInsertionVisitor(), tracingProducer, getSaturationStatistics()),
				// apply only local rules and produce conclusion only to the locally copies
				new HybridLocalRuleApplicationConclusionVisitor(
						getSaturationState(), ruleVisitor, ruleVisitor,
						tracingProducer, tracingProducer));
	}

	@Override
	public LocalTracingSaturationState getSaturationState() {
		return tracingState_;
	}

	
	@Override
	public void dispose() {
		super.dispose();
		/*for (Context cxt : tracingState_.getContexts()) {
			TracedContext context = (TracedContext) cxt;

			context.cleanBlockedInferences();
		}*/
	}

	/**
	 * Checks for cycles
	 */
	private class CycleBlockingProducer extends SaturationStateWriterWrap<SaturationStateWriter> {

		public CycleBlockingProducer(SaturationStateWriter writer) {
			super(writer);
		}

		@Override
		public void produce(IndexedClassExpression root, Conclusion conclusion) {
			// no need to check for duplicates since rules for all conclusions
			// are applied only once.			
			final Inference inference = (Inference) conclusion;
			final TracedContext thisContext = tracingState_.getContext(root);
			
			if (thisContext == null || !CYCLE_AVOIDANCE) {
				super.produce(root, conclusion);
				return;
			}
			
			// get the premise which blocks this inference, if any
			Conclusion cyclicPremise = IsInferenceCyclic.check(inference, root, inferenceReader_);
			
			if (cyclicPremise == null) {
				//cool, the inference isn't cyclic, go ahead
				super.produce(root, inference);
			}
			else {
				final TracedContext inferenceContext = tracingState_.getContext(inference.getInferenceContextRoot(root));
				//block the inference in the context where the inference has been made
				LOGGER_.trace("Inference {} is blocked in {} through {}", inference, inferenceContext, cyclicPremise);
				
				inferenceContext.getBlockedInferences().add(new ConclusionEntry(cyclicPremise), inference);
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
	private class InferenceInserter extends AbstractConclusionVisitor<Context, Boolean> {
		
		private final ConclusionProducer localProducer_;
		private final ConclusionInsertionVisitor contextInserter_;
		private final ConclusionVisitor<Context, Boolean> inferenceInserter_;
		
		public InferenceInserter(ConclusionInsertionVisitor inserter, ConclusionProducer producer, SaturationStatistics localStats) {
			contextInserter_ = inserter;
			inferenceInserter_ = SaturationUtils.getUsedConclusionCountingProcessor(new InferenceInsertionVisitor(inferenceWriter_), localStats);
			localProducer_ = producer;
		}

		@Override
		protected Boolean defaultVisit(final Conclusion conclusion, final Context cxt) {
			//write the inference and count it as used conclusion (if needed)
			conclusion.accept(inferenceInserter_, cxt);
			//see if some inferences can now be unblocked
			if (CYCLE_AVOIDANCE && conclusion instanceof Inference) {
				unblockInferences((Inference) conclusion, (TracedContext) cxt);
			}
			//insert into context
			return conclusion.accept(contextInserter_, cxt);
		}
		
		private void unblockInferences(final Inference premiseInference, final TracedContext cxt) {
			Collection<Inference> blocked = cxt.getBlockedInferences().get(new ConclusionEntry(premiseInference));
			
			if (blocked != null) {
				Iterator<Inference> inferenceIter = blocked.iterator();
				
				for (; inferenceIter.hasNext(); ) {
					final Inference blockedInference = inferenceIter.next();
					
					LOGGER_.trace("Checking if {} should be unblocked in {} since we derived {}", blockedInference, cxt, premiseInference);
					
					//this is the context to which the blocked inference should be produced (if unblocked)
					IndexedClassExpression targetRoot = blockedInference.acceptTraced(new GetInferenceTarget(), cxt);
					//deciding if the new inference should unblock the previously blocked one.
					//i.e. if the new inference derives its conclusion NOT via the conclusion of the blocked inference.
					if (IsInferenceCyclic.isAlternative(premiseInference, (Conclusion) blockedInference, targetRoot)) {
						inferenceIter.remove();
						// unblock the inference
						LOGGER_.trace("Inference {} is unblocked in {}", blockedInference, cxt);
						// produce again to the same producer, the inference
						// will again be checked for cyclicity and may be
						// blocked by another premise or finally produced
						localProducer_.produce(targetRoot, blockedInference);
					}
				}
			}
		}
	}
	
}
