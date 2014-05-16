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
 * A {@link Processor} that can be additionally used to submit jobs of a certain
 * type for concurrent processing. The jobs are submitted using
 * {@link #submit(Object)}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <J>
 *            the type of the jobs to be submitted and processed
 */
public interface InputProcessor<J> extends Processor {

	/**
	 * Submit a job to be processed by this {@link Processor}. This method can
	 * never fail or be interrupted. It is guaranteed that the submitted job is
	 * processed when all subsequent calls of {@link #process()} terminate.
	 * 
	 * @param job
	 *            the job to be submitted
	 */
	public void submit(J job);

}
