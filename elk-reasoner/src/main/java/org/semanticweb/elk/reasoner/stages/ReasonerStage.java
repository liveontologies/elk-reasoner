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

import java.util.List;

public interface ReasonerStage {

	/**
	 * @return a string identifier of this stage
	 */
	public String getName();

	/**
	 * @return <tt>true</tt> if the computation for this stage was already done;
	 *         this does not necessarily mean that this stage was run: the
	 *         results of the computation could have been computed by other
	 *         stages
	 */
	public boolean done();

	/**
	 * @return the list of stages that are required to be executed before
	 *         executing this stage
	 */
	public List<ReasonerStage> getDependencies();

	/**
	 * execution of this stage
	 */
	public void execute();

	/**
	 * @return <tt>true</tt> if the execution for this stage has been
	 *         interrupted
	 */
	public boolean isInterrupted();

	/**
	 * print detailed information about this stage
	 */
	public void printInfo();

}
