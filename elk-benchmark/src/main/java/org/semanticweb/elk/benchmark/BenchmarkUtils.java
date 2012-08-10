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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

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

	public static Task instantiateTask(String className)
			throws ClassNotFoundException, SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		Class<?> clazz = Class.forName(className);
		Constructor<?> constructor = clazz.getConstructor(new Class<?>[] {});

		return (Task) constructor.newInstance(new Object[] {});
	}

	/*
	 * A macro method, reads system properties, instantiates the task and runs
	 * it
	 */
	public static void run() throws Exception {
		Map<String, String> propMap = BenchmarkUtils
				.getSystemProperties(new String[] { Constants.TASK_CLASS_NAME,
						Constants.WARM_UPS, Constants.RUNS,
						Constants.TASK_PARAMS });
		// First, need to instantiate the task
		Task task = BenchmarkUtils.instantiateTask(propMap
				.get(Constants.TASK_CLASS_NAME));
		TaskRunner runner = new TaskRunner(task, Integer.valueOf(propMap
				.get(Constants.WARM_UPS)), Integer.valueOf(propMap
				.get(Constants.RUNS)));

		runner.run(BenchmarkUtils
				.getCommaSeparatedParameter(Constants.TASK_PARAMS));
	}

	public static File getFile(String path) {
		if (path.startsWith("~" + File.separator)) {
			// replace with the user.home path
			path = System.getProperty("user.home") + path.substring(1);
			// TODO Invoke shell to handle stuff like ~username correctly?
		}

		return new File(path);
	}
}
