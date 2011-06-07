package org.semanticweb.elk.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * Class for keeping CPU and system times.
 * 
 * @author Markus Kroetzsch
 */
public class ElkTimer {

	static final ThreadMXBean tmxb = ManagementFactory.getThreadMXBean();

	static final ConcurrentHashMap<ElkTimer, ElkTimer> registeredTimers = new ConcurrentHashMap<ElkTimer, ElkTimer>();

	protected final String name;
	protected final long threadId;

	protected long currentStartCpuTime = -1;
	protected long currentStartSystemTime = -1;
	protected long totalCpuTime = 0;
	protected long totalSystemTime = 0;
	protected int measurements = 0;
	protected int threadCount = 0;

	public ElkTimer(String name, long threadId) {
		this.name = name;
		this.threadId = threadId;

		if (!tmxb.isThreadCpuTimeEnabled()) {
			tmxb.setThreadCpuTimeEnabled(true);
		}
	}

	/**
	 * Create a new Timer for the current thread.
	 * 
	 * @param name
	 * @return
	 */
	static public ElkTimer getTimerForCurrentThread(String name) {
		return new ElkTimer(name, Thread.currentThread().getId());
	}

	/**
	 * Get the total recorded CPU time in nanoseconds.
	 * 
	 * @return recorded CPU time in nanoseconds
	 */
	public long getTotalCpuTime() {
		return totalCpuTime;
	}

	/**
	 * Get the string name of the timer.
	 * 
	 * @return string name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the ID of the thread for which this timer was created.
	 * 
	 * @return thread ID
	 */
	public long getThreadId() {
		return threadId;
	}

	/**
	 * Get the total recorded system time in nanoseconds.
	 * 
	 * @return recorded system time in nanoseconds
	 */
	public long getTotalSystemTime() {
		return totalCpuTime;
	}

	/**
	 * Return true if the timer is running.
	 * 
	 * @return true if running
	 */
	public boolean isRunning() {
		return currentStartSystemTime != -1;
	}

	/**
	 * Start the timer.
	 */
	public synchronized void start() {
		currentStartCpuTime = getThreadCpuTime(threadId);
		currentStartSystemTime = System.nanoTime();
	}

	/**
	 * Stop the timer (if running) and reset all recorded values.
	 */
	public synchronized void reset() {
		currentStartCpuTime = -1;
		currentStartSystemTime = -1;
		totalCpuTime = 0;
		totalSystemTime = 0;
	}

	/**
	 * Stop the timer and return the CPU time that has passed since it had last
	 * been started. The total time (both system and CPU) of all start-stop
	 * cycles is recorded with the timer.
	 * 
	 * @param timerName
	 * @param threadId
	 * @return CPU time that the timer was running, or -1 if timer not running
	 *         or CPU time unavailable for other reasons
	 */
	public synchronized long stop() {
		long totalTime = -1;
		long systemTime = System.nanoTime();
		long cpuTime = getThreadCpuTime(threadId);

		if (currentStartCpuTime != -1) { // was timer started?
			if (cpuTime != -1) { // may fail if thread already dead
				totalTime = cpuTime - currentStartCpuTime;
				totalCpuTime += totalTime;
			}
			totalSystemTime += systemTime - currentStartSystemTime;
			currentStartSystemTime = -1;
			currentStartCpuTime = -1;
			measurements += 1;
		}

		return totalTime;
	}

	/**
	 * Print logging information for the timer using debug priority.
	 * 
	 * @see org.semanticweb.elk.util.ElkTimer#log(Logger,Priority) log()
	 * @param logger
	 */
	public void log(Logger logger) {
		log(logger, Level.DEBUG);
	}

	/**
	 * Print logging information for the timer. The log only shows the recorded
	 * time of the completed start-stop cycles. If the timer is still running,
	 * then it will not be stopped to add the currently measured time to the
	 * output but a warning will be logged.
	 * 
	 * @param logger
	 * @param priority
	 */
	public void log(Logger logger, Priority priority) {
		if (logger.isEnabledFor(priority)) {
			if (threadId != 0) {
				logger.log(priority, "Timer '" + name + "' (thread " + threadId
						+ ") total over " + measurements
						+ " measurements (ms) CPU/System: " + totalCpuTime
						/ 1000000 + "/" + totalSystemTime / 1000000);
			} else if (threadCount > 0) {
				logger.log(priority, "Timer '" + name + "' (sum of "
						+ threadCount + " threads) total over " + measurements
						+ " measurements (ms) CPU/System: " + totalCpuTime
						/ 1000000 + "/" + totalSystemTime / 1000000);
			} else {
				logger.log(priority, "Timer '" + name + "' (no thread) "
						+ " total over " + measurements
						+ " measurements (ms) System: " + totalSystemTime
						/ 1000000);
			}
			if (currentStartCpuTime != -1) {
				logger.warn("Timer '" + name + "' in thread " + threadId
						+ " logged while it was still running");
			}
		}
	}

	/**
	 * Start a timer of the given string name for the current thread. If no such
	 * timer exists yet, then it will be newly created.
	 * 
	 * @param timerName
	 *            the name of the timer
	 */
	public static void startNamedTimer(String timerName) {
		getNamedTimer(timerName).start();
	}

	/**
	 * Start a timer of the given string name for the current thread. If no such
	 * timer exists yet, then it will be newly created.
	 * 
	 * @param timerName
	 *            the name of the timer
	 * @param threadId
	 *            of the thread to track, or 0 if only system clock should be
	 *            tracked
	 */
	public static void startNamedTimer(String timerName, long threadId) {
		getNamedTimer(timerName, threadId).start();
	}

	/**
	 * Stop a timer of the given string name for the current thread. If no such
	 * timer exists, -1 will be returned. Otherwise the return value is the CPU
	 * time that was measured.
	 * 
	 * @param timerName
	 *            the name of the timer
	 * @return CPU time if timer existed and was running, and -1 otherwise
	 */
	public static long stopNamedTimer(String timerName) {
		return stopNamedTimer(timerName, Thread.currentThread().getId());
	}

	/**
	 * Stop a timer of the given string name for the given thread. If no such
	 * timer exists, -1 will be returned. Otherwise the return value is the CPU
	 * time that was measured.
	 * 
	 * @param timerName
	 *            the name of the timer
	 * @param threadId
	 *            of the thread to track, or 0 if only system clock should be
	 *            tracked
	 * @return CPU time if timer existed and was running, and -1 otherwise
	 */
	public static long stopNamedTimer(String timerName, long threadId) {
		ElkTimer key = new ElkTimer(timerName, threadId);
		if (registeredTimers.containsKey(key)) {
			return registeredTimers.get(key).stop();
		} else {
			return -1;
		}
	}

	/**
	 * Reset a timer of the given string name for the current thread. If no such
	 * timer exists yet, then it will be newly created.
	 * 
	 * @param timerName
	 *            the name of the timer
	 */
	public static void resetNamedTimer(String timerName) {
		getNamedTimer(timerName).reset();
	}

	/**
	 * Reset a timer of the given string name for the given thread. If no such
	 * timer exists yet, then it will be newly created.
	 * 
	 * @param timerName
	 *            the name of the timer
	 * @param threadId
	 *            of the thread to track, or 0 if only system clock should be
	 *            tracked
	 */
	public static void resetNamedTimer(String timerName, long threadId) {
		getNamedTimer(timerName, threadId).reset();
	}

	/**
	 * Get a timer of the given string name for the current thread. If no such
	 * timer exists yet, then it will be newly created.
	 * 
	 * @param timerName
	 *            the name of the timer
	 * @return timer
	 */
	public static ElkTimer getNamedTimer(String timerName) {
		return getNamedTimer(timerName, Thread.currentThread().getId());
	}

	/**
	 * Get a timer of the given string name for the given thread. If no such
	 * timer exists yet, then it will be newly created.
	 * 
	 * @param timerName
	 *            the name of the timer
	 * @param threadId
	 *            of the thread to track, or 0 if only system clock should be
	 *            tracked
	 * @return timer
	 */
	public static ElkTimer getNamedTimer(String timerName, long threadId) {
		ElkTimer key = new ElkTimer(timerName, threadId);
		registeredTimers.putIfAbsent(key, key);
		return registeredTimers.get(key);
	}

	/**
	 * Collect the total times measured by all known named timers of the given
	 * name.
	 * 
	 * @param timerName
	 * @return timer
	 */
	public static ElkTimer getNamedTotalTimer(String timerName) {
		long totalCpuTime = 0;
		long totalSystemTime = 0;
		int measurements = 0;
		int threadCount = 0;
		ElkTimer previousTimer = null;
		for (Map.Entry<ElkTimer, ElkTimer> entry : registeredTimers.entrySet()) {
			if (entry.getValue().name.equals(timerName)) {
				previousTimer = entry.getValue();
				threadCount += 1;
				totalCpuTime += previousTimer.totalCpuTime;
				totalSystemTime += previousTimer.totalSystemTime;
				measurements += previousTimer.measurements;
			}
		}

		if (threadCount == 1) {
			return previousTimer;
		} else {
			ElkTimer result = new ElkTimer(timerName, 0);
			result.totalCpuTime = totalCpuTime;
			result.totalSystemTime = totalSystemTime;
			result.measurements = measurements;
			result.threadCount = threadCount;
			return result;
		}
	}

	public static void logAllNamedTimers(String timerName, Logger logger) {
		logAllNamedTimers(timerName, logger, Level.DEBUG);
	}
	
	public static void logAllNamedTimers(String timerName, Logger logger,
			Priority priority) {
		for (Map.Entry<ElkTimer, ElkTimer> entry : registeredTimers.entrySet()) {
			if (entry.getValue().name.equals(timerName)) {
				entry.getValue().log(logger, priority);
			}
		}
	}

	@Override
	public int hashCode() {
		int nameHash = name.hashCode();
		int threadIdHash = new Long(threadId).hashCode();
		return HashGenerator.combineListHash(nameHash, threadIdHash);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (getClass() != obj.getClass()) {
			return false;
		} else if (threadId == ((ElkTimer) obj).threadId
				&& name.equals(((ElkTimer) obj).name)) {
			return true;
		} else {
			return false;
		}
	}

	protected static long getThreadCpuTime(long threadId) {
		if (threadId == 0) { // generally invalid
			return 0;
		} else {
			return tmxb.getThreadCpuTime(threadId);
		}
	}

}
