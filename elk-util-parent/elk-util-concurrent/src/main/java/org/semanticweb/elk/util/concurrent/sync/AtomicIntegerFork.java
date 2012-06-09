/*
 * #%L
 * ELK Utilities for Concurrency
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
package org.semanticweb.elk.util.concurrent.sync;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * An {@link AtomicInteger} through which one can modify the
 * {@link AtomicInteger} in an asynchronous way.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class AtomicIntegerFork extends AtomicInteger {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1553653698465796093L;
	final AtomicInteger parent;

	public AtomicIntegerFork(AtomicInteger parent) {
		this.parent = parent;
	}

	public AtomicInteger getParent() {
		return this.parent;
	}

	/**
	 * move the changes with this counter to the parent counter; the sums of the
	 * counters should be preserved if none of them is modified
	 */
	public void sync() {
		int snapshot = get();
		parent.addAndGet(snapshot);
		this.addAndGet(-snapshot);
	}

}
