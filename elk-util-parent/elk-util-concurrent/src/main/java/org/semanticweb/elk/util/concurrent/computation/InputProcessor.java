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
	 * Submit a job to be processed by this manager. This method can never fail
	 * or be interrupted.
	 * 
	 * @param job
	 *            the job to be submitted
	 */
	public void submit(J job);

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
	 * Indicate that processing of the input is finished. This method should be
	 * eventually called after every call of {@link #process()} (but it is not
	 * necessary that every call of {@link #process()} should be followed by
	 * {@link #finish()}).
	 */
	public void finish();

}
