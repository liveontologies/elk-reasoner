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
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.util.Comparators;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationEngine;
import org.semanticweb.elk.reasoner.saturation.SaturatedClassExpression;
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

	/**
	 * The processed jobs can create new jobs to be submitted to this engine. In
	 * order to avoid stack overflow due to the potentially unbounded recursion,
	 * we will use a queue to buffer such created jobs. This queue will be
	 * emptied after processing of every input.
	 */
	protected final Queue<SaturationJobForTransitiveReduction<?, I, J>> jobQueue = new ConcurrentLinkedQueue<SaturationJobForTransitiveReduction<?, I, J>>();

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
	class SaturationEngineForTransitiveReduction
			extends
			ClassExpressionSaturationEngine<SaturationJobForTransitiveReduction<?, I, J>> {

		/**
		 * The object to which we delegate post-processing of saturation jobs
		 * and inserting possible new jobs into the job queue.
		 */
		protected final SaturationOutputProcessor saturationOutputProcessor = new SaturationOutputProcessor();

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
		 * {@link #processOutput(SaturationJobForTransitiveReduction)}.
		 * 
		 * @param input
		 *            the saturation job required to be processed
		 * @throws InterruptedException
		 *             if interrupted when the thread was idle
		 */
		@Override
		public void process(SaturationJobForTransitiveReduction<?, I, J> input)
				throws InterruptedException {
			super.process(input);
			for (;;) {
				SaturationJobForTransitiveReduction<?, I, J> nextJob = jobQueue
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
		public void processOutput(
				SaturationJobForTransitiveReduction<?, I, J> output)
				throws InterruptedException {
			super.processOutput(output);
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
	class SaturationOutputProcessor implements SaturationJobVisitor<I, J> {

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
			TransitiveReductionState<J> trState = new TransitiveReductionState<J>(
					initiatorJob, superClassIterator);
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
		 * Update the current equivalent and direct super classes of the current
		 * transitive reduction state using the saturation for one of the super
		 * classes
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
			Set<IndexedClassExpression> superClassDerived = superClassSaturation
					.getSuperClassExpressions();
			List<ElkClass> equivalent = trState.getEquivalent();
			if (superClassDerived.contains(root)) {
				equivalent.add(superClass.getElkClass());
				return;
			}
			/*
			 * Next, we iterate over the transitively reduced set of super
			 * classes of the root computed so far.
			 */
			List<IndexedClass> directSuperClasses = trState
					.getDirectSuperClasses();
			Iterator<IndexedClass> iteratorDirectSuperClasses = directSuperClasses
					.iterator();
			boolean addThis = true;
			while (iteratorDirectSuperClasses.hasNext()) {
				IndexedClass directSuperClass = iteratorDirectSuperClasses
						.next();
				/*
				 * If the (already computed) saturation for such reduced super
				 * class contains the given super class, it cannot be direct.
				 */
				if (directSuperClass.getSaturated().getSuperClassExpressions()
						.contains(superClass)) {
					/*
					 * if this super class is equivalent to the direct super
					 * class, but smaller in the ordering, the direct super
					 * class should be replace
					 */
					if (superClassDerived.contains(directSuperClass)
							&& Comparators.ELK_CLASS_COMPARATOR.compare(
									superClass.getElkClass(),
									directSuperClass.getElkClass()) < 0) {
						iteratorDirectSuperClasses.remove();
						/*
						 * no other direct super-classes can subsume or be
						 * equivalent to this class
						 */
						break;
					} else {
						addThis = false;
						break;
					}
				}
				/*
				 * Otherwise, if the direct super class representative occurs in
				 * the set of the derived classes, it a strict sub-class of the
				 * direct class, and so, should be removed.
				 */
				else if (superClassDerived.contains(directSuperClass)) {
					iteratorDirectSuperClasses.remove();
				}
			}
			if (addThis) {
				directSuperClasses.add(superClass);
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
			List<ElkClass> equivalent = trState.equivalent;
			final List<IndexedClass> directSuperClasses = trState
					.getDirectSuperClasses();
			Iterator<IndexedClassExpression> superClassIterator = trState
					.getSuperClassIterator();

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
			 * when we have reached this point, the transitive reduction is
			 * computed
			 */
			TransitiveReductionOutputSatisfiable<I> satisfiable = new TransitiveReductionOutputSatisfiable<I>(
					root, equivalent, directSuperClasses);
			initiatorJob.setOutput(satisfiable);
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace(root + ": transitive reduction finished");
			}
			processOutput(initiatorJob);
		}
	}

}