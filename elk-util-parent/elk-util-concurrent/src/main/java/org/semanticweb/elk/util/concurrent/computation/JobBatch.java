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

import java.util.ArrayList;

/**
 * The class holding a collection of the input elements (a batch) to be
 * processed.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <I>
 *            the type of the input elements
 */
public final class JobBatch<I> extends ArrayList<I> implements Job<I> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8035078348556495610L;

	JobBatch(int size) {
		super(size);
	}

	@Override
	public <O> O accept(JobProcessor<I, O> processor) throws InterruptedException {
		return processor.process(this);
	}

}
