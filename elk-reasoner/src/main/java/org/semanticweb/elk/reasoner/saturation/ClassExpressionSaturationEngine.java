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
package org.semanticweb.elk.reasoner.saturation;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.ReasonerJob;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

/**
 * The engine for computing saturations of class expressions and delegating the
 * results for further post-processing. The jobs initialized with input class
 * expressions are submitted using the method {@link #process(ReasonerJob)}. A
 * hook for post-processing the result when it is ready, is specified by the
 * {@link #processOutput(ReasonerJob)} method which should be implemented
 * accordingly.
 * 
 * The implementation relies heavily on Java's concurrency package and contains
 * a complicated machinery to achieve thread safety and contract guarantees.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 * @param <J>
 *            the type of the saturation jobs to be processed
 */
public class ClassExpressionSaturationEngine<J extends SaturationJob<? extends IndexedClassExpression>>
		implements InputProcessor<J> {

	// Statistical information
	AtomicInteger derivedNo = new AtomicInteger(0);
	AtomicInteger backLinkNo = new AtomicInteger(0);
	AtomicInteger propNo = new AtomicInteger(0);
	AtomicInteger forwLinkNo = new AtomicInteger(0);

	protected final static Logger LOGGER_ = Logger
			.getLogger(ClassExpressionSaturationEngine.class);

	// TODO: try to get rid of the ontology index, if possible
	/**
	 * The index used for executing the rules
	 */
	protected final OntologyIndex ontologyIndex;

	/**
	 * Cached constants
	 */
	protected final IndexedClassExpression owlThing, owlNothing;

	/**
	 * The queue containing all activated contexts. Every activated context
	 * occurs exactly once.
	 */
	protected final Queue<SaturatedClassExpression> activeContexts;

	/**
	 * The buffer for jobs in progress, i.e., for those jobs for which the
	 * method {@link #processOutput(ReasonerJob)} is not yet executed.
	 */
	protected final BlockingQueue<J> buffer;

	/**
	 * The size of the job buffer.
	 */
	final int bufferSize;

	/**
	 * A counter for the number of worker instances processing the active
	 * contexts queue
	 */
	final AtomicInteger activeWorkers = new AtomicInteger(0);

	/**
	 * The counter incremented with every inserted job to the buffer
	 */
	protected final AtomicInteger countJobs = new AtomicInteger(0);

	/**
	 * The counter incremented whenever a job is processed, i.e., the input is
	 * saturated; it should never exceed the counter for the number of the
	 * inserted jobs and should reach that when all computations are over
	 */
	protected final AtomicInteger countProcessedJobs = new AtomicInteger(0);

	/**
	 * The counter incremented whenever the processed job is submitted for
	 * post-processing; it should never exceed the counter for processed jobs
	 * and should reach that in the limit when all computations are over
	 */
	protected final AtomicInteger countOutputJobs = new AtomicInteger(0);

	/**
	 * Indicates that some workers are waiting for new processed jobs
	 */
	protected volatile boolean waitingForNewProcessedJobs = false;

	/**
	 * Indicates that there are new processed jobs that are not yet submitted
	 * for post-processing
	 */
	protected volatile boolean newProcessedJobsAvailable = false;

	/**
	 * Creates a saturation engine using a given ontology index for executing
	 * the rules and the upper limit for the number unprocessed input. The limit
	 * has an effect on the size of batches in which the input is processed and
	 * has an effect on throughput and latency of the processing: in general,
	 * the larger the limit is, the faster it takes to perform the overall
	 * processing of jobs, but it might take longer to process an individual
	 * individual job because the jobs are processed in batches.
	 * 
	 * @param ontologyIndex
	 *            the index used for executing the rules
	 * @param maxUnprocessed
	 *            the maximum number of unprocessed jobs at any given time
	 */
	public ClassExpressionSaturationEngine(OntologyIndex ontologyIndex,
			int maxUnprocessed) {
		this.ontologyIndex = ontologyIndex;
		this.activeContexts = new ConcurrentLinkedQueue<SaturatedClassExpression>();
		this.bufferSize = maxUnprocessed;
		this.buffer = new ArrayBlockingQueue<J>(maxUnprocessed);

		// reset saturation in case of re-saturation after changes
		for (IndexedClassExpression ice : ontologyIndex
				.getIndexedClassExpressions())
			ice.resetSaturated();

		owlThing = ontologyIndex.getIndexed(PredefinedElkClass.OWL_THING);
		owlNothing = ontologyIndex.getIndexed(PredefinedElkClass.OWL_NOTHING);
	}

	/**
	 * Creates a saturation engine using a given ontology index for executing
	 * the rules.
	 * 
	 * @param ontologyIndex
	 *            the index used for executing the rules
	 */
	public ClassExpressionSaturationEngine(OntologyIndex ontologyIndex) {
		this(ontologyIndex, 64);
	}

	/**
	 * Submits a job initialized with an indexed class expression for computing
	 * the saturation. Once the job is processed, a method
	 * {@link #processOutput(ReasonerJob)} will be called to post-process this
	 * job. This method is thread safe and different jobs can be executed
	 * concurrently from different threads, however, it is not safe to submit
	 * the same job object several times. It is not guaranteed that
	 * {@link #processOutput(ReasonerJob)} will be called from the same thread
	 * in which the job was submitted; it can be processed by any of the
	 * concurrently running running workers since the job pool is shared. It is
	 * guaranteed that all submitted jobs will be processed when no instances of
	 * {@link #process(ReasonerJob)} of the same engine object are running.
	 * 
	 * @param job
	 *            the job initialized with the the indexed class expression for
	 *            which the saturation should be computed
	 * @throws InterruptedException
	 *             if interrupted when the thread was idle
	 */
	public void process(J job) throws InterruptedException {
		IndexedClassExpression root = job.getInput();
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace(root + ": saturation started");
		}
		SaturatedClassExpression rootSaturation = root.getSaturated();
		if (rootSaturation != null && rootSaturation.isSaturated()) {
			job.setOutput(rootSaturation);
			processOutput(job);
			activeWorkers.incrementAndGet();
		} else {
			getCreateContext(root);
			/*
			 * invariant: for every buffered job, the context is created
			 * scheduled for processing
			 */
			while (!this.buffer.offer(job)) {
				/* wait until there are new processed jobs */
				if (!newProcessedJobsAvailable) {
					synchronized (buffer) {
						if (!newProcessedJobsAvailable) {
							waitingForNewProcessedJobs = true;
							buffer.wait();
						}
					}
				}
				outputNewProcessedJobs();
			}
			activeWorkers.incrementAndGet();
			/*
			 * it is important to increment the counter for the buffered jobs
			 * only when the number of active workers is positive
			 */
			countJobs.incrementAndGet();
		}
		processActiveContexts();
		if (activeWorkers.decrementAndGet() == 0) {
			int snapshotCountJobs = countJobs.get();
			if (activeWorkers.get() == 0) {
				/*
				 * We increment the counter for the jobs only after a context
				 * for a job was created and it has been added to the buffer,
				 * and only when some worker is active, i.e., the counter for
				 * active workers is greater than zero. At the moment when the
				 * counter for active workers becomes zero, no worker is
				 * processing the active contexts, thus at the time the last
				 * worker finishing processing active contexts, all context
				 * created before become saturated. So at that very time, the
				 * value for the counter for the jobs is equal to the number of
				 * finished jobs. We take a snapshot of the counter between two
				 * such moments, which means that 1) now the value of the
				 * snapshot does not exceed the number of processed jobs and 2)
				 * after every time the last active worker is finished, we will
				 * have a snapshot value (perhaps taken in a different thread)
				 * which will represent at least the number of processed jobs at
				 * that time.
				 * 
				 * Now we update the counter for processed jobs to this
				 * snapshot, provided it is greater. It may happen that the
				 * snapshot value is not greater because several threads can
				 * simultaneously enter this block for different snapshot
				 * values. It is guaranteed, however, that the counter will be
				 * updated to the largest of these values.
				 */
				for (;;) {
					int snapshotCountProcessedJobs = countProcessedJobs.get();
					if (snapshotCountJobs <= snapshotCountProcessedJobs)
						break;
					if (countProcessedJobs.compareAndSet(
							snapshotCountProcessedJobs, snapshotCountJobs)) {
						notifyAboutNewProcessedJobs();
						outputNewProcessedJobs();
						break;
					}
				}
			}
		}
	}

	/**
	 * Notifies, if necessary, that new processed jobs are available for the
	 * output
	 */
	void notifyAboutNewProcessedJobs() {
		newProcessedJobsAvailable = true;
		if (waitingForNewProcessedJobs) {
			synchronized (buffer) {
				waitingForNewProcessedJobs = false;
				buffer.notifyAll();
			}
		}
	}

	/**
	 * Post-processing the newly processed jobs
	 * 
	 * @throws InterruptedException
	 */
	void outputNewProcessedJobs() throws InterruptedException {
		for (;;) {
			int shapshotOutputJobs = countOutputJobs.get();
			if (shapshotOutputJobs == countProcessedJobs.get()) {
				newProcessedJobsAvailable = false;
				/*
				 * before exiting, check to make sure that no new processed jobs
				 * have appeared after the last test and before the variable was
				 * set
				 */
				if (shapshotOutputJobs == countProcessedJobs.get())
					break;
				else
					notifyAboutNewProcessedJobs();
			}
			/*
			 * at this place we know that the number of output jobs is smaller
			 * than the number of processed jobs, if this counter has not been
			 * changed.
			 */
			if (countOutputJobs.compareAndSet(shapshotOutputJobs,
					shapshotOutputJobs + 1)) {
				/*
				 * Since the contexts are created and therefore saturated in the
				 * order they appear in the buffer, it is safe to assume that
				 * the next job in the buffer is processed.
				 */
				J nextJob = buffer.poll();
				SaturatedClassExpression output = nextJob.getInput()
						.getSaturated();
				output.setSaturated();
				nextJob.setOutput(output);
				processOutput(nextJob);
			}
		}
	}

	/**
	 * The hook for post-processing the finished jobs
	 * 
	 * @param job
	 *            the processed job
	 * @throws InterruptedException
	 */
	protected void processOutput(J job) throws InterruptedException {
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace(job.getInput() + ": saturation finished");
		}
	}

	/**
	 * Return a context which has the input indexed class expression as a root.
	 * In case no such context exists, a new one is created with the given root
	 * and is returned. It is ensured that no two different contexts are created
	 * with the same root. In case a new context is created, it is scheduled to
	 * be processed.
	 * 
	 * @param root
	 *            the input indexed class expression for which to return the
	 *            context having it as a root
	 * @return context which root is the input indexed class expression.
	 * 
	 */
	protected SaturatedClassExpression getCreateContext(
			IndexedClassExpression root) {
		if (root.getSaturated() == null) {
			SaturatedClassExpression sce = new SaturatedClassExpression(root);
			if (root.setSaturated(sce)) {
				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace(root + ": context created");
				}
				enqueue(sce, root);

				if (owlThing.occursNegatively())
					enqueue(sce, owlThing);
			}
		}
		return root.getSaturated();
	}

	protected void activateContext(SaturatedClassExpression context) {
		if (context.tryActivate()) {
			activeContexts.add(context);
		}
	}

	protected void deactivateContext(SaturatedClassExpression context) {
		if (context.tryDeactivate())
			if (!context.queue.isEmpty())
				activateContext(context);
	}

	protected void enqueue(Linkable target, Queueable item) {

		/*
		 * so far SaturatedClassExpression is the only implementation of
		 * Linkable
		 */
		if (target instanceof SaturatedClassExpression) {
			SaturatedClassExpression context = (SaturatedClassExpression) target;

			context.queue.add(item);
			activateContext(context);
		}
	}

	protected void processActiveContexts() throws InterruptedException {
		for (;;) {
			SaturatedClassExpression nextContext = activeContexts.poll();
			if (nextContext == null)
				break;
			process(nextContext);
		}
	}

	protected void process(SaturatedClassExpression context) {

		final QueueProcessor queueProcessor = new QueueProcessor(context);

		for (;;) {
			Queueable item = context.queue.poll();
			if (item == null)
				break;
			item.accept(queueProcessor);
		}

		deactivateContext(context);
	}

	private class QueueProcessor implements QueueableVisitor<Void> {
		final SaturatedClassExpression context;

		QueueProcessor(SaturatedClassExpression context) {
			this.context = context;
		}

		public Void visit(BackwardLink backwardLink) {
			IndexedPropertyChain linkRelation = backwardLink.getRelation();
			Linkable target = backwardLink.getTarget();

			if (context.backwardLinksByObjectProperty == null) {
				context.backwardLinksByObjectProperty = new HashSetMultimap<IndexedPropertyChain, Linkable>();

				// start deriving propagations
				for (IndexedClassExpression ice : context.derived)
					if (ice.getNegExistentials() != null)
						for (IndexedObjectSomeValuesFrom e : ice
								.getNegExistentials())
							new Propagation(e.getRelation(),
									new DecomposedClassExpression(e))
									.accept(this);
			}

			if (context.backwardLinksByObjectProperty.add(linkRelation, target)) {
				backLinkNo.incrementAndGet();

				// apply all propagations over the link
				if (context.propagationsByObjectProperty != null) {

					for (IndexedPropertyChain propRelation : new LazySetIntersection<IndexedPropertyChain>(
							linkRelation.getSaturated().getSuperProperties(),
							context.propagationsByObjectProperty.keySet()))

						for (Queueable carry : context.propagationsByObjectProperty
								.get(propRelation))

							enqueue(target, carry);
				}

				/*
				 * if deriveBackwardLinks, then add a forward copy of the link
				 * to consider the link in property compositions
				 */
				if (context.deriveBackwardLinks
						&& linkRelation.getSaturated().propertyCompositionsByLeftSubProperty != null)
					enqueue(target, new ForwardLink(linkRelation, context));

				/* compose the link with all forward links */
				if (linkRelation.getSaturated().propertyCompositionsByRightSubProperty != null
						&& context.forwardLinksByObjectProperty != null) {

					for (IndexedPropertyChain forwardRelation : new LazySetIntersection<IndexedPropertyChain>(
							linkRelation.getSaturated().propertyCompositionsByRightSubProperty
									.keySet(),
							context.forwardLinksByObjectProperty.keySet())) {

						Collection<IndexedBinaryPropertyChain> compositions = linkRelation
								.getSaturated().propertyCompositionsByRightSubProperty
								.get(forwardRelation);
						Collection<Linkable> forwardTargets = context.forwardLinksByObjectProperty
								.get(forwardRelation);

						for (IndexedPropertyChain composition : compositions)
							for (Linkable forwardTarget : forwardTargets)
								enqueue(forwardTarget, new BackwardLink(
										composition, target));
					}
				}

			}

			return null;
		}

		public Void visit(ForwardLink forwardLink) {
			IndexedPropertyChain linkRelation = forwardLink.getRelation();
			Linkable target = forwardLink.getTarget();

			if (context.forwardLinksByObjectProperty == null) {
				context.forwardLinksByObjectProperty = new HashSetMultimap<IndexedPropertyChain, Linkable>();
				initializeDerivationOfBackwardLinks();
			}

			if (context.forwardLinksByObjectProperty.add(linkRelation, target)) {
				forwLinkNo.incrementAndGet();

				/* compose the link with all backward links */
				// assert
				// linkRelation.getSaturated().propertyCompositionsByLeftSubProperty
				// != null
				if (context.backwardLinksByObjectProperty != null) {

					for (IndexedPropertyChain backwardRelation : new LazySetIntersection<IndexedPropertyChain>(
							linkRelation.getSaturated().propertyCompositionsByLeftSubProperty
									.keySet(),
							context.backwardLinksByObjectProperty.keySet())) {

						Collection<IndexedBinaryPropertyChain> compositions = linkRelation
								.getSaturated().propertyCompositionsByLeftSubProperty
								.get(backwardRelation);
						Collection<Linkable> backwardTargets = context.backwardLinksByObjectProperty
								.get(backwardRelation);

						for (IndexedPropertyChain composition : compositions)
							for (Linkable backwardTarget : backwardTargets)
								enqueue(target, new BackwardLink(composition,
										backwardTarget));
					}
				}

			}

			return null;
		}

		public Void visit(Propagation propagation) {
			IndexedPropertyChain propRelation = propagation.getRelation();
			Queueable carry = propagation.getCarry();

			if (context.propagationsByObjectProperty == null) {
				context.propagationsByObjectProperty = new HashSetMultimap<IndexedPropertyChain, Queueable>();
				initializeDerivationOfBackwardLinks();
			}

			if (context.propagationsByObjectProperty.add(propRelation, carry)) {
				propNo.incrementAndGet();

				/* propagate over all backward links */
				if (context.backwardLinksByObjectProperty != null) {

					for (IndexedPropertyChain linkRelation : new LazySetIntersection<IndexedPropertyChain>(
							propRelation.getSaturated().getSubProperties(),
							context.backwardLinksByObjectProperty.keySet()))

						for (Linkable target : context.backwardLinksByObjectProperty
								.get(linkRelation))

							enqueue(target, carry);
				}

			}

			return null;
		}

		public Void visit(DecomposedClassExpression compositeClassExpression) {
			if (context.isSaturated())
				LOGGER_.error(context.root + ": adding "
						+ compositeClassExpression.getClassExpression()
						+ " to a saturated context!");
			if (context.derived.add(compositeClassExpression
					.getClassExpression())) {
				derivedNo.incrementAndGet();
				processClass(compositeClassExpression.getClassExpression());
			}
			return null;
		}

		public Void visit(IndexedClassExpression indexedClassExpression) {
			if (context.isSaturated())
				LOGGER_.error(context.root + ": adding "
						+ indexedClassExpression + " to a saturated context!");
			if (context.derived.add(indexedClassExpression)) {
				derivedNo.incrementAndGet();
				processClass(indexedClassExpression);
				indexedClassExpression.accept(classExpressionDecomposer);
			}
			return null;
		}

		void processClass(IndexedClassExpression ice) {
			// TODO propagate bottom backwards
			if (ice == owlNothing) {
				context.satisfiable = false;
				return;
			}

			/* process subsumptions */
			if (ice.getToldSuperClassExpressions() != null) {
				for (IndexedClassExpression implied : ice
						.getToldSuperClassExpressions())
					enqueue(context, implied);
			}

			/* process negative conjunctions */
			if (ice.getNegConjunctionsByConjunct() != null) {
				for (IndexedClassExpression common : new LazySetIntersection<IndexedClassExpression>(
						ice.getNegConjunctionsByConjunct().keySet(),
						context.derived))
					enqueue(context, new DecomposedClassExpression(ice
							.getNegConjunctionsByConjunct().get(common)));
			}

			/*
			 * process negative existentials only needed when there is at least
			 * one backward link
			 */
			if (context.backwardLinksByObjectProperty != null
					&& ice.getNegExistentials() != null) {
				for (IndexedObjectSomeValuesFrom e : ice.getNegExistentials())
					new Propagation(e.getRelation(),
							new DecomposedClassExpression(e)).accept(this);
			}

		}

		private ClassExpressionDecomposer classExpressionDecomposer = new ClassExpressionDecomposer();

		private class ClassExpressionDecomposer implements
				IndexedClassExpressionVisitor<Void> {

			public Void visit(IndexedClass ice) {
				return null;
			}

			public Void visit(IndexedObjectIntersectionOf ice) {
				enqueue(context, ice.getFirstConjunct());
				enqueue(context, ice.getSecondConjunct());
				return null;
			}

			public Void visit(IndexedObjectSomeValuesFrom ice) {
				enqueue(getCreateContext(ice.getFiller()),
						new BackwardLink(ice.getRelation(), context));
				return null;
			}

			public Void visit(IndexedDataHasValue element) {
				return null;
			}
		}

		/*
		 * adds a forward copy of each backward link effectively allowing these
		 * links to occur as the right components in property compositions
		 */
		void initializeDerivationOfBackwardLinks() {
			if (context.deriveBackwardLinks)
				return;

			context.deriveBackwardLinks = true;

			if (context.backwardLinksByObjectProperty != null)

				for (IndexedPropertyChain linkRelation : context.backwardLinksByObjectProperty
						.keySet())

					if (linkRelation.getSaturated().propertyCompositionsByLeftSubProperty != null)

						for (Linkable target : context.backwardLinksByObjectProperty
								.get(linkRelation))

							enqueue(target, new ForwardLink(linkRelation,
									context));
		}

	}

	// @Override
	// public void waitCompletion() {
	// super.waitCompletion();
	// System.err.println("derived: " + derivedNo);
	// System.err.println("backLnk: " + backLinkNo);
	// System.err.println("  props: " + propNo);
	// System.err.println("forwLnk: " + forwLinkNo);
	// }

}
