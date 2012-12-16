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
package org.semanticweb.elk.util.concurrent.computation;

/**
 * A simple interrupter, which just stores the flag about the interrupt status
 * and interrupts the thread that is assigned to this interrupter
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SimpleInterrupter implements Interrupter {

	/**
	 * the thread which will be interrupted when {@link #interrupt()} is called
	 */
	private volatile Thread toInterrupt;

	/**
	 * the interrupt status of this interrupter
	 */
	volatile boolean interrupted = false;

	/**
	 * Make sure the given thread is interrupted when {@link #interrupt()} is
	 * called
	 * 
	 * @param toInterrupt
	 *            the thread which will be interrupted
	 */
	public void registerThreadToInterrupt(Thread toInterrupt) {
		this.toInterrupt = toInterrupt;
	}

	/**
	 * Assign the current thread is interrupted when {@link #interrupt()} is
	 * called
	 */
	public void registerCurrentThreadToInterrupt() {
		registerThreadToInterrupt(Thread.currentThread());
	}

	/**
	 * De-registers a thread to be interrupted (so that no thread is interrupted
	 * accidently)
	 */
	public synchronized void clearThreadToInterrupt() {
		this.toInterrupt = null;
	}

	@Override
	// I've seen an NPE here, which can only happen
	// if some thread managed to clear the to-be-interrupted thread
	// before another one interrupts it
	public synchronized void interrupt() {
		interrupted = true;
		if (toInterrupt != null)
			toInterrupt.interrupt();
	}

	@Override
	public boolean isInterrupted() {
		return interrupted;
	}

	@Override
	public void clearInterrupt() {
		interrupted = false;
	}
}
