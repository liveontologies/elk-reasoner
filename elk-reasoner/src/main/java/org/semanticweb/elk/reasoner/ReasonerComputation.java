/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner;

import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentComputation;
import org.semanticweb.elk.util.concurrent.computation.ProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ConcurrentComputation} used for executing of reasoner stages
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <F>
 *            the type of the factory for the input processors
 */
public class ReasonerComputation<F extends ProcessorFactory<?>> extends
		ConcurrentComputation<F> {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ReasonerComputation.class);

	public ReasonerComputation(F inputProcessorFactory,
			ComputationExecutor executor, int maxWorkers) {
		super(inputProcessorFactory, executor, maxWorkers);
	}

	/**
	 * Process the given input concurrently using the provided input processor.
	 * If the process has been interrupted, this method can be called again to
	 * continue the computation.
	 */
	public void process() {

		if (!start()) {
			String message = "Could not start workers required for reasoner computation!";
			LOGGER_.error(message);
			throw new ElkRuntimeException(message);
		}

		try {
			finish();
		} catch (InterruptedException e) {
			// restore interrupt status
			Thread.currentThread().interrupt();
			throw new ElkRuntimeException(
					"Reasoner computation interrupted externally!");
		}
	}

}
