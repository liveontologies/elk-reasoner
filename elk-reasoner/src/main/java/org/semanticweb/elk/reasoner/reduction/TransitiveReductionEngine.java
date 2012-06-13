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

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationEngine;
import org.semanticweb.elk.reasoner.saturation.classes.RuleStatistics;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

/**
 * The engine for computing equivalent classes and direct super classes of the
 * given indexed class expression, represented by the
 * {@link TransitiveReductionOutput} object. The jobs are submitted using the
 * method {@link #submit(IndexedClassExpression)}, and all currently submitted
 * jobs are processed using the {@link #process()} method. To every transitive
 * reduction engine it is possible to attach a
 * {@link TransitiveReductionListener}, which can implement hook methods that
 * perform certain actions during the processing, e.g., notifying when the jobs
 * are finished.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <R>
 *            the type of the input class expressions for which to compute the
 *            result
 * @param <J>
 *            the type of the jobs that can be processed by this transitive
 *            reduction engine
 */
public class TransitiveReductionEngine<R extends IndexedClassExpression, J extends TransitiveReductionJob<R>>
		implements InputProcessor<J> {

	// logger for events
	protected final static Logger LOGGER_ = Logger
			.getLogger(TransitiveReductionEngine.class);

	protected final TransitiveReductionShared<R, J> shared;

	/**
	 * The saturation engine for transitive reduction that can only process
	 * instances of {@link SaturationJobForTransitiveReduction}. There are two
	 * types of the jobs. The instances of {@link SaturationJobRoot} are
	 * saturation jobs for the indexed class expression, for which a transitive
	 * reduction is required to be computed. The transitive reduction is
	 * computed by iterating over the derived super classes and computing
	 * saturation for them in order to filter out non-direct super classes. For
	 * this purpose, the second kind of jobs, which are instances of
	 * {@link SaturationJobSuperClass} are used.
	 */
	protected final ClassExpressionSaturationEngine<SaturationJobForTransitiveReduction<R, ?, J>> saturationEngine;

	public TransitiveReductionEngine(TransitiveReductionShared<R, J> shared,
			RuleStatistics statistics) {
		this.shared = shared;
		this.saturationEngine = new ClassExpressionSaturationEngine<SaturationJobForTransitiveReduction<R, ?, J>>(
				shared.saturationShared, statistics);
	}

	@Override
	public final void submit(J job) {
		R root = job.getInput();
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace(root + ": transitive reduction started");
		}
		saturationEngine.submit(new SaturationJobRoot<R, J>(job));
	}

	@Override
	public final void process() throws InterruptedException {
		for (;;) {
			if (Thread.currentThread().isInterrupted())
				return;
			saturationEngine.process();
			SaturationJobForTransitiveReduction<R, ?, J> nextJob = shared.auxJobQueue
					.poll();
			if (nextJob == null) {
				if (!shared.jobQueueEmpty.compareAndSet(false, true))
					break;
				nextJob = shared.auxJobQueue.poll();
				if (nextJob == null)
					break;
				shared.tryNotifyCanProcess();
			}
			saturationEngine.submit(nextJob);
		}
	}

	@Override
	public boolean canProcess() {
		return !shared.auxJobQueue.isEmpty() || saturationEngine.canProcess();
	}

	@Override
	public void finish() {
		saturationEngine.finish();
	}

}