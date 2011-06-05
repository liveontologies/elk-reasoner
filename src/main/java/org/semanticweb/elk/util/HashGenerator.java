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

import java.util.Collection;
import java.util.List;

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
	private static int counter = 0;

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

	/**
	 * Combine many hash codes with an associative commutative hash function.
	 * Associativity ensures that the result of this functions can be further
	 * combined with other hash codes for getting the same result as if all hash
	 * codes had been combined in one step.
	 * 
	 * @param hashes
	 * @return
	 */
	public static int combineSetHash(int... hashes) {
		int hash = 0;
		for (int h : hashes) {
			hash = hash + h;
		}
		return hash;
	}

	/**
	 * Combine the hash codes of a collection of structural hash objects with an
	 * associative commutative hash function. Associativity ensures that the
	 * result of this functions can be further combined with other hash codes
	 * for getting the same result as if all hash codes had been combined in one
	 * step.
	 * 
	 * @param hashObjects
	 * @return
	 */
	public static int combineSetHash(
			Collection<? extends StructuralHashObject> hashObjects) {
		int hash = 0;
		for (StructuralHashObject o : hashObjects) {
			hash += o.structuralHashCode();
		}
		return hash;
	}

	/**
	 * Combine many hash codes into one in a way that depends on their order.
	 * 
	 * @param hashes
	 * @return
	 */
	public static int combineListHash(int... hashes) {
		int hash = 0;
		for (int h : hashes) {
			hash += h;
			hash += (hash << 10);
			hash ^= (hash >> 6);
		}
		hash += (hash << 3);
		hash ^= (hash >> 11);
		hash += (hash << 15);

		return hash;
	}

	/**
	 * Combine the hash codes of a collection of structural hash objects into
	 * one in a way that depends on their order.
	 * 
	 * @param hashObjects
	 * @return
	 */
	public static int combineListHash(
			List<? extends StructuralHashObject> hashObjects) {
		int hash = 0;
		for (StructuralHashObject o : hashObjects) {
			hash += o.structuralHashCode();
			hash += (hash << 10);
			hash ^= (hash >> 6);
		}
		hash += (hash << 3);
		hash ^= (hash >> 11);
		hash += (hash << 15);
		return hash;
	}

}
