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
 * An factory for creation of {@link InputProcessor}s of a given type. This
 * factory is intended to be used in {@link ConcurrentComputationWithInputs} to process
 * the input concurrently by independent workers. In this case, an
 * {@link InputProcessor} will be created for each worker. Each
 * {@link InputProcessor} created by this {@link InputProcessorFactory} should
 * be used from at most one thread since it may contain some non-thread-safe
 * worker-local objects.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <P>
 *            the type of the {@link Processor}s produced by this
 *            {@link ProcessorFactory}
 */
public interface ProcessorFactory<P extends Processor>
		extends InterruptMonitor {

	/**
	 * @return a new {@link Processor} of the given type
	 */
	public P getEngine();

	/**
	 * a hook function to be called when all jobs are processed
	 */
	public void finish();

}
