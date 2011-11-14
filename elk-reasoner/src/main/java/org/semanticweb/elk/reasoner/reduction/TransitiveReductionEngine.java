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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationEngine;
import org.semanticweb.elk.reasoner.saturation.SaturatedClassExpression;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

/**
 * The engine for computing equivalent classes and direct super classes of the
 * given class expression, if satisfiable, represented by the
 * {@link TransitiveReductionOutput} object. The jobs are submitted using the
 * method {@link #process(IndexedClassExpression)}. A hook for post-processing
 * the result when it is ready, is specified by the
 * {@link #processOutput(TransitiveReductionOutput)} method which should be
 * implemented accordingly.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <I>
 *            the type of the input class expressions for which to compute the
 *            result
 */
public class TransitiveReductionEngine<I extends IndexedClassExpression, J extends TransitiveReductionJob<I>>
		implements InputProcessor<J> {

	// logger for events
	protected final static Logger LOGGER_ = Logger
			.getLogger(TransitiveReductionEngine.class);

	/**
	 * The saturation engine that is specific for transitive reduction
	 */
	protected final SaturationEngineForTransitiveReduction saturationEngineForTransitiveReduction;

	public TransitiveReductionEngine(OntologyIndex ontologyIndex) {
		this.saturationEngineForTransitiveReduction = new SaturationEngineForTransitiveReduction(
				ontologyIndex);
	}

	public void process(J job) throws InterruptedException {
		I root = job.getInput();
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace(root + ": transitive reduction started");
		}
		saturationEngineForTransitiveReduction
				.process(new SaturationJobRoot<I, J>(job));
	}

	/**
	 * The hook for post-processing the finished jobs
	 * 
	 * @param output
	 *            the processed job
	 * @throws InterruptedException
	 */
	protected void processOutput(J job) throws InterruptedException {

	}

	/**
	 * The saturation engine for transitive reduction that can only process
	 * instances of {@link SaturationJobTransitiveReduction}. There are two
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
	class SaturationEngineForTransitiveReduction
			extends
			ClassExpressionSaturationEngine<SaturationJobTransitiveReduction<?, I, J>> {

		/**
		 * The processed jobs can create new jobs to be submitted to this
		 * engine. In order to avoid stack overflow due to the potentially
		 * unbounded recursion, we will use a queue to buffer such created jobs.
		 * This queue will be emptied after processing of every input.
		 */
		protected final Queue<SaturationJobTransitiveReduction<?, I, J>> jobQueue = new ConcurrentLinkedQueue<SaturationJobTransitiveReduction<?, I, J>>();

		/**
		 * The object to which we delegate post-processing of saturation jobs
		 * and inserting possible new jobs into the job queue.
		 */
		protected final SaturationOutputProcessor saturationOutputProcessor = new SaturationOutputProcessor(
				jobQueue);

		/**
		 * Creates a new transitive reduction saturation engine using a given
		 * ontology index.
		 * 
		 * @param ontologyIndex
		 */
		public SaturationEngineForTransitiveReduction(
				OntologyIndex ontologyIndex) {
			super(ontologyIndex);
		}

		/**
		 * Processing of saturation jobs required for transitive reduction. Once
		 * the job is processed, it is submitted to the method
		 * {@link #processSatisfiable(SaturationJobTransitiveReduction)}.
		 * 
		 * @param input
		 *            the saturation job required to be processed
		 * @throws InterruptedException
		 *             if interrupted when the thread was idle
		 */
		@Override
		public void process(SaturationJobTransitiveReduction<?, I, J> input)
				throws InterruptedException {
			super.process(input);
			for (;;) {
				SaturationJobTransitiveReduction<?, I, J> nextJob = jobQueue
						.poll();
				if (nextJob == null)
					break;
				super.process(nextJob);
			}
		}

		/**
		 * Post-processing the finished saturation jobs. Either new saturation
		 * jobs will be created and submitted to the job queue, or the result
		 * {@link TransitiveReductionOutput} will be created and submitted to
		 * {@link TransitiveReductionEngine#processOutput(TransitiveReductionOutput)}
		 * .
		 * 
		 * @param output
		 *            the processed saturation job
		 * @throws InterruptedException
		 *             if interrupted when the thread was idle
		 */
		@Override
		public void processSatisfiable(
				SaturationJobTransitiveReduction<?, I, J> output)
				throws InterruptedException {
			super.processSatisfiable(output);
			output.accept(saturationOutputProcessor);
		}
	}

	/**
	 * The class for processing the output of the finished saturation jobs. It
	 * implements the visitor pattern for
	 * {@link SaturationJobTransitiveReduction}.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	class SaturationOutputProcessor implements SaturationJobVisitor<I, J> {

		/**
		 * The job queue into which the newly created saturation jobs are
		 * submitted. This queue is supposed to be shared with
		 * {@link SaturationEngineForTransitiveReduction}.
		 */
		protected final Queue<SaturationJobTransitiveReduction<?, I, J>> jobQueue;

		/**
		 * Creating a new saturation output processor that uses the given
		 * saturation job queue.
		 * 
		 * @param jobQueue
		 *            the queue used for submitting the newly created saturation
		 *            jobs
		 */
		SaturationOutputProcessor(
				Queue<SaturationJobTransitiveReduction<?, I, J>> jobQueue) {
			this.jobQueue = jobQueue;
		}

		public void visit(
				SaturationJobRoot<I, J> saturationJobTransitiveReductionRoot)
				throws InterruptedException {

			J initiatorJob = saturationJobTransitiveReductionRoot
					.getInitiatorJob();
			/*
			 * In this case we need to compute the equivalent and direct super
			 * classes of the root indexed class expression that was the input
			 * of the job that we are post-processing.
			 */
			I root = saturationJobTransitiveReductionRoot.getInput();
			/*
			 * we know that the saturation is already computed as the output of
			 * the job
			 */
			SaturatedClassExpression saturation = saturationJobTransitiveReductionRoot
					.getOutput();
			/*
			 * Unsatisfiable class expressions is a special case: we do not
			 * search for all equivalent unsatisfiable classes and direct
			 * super-classes at this stage.
			 */
			if (!saturation.isSatisfiable()) {
				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace(root
							+ ": transitive reduction finished: unsatisfiable");
				}
				TransitiveReductionOutput<I> output = new TransitiveReductionOutputUnsatisfiable<I>(
						root);
				initiatorJob.setOutput(output);
				processOutput(initiatorJob);
				return;
			}
			/*
			 * We construct equivalent classes and direct super classes by
			 * iterating over the derived super classes using an iterator.
			 */
			Iterator<IndexedClassExpression> superClassIterator = saturation
					.getSuperClassExpressions().iterator();
			/*
			 * For some super-classes we will need to compute saturation, so we
			 * need to save the states we are currently in until the computation
			 * is over.
			 */
			Set<ElkClass> equivalent = new ArrayHashSet<ElkClass>(1);
			List<IndexedClass> directSuperClasses = new LinkedList<IndexedClass>();
			TransitiveReductionState<J> trState = new TransitiveReductionState<J>(
					initiatorJob, equivalent, directSuperClasses,
					superClassIterator);

			/* the iteration over the super classes using the state is done here */
			processSuperClasses(trState);
		}

		public void visit(
				SaturationJobSuperClass<I, J> saturationJobTransitiveReductionClass)
				throws InterruptedException {
			/*
			 * this handles a situation when we have computed the saturation for
			 * one of the super-class in a particular iteration state
			 */
			IndexedClass superClass = saturationJobTransitiveReductionClass
					.getInput();
			SaturatedClassExpression superClassSaturation = saturationJobTransitiveReductionClass
					.getOutput();
			TransitiveReductionState<J> trState = saturationJobTransitiveReductionClass
					.getTransitiveReductionState();

			/*
			 * we use the saturation to update the equivalent classes and direct
			 * super classes of the root
			 */
			updateTransitiveReductionState(superClass, superClassSaturation,
					trState);
			/* continue with the next super classes */
			processSuperClasses(trState);
		}

		/**
		 * Update the current equivalent and reduced super classes of the
		 * current transitive reduction state using the saturation for one of
		 * the super classes
		 * 
		 * @param superClass
		 *            the next super class for which the saturation is computed
		 * @param superClassSaturation
		 *            the computed saturation of the super class
		 * @param trState
		 *            the current transitive reduction state for the root class
		 *            expression
		 * @throws InterruptedException
		 *             if interrupted when the process was idle
		 */
		void updateTransitiveReductionState(IndexedClass superClass,
				SaturatedClassExpression superClassSaturation,
				TransitiveReductionState<J> trState)
				throws InterruptedException {
			/*
			 * First we check if the root class is contained in the saturation
			 * for the super class; in this case the super class is equivalent
			 * to the root.
			 */
			IndexedClassExpression root = trState.getInitiatorJob().getInput();
			Set<ElkClass> equivalent = trState.getEquivalent();
			if (superClassSaturation.getSuperClassExpressions().contains(root)) {
				equivalent.add(superClass.getElkClass());
				return;
			}
			/*
			 * Next, we iterate over the transitively reduced set of super
			 * classes of the root computed so far. If the (already computed)
			 * saturation for such reduced super class contains the given super
			 * class, it cannot be direct. Similarly, if one of the reduced
			 * super classes occurs in the computed saturation, it should be
			 * removed.
			 */
			List<IndexedClass> reducedSuperClasses = trState
					.getReducedSuperClasses();
			Iterator<IndexedClass> iteratorDirectSuperClasses = reducedSuperClasses
					.iterator();
			boolean addThis = true;
			while (iteratorDirectSuperClasses.hasNext()) {
				IndexedClass next = iteratorDirectSuperClasses.next();
				if (next.getSaturated().getSuperClassExpressions()
						.contains(superClass)) {
					addThis = false;
					break;
				}
				if (superClassSaturation.getSuperClassExpressions().contains(
						next)) {
					iteratorDirectSuperClasses.remove();
				}
			}
			if (addThis) {
				reducedSuperClasses.add(superClass);
			}
		}

		/**
		 * Processing the next super classes of a class expression using the
		 * transitive reduction state
		 * 
		 * @param trState
		 *            the current state of the transitive reduction for the root
		 * @throws InterruptedException
		 *             if interrupted when the thread was idle
		 */
		void processSuperClasses(TransitiveReductionState<J> trState)
				throws InterruptedException {

			J initiatorJob = trState.getInitiatorJob();
			I root = initiatorJob.getInput();
			Set<ElkClass> equivalent = trState.equivalent;
			final List<IndexedClass> reducedSuperClasses = trState.reducedSuperClasses;
			Iterator<IndexedClassExpression> superClassIterator = trState.superClassIterator;

			/* finding the next super class */
			while (superClassIterator.hasNext()) {
				IndexedClassExpression superClassExpression = superClassIterator
						.next();
				if (superClassExpression instanceof IndexedClass) {
					IndexedClass superClass = (IndexedClass) superClassExpression;
					if (superClass == root)
						equivalent.add(superClass.getElkClass());
					else {
						SaturatedClassExpression superClassSaturation = superClass
								.getSaturated();
						if (superClassSaturation != null
								&& superClassSaturation.isSaturated()) {
							updateTransitiveReductionState(superClass,
									superClassSaturation, trState);
						} else {
							jobQueue.add(new SaturationJobSuperClass<I, J>(
									superClass, trState));
							return;
						}
					}
				}
			}
			/*
			 * when we reached this point, the transitive reduction is computed
			 */
			TransitiveReductionOutputSatisfiable<I> satisfiable = new TransitiveReductionOutputSatisfiable<I>(
					root, equivalent, reducedSuperClasses);
			initiatorJob.setOutput(satisfiable);
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace(root + ": transitive reduction finished");
			}
			processOutput(initiatorJob);
		}
	}
}