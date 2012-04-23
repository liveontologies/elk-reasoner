package org.semanticweb.elk.benchmark;

/**
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public class StaticTimers {

	private static final Timers sTimers = new Timers();
	
	public static void start(String name) {
		sTimers.start(name);
	}
	
	public static void stop(String name) {
		sTimers.stop(name);
	}
	
	public static void restart(String name) {
		sTimers.restart(name);
	}
	
	public static void stopAll() {
		sTimers.stopAll();
	}
	
	public static void printAll() {
		sTimers.printAll(System.out);
	}
}
