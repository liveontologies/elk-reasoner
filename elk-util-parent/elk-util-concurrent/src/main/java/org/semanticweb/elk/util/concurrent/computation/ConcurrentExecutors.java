package org.semanticweb.elk.util.concurrent.computation;

/*-
 * #%L
 * ELK Utilities for Concurrency
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import java.util.concurrent.TimeUnit;

/**
 * A collection of convenience methods for creating {@link ConcurrentExecutor}s
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ConcurrentExecutors {

	private static ConcurrentExecutor DEFAULT_ = create("elk-worker");

	/**
	 * Creates a {@link ConcurrentExecutor} with the given name (used as a
	 * prefix for creating thread), and given timeout for running threads. If a
	 * thread did not process a job within the given timeout, the thread is
	 * terminated.
	 * 
	 * @param name
	 * @param timeout
	 * @param unit
	 * @return the {@link ConcurrentExecutor} associated with the given
	 *         parameters
	 */
	public static ConcurrentExecutor create(String name, long timeout,
			TimeUnit unit) {
		return new ConcurrentExecutorImpl(name, timeout, unit);
	}

	/**
	 * Creates a {@link ConcurrentExecutor} with the given name (used as a
	 * prefix for creating thread), and the timeout of 1 second for running
	 * threads. If a thread did not process a job within the given timeout, the
	 * thread is terminated.
	 * 
	 * @param name
	 * @return the {@link ConcurrentExecutor} associated with the given
	 *         parameters
	 */
	public static ConcurrentExecutor create(String name) {
		return create(name, 1L, TimeUnit.SECONDS);
	}

	/**
	 * @return the default {@link ConcurrentExecutor} with the timeout of 1
	 *         second for running threads.
	 */
	public static ConcurrentExecutor getDefault() {
		return DEFAULT_;
	}

}
