/**
 * 
 */
package org.semanticweb.elk.benchmark;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface MultiTask extends Task {

	public Iterable<Task> getSubTasks() throws TaskException;
}
