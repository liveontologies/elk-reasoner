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

	public void start(String message) {
		pm.reasonerTaskStarted(message);
		lastProgress = 0;
		lastUpdateTime = System.currentTimeMillis();
	}

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

	public void finish() {
		pm.reasonerTaskStopped();
	}

}
