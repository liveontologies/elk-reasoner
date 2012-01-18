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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
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
import org.semanticweb.elk.reasoner.saturation.RuleApplicationEngine;
import org.semanticweb.elk.reasoner.saturation.SaturatedClassExpression;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

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
	protected void postProcess(J job) throws InterruptedException {

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
	 * {@link SaturationJobSuperClasses} are used.
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
			super(new RuleApplicationEngine(ontologyIndex));
		}

		/**
		 * Processing of saturation jobs required for transitive reduction. Once
		 * the job is processed, it is submitted to the method
		 * {@link #postProcess(SaturationJobForTransitiveReduction)}.
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
		 * {@link TransitiveReductionEngine#postProcess(TransitiveReductionOutput)}
		 * .
		 * 
		 * @param output
		 *            the processed saturation job
		 * @throws InterruptedException
		 *             if interrupted when the thread was idle
		 */
		@Override
		public void postProcess(
				SaturationJobForTransitiveReduction<?, I, J> output)
				throws InterruptedException {
			super.postProcess(output);
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

		public void visit(SaturationJobRoot<I, J> saturationJob)
				throws InterruptedException {

			J initiatorJob = saturationJob.getInitiatorJob();
			/*
			 * In this case we need to compute the equivalent and direct super
			 * classes of the root indexed class expression that was the input
			 * of the initiator job and the job that we are post-processing.
			 */
			I root = initiatorJob.getInput();
			/*
			 * we know that the saturation is already computed for the root
			 */
			SaturatedClassExpression saturation = root.getSaturated();
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
				postProcess(initiatorJob);
				return;
			}
			/*
			 * Otherwise, to perform the transitive reduction, we need to
			 * compute the saturation for every derived super-class of the
			 * saturation. We create a special instance of the saturation job
			 * for this purpose and add it to the job queue.
			 */
			jobQueue.add(new SaturationJobSuperClasses<I, J>(initiatorJob));
		}

		public void visit(SaturationJobSuperClasses<I, J> saturationJob)
				throws InterruptedException {
			/*
			 * The saturations for all derived super-classes of the initiator
			 * job have been computed. Therefore, we can compute the result of
			 * the transitive reduction.
			 */
			final I root = saturationJob.getInitiatorJob().getInput();
			/**
			 * The list containing all equivalent classes of the root.
			 */
			final List<ElkClass> equivalent = new ArrayList<ElkClass>(1);
			/**
			 * The list consisting direct super-classes of the root. If some of
			 * the direct super-classes are equivalent, the list contains only
			 * the smallest in the ordering according to
			 * {@link Comparators.ELK_CLASS_COMPARATOR}
			 */
			final List<IndexedClass> directSuperClasses = new LinkedList<IndexedClass>();
			for (IndexedClass candidate : saturationJob) {
				if (candidate == root) {
					equivalent.add(candidate.getElkClass());
					continue;
				}
				Set<IndexedClassExpression> candidateDerived = candidate
						.getSaturated().getSuperClassExpressions();
				/*
				 * If the saturation for the candidate contains the root, it is
				 * equivalent to the root
				 */
				if (candidateDerived.contains(root)) {
					equivalent.add(candidate.getElkClass());
					continue;
				}
				/*
				 * To check if the candidate should be added to the list of
				 * direct super-classes, we iterate over the transitively
				 * reduced set of super classes of the root computed so far.
				 */
				Iterator<IndexedClass> iteratorDirectSuperClasses = directSuperClasses
						.iterator();
				/*
				 * If the value of this flag is true after the iteration, the
				 * candidate should be added to the list
				 */
				boolean addCandidate = true;
				while (iteratorDirectSuperClasses.hasNext()) {
					IndexedClass directSuperClass = iteratorDirectSuperClasses
							.next();
					/*
					 * If the (already computed) saturation for direct
					 * super-class contains the candidate, it cannot be direct.
					 */
					if (directSuperClass.getSaturated()
							.getSuperClassExpressions().contains(candidate)) {
						/*
						 * If, conversely, the saturation for the candidate
						 * contains the direct super class, they are equivalent.
						 * In this case, if the candidate is smaller
						 * lexicographically, we remove the super-class from the
						 * list and add the candidate instead.
						 */
						if (candidateDerived.contains(directSuperClass)
								&& Comparators.ELK_CLASS_COMPARATOR.compare(
										candidate.getElkClass(),
										directSuperClass.getElkClass()) < 0) {
							iteratorDirectSuperClasses.remove();
							/*
							 * no other direct super-classes computed so far can
							 * subsume or be equivalent to the candidate because
							 * this was the case for the super-class that we
							 * have removed.
							 */
							break;
						} else {
							addCandidate = false;
							break;
						}
					}
					/*
					 * The candidate is not contained in the saturation of the
					 * super-class. We check, if conversely, the saturation of
					 * the candidate contains the super-class. In this case the
					 * super-class is not direct and should be removed from the
					 * list.
					 */
					else if (candidateDerived.contains(directSuperClass)) {
						iteratorDirectSuperClasses.remove();
					}
				}
				if (addCandidate) {
					directSuperClasses.add(candidate);
				}
			}
			/* Set the output of the initiator job and prost-process it */
			TransitiveReductionOutputSatisfiable<I> satisfiable = new TransitiveReductionOutputSatisfiable<I>(
					root, equivalent, directSuperClasses);
			J initiatorJob = saturationJob.getInitiatorJob();
			initiatorJob.setOutput(satisfiable);
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace(root + ": transitive reduction finished");
			}
			postProcess(initiatorJob);
		}
	}
}