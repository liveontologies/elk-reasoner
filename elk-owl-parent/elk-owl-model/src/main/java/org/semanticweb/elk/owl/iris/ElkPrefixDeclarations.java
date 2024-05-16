/*
 * #%L
 * ELK OWL Object Interfaces
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
package org.semanticweb.elk.owl.iris;

/**
 * @author Frantisek Simancik
 * 
 */
public interface ElkPrefixDeclarations {
	/**
	 * Add prefix declaration. Rejects if a prefix with the same name has
	 * already been declared.
	 * 
	 * @param prefix
	 *            the prefix to be declared.
	 * @return {@code true} if the prefix has been added and {@code false} if
	 *         the prefix was already registered
	 */
	public boolean addPrefix(ElkPrefix prefix);

	/**
	 * @param prefixName
	 *            the name of the prefix to be retrieved
	 * @return The ElkPrefix associated with the prefixName or {@code null} if
	 *         it does not exist.
	 */
	public ElkPrefix getPrefix(String prefixName);

}
