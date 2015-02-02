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
 * An factory for creation of input processor engines of a given type. This
 * factory is intended to be used in {@link ConcurrentComputation} to process
 * the input concurrently by independent workers. In this case, an engine will
 * be created for every worker. This allows one to store some worker-local
 * objects in engines as well as shared objects to obtain an optimal performance
 * of the computation.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <I>
 *            the type of the input processed by the input processors
 * @param <P>
 *            the type of the input processors produced by this factory
 */
public interface InputProcessorFactory<I, P extends InputProcessor<I>> extends
		Interrupter {

	/**
	 * @return a new input processor of the given type
	 */
	public P getEngine();

	/**
	 * a hook function to be called when all jobs are successfully processed
	 */
	void finish();

}
