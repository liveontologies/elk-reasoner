/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationEngine;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationListener;
import org.semanticweb.elk.reasoner.saturation.classes.ContextClassSaturation;
import org.semanticweb.elk.reasoner.saturation.rulesystem.Context;
import org.semanticweb.elk.util.concurrent.computation.AbstractJobManager;

/**
 * The engine for computing equivalent classes and direct super classes of the
 * given class expression, if satisfiable, represented by the
 * {@link TransitiveReductionOutput} object. The jobs are submitted using the
 * method {@link #process(IndexedClassExpression)}. A hook for post-processing
 * the result when it is ready, is specified by the
 * {@link #postProcess(TransitiveReductionOutput)} method which should be
 * implemented accordingly.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <R>
 *            the type of the input class expressions for which to compute the
 *            result
 */
public class TransitiveReductionEngine<R extends IndexedClassExpression, J extends TransitiveReductionJob<R>>
		extends AbstractJobManager<J> {

	// logger for events
	protected final static Logger LOGGER_ = Logger
			.getLogger(TransitiveReductionEngine.class);

	/**
	 * The listener for transitive reduction callbacks
	 */
	protected final TransitiveReductionListener<J> listener;
	/**
	 * The saturation engine used for computing saturations of class expressions
	 */
	protected final ClassExpressionSaturationEngine<SaturationJobForTransitiveReduction<R, ?, J>> saturationEngine;

	/**
	 * The processed jobs can create new saturation jobs for super classes to be
	 * submitted to this engine. In order to avoid stack overflow due to the
	 * potentially unbounded recursion, we do not submit the jobs immediately,
	 * but use a queue to buffer such created jobs. This queue will be emptied
	 * when calling the {@link TransitiveReductionEngine#process()} method.
	 */
	protected final Queue<SaturationJobSuperClass<R, J>> auxJobQueue;

	/**
	 * <tt>true</tt> if the {@link #auxJobQueue} queue is empty. This flag is
	 * used for notification that the jobs can be processed.
	 */
	protected final AtomicBoolean jobQueueEmpty;

	/**
	 * Creating the transitive reduction engine for the input ontology index.
	 * 
	 * @param ontologyIndex
	 *            the ontology index for which the engine is created
	 */
	public TransitiveReductionEngine(OntologyIndex ontologyIndex,
			TransitiveReductionListener<J> listener) {

		this.listener = listener;
		this.saturationEngine = new ClassExpressionSaturationEngine<SaturationJobForTransitiveReduction<R, ?, J>>(
				ontologyIndex,
				new ClassExpressionSaturationListenerForTransitiveReduction());
		this.auxJobQueue = new ConcurrentLinkedQueue<SaturationJobSuperClass<R, J>>();
		this.jobQueueEmpty = new AtomicBoolean(true);
	}

	public final void submit(J job) throws InterruptedException {
		R root = job.getInput();
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace(root + ": transitive reduction started");
		}
		saturationEngine.submit(new SaturationJobRoot<R, J>(job));
	}

	public final void process() throws InterruptedException {
		for (;;) {
			saturationEngine.process();
			SaturationJobForTransitiveReduction<R, ?, J> nextJob = auxJobQueue
					.poll();
			if (nextJob == null) {
				if (!jobQueueEmpty.compareAndSet(false, true))
					break;
				nextJob = auxJobQueue.poll();
				if (nextJob == null)
					break;
				tryNotifyCanProcess();
			}
			saturationEngine.submit(nextJob);
		}
	}

	public boolean canProcess() {
		return !auxJobQueue.isEmpty() || saturationEngine.canProcess();
	}

	/**
	 * Print statistics about transitive reduction
	 */
	public void printStatistics() {
		saturationEngine.printStatistics();
	}

	private void tryNotifyCanProcess() {
		if (jobQueueEmpty.compareAndSet(true, false))
			notifyCanProcess();
	}

	/**
	 * The saturation engine for transitive reduction that can only process
	 * instances of {@link SaturationJobForTransitiveReduction}. There are two
	 * types of the jobs. The instances of {@link SaturationJobRoot} are
	 * saturation jobs for the indexed class expression, for which a transitive
	 * reduction needs to be computed. The transitive reduction is computed by
	 * iterating over the derived super classes, and computing saturation for
	 * them in order to filter out non-direct super classes. For this purpose,
	 * the second kind of jobs, which are instances of
	 * {@link SaturationJobSuperClass} are used.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	class ClassExpressionSaturationListenerForTransitiveReduction
			implements
			ClassExpressionSaturationListener<SaturationJobForTransitiveReduction<R, ?, J>> {

		/**
		 * The object to which we delegate post-processing of saturation jobs
		 * and inserting possible new jobs into the job queue.
		 */
		protected final SaturationOutputProcessor saturationOutputProcessor = new SaturationOutputProcessor();

		public void notifyCanProcess() {
			listener.notifyCanProcess();
		}

		/**
		 * Post-processing the finished saturation jobs using the
		 * {@link SaturationOutputProcessor}.
		 * 
		 * @param output
		 *            the processed saturation job
		 * @throws InterruptedException
		 *             if interrupted during post-processing
		 */
		public void notifyProcessed(
				SaturationJobForTransitiveReduction<R, ?, J> output)
				throws InterruptedException {
			output.accept(saturationOutputProcessor);
		}
	}

	/**
	 * The class for processing the output of the finished saturation jobs. It
	 * implements the visitor pattern for
	 * {@link SaturationJobForTransitiveReduction}.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	class SaturationOutputProcessor implements SaturationJobVisitor<R, J> {

		public void visit(SaturationJobRoot<R, J> saturationJob)
				throws InterruptedException {
			/*
			 * We know that the saturation for the root indexed class expression
			 * of the initiator job should already be computed
			 */
			J initiatorJob = saturationJob.initiatorJob;
			R root = initiatorJob.getInput();
			Context saturation = root.getContext();
			/*
			 * If saturation is unsatisfiable, return the unsatisfiable output.
			 */
			if (!((ContextClassSaturation) saturation).isSatisfiable()) {
				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace(root
							+ ": transitive reduction finished: unsatisfiable");
				}
				TransitiveReductionOutput<R> output = new TransitiveReductionOutputUnsatisfiable<R>(
						root);
				initiatorJob.setOutput(output);
				listener.notifyProcessed(initiatorJob);
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
			 * processing this state; when the output will be computed, it will
			 * be submitted
			 */
			processTransitiveReductionState(state);
		}

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
		 * Processing of transitive reduction state by iterating over the
		 * derived indexed class expression candidates and updating the
		 * equivalent and direct-super classes using their saturations. If the
		 * saturation is not yet computed, a new saturation job is created for
		 * this purpose and the processing of the state is suspended until the
		 * job is finished.
		 * 
		 * @param state
		 *            the transitive reduction state to be processed
		 * @throws InterruptedException
		 *             if was interrupted during the processing
		 */
		private void processTransitiveReductionState(
				TransitiveReductionState<R, J> state)
				throws InterruptedException {

			Iterator<IndexedClassExpression> superClassExpressionsIterator = state.superClassExpressionsIterator;
			while (superClassExpressionsIterator.hasNext()) {
				IndexedClassExpression next = superClassExpressionsIterator
						.next();
				if (!(next instanceof IndexedClass))
					continue;
				IndexedClass candidate = (IndexedClass) next;
				Context candidateSaturation = candidate
						.getContext();
				/*
				 * If the saturation for the candidate is not yet computed,
				 * create a corresponding saturation job and suspend processing
				 * of the state until the job will be finished.
				 */
				if (candidateSaturation == null
						|| !((ContextClassSaturation) candidateSaturation).isSaturated()) {
					auxJobQueue.add(new SaturationJobSuperClass<R, J>(
							candidate, state));
					tryNotifyCanProcess();
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
			if (LOGGER_.isTraceEnabled()) {
				R root = output.root;
				LOGGER_.trace(root + ": transitive reduction finished");
				for (TransitiveReductionOutputEquivalent<IndexedClass> direct : output.directSuperClasses) {
					LOGGER_.trace(root + ": direct super class "
							+ direct.getRoot());
				}
			}
			listener.notifyProcessed(state.initiatorJob);
		}

		/**
		 * Updates the output of the transitive reduction using a new candidate
		 * indexed super class and its saturation.
		 * 
		 * @param output
		 *            the partially computed transitive reduction output
		 * @param candidate
		 *            the super class that can be equivalent or direct super
		 *            class
		 * @param candidateSaturation
		 *            the saturation of the candidate
		 */
		private void updateTransitiveReductionOutput(
				TransitiveReductionOutputEquivalentDirect<R> output,
				IndexedClass candidate,
				Context candidateSaturation) {
			R root = output.root;
			if (candidate == root) {
				output.equivalent.add(candidate.getElkClass());
				return;
			}
			Set<IndexedClassExpression> candidateDerived = ((ContextClassSaturation) candidateSaturation)
					.getSuperClassExpressions();
			/*
			 * If the saturation for the candidate contains the root, the
			 * candidate is equivalent to the root
			 */
			if (candidateDerived.contains(root)) {
				output.equivalent.add(candidate.getElkClass());
				return;
			}
			/*
			 * To check if the candidate should be added to the list of direct
			 * super-classes, we iterate over the direct super classes computed
			 * so far.
			 */
			Iterator<TransitiveReductionOutputEquivalent<IndexedClass>> iteratorDirectSuperClasses = output.directSuperClasses
					.iterator();
			while (iteratorDirectSuperClasses.hasNext()) {
				TransitiveReductionOutputEquivalent<IndexedClass> directSuperClassEquivalent = iteratorDirectSuperClasses
						.next();
				IndexedClass directSuperClass = directSuperClassEquivalent
						.getRoot();
				/*
				 * If the (already computed) saturation for direct super-class
				 * contains the candidate, it cannot be direct.
				 */
				if (((ContextClassSaturation) directSuperClass.getContext()).getSuperClassExpressions()
						.contains(candidate)) {
					/*
					 * If, in addition, the saturation for the candidate
					 * contains the direct super class, they are equivalent, so
					 * it is added to the equivalence class.
					 */
					if (candidateDerived.contains(directSuperClass))
						directSuperClassEquivalent.equivalent.add(candidate
								.getElkClass());
					return;
				}
				/*
				 * At this point we know that the candidate is not contained in
				 * the saturation of the super-class. We check, if conversely,
				 * the saturation of the candidate contains the super-class. In
				 * this case the super-class is not direct and should be removed
				 * from the list.
				 */
				if (candidateDerived.contains(directSuperClass)) {
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
			output.directSuperClasses.add(candidateOutput);
		}
	}

}