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
	 * the frequency of update for the time snapshot values in milliseconds
	 */
	private static final int UPDATE_FREQUENCY_ = 10;

	/**
	 * the current time in milliseconds delayed by at most 10 milliseconds the
	 * value that would be returned by {@link System#currentTimeMillis()}
	 */
	public static volatile long currentTimeMillis = System.currentTimeMillis();

	CachedTimeThread() {
		setDaemon(true);
	}

	static {
		new CachedTimeThread().start();
	}

	@Override
	public void run() {
		for (;;) {
			currentTimeMillis = System.currentTimeMillis();
			try {
				Thread.sleep(UPDATE_FREQUENCY_);
			} catch (InterruptedException e) {
				// will continue; the thread should die automatically
			}
		}
	}
}
