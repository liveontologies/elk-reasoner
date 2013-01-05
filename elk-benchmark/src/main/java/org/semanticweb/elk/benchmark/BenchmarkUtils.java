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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;

/**
 * A bunch of utility methods for benchmarking, e.g., instantiating tasks, etc.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class BenchmarkUtils {

	/*
	 * Checks that all System properties are set. We use System properties
	 * (other than cmd args) because they're easier to pass parameters into
	 * maven tests (via Surefire or Failsafe plugins)
	 */
	public static Map<String, String> getSystemProperties(String... names) {
		Map<String, String> propMap = new HashMap<String, String>();

		for (String name : names) {
			checkIfSet(name, propMap);
		}

		return propMap;
	}

	public static String[] getCommaSeparatedParameter(String propName) {
		String propValue = System.getProperty(propName);

		if (propValue != null) {
			return propValue.split(",");
		} else {
			return new String[] {};
		}
	}

	public static void checkIfSet(String propName, Map<String, String> propMap) {
		String propValue = System.getProperty(propName);

		if (propValue == null) {
			System.err.println(propName + " property is not set, re-run with "
					+ "-D" + propName + "=<value>?");
		} else {
			propMap.put(propName, propValue);
		}
	}

	/*
	 * A macro method, reads system properties, instantiates the task and runs
	 * it
	 */
	public static void runTask() throws Exception {
		Map<String, String> propMap = BenchmarkUtils
				.getSystemProperties(new String[] { Constants.TASK_CLASS_NAME,
						Constants.WARM_UPS, Constants.RUNS,
						Constants.TASK_PARAMS });
		// First, need to instantiate the task
		Task task = TaskFactory.createTask(propMap
				.get(Constants.TASK_CLASS_NAME), BenchmarkUtils
				.getCommaSeparatedParameter(Constants.TASK_PARAMS));
		TaskRunner runner = new TaskRunner(Integer.valueOf(propMap
				.get(Constants.WARM_UPS)), Integer.valueOf(propMap
				.get(Constants.RUNS)));

		runner.run(task);
	}
	
	public static void runTask(String taskClass, int warmups, int runs, String[] params) throws Exception {
		Task task = TaskFactory.createTask(taskClass, params);
		TaskRunner runner = new TaskRunner(warmups, runs);

		runner.run(task);
	}	

	/*
	 * A macro method, reads system properties, instantiates the task collection
	 * and runs it
	 */
	public static void runTaskCollection() throws Exception {
		Map<String, String> propMap = BenchmarkUtils
				.getSystemProperties(new String[] { Constants.TASK_CLASS_NAME,
						Constants.WARM_UPS, Constants.RUNS,
						Constants.TASK_PARAMS });

		TaskCollection collection = TaskFactory.createTaskCollection(propMap
				.get(Constants.TASK_CLASS_NAME), BenchmarkUtils
				.getCommaSeparatedParameter(Constants.TASK_PARAMS));
		TaskCollectionRunner runner = new TaskCollectionRunner(Integer.valueOf(propMap
				.get(Constants.WARM_UPS)), Integer.valueOf(propMap
				.get(Constants.RUNS)));

		runner.run(collection);
	}
	
	public static void runTaskCollection(String taskClass, int warmups, int runs, String[] params) throws Exception {
		TaskCollection collection = TaskFactory.createTaskCollection(taskClass, params);
		TaskCollectionRunner runner = new TaskCollectionRunner(warmups, runs);

		runner.run(collection);
	}	

	public static File getFile(String path) {
		if (path.startsWith("~" + File.separator)) {
			// replace with the user.home path
			path = System.getProperty("user.home") + path.substring(1);
			// TODO Invoke shell to handle stuff like ~username correctly?
		}

		return new File(path);
	}
	
	public static ReasonerConfiguration getReasonerConfiguration(String[] args) {
		ReasonerConfiguration config = ReasonerConfiguration.getConfiguration();
		
		if (args.length > 1) {
			config.setParameter(ReasonerConfiguration.NUM_OF_WORKING_THREADS, args[1]);
		}
		
		return config;
	}
}
