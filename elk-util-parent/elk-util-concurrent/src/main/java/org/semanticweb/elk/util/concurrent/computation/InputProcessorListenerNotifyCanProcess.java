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
 * A listener to be used with {@link InputProcessor<J>} that can be used to
 * perform actions when new computations are available.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <J>
 *            the type of the jobs used in the job processor
 * @param <P>
 *            the type of the job processor
 * 
 */
public interface InputProcessorListenerNotifyCanProcess<P extends InputProcessor<?>> {

	/**
	 * This function is called after the input processor detects it can continue
	 * processing jobs. If {@link P#canProcess()} returns <tt>false</tt> and
	 * there is a thread that runs {@link P#process()}, then this method will be
	 * called from one of such threads after the input processor detects that
	 * there are new computations available, i.e., {@link P#process()} can be
	 * used to perform some computations. A typical scenario, is when the jobs
	 * are processed from a fixed pool of threads. In this case, if
	 * {@link P#canProcess()} returns <tt>false</tt>, and there are other
	 * threads processing the jobs, the thread is put to sleep. The method
	 * {@link #notifyCanProcess()} can then help to wake up all sleeping
	 * threads, so the threads can be used to execute {@link P#process()}).
	 */
	public void notifyCanProcess();

}
