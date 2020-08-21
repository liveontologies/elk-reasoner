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
public class BasicTestManifest<I extends TestInput, O extends DiffableOutput<?, O>>
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
		boolean actualContainsAllExpected = actualOutput
				.containsAllElementsOf(expOutput);
		boolean expectedContainsAllActual = expOutput
				.containsAllElementsOf(actualOutput);
		if (actualContainsAllExpected && expectedContainsAllActual) {
			return;
		}
		// else
		final StringBuilder message = new StringBuilder(
				"Actual output is not equal to the expected output:");
		if (!actualContainsAllExpected) {
			actualOutput.reportMissingElementsOf(expOutput,
					getPrintingListener("< ", message));
		}
		if (!expectedContainsAllActual) {
			expOutput.reportMissingElementsOf(actualOutput,
					getPrintingListener("< ", message));
		}
		throw new TestResultComparisonException(message.toString(), expOutput,
				actualOutput);
	}

	static <E> DiffableOutput.Listener<E> getPrintingListener(
			final String prefix, final StringBuilder message) {
		return new DiffableOutput.Listener<E>() {

			@Override
			public void missing(E element) {
				message.append('\n');
				message.append(prefix);
				message.append(element);
			}
		};
	}

}
