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

import java.util.Collection;

import org.semanticweb.elk.util.logging.ElkTimer;
import org.semanticweb.elk.util.logging.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Runs all sub-tasks before repeating.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class RunAllOnceThenRepeatRunner {

	private static final Logger LOGGER_ = LoggerFactory.getLogger(RunAllOnceThenRepeatRunner.class);
	
	protected final int warmups;
	protected final int runs;
	
	protected RunAllOnceThenRepeatRunner(int warmups, int runs) {
		this.warmups = warmups;
		this.runs = runs;
	}
	
	private void runOnce(Collection<Task> tasks, Metrics aggregateMetrics) throws TaskException {
		int cnt = 0;
		
		for (Task task : tasks) {
			//FIXME
			if (cnt >= 200) break;
			
			cnt++;
			
			ElkTimer timer = ElkTimer.getNamedTimer(task.getName());
			
			System.err.println("Running " + task.getName() + ", " + cnt + "/" + tasks.size());
			
			task.prepare();
			
			timer.start();
			task.run();
			timer.stop();
			
			task.postRun();
			
			if (task.getMetrics() != null && aggregateMetrics != null) {
				aggregateMetrics.add(task.getMetrics());
			}
		}
	}
	
	public void run(TaskCollection collection) throws TaskException {
		Collection<Task> tasks = collection.getTasks();
		
		for (int i = 0; i < warmups; i++) {
			System.out.println("Warm-up run #" + i);
			
			runOnce(tasks, null);
		}
		
		for (ElkTimer timer : ElkTimer.getNamedTimers()) {
			collection.getMetrics().reset();
			timer.reset();
		}
		
		for (int i = 0; i < runs; i++) {
			System.out.println("Actual run #" + i);
			
			runOnce(tasks, collection.getMetrics());
		}
		
		logStats(collection);		
		collection.dispose();
	}
	
	public void logStats(TaskCollection collection) {
		for (ElkTimer timer : ElkTimer.getNamedTimers()) {
			timer.log(LOGGER_, LogLevel.INFO);
		}
				
		if (collection.getMetrics() != null) {
			//System.err.println(collection.getMetrics());
			collection.getMetrics().printAverages(LOGGER_, LogLevel.WARN);
		}
	}
}
