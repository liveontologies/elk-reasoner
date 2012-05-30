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
package org.semanticweb.elk.reasoner.stages;

/**
 * An basic computation unit that can be executed by a reasoner. A
 * {@link ReasonerStage} can specify other {@link ReasonerStage}s as
 * dependencies. Thus, several stages can be chained within a reasoning process.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface ReasonerStage {

	/**
	 * @return a string identifier of this stage
	 */
	public String getName();

	/**
	 * @return <tt>true</tt> if the results for this stage have been already
	 *         computed; this does not necessarily mean that this stage was
	 *         executed: the results of the computation could have been computed
	 *         by other stages
	 */
	public boolean done();

	/**
	 * @return the list of stages that are required to be executed before
	 *         executing this stage; the order of the execution does not matter
	 */
	public Iterable<ReasonerStage> getDependencies();

	/**
	 * Performs the execution of this stage; in order to ensure correctness of
	 * the execution, it is necessary that all staged from the dependencies are
	 * done. If the execution of this stage has not been interrupted, the
	 * results for this stage should be computed and the function
	 * {@link #done()} should return <tt>true</tt>.
	 */
	public void execute();

	/**
	 * @return <tt>true</tt> if the execution for this stage has been
	 *         interrupted
	 */
	public boolean isInterrupted();

	/**
	 * Prints detailed information about the (progress) of this stage. This
	 * function can be used to print statistics after this stage is executed or
	 * interrupted.
	 */
	public void printInfo();

}
