/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
 * @author Yevgeny Kazakov, Jul 1, 2011
 */
package org.semanticweb.elk.reasoner;

/**
 * 
 * Interface for monitoring progress in processes that can take some time to
 * complete.
 * 
 * @author Yevgeny Kazakov
 * 
 */
public interface ProgressMonitor {
	
	/**
	 * This method should be called in the beginning of the process. It should
	 * be called only once until the method {@link #finish()} is called.
	 * 
	 * @param message
	 *            a description of the process.
	 */
	public void start(String message);

	/**
	 * Reports the current progress.
	 * 
	 * @param state
	 *            the current value of the progress. Must not be larger then
	 *            {@code max}.
	 * @param maxState
	 *            the maximal (estimated) value of the progress.
	 */
	public void report(int state, int maxState);

	/**
	 * Indicates that the process is finished. Should be be only called if the
	 * method {@link #start(String)} was called before.
	 * 
	 */
	public void finish();

}
