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
package org.semanticweb.elk.reasoner.indexing;

import java.util.List;

import org.semanticweb.elk.reasoner.saturation.SaturatedObjectProperty;
import org.semanticweb.elk.syntax.ElkObjectProperty;

/**
 * Represents all occurrences of an ElkObjectProperty in an ontology.
 * To this end, objects of this class keeps a list of sub- and super- 
 * object properties. The data structures are optimized for quickly retrieving
 * the relevant relationships during inferencing.
 * 
 * @author Frantisek Simancik
 * @author Markus Kroetzsch
 */

public interface IndexedObjectProperty {
	/**
	 * The represented object property expression.
	 */
	ElkObjectProperty getElkObjectProperty();
	
	/**
	 * List of all told subproperties of this object property.
	 */
	List<IndexedObjectProperty> getToldSubObjectProperties();

	/** 
	 * List of all told superproperties of this object property.
	 */
	List<IndexedObjectProperty> getToldSuperObjectProperties();

	/**
	 * True iff this object property is told transitive.
	 */
	boolean isTransitive();
	
	/**
	 * Gets the SaturatedObjectProperty of this object property,
	 * null if none was assigned.
	 */
	SaturatedObjectProperty getSaturatedObjectProperty();

	/**
	 * Sets the SaturatedObjectProperty of this object property. 
	 */
	void setSaturatedObjectProperty(SaturatedObjectProperty sop);
}
