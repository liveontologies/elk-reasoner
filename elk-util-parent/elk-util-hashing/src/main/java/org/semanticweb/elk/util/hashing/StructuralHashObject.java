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
package org.semanticweb.elk.util.hashing;

/**
 * General interface for any object that can be hashed. Used to avoid building
 * lists of has codes when invoking HashGenerator functions on lists of objects.
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface StructuralHashObject {

	/**
	 * Get a structural hash code, i.e. a hash code that faithfully represents
	 * the relevant internal structure of an object.
	 * 
	 * @return structural hash code
	 */
	public int structuralHashCode();

}
