/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner;

/**
 * A general class for jobs submitted for computation. Every job is initialized
 * with some input, and when the input is processed, this job can be used to
 * obtain the result of the computation.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <I>
 *            the type of the input of the job
 * @param <O>
 *            the type of the result of the computation
 */
public class ReasonerJob<I, O> {

	private final I input;

	private O output = null;

	/**
	 * Creating a job instance for the given input.
	 * 
	 * @param input
	 *            the input to be processed
	 */
	public ReasonerJob(I input) {
		this.input = input;
	}

	/**
	 * Returns the input for this job.
	 * 
	 * @return the input for this job
	 */
	public I getInput() {
		return this.input;
	}

	/**
	 * Returns the output of this job, or {@code null} if the job has not been
	 * processed yet.
	 * 
	 * @return the output of this job
	 */
	public O getOutput() {
		return this.output;
	}

	/**
	 * Set the output of this job to the given value.
	 * 
	 * @param output
	 *            the output of this job
	 */
	protected void setOutput(O output) {
		this.output = output;
	}

}
