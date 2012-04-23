package org.semanticweb.elk.benchmark;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages a collection of timers identified by name
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public class Timers {

	private Map<String, Timer> mTimers = new HashMap<String, Timer>();
	
	public void start(String name) {
		Timer timer = getTimer(name);
		
		timer.start();
	}
	
	public void stop(String name) {
		Timer timer = getTimer(name);
		
		timer.stop();
	}
	
	public void reset(String name) {
		getTimer(name).reset();
	}
	
	public void restart(String name) {
		getTimer(name).restart();
	}
	
	public void print(String name, PrintStream stream) {
		getTimer(name).print(stream);
	}
	
	public synchronized void stopAll() {
		for (Timer timer : mTimers.values()) timer.stop();
	}
	
	public void printAll(PrintStream stream) {
		stream.printf("%-16s\t%8s\t%s\t%8s\n", "Name", "Count", "Avg run", "Total");
		stream.println("------------------------------------------------------");
		
		for (Map.Entry<String, Timer> entry : mTimers.entrySet()) {
			Timer timer = entry.getValue();
			
			stream.printf("%-16s\t%8s\t%s\t%8s\n", entry.getKey(), timer.getStartCount(), timer.getAvgRuntime(), timer.getTotalTime());
		}
		
		stream.println("------------------------------------------------------");
	}

	public synchronized Timer getTimer(String name) {
		Timer timer = mTimers.get(name);
		
		if (timer == null) {
			timer = new Timer();
			mTimers.put(name, timer);
		}
		
		return timer;
	}
}