package org.semanticweb.elk.benchmark;

import java.io.PrintStream;

/**
 * A utility class for measuring elapsed time
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public class Timer {

	private long mStartTime = 0;
	private long mStartCount = 0;
	private long mTotalTime = 0;

	public void start() {
		mStartTime = System.currentTimeMillis();
		mStartCount++;
	}

	public void stop() {
		mTotalTime += System.currentTimeMillis() - mStartTime;
	}
	
	public void reset() {
		mTotalTime = 0;
		mStartCount = 0;
	}
	
	public void restart() {
		reset();
		start();
	}

	public long getStartTime() {
		return mStartTime;
	}

	public long getTotalTime() {
		return mTotalTime;
	}
	
	public long getStartCount() {
		return mStartCount;
	}
	
	public long getAvgRuntime() {
		return mTotalTime / mStartCount;
	}
	
	public void print(PrintStream stream) {
		stream.printf("%d\t\t%d\t\t%d\n", getStartCount(), getAvgRuntime(), getTotalTime());
	}
}