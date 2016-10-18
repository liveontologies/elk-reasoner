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

import org.semanticweb.elk.testing.TestOutput;

/**
 * Specific steps of generic tests are delegated to instances of this interface.
 * 
 * @author Peter Skocovsky
 *
 * @param <AO>
 *            The type of actual test output.
 */
public interface ReasoningTestDelegate<AO extends TestOutput> {

	/**
	 * Called before the test is run.
	 * 
	 * @throws Exception
	 */
	void before() throws Exception;

	/**
	 * Computes and returns the actual test output.
	 * 
	 * @return the actual test output
	 * @throws Exception
	 */
	AO getActualOutput() throws Exception;

	/**
	 * Called after the test is run.
	 */
	void after();

}
