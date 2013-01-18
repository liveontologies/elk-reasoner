/**
 * 
 */
package org.semanticweb.elk.benchmark;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.elk.util.logging.ElkTimer;


/**
 * Runs all sub-tasks before repeating.
 * 
 * TODO Invent a meaningful name!
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TaskCollectionRunner2 {

	private static final Logger LOGGER_ = Logger.getLogger(TaskCollectionRunner2.class);
	
	protected final int warmups;
	protected final int runs;
	
	protected TaskCollectionRunner2(int warmups, int runs) {
		this.warmups = warmups;
		this.runs = runs;
	}
	
	private void runOnce(TaskCollection collection) throws TaskException {
		for (Task task : collection.getTasks()) {
			ElkTimer timer = ElkTimer.getNamedTimer(task.getName());
			
			System.err.println("Running " + task.getName());
			
			task.prepare();
			timer.start();
			task.run();
			timer.stop();
		}
	}
	
	public void run(TaskCollection collection) throws TaskException {
		for (int i = 0; i < warmups; i++) {
			System.out.println("Warm-up run #" + i);
			
			runOnce(collection);
		}
		
		for (ElkTimer timer : ElkTimer.getNamedTimers()) {
			timer.reset();
		}
		
		for (int i = 0; i < runs; i++) {
			System.out.println("Actual run #" + i);
			
			runOnce(collection);
		}
		
		for (ElkTimer timer : ElkTimer.getNamedTimers()) {
			timer.log(LOGGER_, Level.INFO);
		}
		
		collection.dispose();
		
		System.err.println(collection.getMetrics());
	}	
}
