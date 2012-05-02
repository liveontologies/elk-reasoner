/*
 * #%L
 * ELK OWL API
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
/**
 * @author Yevgeny Kazakov, Jul 1, 2011
 */
package org.semanticweb.elk.owlapi;

import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

/**
 * Wrapper for OWL {@link ReasonerProgressMonitor} for ELK
 * {@link ProgressMonitor} interface.
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ElkReasonerProgressMonitor implements ProgressMonitor {

	private final ReasonerProgressMonitor pm;
	private static final long updateInterval = 10;
	private static final double mimimalIncrement = 0.005;
	private long lastUpdateTime;
	private double lastProgress;

	public ElkReasonerProgressMonitor(ReasonerProgressMonitor pm) {
		this.pm = pm;
	}

	@Override
	public void start(String message) {
		pm.reasonerTaskStarted(message);
		lastProgress = 0;
		lastUpdateTime = System.currentTimeMillis();
	}

	@Override
	public void report(int state, int maxState) {
		long time = System.currentTimeMillis();
		double progress;
		if (maxState == 0)
			progress = 0;
		else
			progress = (double) state / (double) maxState;
		if (time > lastUpdateTime + updateInterval
				&& progress > lastProgress + mimimalIncrement) {
			pm.reasonerTaskProgressChanged(state, maxState);
			lastUpdateTime = time;
			lastProgress = progress;
		}
	}

	@Override
	public void finish() {
		pm.reasonerTaskStopped();
	}

}
