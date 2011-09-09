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
package org.semanticweb.elk.util.hashing;

import java.util.Iterator;
import java.util.List;

/**
 * Class used to generate strong hash codes for newly created objects, and for
 * combining multiple hash codes into new ones in various ways.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 */
public class HashGenerator {

	/**
	 * The counter incremented with each generated hash code which will be used
	 * for generating a hash code by generateNextHashCode().
	 */
	private static int counter = 0;

	/**
	 * Generate a new "hash code" integer. Consecutive calls to this method
	 * create a sequence of integers with near-uniform distribution. These codes
	 * may be used as hash codes for objects that are managed in such a way that
	 * no two objects of different content can occur. Different hash codes then
	 * guarantee that the objects are not equal, even if the hash code does is
	 * not based on the hashed object at all. Obviously, hash codes of this form
	 * are not stable over multiple runs of the program, and can therefore not
	 * be used for comparing objects across runs (e.g. in testing). However,
	 * they can be useful in hash-based data structures.
	 * 
	 * @return the generated hash code
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
	 * If finalize is false, then the method as such represents an associative
	 * commutative hash, i.e. the return value can be combined with other
	 * set-based hash codes in any order without making a difference. If
	 * finalize is true, then the method combines its arguments with a
	 * commutative hash, but shuffles the overall result. The method as such
	 * then is neither an associative nor a commutative operation. This mode of
	 * operation should be used whenever no further elements are to be added to
	 * the set of hashes.
	 * 
	 * @param finalize
	 * @param hashes
	 * @return
	 */
	public static int combineMultisetHash(boolean finalize, int... hashes) {
		int hash = 0;
		for (int h : hashes) {
			hash = hash + h;
		}
		if (finalize) {
			hash = combineListHash(hash);
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
	 * If finalize is false, then the method as such represents an associative
	 * commutative hash, i.e. the return value can be combined with other
	 * set-based hash codes in any order without making a difference. If
	 * finalize is true, then the method combines its arguments with a
	 * commutative hash, but shuffles the overall result. The method as such
	 * then is neither an associative nor a commutative operation. This mode of
	 * operation should be used whenever no further elements are to be added to
	 * the set of hashes.
	 * 
	 * @param finalize
	 * @param hashObjects
	 * @return
	 */
	public static int combineMultisetHash(boolean finalize,
			Iterable<? extends StructuralHashObject> hashObjects) {
		return combineMultisetHash(finalize, hashObjects.iterator());
	}

	/**
	 * Combine the hash codes of a collection of structural hash objects with an
	 * associative commutative hash function. Associativity ensures that the
	 * result of this functions can be further combined with other hash codes
	 * for getting the same result as if all hash codes had been combined in one
	 * step.
	 * 
	 * If finalize is false, then the method as such represents an associative
	 * commutative hash, i.e. the return value can be combined with other
	 * set-based hash codes in any order without making a difference. If
	 * finalize is true, then the method combines its arguments with a
	 * commutative hash, but shuffles the overall result. The method as such
	 * then is neither an associative nor a commutative operation. This mode of
	 * operation should be used whenever no further elements are to be added to
	 * the set of hashes.
	 * 
	 * @param finalize
	 * @param hashObjects
	 * @return
	 */
	public static int combineMultisetHash(boolean finalize,
			Iterator<? extends StructuralHashObject> hashObjectIterator) {
		int hash = 0;
		while (hashObjectIterator.hasNext()) {
			hash += hashObjectIterator.next().structuralHashCode();
		}
		if (finalize) {
			hash = combineListHash(hash);
		}
		return hash;
	}

	/**
	 * Combine many hash codes into one in a way that depends on their order.
	 * 
	 * The current implementation is based on the Jenkins One-at-a-Time hash,
	 * see http://www.burtleburtle.net/bob/hash/doobs.html and also
	 * http://en.wikipedia.org/wiki/Jenkins_hash_function.
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
	 * The current implementation is based on the Jenkins One-at-a-Time hash,
	 * see http://www.burtleburtle.net/bob/hash/doobs.html and also
	 * http://en.wikipedia.org/wiki/Jenkins_hash_function.
	 * 
	 * @param hashObjects
	 * @return
	 */
	public static int combineListHash(
			List<? extends StructuralHashObject> hashObjects) {
		return combineListHash(hashObjects.iterator());
	}

	/**
	 * Combine the hash codes of a collection of structural hash objects into
	 * one in a way that depends on their order.
	 * 
	 * The current implementation is based on the Jenkins One-at-a-Time hash,
	 * see http://www.burtleburtle.net/bob/hash/doobs.html and also
	 * http://en.wikipedia.org/wiki/Jenkins_hash_function.
	 * 
	 * @param hashObjects
	 * @return
	 */
	public static int combineListHash(
			Iterator<? extends StructuralHashObject> hashObjectIterator) {
		int hash = 0;
		while (hashObjectIterator.hasNext()) {
			hash += hashObjectIterator.next().structuralHashCode();
			hash += (hash << 10);
			hash ^= (hash >> 6);
		}
		hash += (hash << 3);
		hash ^= (hash >> 11);
		hash += (hash << 15);
		return hash;
	}

}
