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
package org.semanticweb.elk.reasoner.query;

import org.junit.Assert;
import org.semanticweb.elk.reasoner.incremental.IncrementalReasoningCorrectnessTestWithInterrupts;
import org.semanticweb.elk.reasoner.incremental.IncrementalReasoningTestWithInterruptsDelegate;
import org.semanticweb.elk.testing.DiffableOutput;
import org.semanticweb.elk.testing.TestManifest;

public abstract class BaseIncrementalQueryTest<Q, A, O extends DiffableOutput<?, O>>
		extends
		IncrementalReasoningCorrectnessTestWithInterrupts<QueryTestInput<Q>, A, O, IncrementalReasoningTestWithInterruptsDelegate<A, O>> {

	public BaseIncrementalQueryTest(
			final TestManifest<QueryTestInput<Q>> testManifest,
			final IncrementalReasoningTestWithInterruptsDelegate<A, O> testDelegate) {
		super(testManifest, testDelegate);
	}

	@Override
	protected void correctnessCheck(final O actualOutput,
			final O expectedOutput) throws Exception {

		boolean actualContainsAllExpected = actualOutput
				.containsAllElementsOf(expectedOutput);
		boolean expectedContainsAllActual = expectedOutput
				.containsAllElementsOf(actualOutput);
		if (actualContainsAllExpected && expectedContainsAllActual) {
			return;
		}
		// else
		final StringBuilder message = new StringBuilder(
				"Actual output is not equal to the expected output:");
		if (!actualContainsAllExpected) {
			actualOutput.reportMissingElementsOf(expectedOutput,
					getOutputListener("< ", message));
		}
		if (!expectedContainsAllActual) {
			expectedOutput.reportMissingElementsOf(actualOutput,
					getOutputListener("<>", message));
		}
		Assert.fail(message.toString());
	}

	private static <E> DiffableOutput.Listener<E> getOutputListener(
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
