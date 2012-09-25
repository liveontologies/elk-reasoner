/**
 * 
 */
package org.semanticweb.elk.benchmark;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.elk.util.logging.ElkTimer;

/**
 * The only reason this class exists is because sometimes it's convenient to
 * finish w/ one task before preparing the next one.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class MultiTaskRunner extends TaskRunner {

	private static final Logger LOGGER_ = Logger.getLogger(MultiTask.class);	
	
	protected MultiTaskRunner(MultiTask task, int warmups, int runs) {
		super(task, warmups, runs);
	}

	private MultiTask getTask() {
		return (MultiTask) task;
	}

	@Override
	public void run() throws TaskException {
		for (Task nextTask : getTask().getSubTasks()) {
			run(nextTask);
		}
		
		for (Task nextTask : getTask().getSubTasks()) {
			ElkTimer.getNamedTimer(nextTask.getName()).log(LOGGER_, Level.INFO);
		}
	}
}
