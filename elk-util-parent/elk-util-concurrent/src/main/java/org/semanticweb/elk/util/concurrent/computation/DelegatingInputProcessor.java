/**
 * 
 */
package org.semanticweb.elk.util.concurrent.computation;
/*
 * #%L
 * ELK Utilities for Concurrency
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @param <J>
 *            the type of the jobs to be submitted and processed
 */
public class DelegatingInputProcessor<J> implements InputProcessor<J> {

	private final InputProcessor<J> processor_;

	public DelegatingInputProcessor(InputProcessor<J> p) {
		processor_ = p;
	}

	@Override
	public void submit(J job) {
		preSubmit(job);
		processor_.submit(job);
		postSubmit(job);
	}

	@Override
	public void process() throws InterruptedException {
		preProcess();
		processor_.process();
		postProcess();
	}

	@Override
	public void finish() {
		processor_.finish();
	}

	protected void preSubmit(J job) {
	}

	protected void postSubmit(J job) {
	}

	protected void preProcess() {
	}

	protected void postProcess() {
	}
}
