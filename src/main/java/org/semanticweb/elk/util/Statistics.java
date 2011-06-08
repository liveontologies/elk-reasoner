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
/**
 * @author Yevgeny Kazakov, Jun 6, 2011
 */
package org.semanticweb.elk.util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 */
public class Statistics {

	// Declare the size
	final static int megaBytes = 1024 * 1024;
	
	public static void logOperationStart(String operationName, Logger logger) {
		logOperationStart(operationName, logger, Level.INFO);
	}
	
	public static void logOperationStart(String operationName, Logger logger, Priority priority) {
		if (logger.isEnabledFor(priority)) {
			logger.log(priority, operationName + " started");
			ElkTimer timer = ElkTimer.getNamedTimer(operationName, ElkTimer.RECORD_WALLTIME);
			timer.reset(); // needed in case this was done before
			timer.start();
		}
	}
	
	public static void logOperationFinish(String operationName, Logger logger) {
		logOperationFinish(operationName, logger, Level.INFO);
	}
	
	public static void logOperationFinish(String operationName, Logger logger, Priority priority) {
		if (logger.isEnabledFor(priority)) {
			ElkTimer timer = ElkTimer.getNamedTimer(operationName, ElkTimer.RECORD_WALLTIME);
			timer.stop();
			logger.log(priority, operationName + " finished in " + timer.getTotalWallTime() / 1000000 + " ms");
		}
	}

	public static void logMemoryUsage(Logger logger) {
		logMemoryUsage(logger, Level.DEBUG);
	}

	public static void logMemoryUsage(Logger logger, Priority priority) {
		if (logger.isEnabledFor(priority)) {
			// Getting the runtime reference from system
			Runtime runtime = Runtime.getRuntime();
			logger.log(priority, "Memory (MB) Used/Total/Max: "
					+ (runtime.totalMemory() - runtime.freeMemory())
					/ megaBytes + "/" + runtime.totalMemory() / megaBytes + "/"
					+ runtime.maxMemory() / megaBytes);
		}
	}

}
