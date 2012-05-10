/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkClass;

/**
 * Basic interface for representing sets of equivalent classes with one
 * canonical representative.
 * 
 * @author Markus Kroetzsch
 */
public interface ClassNode {

	/**
	 * Get an unmodifiable set of ElkClass objects that this ClassNode
	 * represents.
	 * 
	 * @return collection of equivalent ElkClass objects
	 */
	public Set<ElkClass> getMembers();

	/**
	 * Get one ElkClass object to canonically represent the classes in this
	 * ClassNode. It is guaranteed that the least object is the least one
	 * according to the ordering defined by PredefinedElkIri.compare().
	 * 
	 * @return canonical ElkClass object
	 */
	public ElkClass getCanonicalMember();


}
