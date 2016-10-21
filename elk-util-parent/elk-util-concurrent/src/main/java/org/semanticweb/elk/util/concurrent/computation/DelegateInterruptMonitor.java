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
 * A {@link InterruptMonitor} that delegates the calls to the delegate specified
 * in the constructor.
 * 
 * @author Peter Skocovsky
 */
public class DelegateInterruptMonitor implements InterruptMonitor {

	/**
	 * The {@link InterruptMonitor} that is checked for interruptions.
	 */
	protected final InterruptMonitor interrupter_;

	/**
	 * @param interrupter
	 *            The {@link InterruptMonitor} to which the calls are delegated.
	 *            Must <strong>not</strong> be {@code null}!
	 */
	public DelegateInterruptMonitor(final InterruptMonitor interrupter) {
		if (interrupter == null) {
			throw new IllegalArgumentException("delegate must not be null!");
		}
		this.interrupter_ = interrupter;
	}

	@Override
	public boolean isInterrupted() {
		return interrupter_.isInterrupted();
	}

}
