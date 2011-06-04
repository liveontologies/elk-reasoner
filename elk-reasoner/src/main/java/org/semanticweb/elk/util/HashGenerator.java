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

	/**
	 * Compute a hash code from an initial hash code and a list of objects that
	 * supply further hashes. The computed hash takes the order of inputs into
	 * account.
	 * 
	 * @param initialHash
	 * @param hashObjects
	 * @return
	 */
	public static int computeListHash(int initialHash,
			StructuralHashObject... hashObjects) {
		int hash = initialHash;
		hash += (hash << 10);
		hash ^= (hash >> 6);

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

	/**
	 * Compute a hash code from an initial hash code and a list of objects that
	 * supply further hashes. The computed hash does not depend on the order of
	 * the list, but is combined with the initialHash in an order-dependent way.
	 * 
	 * @param initialHash
	 * @param hashObjects
	 * @return
	 */
	public static int computeSetHash(int initialHash,
			StructuralHashObject... hashObjects) {
		int hash = initialHash;
		hash += (hash << 10);
		hash ^= (hash >> 6);

		int setHash = 0;
		for (StructuralHashObject o : hashObjects) {
			setHash ^= o.structuralHashCode();
		}

		hash += setHash;
		hash += (hash << 10);
		hash ^= (hash >> 6);

		hash += (hash << 3);
		hash ^= (hash >> 11);
		hash += (hash << 15);
		return hash;
	}

	/**
	 * Combine two hash codes into one in a way that does not depend on their
	 * order.
	 * 
	 * @param hash1
	 * @param hash2
	 * @return
	 */
	public static int combineSetHash(int hash1, int hash2) {
		return hash1 ^ hash2;
	}

}
