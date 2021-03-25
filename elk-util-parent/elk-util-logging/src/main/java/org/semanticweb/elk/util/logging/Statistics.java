/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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

package org.semanticweb.elk.util.logging;

import org.slf4j.Logger;


/**
 * Collection of several static methods that help in taking and logging
 * statistical information.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 */
public class Statistics {

	// Declare the size
	final static int megaBytes = 1024 * 1024;

	/**
	 * Log the start of a particular operation with INFO priority. This method
	 * should be used for long-running tasks and is mainly intended. Multiple
	 * threads can independently log operations of the same name, but
	 * (obviously) no single thread should use the same operation name to record
	 * the start and end of overlapping code.
	 * 
	 * @param operationName
	 * @param logger
	 */
	public static void logOperationStart(String operationName, Logger logger) {
		logOperationStart(operationName, logger, LogLevel.DEBUG);
	}

	/**
	 * Log the start of a particular operation with the given priority. This
	 * method should be used for long-running tasks and is mainly intended.
	 * Multiple threads can independently log operations of the same name, but
	 * (obviously) no single thread should use the same operation name to record
	 * the start and end of overlapping code.
	 * 
	 * @param operationName
	 * @param logger
	 * @param priority
	 */
	public static void logOperationStart(String operationName, Logger logger,
			LogLevel priority) {
		if (LoggerWrap.isEnabledFor(logger, priority)) {
			LoggerWrap.log(logger, priority, operationName + " started");
			ElkTimer timer = ElkTimer.getNamedTimer(operationName,
					ElkTimer.RECORD_WALLTIME);
			timer.reset(); // needed in case this was done before
			timer.start();
		}
	}

	/**
	 * Log the end of a particular operation the beginning of which has been
	 * logged using logOperationStart(), using INFO priority.
	 * 
	 * @param operationName
	 * @param logger
	 */
	public static void logOperationFinish(String operationName, Logger logger) {
		logOperationFinish(operationName, logger, LogLevel.DEBUG);
	}

	/**
	 * Log the end of a particular operation the beginning of which has been
	 * logged using logOperationStart(), using the given logging priority.
	 * 
	 * @param operationName
	 * @param logger
	 * @param priority
	 */
	public static void logOperationFinish(String operationName, Logger logger,
			LogLevel priority) {
		if (LoggerWrap.isEnabledFor(logger, priority)) {
			ElkTimer timer = ElkTimer.getNamedTimer(operationName,
					ElkTimer.RECORD_WALLTIME);
			timer.stop();
			LoggerWrap.log(logger, priority, operationName + " took "
					+ timer.getTotalWallTime() / 1000000 + " ms");
		}
	}

	/**
	 * Log the current total memory usage using DEBUG priority.
	 * 
	 * @param logger
	 */
	public static void logMemoryUsage(Logger logger) {
		logMemoryUsage(logger, LogLevel.DEBUG);
	}

	/**
	 * Log the current total memory usage with the specified priority.
	 * 
	 * @param logger
	 * @param priority 
	 */
	public static void logMemoryUsage(Logger logger, LogLevel priority) {
		if (LoggerWrap.isEnabledFor(logger, priority)) {
			// Getting the runtime reference from system
			Runtime runtime = Runtime.getRuntime();
			
			LoggerWrap.log(logger, priority, "Memory (MB) Used/Total/Max: "
					+ (runtime.totalMemory() - runtime.freeMemory())
					/ megaBytes + "/" + runtime.totalMemory() / megaBytes + "/"
					+ runtime.maxMemory() / megaBytes);
		}
	}

}
