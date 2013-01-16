/*
 * #%L
 * ELK Bencharking Package
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
 * 
 */
package org.semanticweb.elk.benchmark;

import org.semanticweb.elk.util.logging.ElkTimer;



/**
 * A very simple task runner
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TaskRunner {

	protected final int warmups;
	protected final int runs;
	
	protected TaskRunner(int warmups, int runs) {
		this.warmups = warmups;
		this.runs = runs;
	}
	
	public void run(Task task) throws TaskException {
		ElkTimer timer = ElkTimer.getNamedTimer(task.getName());
		
		for (int i = 0; i < warmups; i++) {
			System.out.println("Warm-up run #" + i);
			task.prepare();
			task.run();
		}
		
		long wallTimeElapsed = 0;
		
		for (int i = 0; i < runs; i++) {
			System.out.print("Actual run #" + i + "... ");
			task.prepare();
			
			timer.start();
			task.run();			
			timer.stop();
			
			long wallRuntime = (timer.getTotalWallTime() - wallTimeElapsed)/1000000;
			
			System.out.println("... finished in " + wallRuntime  + " ms");
			
			wallTimeElapsed = timer.getTotalWallTime();
		}
		
		task.dispose();
		
		System.out.println("Average running time: " + timer.getAvgWallTime() / 1000000 + " ms");
	}
}
