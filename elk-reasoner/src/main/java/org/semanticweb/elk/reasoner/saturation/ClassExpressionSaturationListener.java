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
package org.semanticweb.elk.reasoner.saturation;

/**
 * The interface for listeners which can be used with
 * {@link ClassExpressionSaturationEngine}, which can implement method hooks for
 * triggering some actions
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <J>
 */
public interface ClassExpressionSaturationListener<J> {

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
	 * An callback hook to the method using which one can receive notifications
	 * about processed jobs to perform further actions, e.g., post-processing.
	 * If {@link #submit(job)} is called followed with {@link #process()}, it is
	 * guaranteed that {@link #notifyFinished(job)} will be called (perhaps from
	 * some other thread) before no instance of {@link #process()} is running.
	 * 
	 * @param job
	 *            the job that has been processed
	 * @throws InterruptedException
	 *             if interrupted during the notification
	 */
	public void notifyFinished(J job) throws InterruptedException;

}
