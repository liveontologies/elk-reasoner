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