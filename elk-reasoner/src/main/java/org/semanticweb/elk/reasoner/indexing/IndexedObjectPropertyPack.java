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

package org.semanticweb.elk.reasoner.indexing;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.reasoner.saturation.SaturatedObjectProperty;
import org.semanticweb.elk.syntax.ElkObjectProperty;
import org.semanticweb.elk.util.HashGenerator;

/**
 * Represents all occurrences of an ElkObjectPropertyExpression in an ontology.
 * To this end, objects of this class keeps a list of super property
 * expressions. The data structures are optimized for quickly retrieving the
 * relevant relationships during inferencing.
 * 
 * This class is mainly a data container that provides direct public access to
 * its content. The task of updating index structures consistently in a global
 * sense is left to callers.
 * 
 * @author Frantisek Simancik
 * @author Markus Kroetzsch
 */

public class IndexedObjectPropertyPack
		implements IndexedObjectProperty {
	
	protected final ElkObjectProperty elkObjectProperty;

	protected List<IndexedObjectProperty> toldSubObjectProperties;

	protected List<IndexedObjectProperty> toldSuperObjectProperties;

	protected boolean isTransitive;
	
	protected SaturatedObjectProperty saturatedObjectProperty;


	/**
	 * Creates an object representing the given ElkObjectProperty.
	 * 
	 * @param elkObjectPropertyExpression
	 */
	IndexedObjectPropertyPack(ElkObjectProperty eop) {
		elkObjectProperty = eop;
	}

	
	public ElkObjectProperty getElkObjectProperty() {
		return elkObjectProperty;
	}
	
	
	public List<IndexedObjectProperty> getToldSubObjectProperties() {
		return toldSubObjectProperties;
	}

	
	public List<IndexedObjectProperty> getToldSuperObjectProperties() {
		return toldSuperObjectProperties;
	}

	
	public boolean isTransitive() {
		return isTransitive;
	}
	
	
	public SaturatedObjectProperty getSaturatedObjectProperty() {
		return saturatedObjectProperty;
	}
	
	
	public void setSaturatedObjectProperty(SaturatedObjectProperty sop) {
		saturatedObjectProperty = sop;
	}


	void addToldSubObjectProperty(IndexedObjectProperty iop) {
		if (toldSubObjectProperties == null)
			toldSubObjectProperties = new ArrayList<IndexedObjectProperty> (1);
		toldSubObjectProperties.add(iop);
	}

		
	void addToldSuperObjectProperty(IndexedObjectProperty iop) {
		if (toldSuperObjectProperties == null)
			toldSuperObjectProperties = new ArrayList<IndexedObjectProperty> (1);
		toldSuperObjectProperties.add(iop);
	}
		
	
	void setTransitive() {
		isTransitive = true;
	}
		
	
	/**
	 * Represent the object's ElkObjectProperty as a string. This implementation
	 * reflects the fact that we generally consider only one
	 * IndexedObjectProperty for each ElkObjectPropertyExpression.
	 * 
	 * @return string representation
	 */
	@Override
	public String toString() {
		return "[" + elkObjectProperty.toString() + "]";
	}
	
		
	/** Hash code for this object. */
	private final int hashCode_ = HashGenerator.generateNextHashCode();

	
	/**
	 * Get an integer hash code to be used for this object.
	 * 
	 * @return integer hash code
	 */
	@Override
	public final int hashCode() {
		return hashCode_;
	}
	
}