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
package org.semanticweb.elk.testing;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 * @param <I>
 * @param <EO>
 * @param <AO>
 * 
 */
public class BasicTestManifest<I extends TestInput, EO extends TestOutput, AO extends TestOutput>
		implements TestManifestWithOutput<I, EO, AO> {

	private final String name;
	private final I input;
	private final EO expOutput;

	public BasicTestManifest(String name, I input, EO expOutput) {
		this.name = name;
		this.input = input;
		this.expOutput = expOutput;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public I getInput() {
		return input;
	}

	@Override
	public EO getExpectedOutput() {
		return expOutput;
	}

	@Override
	public String toString() {
		return "Input: " + input + System.getProperty("line.separator")
				+ "Expected output: " + expOutput;
	}

	@Override
	public void compare(final AO actualOutput)
			throws TestResultComparisonException {
		if (expOutput == null ? actualOutput != null
				: !expOutput.equals(actualOutput)) {

			// @formatter:off
			final String message = "Actual output is not equal to the expected output\n"
					+ "Input: " + getInput().getName() + "\n"
					+ "expected:\n" + expOutput + "\n"
					+ "actual:\n" + actualOutput + "\n";
			// @formatter:on

			throw new TestResultComparisonException(message, expOutput,
					actualOutput);
		}
	}

}
