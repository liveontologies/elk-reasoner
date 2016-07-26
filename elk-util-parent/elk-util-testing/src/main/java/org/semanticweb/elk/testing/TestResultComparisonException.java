/*
 * #%L
 * ELK Utilities for Testing
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
/**
 * 
 */
package org.semanticweb.elk.testing;

/**
 * Thrown if the actual test value does not match the expected test value as specified in the test manifest.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public class TestResultComparisonException extends AssertionError {

	private TestOutput expOutput;
	private TestOutput actualOutput;
	
	public TestResultComparisonException() {}
	
	public TestResultComparisonException(String msg, TestOutput expected, TestOutput actual) {
		super(msg);
		
		expOutput = expected;
		actualOutput = actual;
	}
	
	public TestOutput getExpectedOutput() {
		return expOutput;
	}

	public TestOutput getActualOutput() {
		return actualOutput;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
