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
 * A general interface for jobs processed by workers.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <I>
 *            the type of the input to be processed
 */
public interface Job<I> {

	/**
	 * processing of the job using a job processor
	 * 
	 * @param processor
	 *            the job processor with the input type {@link I} and output
	 *            type {@link O}
	 * @return
	 */
	public <O> O accept(JobProcessor<I, O> processor);

}
