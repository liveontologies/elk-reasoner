/*-
 * #%L
 * ELK Utilities Collections
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.util.collections;

public class Evictors {

	private Evictors() {
		// Forbid instantiation of an utility class.
	}

	static String[] parseArgs(final String value, final Class<?> clazz,
			final Integer argCount) throws IllegalArgumentException {
		final String trimmed = value.trim();
		final String argTuple;
		if (trimmed.startsWith(clazz.getName())) {
			argTuple = trimmed.substring(clazz.getName().length()).trim();
		} else if (trimmed.startsWith(clazz.getSimpleName())) {
			argTuple = trimmed.substring(clazz.getSimpleName().length()).trim();
		} else {
			throw new IllegalArgumentException(
					"The value is not if this type! value: " + value);
		}
		if (!argTuple.startsWith("(") || !argTuple.endsWith(")")) {
			throw new IllegalArgumentException(
					"Error when parsing argument tuple! value: " + value);
		}
		final String[] args = argTuple.substring(1, argTuple.length() - 1)
				.split(",");
		if (argCount != null && args.length != argCount) {
			throw new IllegalArgumentException(
					"Expected " + argCount + " arguments, but got "
							+ args.length + "! value: " + value);
		}
		return args;
	}

}
