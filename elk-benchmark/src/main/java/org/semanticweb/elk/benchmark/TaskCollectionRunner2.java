/**
 * 
 */
package org.semanticweb.elk.benchmark;
/*
 * #%L
 * ELK Benchmarking Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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
			collection.getMetrics().reset();
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
		
		if (collection.getMetrics() != null) {
			System.err.println(collection.getMetrics());
			collection.getMetrics().printAverages(LOGGER_, Level.WARN);
		}
	}	
}
