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
import org.semanticweb.elk.reasoner.rules.SaturatedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationEngine;
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
 * @param <I>
 *            the type of the input class expressions for which to compute the
 *            result
 */
public class TransitiveReductionEngine<I extends IndexedClassExpression, J extends TransitiveReductionJob<I>>
		extends AbstractJobManager<J> {

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
	protected final Queue<SaturationJobForTransitiveReduction<?, I, J>> jobQueue;

	/** <tt>true</tt> if the {@link #jobQueue} queue is not empty */
	protected final AtomicBoolean jobQueueNotEmpty;

	public TransitiveReductionEngine(OntologyIndex ontologyIndex) {

		this.saturationEngineForTransitiveReduction = new SaturationEngineForTransitiveReduction(
				ontologyIndex);
		this.jobQueue = new ConcurrentLinkedQueue<SaturationJobForTransitiveReduction<?, I, J>>();
		this.jobQueueNotEmpty = new AtomicBoolean(false);
	}

	public final void submit(J job) throws InterruptedException {
		I root = job.getInput();
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace(root + ": transitive reduction started");
		}
		saturationEngineForTransitiveReduction
				.submit(new SaturationJobRoot<I, J>(job));
	}

	public final void process() throws InterruptedException {
		for (;;) {
			saturationEngineForTransitiveReduction.process();
			SaturationJobForTransitiveReduction<?, I, J> nextJob = jobQueue
					.poll();
			if (nextJob == null) {
				if (jobQueueNotEmpty.get()
						&& jobQueueNotEmpty.compareAndSet(true, false)) {
					nextJob = jobQueue.poll();
					if (nextJob == null)
						break;
					else
						tryNotifyCanProcess();
				} else
					break;
			}
			saturationEngineForTransitiveReduction.submit(nextJob);
		}
	}

	@Override
	public boolean canProcess() {
		return !jobQueue.isEmpty()
				|| saturationEngineForTransitiveReduction.canProcess();
	}

	private void tryNotifyCanProcess() {
		if (!jobQueueNotEmpty.get()
				&& jobQueueNotEmpty.compareAndSet(false, true))
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
			super(ontologyIndex);
		}

		@Override
		public void notifyCanProcess() {
			TransitiveReductionEngine.this.notifyCanProcess();
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
		 */
		@Override
		public void notifyProcessed(
				SaturationJobForTransitiveReduction<?, I, J> output)
				throws InterruptedException {
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace(output.getInitiatorJob().getInput()
						+ ": saturated for transitive reduction");
			}
			super.notifyProcessed(output);
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
				notifyProcessed(initiatorJob);
				return;
			}
			/*
			 * Otherwise, to perform the transitive reduction, we need to
			 * compute the saturation for every derived super-class of the
			 * saturation. We create a special instance of the saturation job
			 * for this purpose and add it to the job queue.
			 */
			jobQueue.add(new SaturationJobSuperClasses<I, J>(initiatorJob));
			tryNotifyCanProcess();
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
			 * Initializing the output for the root.
			 */
			final TransitiveReductionOutputEquivalentDirect<I> output = new TransitiveReductionOutputEquivalentDirect<I>(
					root);
			for (IndexedClass candidate : saturationJob) {
				if (candidate == root) {
					output.equivalent.add(candidate.getElkClass());
					continue;
				}
				Set<IndexedClassExpression> candidateDerived = candidate
						.getSaturated().getSuperClassExpressions();
				/*
				 * If the saturation for the candidate contains the root, it is
				 * equivalent to the root
				 */
				if (candidateDerived.contains(root)) {
					output.equivalent.add(candidate.getElkClass());
					continue;
				}
				/*
				 * To check if the candidate should be added to the list of
				 * direct super-classes, we iterate over the direct super
				 * classes computed so far.
				 */
				Iterator<TransitiveReductionOutputEquivalent<IndexedClass>> iteratorDirectSuperClasses = output.directSuperClasses
						.iterator();
				/*
				 * If the value of this flag is true after the iteration, the
				 * candidate should be added to the list
				 */
				boolean addCandidate = true;
				while (iteratorDirectSuperClasses.hasNext()) {
					TransitiveReductionOutputEquivalent<IndexedClass> directSuperClassEquivalent = iteratorDirectSuperClasses
							.next();
					IndexedClass directSuperClass = directSuperClassEquivalent
							.getRoot();
					/*
					 * If the (already computed) saturation for direct
					 * super-class contains the candidate, it cannot be direct.
					 */
					if (directSuperClass.getSaturated()
							.getSuperClassExpressions().contains(candidate)) {
						addCandidate = false;
						/*
						 * If, in addition, the saturation for the candidate
						 * contains the direct super class, they are equivalent,
						 * so it is added to the equivalence class.
						 */
						if (candidateDerived.contains(directSuperClass))
							directSuperClassEquivalent.equivalent.add(candidate
									.getElkClass());
						break;
					}
					/*
					 * At this point we know that the candidate is not contained
					 * in the saturation of the super-class. We check, if
					 * conversely, the saturation of the candidate contains the
					 * super-class. In this case the super-class is not direct
					 * and should be removed from the list.
					 */
					if (candidateDerived.contains(directSuperClass)) {
						iteratorDirectSuperClasses.remove();
					}
				}
				if (addCandidate) {
					TransitiveReductionOutputEquivalent<IndexedClass> candidateOutput = new TransitiveReductionOutputEquivalent<IndexedClass>(
							candidate);
					candidateOutput.equivalent.add(candidate.getElkClass());
					output.directSuperClasses.add(candidateOutput);
				}
			}
			/* Set the output of the initiator job and prost-process it */
			J initiatorJob = saturationJob.getInitiatorJob();
			initiatorJob.setOutput(output);
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace(root + ": transitive reduction finished");
				for (TransitiveReductionOutputEquivalent<IndexedClass> direct : output.directSuperClasses) {
					LOGGER_.trace(root + ": direct super class "
							+ direct.getRoot());
				}
			}
			notifyProcessed(initiatorJob);
		}
	}

}