/**
 * 
 */
package org.semanticweb.elk.reasoner.tracing.factories;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.semanticweb.elk.owl.visitors.DummyElkAxiomVisitor;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.tracing.DummyConclusionVisitor;
import org.semanticweb.elk.reasoner.tracing.ModifiableTracingInferenceSet;
import org.semanticweb.elk.reasoner.tracing.ModifiableTracingInferenceSetImpl;
import org.semanticweb.elk.reasoner.tracing.TraceState;
import org.semanticweb.elk.reasoner.tracing.TracingInference;
import org.semanticweb.elk.reasoner.tracing.TracingInferencePremiseVisitor;
import org.semanticweb.elk.util.concurrent.computation.Processor;
import org.semanticweb.elk.util.concurrent.computation.ProcessorFactory;
import org.semanticweb.elk.util.concurrent.computation.SimpleInterrupter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The factory for engines that concurrently perform unwinding of proofs for
 * untraced {@link ClassConclusion}s of the {@link TraceState}. That is, the
 * engines compute the {@link ClassInference}s deriving these
 * {@link ClassConclusion}s and, recursively, the premises of these inferences,
 * and store them in the {@link TraceState}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class ProofUnwindingFactory extends SimpleInterrupter
		implements ProcessorFactory<ProofUnwindingFactory.Engine> {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ContextTracingFactory.class);
	
	/**
	 * the object where the traced inferences are stored
	 */
	private final TraceState traceState_;

	/**
	 * the factory to compute all inferences for contexts
	 */
	private final ContextTracingFactory<IndexedContextRoot, ContextTracingJobForProofUnwinding> contextTracingFactory_;

	/**
	 * the traced inferences stored by context; not all of them may be needed
	 */
	private final ConcurrentMap<IndexedContextRoot, ModifiableTracingInferenceSet<ClassInference>> inferencesByContext_;

	public ProofUnwindingFactory(SaturationState<?> mainSaturationState,
			TraceState traceState, int maxWorkers) {
		traceState_ = traceState;
		contextTracingFactory_ = new ContextTracingFactory<IndexedContextRoot, ContextTracingJobForProofUnwinding>(
				mainSaturationState, maxWorkers,
				new ThisContextTracingListener());
		inferencesByContext_ = new ConcurrentHashMap<IndexedContextRoot, ModifiableTracingInferenceSet<ClassInference>>();
	}

	@Override
	public ProofUnwindingFactory.Engine getEngine() {
		return new Engine();
	}

	@Override
	public void finish() {
		contextTracingFactory_.finish();
	}

	public void printStatistics() {
		contextTracingFactory_.printStatistics();
	}

	public SaturationStatistics getRuleAndConclusionStatistics() {
		return contextTracingFactory_.getRuleAndConclusionStatistics();
	}

	class Engine implements Processor {

		/**
		 * The saturation engine with tracing that saves all inferences in
		 * special contexts
		 */
		private final ContextTracingFactory<IndexedContextRoot, ContextTracingJobForProofUnwinding>.Engine tracingEngine_;

		private Engine() {
			this.tracingEngine_ = contextTracingFactory_.getEngine();
		}

		@Override
		public void process() throws InterruptedException {
			for (;;) {
				if (isInterrupted()) {
					return;
				}
				tracingEngine_.process();
				ClassConclusion conclusion = traceState_.pollToTrace();
				if (conclusion == null) {
					break;
				}
				// else
				tracingEngine_.submit(
						new ContextTracingJobForProofUnwinding(conclusion));
			}
		}

		@Override
		public void finish() {
			tracingEngine_.finish();
		}

	}

	private class ThisContextTracingListener implements
			ContextTracingListener<IndexedContextRoot, ContextTracingJobForProofUnwinding> {

		private final TracingInference.Visitor<Void> inferencePremiseInsertionVisitor = new TracingInferencePremiseVisitor<Void>(
				new DummyConclusionVisitor<Void>() {
					@Override
					protected Void defaultVisit(ClassConclusion premise) {
						traceState_.toTrace(premise);
						return null;
					}
				}, new DummyElkAxiomVisitor<Void>());

		@Override
		public void notifyFinished(ContextTracingJobForProofUnwinding job) {
			IndexedContextRoot root = job.getInput();
			ModifiableTracingInferenceSet<ClassInference> inferenceSet = inferencesByContext_
					.get(root);
			if (inferenceSet == null) {
				ModifiableTracingInferenceSet<ClassInference> newInferenceSet = new ModifiableTracingInferenceSetImpl<ClassInference>();
				synchronized (newInferenceSet) {
					inferenceSet = inferencesByContext_.putIfAbsent(root,
							newInferenceSet);
					if (inferenceSet == null) {
						inferenceSet = newInferenceSet;
						ClassInferenceBlockingFilter filter = new ClassInferenceBlockingFilter(
								inferenceSet);
						for (ClassInference inference : job.getOutput()) {
							filter.produce(inference);
						}
					}
				}
			}
			Iterable<? extends ClassInference> inferences;
			synchronized (inferenceSet) {
				inferences = inferenceSet.getInferences(job.tracedConclusion);
			}
			boolean provable = false;
			for (ClassInference inference : inferences) {
				traceState_.produce(inference);
				provable = true;
				inference.accept(inferencePremiseInsertionVisitor);
			}
			if (!provable) {
				LOGGER_.error("{}: no inferences traced!", job.tracedConclusion);
			}
		}
	}
}
