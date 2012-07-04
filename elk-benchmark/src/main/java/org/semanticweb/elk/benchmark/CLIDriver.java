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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A simple command line tool which instantiates a task and passes it to the
 * task runner.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class CLIDriver {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// First, need to instantiate the task 
		beforeRun(args);

		Task task = instantiateTask(args[0]);
		TaskRunner runner = new TaskRunner(task, Integer.valueOf(args[1]),
				Integer.valueOf(args[2]));
		String[] runnerArgs = new String[] {};

		if (args.length > 3) {
			runnerArgs = new String[args.length - 3];

			System.arraycopy(args, 3, runnerArgs, 0, args.length - 3);
		}

		runner.run(runnerArgs);
	}

	private static Task instantiateTask(String className)
			throws ClassNotFoundException, SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		Class<?> clazz = Class.forName(className);
		Constructor<?> constructor = clazz.getConstructor(new Class<?>[] {});

		return (Task) constructor.newInstance(new Object[] {});
	}

	private static void beforeRun(String[] args) {
		if (args.length < 3) {
			System.out
					.println("Usage: CLIDriver <task-runner-class-name> <number of warm-ups> <number of runs> <parameters to pass to the runner>");

			System.exit(0);
		}
	}
}
