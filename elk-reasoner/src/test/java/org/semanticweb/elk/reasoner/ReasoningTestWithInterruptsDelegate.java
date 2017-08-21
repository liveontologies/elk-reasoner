/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
 * A test delegate for tests with interrupts.
 * 
 * @author Peter Skocovsky
 *
 * @param <O>
 *            The type of test output.
 */
public interface ReasoningTestWithInterruptsDelegate<O>
		extends ReasoningTestDelegate<O> {

	/**
	 * @return The probability that the reasoning is interrupted within the
	 *         interval provided by {@link #getInterruptionIntervalNanos()}.
	 */
	double getInterruptionChance();

	/**
	 * @return The interval in nanoseconds within which the interruption chance
	 *         is {@link #getInterruptionChance()}.
	 */
	long getInterruptionIntervalNanos();

	/**
	 * Called at the beginning of the test with interrupts.
	 * 
	 * @throws Exception
	 */
	void initWithInterrupts() throws Exception;

	/**
	 * @return Class of exception that is thrown when the process executed in
	 *         {@link #getActualOutput()} is interrupted.
	 */
	Class<? extends Exception> getInterruptionExceptionClass();

}
