/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.reduction;

import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationFactory;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;

/**
 * The factory for engines that concurrently perform the transitive reduction of
 * the derived subsumption hierarchy between classes. The engines accept
 * instances of {@link TransitiveReductionJob} with the specified root
 * {@link IndexedClassExpression}. Upon successful completion of the job, one of
 * the two types of the {@link TransitiveReductionOutput} can be assigned:
 * either {@link TransitiveReductionOutputUnsatisfiable}, which means that the
 * given root {@link IndexedClassExpression} is unsatisfiable, or
 * {@link TransitiveReductionOutputEquivalentDirect}, which contains information
 * about equivalent classes of the given root {@link IndexedClassExpression} and
 * its direct super-classes.
 * 
 * As usual, to this engine factory it is possible to attach a
 * {@link TransitiveReductionListener} using which one can monitor the
 * processing of jobs and perform actions accordingly.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <R>
 *            the type of the input class expressions for which to compute the
 *            result
 * @param <J>
 *            the type of the jobs that can be processed by this transitive
 *            reduction engine
 * 
 * @see TransitiveReductionOutput
 * @see TransitiveReductionOutputUnsatisfiable
 * @see TransitiveReductionOutputEquivalentDirect
 * @see TransitiveReductionListener
 */
public class TransitiveReductionFactory<R extends IndexedClassExpression, J extends TransitiveReductionJob<R>>
		implements
		InputProcessorFactory<J, TransitiveReductionFactory<R, J>.Engine> {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(TransitiveReductionFactory.class);

	/**
	 * The listener object implementing callback functions for this engine
	 */
	private final TransitiveReductionListener<J> listener;
	/**
	 * The object used to process the finished saturation jobs
	 */
	private final SaturationOutputProcessor saturationOutputProcessor = new SaturationOutputProcessor();
	/**
	 * The processed jobs can create new saturation jobs for super classes to be
	 * submitted to this engine. In order to avoid stack overflow due to the
	 * potentially unbounded recursion, we do not submit the jobs immediately,
	 * but use a queue to buffer such created jobs. This queue will be emptied
	 * every time the {@link Engine#process()} method is called.
	 */
	private final Queue<SaturationJobSuperClass<R, J>> auxJobQueue;

	/**
	 * The jobs which root {@link IndexedClassExpression}s are already saturated
	 */
	private final Queue<J> jobsWithSaturatedRoot;

	/**
	 * The saturation factory used for computing saturations for relevant
	 * indexed class expressions
	 */
	private final ClassExpressionSaturationFactory<SaturationJobForTransitiveReduction<R, ?, J>> saturationFactory;

	/**
	 * Creating a new transitive reduction engine for the input ontology index
	 * and a listener for executing callback functions.
	 * 
	 * @param saturationState
	 *            the saturation state of the reasoner
	 * @param maxWorkers
	 *            the maximum number of workers that can use this factory
	 * @param listener
	 *            the listener object implementing callback functions for this
	 *            engine
	 */
	public TransitiveReductionFactory(SaturationState saturationState,
			int maxWorkers, TransitiveReductionListener<J> listener) {
		this.listener = listener;
		this.auxJobQueue = new ConcurrentLinkedQueue<SaturationJobSuperClass<R, J>>();
		this.jobsWithSaturatedRoot = new ConcurrentLinkedQueue<J>();
		this.saturationFactory = new ClassExpressionSaturationFactory<SaturationJobForTransitiveReduction<R, ?, J>>(
				saturationState, maxWorkers,
				new ThisClassExpressionSaturationListener());
	}

	@Override
	public Engine getEngine() {
		return new Engine();
	}

	@Override
	public void finish() {
		saturationFactory.finish();
	}

	/**
	 * Print statistics about the transitive reduction stage
	 */
	public void printStatistics() {
		saturationFactory.printStatistics();
	}

	public SaturationStatistics getRuleAndConclusionStatistics() {
		return saturationFactory.getRuleAndConclusionStatistics();
	}

	/**
	 * The listener class used for the class expression saturation engine, which
	 * is used within this transitive reduction engine
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private class ThisClassExpressionSaturationListener
			implements
			ClassExpressionSaturationListener<SaturationJobForTransitiveReduction<R, ?, J>> {

		@Override
		public void notifyFinished(
				SaturationJobForTransitiveReduction<R, ?, J> output)
				throws InterruptedException {
			output.accept(saturationOutputProcessor);
		}
	}

	/**
	 * The class for processing the finished saturation jobs. It implements the
	 * visitor pattern for {@link SaturationJobForTransitiveReduction}.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private class SaturationOutputProcessor implements
			SaturationJobVisitor<R, J> {

		@Override
		public void visit(SaturationJobRoot<R, J> saturationJob)
				throws InterruptedException {
			processRootSaturation(saturationJob.initiatorJob);

		}

		@Override
		public void visit(SaturationJobSuperClass<R, J> saturationJob)
				throws InterruptedException {
			/*
			 * In this case the saturation for the super-class candidate is
			 * computed; we need to update the output of the transitive
			 * reduction using this candidate and resume processing of the
			 * transitive reduction state.
			 */
			IndexedClass candidate = saturationJob.getInput();
			TransitiveReductionState<R, J> state = saturationJob.state;
			updateTransitiveReductionOutput(state.output, candidate,
					candidate.getContext());
			processTransitiveReductionState(state);
		}

		/**
		 * Processing jobs for which saturation of the root
		 * {@link IndexedClassExpression} has been computed
		 * 
		 * @param initiatorJob
		 *            the jobs for which saturation of the root
		 *            {@link IndexedClassExpression} has been computed
		 * @throws InterruptedException
		 *             if interrupted while processing
		 */
		private void processRootSaturation(J initiatorJob)
				throws InterruptedException {
			/*
			 * It is required that the saturation for the root indexed class
			 * expression of the initiator job should already be computed
			 */
			R root = initiatorJob.getInput();
			Context saturation = root.getContext();

			/*
			 * If saturation is unsatisfiable, return the unsatisfiable output.
			 */
			if (saturation.isInconsistent()) {
				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace(root
							+ ": transitive reduction finished: inconsistent");
				}
				TransitiveReductionOutput<R> output = new TransitiveReductionOutputUnsatisfiable<R>(
						root);
				initiatorJob.setOutput(output);
				listener.notifyFinished(initiatorJob);
				return;
			}
			/*
			 * Otherwise, to perform the transitive reduction, we need to
			 * compute the saturation for every derived indexed super-class of
			 * the saturation. We initialize the transitive reduction state for
			 * this purpose.
			 */
			TransitiveReductionState<R, J> state = new TransitiveReductionState<R, J>(
					initiatorJob);
			/*
			 * here we processing this state where we compute the output of the
			 * transitive reduction; when the state will be processed, its
			 * output will be submitted as the output of transitive reduction.
			 */
			processTransitiveReductionState(state);
		}

		/**
		 * Processing of transitive reduction state by iterating over the
		 * derived subsumers and updating the equivalent and direct subsumers
		 * using their saturations. If the saturation is not yet computed, a new
		 * saturation job is created for this purpose and the processing of the
		 * state is suspended until the job is finished.
		 * 
		 * @param state
		 *            the transitive reduction state to be processed
		 * @throws InterruptedException
		 *             if was interrupted during the processing
		 */
		private void processTransitiveReductionState(
				TransitiveReductionState<R, J> state)
				throws InterruptedException {

			Iterator<IndexedClassExpression> subsumerIterator = state.subsumerIterator;
			while (subsumerIterator.hasNext()) {
				IndexedClassExpression next = subsumerIterator.next();
				if (!(next instanceof IndexedClass))
					continue;
				IndexedClass candidate = (IndexedClass) next;
				Context candidateSaturation = candidate.getContext();
				/*
				 * If the saturation for the candidate is not yet computed,
				 * create a corresponding saturation job and suspend processing
				 * of the state until the job will be finished.
				 */
				if (candidateSaturation == null
						|| !candidateSaturation.isSaturated()) {
					auxJobQueue.add(new SaturationJobSuperClass<R, J>(
							candidate, state));
					return;
				}
				/*
				 * Otherwise update the output of the transitive reduction using
				 * the saturation
				 */
				updateTransitiveReductionOutput(state.output, candidate,
						candidateSaturation);
			}
			/* When all candidates are processed, the output is computed */
			TransitiveReductionOutputEquivalentDirect<R> output = state.output;
			state.initiatorJob.setOutput(state.output);
			listener.notifyFinished(state.initiatorJob);
			if (LOGGER_.isTraceEnabled()) {
				R root = output.root;
				LOGGER_.trace(root + ": transitive reduction finished");
				for (ElkClass equivalent : output.equivalent) {
					LOGGER_.trace(root + ": equivalent " + equivalent.getIri());
				}
				for (TransitiveReductionOutputEquivalent<IndexedClass> direct : output.directSubsumers) {
					String message = root + ": direct super class ["
							+ direct.getRoot();
					for (ElkClass equivalent : direct.equivalent)
						message = message + ", " + equivalent.getIri();
					message = message + "]";
					LOGGER_.trace(message);
				}
			}
		}

		/**
		 * Updates the output of the transitive reduction using a new candidate
		 * indexed super class and its saturation.
		 * 
		 * @param output
		 *            the partially computed transitive reduction output
		 * @param candidate
		 *            the super class of the root
		 * @param candidateSaturation
		 *            the saturation of the candidate
		 */
		private void updateTransitiveReductionOutput(
				TransitiveReductionOutputEquivalentDirect<R> output,
				IndexedClass candidate, Context candidateSaturation) {

			R root = output.root;

			if (candidate == root) {
				output.equivalent.add(candidate.getElkClass());
				return;
			}

			Set<IndexedClassExpression> candidateSupers = candidateSaturation
					.getSubsumers();
			/*
			 * If the saturation for the candidate contains the root, the
			 * candidate is equivalent to the root
			 */
			if (candidateSupers.contains(root)) {
				output.equivalent.add(candidate.getElkClass());
				return;
			}
			/*
			 * To check if the candidate should be added to the list of direct
			 * super-classes, we iterate over the direct super classes computed
			 * so far.
			 */
			Iterator<TransitiveReductionOutputEquivalent<IndexedClass>> iteratorDirectSuperClasses = output.directSubsumers
					.iterator();
			while (iteratorDirectSuperClasses.hasNext()) {
				TransitiveReductionOutputEquivalent<IndexedClass> directSuperClassEquivalent = iteratorDirectSuperClasses
						.next();
				IndexedClass directSuperClass = directSuperClassEquivalent
						.getRoot();
				/*
				 * If the (already computed) saturation for the direct
				 * super-class contains the candidate, it cannot be direct.
				 */
				if (directSuperClass.getContext().getSubsumers()
						.contains(candidate)) {
					/*
					 * If, in addition, the saturation for the candidate
					 * contains the direct super class, they are equivalent, so
					 * the candidate is added to the equivalence class of the
					 * direct super class.
					 */
					if (candidateSupers.contains(directSuperClass))
						directSuperClassEquivalent.equivalent.add(candidate
								.getElkClass());
					return;
				}
				/*
				 * At this point we know that the candidate is not contained in
				 * the saturation of the direct super-class. We check, if
				 * conversely, the saturation of the candidate contains the
				 * direct super-class. In this case the direct super-class is
				 * not direct anymore and should be removed from the list.
				 */
				if (candidateSupers.contains(directSuperClass)) {
					iteratorDirectSuperClasses.remove();
				}
			}
			/*
			 * if the candidate has survived all the tests, then it is a direct
			 * super-class
			 */
			TransitiveReductionOutputEquivalent<IndexedClass> candidateOutput = new TransitiveReductionOutputEquivalent<IndexedClass>(
					candidate);
			candidateOutput.equivalent.add(candidate.getElkClass());
			output.directSubsumers.add(candidateOutput);
		}
	}

	public class Engine implements InputProcessor<J> {

		/**
		 * The saturation engine used for transitive reduction computation
		 */
		private final ClassExpressionSaturationFactory<SaturationJobForTransitiveReduction<R, ?, J>>.Engine saturationEngine = saturationFactory
				.getEngine();

		// don't allow creating of engines directly; only through the factory
		private Engine() {
		}

		@Override
		public final void submit(J job) {
			R root = job.getInput();
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace(root + ": transitive reduction started");
			}
			Context context = root.getContext();
			if (context != null && context.isSaturated()) {
				jobsWithSaturatedRoot.add(job);
			} else {
				saturationEngine.submit(new SaturationJobRoot<R, J>(job));
			}
		}

		@Override
		public final void process() throws InterruptedException {
			for (;;) {
				if (Thread.currentThread().isInterrupted())
					return;
				J processedJob = jobsWithSaturatedRoot.poll();
				if (processedJob != null) {
					saturationOutputProcessor
							.processRootSaturation(processedJob);
					continue;
				}
				saturationEngine.process();
				SaturationJobForTransitiveReduction<R, ?, J> nextJob = auxJobQueue
						.poll();
				if (nextJob == null)
					break;
				saturationEngine.submit(nextJob);
			}
		}

		@Override
		public void finish() {
			saturationEngine.finish();
		}

	}

}
