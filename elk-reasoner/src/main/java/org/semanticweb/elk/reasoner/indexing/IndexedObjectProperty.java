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
import java.util.concurrent.atomic.AtomicReference;

import org.semanticweb.elk.reasoner.saturation.SaturatedObjectProperty;
import org.semanticweb.elk.syntax.ElkObjectProperty;
import org.semanticweb.elk.util.HashGenerator;

/**
 * Represents all occurrences of an ElkObjectPropertyExpression in an ontology.
 * To this end, objects of this class keeps a list of sub and super property
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

public class IndexedObjectProperty {
	
	protected final ElkObjectProperty elkObjectProperty;
	protected List<IndexedObjectProperty> toldSubObjectProperties;
	protected List<IndexedObjectProperty> toldSuperObjectProperties;
	protected int isTransitive = 0;
	
	/**
	 * This counts how often this object occurred in the ontology.
	 */
	protected int occurrenceNo = 0;
	
	
	/**
	 * Creates an object representing the given ElkObjectProperty.
	 */
	protected IndexedObjectProperty(ElkObjectProperty elkObjectProperty) {
		this.elkObjectProperty = elkObjectProperty;
	}

	
	/**
	 * @return The represented object property expression.
	 */
	public ElkObjectProperty getElkObjectProperty() {
		return elkObjectProperty;
	}
	
	
	/**
	 * @return All told sub object properties of this object property, possibly null.
	 */
	public List<IndexedObjectProperty> getToldSubObjectProperties() {
		return toldSubObjectProperties;
	}

	
	/** 
	 * @return All told super object properties of this object property, possibly null.
	 */
	public List<IndexedObjectProperty> getToldSuperObjectProperties() {
		return toldSuperObjectProperties;
	}
	

	/**
	 * @return True if this object property is told transitive.
	 */
	public boolean isTransitive() {
		return isTransitive > 0;
	}


	protected void addToldSubObjectProperty(IndexedObjectProperty subObjectProperty) {
		if (toldSubObjectProperties == null)
			toldSubObjectProperties = new ArrayList<IndexedObjectProperty> (1);
		toldSubObjectProperties.add(subObjectProperty);
	}
	
	protected boolean removeToldSubObjectProperty(IndexedObjectProperty subObjectProperty) {
		boolean success = false;
		if (toldSubObjectProperties != null) {
			success = toldSubObjectProperties.remove(subObjectProperty);
			if (toldSubObjectProperties.isEmpty())
				toldSubObjectProperties = null;
		}
		return success;
	}

	
	protected void addToldSuperObjectProperty(IndexedObjectProperty superObjectProperty) {
		if (toldSuperObjectProperties == null)
			toldSuperObjectProperties = new ArrayList<IndexedObjectProperty> (1);
		toldSuperObjectProperties.add(superObjectProperty);
	}

	protected boolean removeToldSuperObjectProperty(IndexedObjectProperty superObjectProperty) {
		boolean success = false;
		if (toldSuperObjectProperties != null) {
			success = toldSuperObjectProperties.remove(superObjectProperty);
			if (toldSuperObjectProperties.isEmpty())
				toldSuperObjectProperties = null;
		}
		return success;
	}

	
	protected void addTransitive() {
		isTransitive++;
	}
	
	protected boolean removeTransitive() {
		if (isTransitive > 0) {
			isTransitive--;
			return true;
		}
		return false;
	}

	
	
	
	protected final AtomicReference<SaturatedObjectProperty> saturated =
		new AtomicReference<SaturatedObjectProperty> ();
	
	/**
	 * @return The corresponding saturated object property, 
	 * null if none was assigned.
	 */
	public SaturatedObjectProperty getSaturated() {
		return saturated.get();
	}
	
	
	/**
	 * Sets the corresponding saturated object property if none
	 * was yet assigned. 
	 * 
	 * @return True if the operation succeeded. 
	 */
	public boolean setSaturated(SaturatedObjectProperty saturatedObjectProperty) {
		return saturated.compareAndSet(null, saturatedObjectProperty);
	}
	
	
	/**
	 * Resets the corresponding saturated object property to null.  
	 */
	public void resetSaturated() {
		saturated.set(null);
	}

		

	
	
	/**
	 * Represent the object's ElkObjectProperty as a string. This implementation
	 * reflects the fact that we generally consider only one
	 * IndexedObjectProperty for each ElkObjectPropertyExpression.
	 * 
	 * @return String representation.
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
	 * @return Hash code.
	 */
	@Override
	public final int hashCode() {
		return hashCode_;
	}
	
}
