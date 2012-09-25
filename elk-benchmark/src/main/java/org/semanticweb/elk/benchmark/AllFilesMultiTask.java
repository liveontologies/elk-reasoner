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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class AllFilesMultiTask implements MultiTask {

	private final String[] args_;
	
	public AllFilesMultiTask(String[] args) {
		args_ = args;
	}
	
	@Override
	public abstract String getName();

	@Override
	public void prepare() throws TaskException {
		//TODO prepare all subtasks
	}

	@Override
	public Result run() throws TaskException {
		// TODO run all sub tasks
		return null;
	}

	@Override
	public Iterable<Task> getSubTasks() throws TaskException {
		File dir = BenchmarkUtils.getFile(args_[0]);
		Collection<Task> tasks = new ArrayList<Task>();
		String[] taskArgs = new String[args_.length];
		
		System.arraycopy(args_, 0, taskArgs, 0, args_.length);
		
		for (File file : dir.listFiles()) {
			
			try {
				taskArgs[0] = file.getCanonicalPath();
			} catch (IOException e) {
				throw new TaskException(e);
			}
			
			tasks.add(instantiateSubTask(taskArgs));
		}
		
		return tasks;
	}

	public abstract Task instantiateSubTask(String[] args);
}
