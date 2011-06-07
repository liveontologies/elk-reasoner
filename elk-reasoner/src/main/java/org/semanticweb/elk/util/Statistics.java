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

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 */
public class Statistics {

	static class TimeRecord {
		long currentStartCpuTime = -1;
		long currentStartSystemTime = -1;
		long totalCpuTime = 0;
		long totalSystemTime = 0;
	}

	static final ConcurrentHashMap<Pair<Long, String>, TimeRecord> timeRecords = new ConcurrentHashMap<Pair<Long, String>, TimeRecord>();

	/**
	 * Start a (possibly new) timer with the given name for the current thread.
	 */
	public static void startCurrentThreadTimer(String timerName) {
		startTimer(timerName, Thread.currentThread().getId());
	}

	/**
	 * Start a (possibly new) timer with the given name for the given thread.
	 * 
	 * @param timerName
	 * @param threadId
	 */
	public static void startTimer(String timerName, long threadId) {
		Pair<Long, String> timerKey = new Pair<Long, String>(threadId,
				timerName);
		if (!timeRecords.containsKey(timerKey)) {
			timeRecords.put(timerKey, new TimeRecord());
		}
		TimeRecord timeRecord = timeRecords.get(timerKey);
		synchronized (timeRecord) {
			timeRecord.currentStartSystemTime = System.nanoTime();
			timeRecord.currentStartCpuTime = getThreadCpuTime(threadId);
		}
	}

	public static long stopCurrentThreadTimer(String timerName) {
		return stopTimer(timerName, Thread.currentThread().getId());
	}

	/**
	 * Stop the timer with the given name, and return the CPU time that has
	 * passed since it had last been started. The total time (both system and
	 * CPU) of all start-stop cycles is recorded with the timer.
	 * 
	 * @param timerName
	 * @param threadId
	 * @return CPU time that the timer was running, or -1 if timer not running
	 */
	public static long stopTimer(String timerName, long threadId) {
		Pair<Long, String> timerKey = new Pair<Long, String>(threadId,
				timerName);

		long totalTime = -1;
		if (timeRecords.containsKey(timerKey)) {
			TimeRecord timeRecord = timeRecords.get(timerKey);
			long cpuTime = getThreadCpuTime(threadId);
			long systemTime = System.nanoTime();
			synchronized (timeRecord) {
				if (timeRecord.currentStartCpuTime != -1) { // was timer
					// started?
					if (cpuTime != -1) { // may fail if thread already dead
						totalTime = cpuTime - timeRecord.currentStartCpuTime;
						timeRecord.totalCpuTime += totalTime;
					}
					timeRecord.totalSystemTime += systemTime
							- timeRecord.currentStartSystemTime;
					timeRecord.currentStartSystemTime = -1;
					timeRecord.currentStartCpuTime = -1;
				}
			}
		}
		return totalTime;
	}

	public static void logAllTimers(Logger logger) {
		for (Pair<Long, String> timerKey : timeRecords.keySet()) {
			logTimer(timerKey.second, timerKey.first, logger, Level.DEBUG);
		}
	}

	/**
	 * Print logging information for the given timer and current thread using
	 * Debug as the default logging priority.
	 * 
	 * @see org.semanticweb.elk.util.Statistics#logTimer(java.lang.String, long,
	 *      org.apache.log4j.Logger, org.apache.log4j.Priority) logTimer
	 * @param timerName
	 * @param logger
	 */
	public static void logCurrentThreadTimer(String timerName, Logger logger) {
		logTimer(timerName, Thread.currentThread().getId(), logger);
	}

	/**
	 * Print logging information for the given timer and current thread.
	 * 
	 * @see org.semanticweb.elk.util.Statistics#logTimer(java.lang.String, long,
	 *      org.apache.log4j.Logger, org.apache.log4j.Priority) logTimer
	 * @param timerName
	 * @param logger
	 * @param priority
	 */
	public static void logCurrentThreadTimer(String timerName, Logger logger,
			Priority priority) {
		logTimer(timerName, Thread.currentThread().getId(), logger, priority);
	}

	/**
	 * Print logging information for the given timer using Debug as the default
	 * logging priority.
	 * 
	 * @see org.semanticweb.elk.util.Statistics#logTimer(java.lang.String, long,
	 *      org.apache.log4j.Logger, org.apache.log4j.Priority) logTimer
	 * 
	 * @param timerName
	 * @param threadId
	 * @param logger
	 */
	public static void logTimer(String timerName, long threadId, Logger logger) {
		logTimer(timerName, threadId, logger, Level.DEBUG);
	}

	/**
	 * Print logging information for the given timer. The log only shows the
	 * recorded time of the completed start-stop cycles. If the timer is still
	 * running, then it will not be stopped to add the currently measured time
	 * to the output but a warning will be logged.
	 * 
	 * @param timerName
	 * @param threadId
	 * @param logger
	 * @param priority
	 */
	public static void logTimer(String timerName, long threadId, Logger logger,
			Priority priority) {
		if (logger.isEnabledFor(priority)) {
			Pair<Long, String> timerKey = new Pair<Long, String>(threadId,
					timerName);

			if (timeRecords.containsKey(timerKey)) {
				TimeRecord timeRecord = timeRecords.get(timerKey);
				long cpuTime = timeRecord.totalCpuTime;
				long systemTime = timeRecord.totalSystemTime;
				logger.log(priority, "Timer '" + timerName + "' in thread "
						+ threadId + " total (ms) CPU/System: " + cpuTime
						/ 1000000 + "/" + systemTime / 1000000);
				if (timeRecord.currentStartCpuTime != -1) {
					logger.warn("Timer '" + timerName + "' in thread "
							+ threadId + " logged while it was still running");
				}
			} else {
				logger.warn("Timer '" + timerName
						+ "' does not exist for thread " + threadId + ".");
				logger.warn("Available timers: ");
				for (Pair<Long, String> name : timeRecords.keySet()) {
					logger.warn(name.second + " in thread " + name.first);
				}
			}
		}
	}

	// Declare the size
	final static int megaBytes = 1024 * 1024;

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

	public static long getThreadCpuTime(long threadId) {
		ThreadMXBean tm = ManagementFactory.getThreadMXBean();
		if (!tm.isThreadCpuTimeEnabled()) {
			tm.setThreadCpuTimeEnabled(true);
		}
		try {
			return tm.getThreadCpuTime(threadId);
		} catch (UnsupportedOperationException e) {
			return -1;
		}
	}
}
