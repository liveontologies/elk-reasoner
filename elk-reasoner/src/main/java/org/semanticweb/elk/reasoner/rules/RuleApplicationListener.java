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
package org.semanticweb.elk.reasoner.rules;

public interface RuleApplicationListener {

	/**
	 * An callback hook to the method using which one can signal that the
	 * manager can perform some computations using the {@link #process()}
	 * method. If {@link #canProcess()} returns <tt>false</tt> and there is a
	 * thread that runs {@link #process()}, then this method will be called from
	 * such thread when new computational power can be used for processing jobs,
	 * i.e., {@link #process()} can be run to perform some computations. A
	 * typical scenario, is when the jobs are processed from a fixed pool of
	 * threads. In this case, if {@link #canProcess()} returns <tt>false</tt>,
	 * and there are other threads processing the jobs, the thread is put to
	 * sleep. The method {@link #notifyCanProcess()} then can help to wake up
	 * all sleeping threads, so the threads will resume the computation (execute
	 * {@link #process()}) when new computational power can be required.
	 */
	public void notifyCanProcess();

	/**
	 * This function is called with the context when it is created
	 * 
	 * @param context
	 */
	public void notifyCreated(SaturatedClassExpression context);

	/**
	 * This function called whenever a saturation for the context has changed
	 * 
	 * @param context
	 */
	public void notifyMofidified(SaturatedClassExpression context);

}
