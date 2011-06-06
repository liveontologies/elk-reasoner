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
 * @author Yevgeny Kazakov
 * 
 */

public abstract class ElkObject implements StructuralHashObject {
	protected static ElkObjectFactory factory = new WeakCanonicalSet();
	
	protected int structuralHashCode;

	public final int structuralHashCode() {
		return structuralHashCode;
	}
	
	public abstract boolean structuralEquals(ElkObject object);
	
	private final int hashCode_ = HashGenerator.generateNextHashCode();
	
	public final int hashCode() {
		return hashCode_;
	}

	public final boolean equals(Object obj) {
		return this == obj;
	}
	
	public abstract <O> O accept(ElkObjectVisitor<O> visitor);

	public static int computeCompositeHash(int constructorHash, List<? extends StructuralHashObject> subObjects) {
		return HashGenerator.combineListHash(constructorHash, HashGenerator.combineListHash(subObjects));
	}
}
