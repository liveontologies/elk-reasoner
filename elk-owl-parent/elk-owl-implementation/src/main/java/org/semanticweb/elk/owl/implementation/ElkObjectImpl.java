/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkObject.java 265 2011-08-04 09:45:18Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/ElkObject.java $
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
package org.semanticweb.elk.owl.implementation;

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.semanticweb.elk.util.hashing.StructuralHashObject;

/**
 * Basic implementation of hashable objects in ELK, typically syntactic
 * structures like axioms or class expressions. ElkObjects are immutable and
 * their equality is based on their actual structural content.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 */
public abstract class ElkObjectImpl implements ElkObject {

	/**
	 * Structural hash code for this object. Must be initialized on
	 * construction.
	 */
	protected int structuralHashCode;

	public final int structuralHashCode() {
		return structuralHashCode;
	}

	public final int hashCode() {
		return structuralHashCode;
	}

	public final boolean equals(Object obj) {
		return structuralEquals(obj);
	}

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
