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
 * @author Yevgeny Kazakov, May 15, 2011
 */
package org.semanticweb.elk.reasoner.classification;

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkClass;

/**
 * Classes that implement this interface represent a class hierarchy based on
 * ElkClass objects. For each such object, the taxonomy holds a ClassNode object
 * from which direct sub- and superclasses can be retrieved.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 * @author Frantisek Simancik
 * 
 */
public interface ClassTaxonomy {

	/**
	 * Returns the ClassNode containing the given elkClass as a member. Null if
	 * elkClass does not occur in the ontology.
	 */
	public ClassNode getNode(ElkClass elkClass);

	/**
	 * Obtain an unmodifiable List of all nodes in this ClassTaxonomy.
	 * 
	 * @return an unmodifiable Collection
	 */
	public List<ClassNode> getNodes();

}
