package org.semanticweb.elk.util.concurrent.computation;

/**
 * An abstract interface for interrupting computations and monitoring if
 * computations have been interrupted.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface Interrupter {

	/**
	 * Requests the computation to be interrupted
	 */
	public void interrupt();

	/**
	 * Checks if the computation was requested to be interrupted
	 */
	public boolean isInterrupted();

}
