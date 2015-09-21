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
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.ClassInferenceSet;
import org.semanticweb.elk.reasoner.saturation.tracing.ModifiableClassInferenceTracingState;
import org.semanticweb.elk.reasoner.saturation.tracing.RecursiveTraceUnwinder;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.semanticweb.elk.util.concurrent.computation.SimpleInterrupter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The factory for engines that concurrently perform unwinding of proofs for
 * {@link Conclusion}s. The {@link Conclusion}s are submitted as the input of
 * the {@link ProofUnwindingJob}, and the engines of the factory compute all
 * {@link ClassInference}s that derive these {@link Conclusion}s and,
 * recursively, all premises of such inferences. The computed
 * {@link ClassInference}s are saved in the
 * {@link ModifiableClassInferenceTracingState} given in the input of this
 * factory. A {@link ProofUnwindingListener} can be used to receive the
 * notification when the submitted {@link ProofUnwindingJob} is processed.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class ProofUnwindingFactory<C extends Conclusion, J extends ProofUnwindingJob<C>>
		extends SimpleInterrupter implements
		InputProcessorFactory<J, ProofUnwindingFactory<C, J>.Engine> {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ProofUnwindingFactory.class);

	private final ContextTracingFactory<IndexedContextRoot, ContextTracingJobForProofUnwinding<C, J>> contextTracingFactory_;

	private final Queue<ContextTracingJobForProofUnwinding<C, J>> jobsToDo_;

	private final ModifiableClassInferenceTracingState tracingState_;

	/**
	 * The listener object implementing callback functions for this engine
	 */
	private final ProofUnwindingListener<C, J> listener_;

	public ProofUnwindingFactory(SaturationState<?> mainSaturationState,
			ModifiableClassInferenceTracingState tracingState, int maxWorkers,
			ProofUnwindingListener<C, J> listener) {
		tracingState_ = tracingState;
		contextTracingFactory_ = new ContextTracingFactory<IndexedContextRoot, ContextTracingJobForProofUnwinding<C, J>>(
				mainSaturationState, maxWorkers,
				new ThisContextTracingListener());
		jobsToDo_ = new ConcurrentLinkedQueue<ContextTracingJobForProofUnwinding<C, J>>();
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
		Conclusion next;
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
			nextInference.accept(ProofUnwindingState.PREMISE_INSERTION_VISITOR,
					unwindingState);
		}
		jobsToDo_.add(new ContextTracingJobForProofUnwinding<C, J>(next,
				unwindingState));
	}

	/**
	 * A simple wrapper around {@link RecursiveTraceUnwinder} which does all the
	 * heavy lifting.
	 */
	class Engine implements InputProcessor<J> {

		/**
		 * The saturation engine used for transitive reduction computation
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

	private class ThisContextTracingListener
			implements
			ContextTracingListener<IndexedContextRoot, ContextTracingJobForProofUnwinding<C, J>> {

		@Override
		public void notifyFinished(ContextTracingJobForProofUnwinding<C, J> job) {
			IndexedContextRoot originRoot = job.getInput();
			ClassInferenceSet inferences = tracingState_
					.getInferencesForOrigin(originRoot);
			if (inferences == null) {
				// not computed yet
				inferences = tracingState_.setClassInferences(originRoot,
						job.getOutput());
			}
			for (ClassInference inference : inferences
					.getClassInferences(job.conclusionToDo)) {
				job.unwindingState.todoInferences.add(inference);
			}
			continueUnwinding(job.unwindingState);
		}
	}
}
