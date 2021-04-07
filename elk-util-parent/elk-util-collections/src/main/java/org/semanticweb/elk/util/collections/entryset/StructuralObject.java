package org.semanticweb.elk.util.collections.entryset;

/*-
 * #%L
 * ELK Utilities Collections
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2021 Department of Computer Science, University of Oxford
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
 * An object that can be structurally compared to other objects. Structural
 * equality {@link #structuralEquals(Object)} complements the object equality
 * {@link Object#equals(Object)}, and can be used to identify not necessarily
 * equal objects that consist of (structurally) equal parts. This is in a
 * similar way that {@link Object#equals(Object)} complements {@code ==}. The
 * main purpose is to use an additional comparison functions (which is not used
 * in standard collections) to identify structurally equal objects, which can be
 * used, for example, for
 * <a href="https://en.wikipedia.org/wiki/Memoization">memoization</a>.
 * 
 * 
 * Unlike {@link Object#equals(Object)}, the method
 * {@link #structuralEquals(Object)} does not return a boolean but either the
 * input object, if the equality holds, or {@code null} if it does not hold.
 * 
 * As for the object equality, structural equality must be symmetric and
 * transitive. The structural equality must extend equality in the same way as
 * {@link Object#equals(Object)} extends {@code ==}. That is, any two equal
 * structural objects must be structurally equal.
 * 
 * In addition to structural equality, there is also a structural hash code
 * {@link #structuralHashCode()} that is similar to the object hash code
 * {@link Object#hashCode()}. As usual, the structural hash code must be
 * compatible with structural equality, that is, if two object are structurally
 * equal, they must have the same hash code.
 * 
 * @author Yevgeny Kazakov
 *
 */
public interface StructuralObject {

	/**
	 * Performs the structural equality comparison of this object with the given
	 * object. If the equality holds, then the input object must be also a
	 * {@link StructuralObject}, and it is returned in the output.
	 * 
	 * @param other
	 *            the object with which the comparison is performed
	 * @return the object if the structural equality test holds, or {@code null}
	 *         if the structural equality test does not hold
	 * 
	 * @see Object#equals(Object)
	 */
	StructuralObject structuralEquals(Object other);

	/**
	 * Computes the structural hash code of this object, which is an integer
	 * uniquely determined by the structure of this object. The same integer
	 * value should be returned for any two structurally equal objects (in terms
	 * of {@link #structuralEquals(Object)}.
	 * 
	 * @return a number that is uniquely determined by the structure of this
	 *         object.
	 * 
	 * @see Object#hashCode()
	 */
	int structuralHashCode();

}
