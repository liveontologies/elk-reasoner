package org.semanticweb.elk.util.concurrent.computation;
/*
 * #%L
 * ELK Utilities for Concurrency
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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
 * An abstract interface for concurrent processing. Processing is performed by
 * calling the method {@link #process()}. It is assumed that each
 * {@link Processor} can be used only within one thread, but several different
 * {@link Processor}s can work concurrently. In this case, processing is
 * finished when all of the concurrent {@link Processor}s are finished.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface Processor {

	/**
	 * Performs processing using this {@link Processor}. For each
	 * {@link Processor} object the method should be called only from one
	 * thread, but several {@link Processor} objects can call this method
	 * concurrently. In this case, processing performs concurrently and is
	 * finished when all concurrent calls of {@link #process()} are finished.
	 * 
	 * @throws InterruptedException
	 *             if interrupted during processing
	 */
	public void process() throws InterruptedException;

	/**
	 * Indicate that processing of by this object is finished. This method
	 * should be eventually called after every call of {@link #process()}. But
	 * it is not necessary that every call of {@link #process()} should be
	 * followed by {@link #finish()}. E.g., it can be followed by
	 * {@link #process()} when it is determined that processing is not yet
	 * finished (e.g., due to concurrent computation).
	 */
	public void finish();

}
