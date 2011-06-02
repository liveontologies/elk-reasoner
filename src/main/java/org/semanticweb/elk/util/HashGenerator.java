/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
 * @author Yevgeny Kazakov, Jun 2, 2011
 */
package org.semanticweb.elk.util;

/**
 * Class used to generate strong hash codes for newly created objects.
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class HashGenerator {

	/**
	 * The counter incremented with each generated hash code which will be used
	 * for generating a hash code
	 */
	static int counter = 0;

	/**
	 * Generates the next hash code.
	 * 
	 * @return the generated hash code.
	 */
	public static int generateNextHashCode() {
		// we use a strategy from the java.util.HashMap
		int h = ++counter;
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}
}
