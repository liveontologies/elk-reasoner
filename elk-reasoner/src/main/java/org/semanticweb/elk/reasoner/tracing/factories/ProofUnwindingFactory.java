/**
 * 
 */
package org.semanticweb.elk.reasoner.tracing.factories;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.tracing.InferenceProducer;
import org.semanticweb.elk.reasoner.tracing.ModifiableInferenceSet;
import org.semanticweb.elk.reasoner.tracing.ModifiableInferenceSetImpl;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.semanticweb.elk.util.concurrent.computation.SimpleInterrupter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The factory for engines that concurrently perform unwinding of proofs for
 * {@link ClassConclusion}s. The {@link ClassConclusion}s are submitted as the
 * input of the {@link ProofUnwindingJob}, and the engines of the factory
 * compute all {@link ClassInference}s used in minimal derivations for these
 * {@link ClassConclusion}s. The computed {@link ClassInference}s are reported
 * using the provided {@link InferenceProducer}. A
 * {@link ProofUnwindingListener} can be used to receive the notification when
 * the submitted {@link ProofUnwindingJob} is processed.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class ProofUnwindingFactory<C extends ClassConclusion, J extends ProofUnwindingJob<C>>
		extends SimpleInterrupter implements
		InputProcessorFactory<J, ProofUnwindingFactory<C, J>.Engine> {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ProofUnwindingFactory.class);

	/**
	 * the object used to report the final inferences
	 */
	private final InferenceProducer<? super ClassInference> inferenceProducer_;

	/**
	 * the factory to compute all inferences for contexts
	 */
	private final ContextTracingFactory<IndexedContextRoot, ContextTracingJobForProofUnwinding<C, J>> contextTracingFactory_;

	/**
	 * the job queue
	 */
	private final Queue<ContextTracingJobForProofUnwinding<C, J>> jobsToDo_;

	/**
	 * the traced inferences stored by context; not all of them may be needed
	 */
	private final ConcurrentMap<IndexedContextRoot, ModifiableInferenceSet<ClassInference>> inferencesByContext_;

	/**
	 * The listener object implementing callback functions for this engine
	 */
	private final ProofUnwindingListener<C, J> listener_;

	public ProofUnwindingFactory(SaturationState<?> mainSaturationState,
			InferenceProducer<? super ClassInference> inferenceProducer,
			int maxWorkers, ProofUnwindingListener<C, J> listener) {
		inferenceProducer_ = inferenceProducer;
		contextTracingFactory_ = new ContextTracingFactory<IndexedContextRoot, ContextTracingJobForProofUnwinding<C, J>>(
				mainSaturationState, maxWorkers,
				new ThisContextTracingListener());
		jobsToDo_ = new ConcurrentLinkedQueue<ContextTracingJobForProofUnwinding<C, J>>();
		inferencesByContext_ = new ConcurrentHashMap<IndexedContextRoot, ModifiableInferenceSet<ClassInference>>();
		this.listener_ = listener;
	}

	@Override
	public ProofUnwindingFactory<C, J>.Engine getEngine() {
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

	private void continueUnwinding(ProofUnwindingState<C, J> unwindingState) {
		ClassConclusion next;
		for (;;) {
			next = unwindingState.todoConclusions.poll();
			if (next != null) {
				if (unwindingState.processedConclusions.add(next))
					break;
				// else
				continue;
			}
			// else
			ClassInference nextInference = unwindingState.todoInferences.poll();
			if (nextInference == null) {
				// everything is traced
				J job = unwindingState.initiatorJob;
				LOGGER_.trace("{}: job finished", job);
				listener_.notifyFinished(job);
				return;
			}
			// else
			inferenceProducer_.produce(nextInference);
			unwindingState.todoInferencePremises(nextInference);
		}
		jobsToDo_.add(new ContextTracingJobForProofUnwinding<C, J>(next,
				unwindingState));
	}

	class Engine implements InputProcessor<J> {

		/**
		 * The saturation engine with tracing that saves all inferences in
		 * special contexts
		 */
		private final ContextTracingFactory<IndexedContextRoot, ContextTracingJobForProofUnwinding<C, J>>.Engine tracingEngine_;

		private Engine() {
			this.tracingEngine_ = contextTracingFactory_.getEngine();
		}

		@Override
		public void submit(J job) {
			LOGGER_.trace("{}: job started", job);
			ProofUnwindingState<C, J> unwindingState = new ProofUnwindingState<C, J>(
					job);
			continueUnwinding(unwindingState);
		}

		@Override
		public void process() throws InterruptedException {
			for (;;) {
				if (isInterrupted()) {
					return;
				}
				tracingEngine_.process();
				ContextTracingJobForProofUnwinding<C, J> tracingJob = jobsToDo_
						.poll();
				if (tracingJob == null) {
					break;
				}
				tracingEngine_.submit(tracingJob);
			}
		}

		@Override
		public void finish() {
			tracingEngine_.finish();
		}

	}

	private class ThisContextTracingListener implements
			ContextTracingListener<IndexedContextRoot, ContextTracingJobForProofUnwinding<C, J>> {

		@Override
		public void notifyFinished(
				ContextTracingJobForProofUnwinding<C, J> job) {
			IndexedContextRoot root = job.getInput();
			ModifiableInferenceSet<ClassInference> inferenceSet = inferencesByContext_
					.get(root);
			if (inferenceSet == null) {
				ModifiableInferenceSet<ClassInference> newInferenceSet = new ModifiableInferenceSetImpl<ClassInference>();
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
			for (ClassInference inference : inferenceSet
					.getInferences(job.tracedConclusion)) {
				job.unwindingState.todoInferences.add(inference);
			}
			continueUnwinding(job.unwindingState);
		}
	}
}
