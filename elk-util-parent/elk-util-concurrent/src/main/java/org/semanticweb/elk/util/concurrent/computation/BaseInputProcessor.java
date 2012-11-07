/**
 * 
 */
package org.semanticweb.elk.util.concurrent.computation;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Implements basic job queueing and lets subclasses focus
 * on processing single jobs
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class BaseInputProcessor<J> implements InputProcessor<J> {

	/**
	 * The buffer for jobs that need to be processed, i.e., those for which the
	 * method {@link submit(J)} was executed but processing
	 * of jobs has not been started yet.
	 */
	private final Queue<J> jobsToDo_ = new ConcurrentLinkedQueue<J>();
	
	private final InputProcessorListenerNotifyFinishedJob<J> listener_;
	
	public BaseInputProcessor() {
		this(null);
	}
	
	public BaseInputProcessor(final InputProcessorListenerNotifyFinishedJob<J> listener) {
		listener_ = listener;
	}
	
	@Override
	public void submit(J job) {
		jobsToDo_.add(job);
	}

	@Override
	public void process() throws InterruptedException {
		for (;;) {
			if (Thread.currentThread().isInterrupted()) {
				break;
			}
			
			J nextJob = jobsToDo_.poll();
			
			if (nextJob == null) {
				break;
			}
			
			process(nextJob);
			
			if (listener_ != null) {
				listener_.notifyFinished(nextJob);
			}
		}
	}

	@Override
	public void finish() {}

	protected abstract void process(J job);
}