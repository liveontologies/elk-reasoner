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
 * An interface for a job processor based on the visitor pattern.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <I>
 *            the type of the input of the job
 * @param <O>
 *            the type of the output of the processor
 */
public interface JobProcessor<I, O> {

	public O process(JobBatch<I> job) throws InterruptedException;

	public O process(JobPoison<I> job);

}
