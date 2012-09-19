package org.semanticweb.elk.util.logging;

/*
 * #%L
 * ELK Utilities for Logging
 * $Id:$
 * $HeadURL:$
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
 * A class that caches the time every interval
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class CachedTimeThread extends Thread {

	/**
	 * the frequency of update for the time snapshot values
	 */
	private final static int UPDATE_FREQUENCY_ = 1024;

	/**
	 * the snapshot of the current time computed last time
	 */
	private static volatile long currentTimeMillis = System.currentTimeMillis();

	private static volatile short increment = 0;

	CachedTimeThread(int updateInterval) {
		setDaemon(true);
	}

	static {
		new CachedTimeThread(1).start();
	}

	private static void updateTimeSnapshot() {
		currentTimeMillis = System.currentTimeMillis();
		increment = 0;
	}

	public static long currentTimeMillis() {
		if (increment > UPDATE_FREQUENCY_) {
			updateTimeSnapshot();
		}
		return currentTimeMillis + increment;
	}

	@Override
	public void run() {
		for (;;) {
			if (increment++ == UPDATE_FREQUENCY_)
				updateTimeSnapshot();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
}
