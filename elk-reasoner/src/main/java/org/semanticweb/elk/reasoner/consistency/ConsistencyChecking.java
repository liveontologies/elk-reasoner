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
package org.semanticweb.elk.reasoner.consistency;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputation;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassEntity;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationFactory;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationJob;
import org.semanticweb.elk.reasoner.saturation.classes.ContextClassSaturation;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;

/**
 * A {@link ReasonerComputation} for checking consistency of the ontology. This
 * is done by checking consistency of <tt>owl:Thing</tt> and of all individuals
 * occurring in the ontology.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class ConsistencyChecking
		extends
		ReasonerComputation<SaturationJob<IndexedClassEntity>, ClassExpressionSaturationFactory<SaturationJob<IndexedClassEntity>>.Engine, ClassExpressionSaturationFactory<SaturationJob<IndexedClassEntity>>> {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(ConsistencyChecking.class);

	/**
	 * The object for setting and monitoring the consistency status of the
	 * ontology; Once ontology becomes inconsistent, the computation can be
	 * interrupted.
	 */
	private final ConsistencyMonitor consistencyMonitor;

	/**
	 * Auxiliary class constructor
	 * 
	 * @param inputJobs
	 *            the saturation jobs to be executed
	 * @param consistencyMonitor
	 *            the monitor for the consistency status
	 * @param saturationFactory
	 *            the factory for computing the saturation
	 * @param executor
	 *            the executor service used for running the tasks by the
	 *            reasoner
	 * @param maxWorkers
	 *            the maximum number of workers that can be used
	 * @param progressMonitor
	 *            the monitor for reporting the progress of the computation
	 */
	ConsistencyChecking(
			Collection<SaturationJob<IndexedClassEntity>> inputJobs,
			ConsistencyMonitor consistencyMonitor,
			ClassExpressionSaturationFactory<SaturationJob<IndexedClassEntity>> saturationFactory,
			ComputationExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor) {
		super(inputJobs, saturationFactory, executor, maxWorkers,
				progressMonitor);
		this.consistencyMonitor = consistencyMonitor;
	}

	/**
	 * Constructing the object for checking if all given entities are
	 * consistent.
	 * 
	 * @param inputEntities
	 *            the entities to check for consistency
	 * @param consistencyMonitor
	 *            the monitor for the consistency status
	 * @param ontologyIndex
	 *            the indexed representation of the ontology
	 * @param executor
	 *            the executor service used for running the tasks by the
	 *            reasoner
	 * @param maxWorkers
	 *            the maximum number of workers that can be used
	 * @param progressMonitor
	 *            the monitor for reporting the progress of the computation
	 */
	public ConsistencyChecking(Collection<IndexedClassEntity> inputEntities,
			ConsistencyMonitor consistencyMonitor, OntologyIndex ontologyIndex,
			ComputationExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor) {
		this(
				new TodoJobs(inputEntities, consistencyMonitor),
				consistencyMonitor,
				new ClassExpressionSaturationFactory<SaturationJob<IndexedClassEntity>>(
						ontologyIndex, maxWorkers,
						new ThisClassExpressionSaturationListener(
								consistencyMonitor)), executor, maxWorkers,
				progressMonitor);
	}

	/**
	 * @param ontologyIndex
	 *            the representation of the ontology
	 * 
	 * @return the entities such that the ontology is consistent if and only if
	 *         all of these entities are consistent
	 */
	public static Collection<IndexedClassEntity> getTestEntities(
			final OntologyIndex ontologyIndex) {
		if (!ontologyIndex.getIndexedOwlNothing().occursPositively()) {
			/*
			 * if the ontology does not have any positive occurrence of bottom,
			 * everything is always consistent
			 */
			return Collections.emptySet();
		} else {
			/*
			 * first consistency is checked for {@code owl:Thing}, then for the
			 * individuals in the ontology
			 */
			return new AbstractCollection<IndexedClassEntity>() {

				@Override
				public Iterator<IndexedClassEntity> iterator() {
					return Operations.concat(
							Operations.singleton(ontologyIndex
									.getIndexedOwlThing()),
							ontologyIndex.getIndexedIndividuals()).iterator();
				}

				@Override
				public int size() {
					return ontologyIndex.getIndexedIndividuals().size() + 1;
				}
			};
		}
	}

	/**
	 * Constructing the object for checking if the given ontology is consistent
	 * 
	 * @param executor
	 *            the executor service used for running the tasks by the
	 *            reasoner
	 * @param maxWorkers
	 *            the maximum number of workers that can be used
	 * @param progressMonitor
	 *            the monitor for reporting the progress of the computation
	 * @param ontologyIndex
	 *            the indexed representation of the ontology
	 */
	public ConsistencyChecking(ComputationExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor, OntologyIndex ontologyIndex) {
		this(getTestEntities(ontologyIndex), new ConsistencyMonitor(),
				ontologyIndex, executor, maxWorkers, progressMonitor);
	}

	@Override
	public void process() {
		consistencyMonitor.registerCurrentThreadToInterrupt();
		super.process();
		consistencyMonitor.clearThreadToInterrupt();
	}

	/**
	 * @return <tt>true</tt> if the ontology is consistent; should be called
	 *         after the consistency checking is performed using the method
	 *         {@link #process()}
	 */
	public boolean isConsistent() {
		return !consistencyMonitor.isInconsistent();
	}

	/**
	 * Print statistics about consistency checking
	 */
	public void printStatistics() {
		inputProcessorFactory.printStatistics();
	}

	/**
	 * The listener class used for the class expression saturation engine, which
	 * is used within this consistency engine
	 * 
	 */
	private static class ThisClassExpressionSaturationListener
			implements
			ClassExpressionSaturationListener<SaturationJob<IndexedClassEntity>, ClassExpressionSaturationFactory<SaturationJob<IndexedClassEntity>>.Engine> {

		private final ConsistencyMonitor consistenceMonitor;

		ThisClassExpressionSaturationListener(
				ConsistencyMonitor consistenceMonitor) {
			this.consistenceMonitor = consistenceMonitor;
		}

		@Override
		public void notifyCanProcess() {
		}

		@Override
		public void notifyFinished(SaturationJob<IndexedClassEntity> job) {
			if (!((ContextClassSaturation) job.getOutput()).isSatisfiable())
				consistenceMonitor.setInconsistent();
			if (LOGGER_.isTraceEnabled())
				LOGGER_.trace(job.getInput()
						+ ": consistency checking finished: "
						+ (((ContextClassSaturation) job.getOutput())
								.isSatisfiable() ? "satisfiable"
								: "unsatisfiable"));
		}

	}

	/**
	 * A simple monitor to set and monitor inconsistency status; it should be
	 * thread safe; by default the monitor is not inconsistent
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	static class ConsistencyMonitor {
		private volatile boolean inconsistent = false;
		private volatile Thread controlThread;

		public void registerThreadToInterrupt(Thread controlThread) {
			this.controlThread = controlThread;
		}

		public void registerCurrentThreadToInterrupt() {
			registerThreadToInterrupt(Thread.currentThread());
		}

		public void clearThreadToInterrupt() {
			this.controlThread = null;
		}

		public boolean isInconsistent() {
			return inconsistent;
		}

		public void setInconsistent() {
			inconsistent = true;
			// interrupt the reasoner
			if (controlThread != null)
				controlThread.interrupt();
		}

	}

	/**
	 * Dynamic collection view for consistency checking jobs that correspond to
	 * the given input of entities. If ontology becomes inconsistent as reported
	 * by the provided consistency monitor, collection becomes empty.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	static class TodoJobs extends
			AbstractCollection<SaturationJob<IndexedClassEntity>> implements
			Collection<SaturationJob<IndexedClassEntity>> {

		private final Collection<IndexedClassEntity> inputs;
		private final ConsistencyMonitor consistencyMonitor;

		TodoJobs(Collection<IndexedClassEntity> inputs,
				ConsistencyMonitor consistenceMonitor) {
			this.inputs = inputs;
			this.consistencyMonitor = consistenceMonitor;
		}

		@Override
		public int size() {
			return inputs.size();
		}

		@Override
		public Iterator<SaturationJob<IndexedClassEntity>> iterator() {
			return new Iterator<SaturationJob<IndexedClassEntity>>() {

				final Iterator<IndexedClassEntity> inputsIterator = inputs
						.iterator();

				@Override
				public boolean hasNext() {
					if (consistencyMonitor.isInconsistent())
						return false;
					else
						return inputsIterator.hasNext();
				}

				@Override
				public SaturationJob<IndexedClassEntity> next() {
					if (consistencyMonitor.isInconsistent())
						throw new NoSuchElementException();
					else {
						SaturationJob<IndexedClassEntity> job = new SaturationJob<IndexedClassEntity>(
								inputsIterator.next());
						if (LOGGER_.isTraceEnabled())
							LOGGER_.trace(job.getInput()
									+ ": consistency checking submitted");
						return job;
					}
				}

				@Override
				public void remove() {
					inputsIterator.remove();
				}

			};

		}
	}

}