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
 *            The type of test input.
 * @param <O>
 *            The type of test output.
 */
public class BasicTestManifest<I extends TestInput, O>
		implements TestManifestWithOutput<I, O> {

	private final I input;
	private final O expOutput;

	public BasicTestManifest(I input, O expOutput) {
		this.input = input;
		this.expOutput = expOutput;
	}

	@Override
	public String getName() {
		return input.getName();
	}

	@Override
	public I getInput() {
		return input;
	}

	@Override
	public O getExpectedOutput() {
		return expOutput;
	}

	@Override
	public String toString() {
		return "Input: " + input + System.getProperty("line.separator")
				+ "Expected output: " + expOutput;
	}

	@Override
	public void compare(final O actualOutput)
			throws TestResultComparisonException {
		if (expOutput == null ? actualOutput != null
				: expOutput.hashCode() != actualOutput.hashCode()
						|| !expOutput.equals(actualOutput)) {
			
			final StringBuilder message = new StringBuilder(
					"Actual output is not equal to the expected output");
			message.append("\nInput: ").append(getInput().getName());
			appendDiff(actualOutput, message.append("\nDiff:\n"));
			appendOutput(expOutput, message.append("\nExpected:\n"));
			appendOutput(actualOutput, message.append("\nActual:\n"));

			throw new TestResultComparisonException(message.toString(),
					expOutput, actualOutput);
		}
	}

	/**
	 * Writes the difference of {@link #getExpectedOutput()} and the specified
	 * actual output to the provided {@link StringBuilder}.
	 * <p>
	 * Default implementation writes nothing.
	 * 
	 * @param actualOutput
	 * @param result
	 */
	protected void appendDiff(final O actualOutput,
			final StringBuilder result) {
		// Empty by default.
	}

	/**
	 * Writes the specified output to the provided {@link StringBuilder}.
	 * <p>
	 * Default implementation writes {@link #toString()} of the output.
	 * 
	 * @param output
	 * @param result
	 */
	protected void appendOutput(final O output, final StringBuilder result) {
		result.append(output);
	}

}
