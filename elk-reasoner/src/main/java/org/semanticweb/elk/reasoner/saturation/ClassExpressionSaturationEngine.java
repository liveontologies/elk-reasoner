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

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.classes.ContextClassSaturation;
import org.semanticweb.elk.reasoner.saturation.classes.RuleStatistics;
import org.semanticweb.elk.reasoner.saturation.rulesystem.Context;
import org.semanticweb.elk.reasoner.saturation.rulesystem.RuleApplicationEngine;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

/**
 * The engine for submitting, processing, and post-processing of saturation
 * jobs. Each saturation job requires to compute implied super-classes of a
 * given indexed class expression. The jobs are submitted using the
 * {@link #submit(SaturationJob)} method, and all currently submitted jobs are
 * processed using the {@link #process()} method. To every saturation engine it
 * is possible to attach a {@link ClassExpressionSaturationListener}, which can
 * implement hook methods that perform certain actions during the processing,
 * e.g., notifying when the jobs are finished.
 * 
 * The implementation relies heavily on the Java's concurrency package and uses
 * several atomic integer to monitor the progress of the computation.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 * @param <J>
 *            the type of the saturation jobs that can be processed by this
 *            saturation engine
 */
public class ClassExpressionSaturationEngine<J extends SaturationJob<? extends IndexedClassExpression>>
		implements InputProcessor<J> {

	protected final static Logger LOGGER_ = Logger
			.getLogger(ClassExpressionSaturationEngine.class);

	protected ClassExpressionSaturationShared<J> shared;

	protected RuleApplicationEngine ruleApplicationEngine;

	public ClassExpressionSaturationEngine(
			ClassExpressionSaturationShared<J> shared, RuleStatistics statistics) {
		this.shared = shared;
		this.ruleApplicationEngine = new RuleApplicationEngine(
				shared.ruleApplicationShared, statistics);
	}

	@Override
	public void submit(J job) {
		shared.jobsToDo.add(job);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.util.concurrent.computation.InputProcessor#process()
	 */
	@Override
	public void process() throws InterruptedException {
		/*
		 * if the number of unprocessed contexts exceeds the threshold, suspend
		 * the computation; whenever workers wake up, try to process the jobs
		 */
		for (;;) {
			if (Thread.currentThread().isInterrupted())
				return;
			/*
			 * try to process the jobs in progress; there are counters to keep
			 * track of how many workers have been started and finished: if
			 * their values coincide, then there is no worker in the block
			 * between them. In addition, there is a variable to save the
			 * identifier of the last started worker which has been interrupted;
			 * this is used to check if the computation has been finished or
			 * not.
			 */
			int snapshotFinishedWorkers;
			shared.startedWorkers.incrementAndGet();
			ruleApplicationEngine.process();
			if (Thread.currentThread().isInterrupted())
				ClassExpressionSaturationShared.updateIfSmaller(
						shared.lastInterruptedWorker,
						shared.startedWorkers.get());
			snapshotFinishedWorkers = shared.finishedWorkers.incrementAndGet();
			shared.updateProcessedCounters(snapshotFinishedWorkers);
			shared.processFinishedJobs();
			if (shared.ruleApplicationShared.getContextNumber()
					- shared.countContextsProcessed.get() > shared.threshold) {
				synchronized (shared.countContextsProcessed) {
					if (canProcess())
						continue;
					shared.workersWaiting = true;
					shared.countContextsProcessed.wait();
					continue;
				}
			}
			J nextJob = shared.jobsToDo.poll();
			if (nextJob == null)
				return;
			IndexedClassExpression root = nextJob.getInput();
			/*
			 * if saturation is already assigned, this job is already started or
			 * finished
			 */
			Context rootContext = root.getContext();
			if (rootContext != null
					&& ((ContextClassSaturation) rootContext).isSaturated()) {
				nextJob.setOutput(rootContext);
				shared.listener.notifyFinished(nextJob);
				continue;
			}
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace(root + ": saturation started");
			}
			/*
			 * submit the job to the rule engine and start processing it
			 */
			shared.startedWorkers.incrementAndGet();
			shared.jobsInProgress.add(nextJob);
			shared.countJobsSubmitted.incrementAndGet();
			ruleApplicationEngine.submit(root);
			ruleApplicationEngine.process();
			if (Thread.currentThread().isInterrupted())
				ClassExpressionSaturationShared.updateIfSmaller(
						shared.lastInterruptedWorker,
						shared.startedWorkers.get());
			snapshotFinishedWorkers = shared.finishedWorkers.incrementAndGet();
			shared.updateProcessedCounters(snapshotFinishedWorkers);
			shared.processFinishedJobs();
		}

	}

	@Override
	public boolean canProcess() {
		return ruleApplicationEngine.canProcess()
				|| shared.countJobsFinished.get() > shared.countJobsProcessed
						.get();
	}

	@Override
	public void finish() {
		ruleApplicationEngine.finish();
	}

}
