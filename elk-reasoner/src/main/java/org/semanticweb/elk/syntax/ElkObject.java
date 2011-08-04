/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
 * @author Yevgeny Kazakov, Apr 8, 2011
 */
package org.semanticweb.elk.syntax;

import java.util.List;

import org.semanticweb.elk.util.HashGenerator;
import org.semanticweb.elk.util.StructuralHashObject;

/**
 * Basic implementation of hashable objects in ELK, typically syntactic
 * structures like axioms or class expressions. In addition to a structural hash
 * code that reflects the content of an ELKObject, this class also provides a
 * basic hash code that acts as an ID for the actual Java object and which is
 * used in managing such objects.
 * 
 * @author Yevgeny Kazakov
 */
public abstract class ElkObject implements StructuralHashObject {
	protected static ElkObjectFactory factory = new WeakCanonicalSet();

	/**
	 * Structural hash code for this object. Must be initialized on
	 * construction.
	 */
	protected int structuralHashCode;

	public final int structuralHashCode() {
		return structuralHashCode;
	}

	/**
	 * Compare the structure of two ELKObjects and return true if they are
	 * structurally equivalent.
	 * 
	 * @param object
	 * @return True if objects are structurally equal
	 */
	public abstract boolean structuralEquals(ElkObject object);

	private final int hashCode_ = HashGenerator.generateNextHashCode();

	/**
	 * Get a hash code. The hash code is not related to the content of the
	 * object but it is uniformly distributed over the range of int, and it is
	 * never the same for two ELKObjects (modulo very rare hash collisions). So
	 * it can be used for filing objects in hash maps etc. but not to compare
	 * their content. Use structuralHashCode() or structuralEquals() for the
	 * latter.
	 * 
	 * @return integer hash code
	 */
	public final int hashCode() {
		return hashCode_;
	}

	/**
	 * Check if the object is identical to another object. This is not a
	 * structural comparison. For the latter, use structuralEquals().
	 * 
	 * @return true if objects are the same
	 */
	public final boolean equals(Object obj) {
		return this == obj;
	}

	public abstract <O> O accept(ElkObjectVisitor<O> visitor);

	/**
	 * Helper method to compute a composite hash from a constructor hash and a
	 * list of objects. The constructor hash seeds the hash generation and can
	 * be, e.g., the hash code of a string name that identifies the kind of
	 * hash. The list of hash objects represents the components of the structure
	 * that is represented by the hash. Their order is taken into account when
	 * computing the hash.
	 * 
	 * @param constructorHash
	 * @param subObjects
	 * @return hash code
	 */
	public static int computeCompositeHash(int constructorHash,
			List<? extends StructuralHashObject> subObjects) {
		return HashGenerator.combineListHash(constructorHash,
				HashGenerator.combineListHash(subObjects));
	}
}
