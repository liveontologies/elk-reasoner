/*-
 * #%L
 * ELK Reasoner Core
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

import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

public class EntailmentQueryTestManifest<Q, A>
		extends QueryTestManifest<Q, EntailmentQueryTestOutput<A>> {

	public EntailmentQueryTestManifest(final String name, final URL input,
			final Q query, final EntailmentQueryTestOutput<A> expectedOutput) {
		super(name, input, query, expectedOutput);
	}

	@Override
	protected void appendDiff(final EntailmentQueryTestOutput<A> actualOutput,
			final StringBuilder result) {
		final EntailmentQueryTestOutput<A> expOutput = getExpectedOutput();
		final Map<A, Boolean> expMap = expOutput == null
				? Collections.<A, Boolean> emptyMap() : expOutput.getOutput();
		final Map<A, Boolean> actMap = actualOutput == null
				? Collections.<A, Boolean> emptyMap()
				: actualOutput.getOutput();
		writeMapDiff(expMap, actMap, result);
	}

	private static <K, V> void writeMapDiff(final Map<K, V> expected,
			final Map<K, V> actual, final StringBuilder result) {

		for (final Entry<K, V> expectedEntry : expected.entrySet()) {
			final K key = expectedEntry.getKey();
			final V expectedValue = expectedEntry.getValue();
			final V actualValue = actual.get(expectedEntry.getKey());
			if (actualValue == null || !expectedValue.equals(actualValue)) {
				result.append(key).append(" -> expected: ")
						.append(expectedValue).append(", actual: ")
						.append(actualValue).append("\n");
			}
		}

		for (final Entry<K, V> actualEntry : actual.entrySet()) {
			// only keys missing in expected
			final K key = actualEntry.getKey();
			final V actualValue = actualEntry.getValue();
			final V expectedValue = expected.get(actualEntry.getKey());
			if (expectedValue == null) {
				result.append(key).append(" -> expected: ")
						.append(expectedValue).append(", actual: ")
						.append(actualValue).append("\n");
			}
		}

	}

	@Override
	protected void appendOutput(final EntailmentQueryTestOutput<A> output,
			final StringBuilder result) {
		// Do not write output!
	}

}
