/*
 * #%L
 * ELK Utilities for Concurrency
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
package org.semanticweb.elk.util.concurrent.computation;

/**
 * An abstract interface for submitting and processing jobs of a certain type.
 * It has a method for submitting jobs and processing of the submitted jobs,
 * which are typically executed from concurrently running workers.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <J>
 *            the type of the jobs to be submitted and processed
 */
public interface InputProcessor<J> {

	/**
	 * Submit a job to be processed by this manager.
	 * 
	 * @param job
	 *            the job to be submitted
	 * @throws InterruptedException
	 *             if interrupted during submitting the job
	 */
	public void submit(J job) throws InterruptedException;

	/**
	 * Process all currently submitted jobs. This method is intended to be
	 * executed from several threads, therefore it is not guaranteed that all
	 * jobs will be processed when the method terminates.
	 * 
	 * @throws InterruptedException
	 *             if interrupted during processing
	 */
	public void process() throws InterruptedException;

	/**
	 * Returns <tt>true</tt> if new computational power can be used for
	 * processing jobs. This means that running the {@link #process()} method
	 * can perform some computations. The method typically returns <tt>true</tt>
	 * after a job has been submitted but not started to be processed from some
	 * thread, but it can also return <tt>true</tt> when the job that is being
	 * processed creates (spawns) some other sub-jobs.
	 * 
	 * @return <tt>true</tt> if the method {@link #process()} can be called to
	 *         process submitted jobs.
	 */
	public boolean canProcess();

}
