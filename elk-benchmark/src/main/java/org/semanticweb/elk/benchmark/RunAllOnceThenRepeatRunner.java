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

import org.semanticweb.elk.MutableInteger;
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
	
	private void runOnce(final TaskCollection2 collection, final Metrics aggregateMetrics) throws TaskException {
		final MutableInteger cnt = new MutableInteger(0);
		
		collection.visitTasks(new TaskVisitor() {
			
			@Override
			public void visit(Task task) throws TaskException {
				cnt.increment();
				//FIXME
				/*if (cnt.get() > 10000) {
					logStats(collection);
					throw new RuntimeException("enough");
				}*/
				
				ElkTimer timer = ElkTimer.getNamedTimer(task.getName());
				
				//if (cnt.get() % 10000 == 0) {
				System.err.println("Running " + task.getName() + ", " + cnt);
				//}
				
				task.prepare();
				
				timer.start();
				task.run();
				timer.stop();
				
				task.postRun();
				
				if (task.getMetrics() != null && aggregateMetrics != null) {
					//task.getMetrics().printAverages(LOGGER_, LogLevel.WARN);
					task.getMetrics().updateLongMetric(task.getName() + " time", timer.getAvgWallTime());
					aggregateMetrics.add(task.getMetrics());
				}
			}
		});
	}
	
	public void run(TaskCollection2 collection) throws TaskException {
		
		for (int i = 0; i < warmups; i++) {
			System.out.println("Warm-up run #" + i);
			
			runOnce(collection, null);
		}
		
		for (ElkTimer timer : ElkTimer.getNamedTimers()) {
			collection.getMetrics().reset();
			timer.reset();
		}
		
		for (int i = 0; i < runs; i++) {
			System.out.println("Actual run #" + i);
			
			runOnce(collection, collection.getMetrics());
		}
		
		logStats(collection);		
		collection.dispose();
	}
	
	public void logStats(TaskCollection2 collection) {
		for (ElkTimer timer : ElkTimer.getNamedTimers()) {
			timer.log(LOGGER_, LogLevel.ERROR);
		}
				
		if (collection.getMetrics() != null) {
			//System.err.println(collection.getMetrics());
			collection.getMetrics().printAverages(LOGGER_, LogLevel.ERROR);
		}
	}
}
