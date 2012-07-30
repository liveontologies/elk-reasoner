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

package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectPropertyVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;

/**
 * Represents all occurrences of an ElkObjectProperty in an ontology.
 * 
 * @author Frantisek Simancik
 * @author Markus Kroetzsch
 */

public class IndexedObjectProperty extends IndexedPropertyChain {
	protected final ElkObjectProperty elkObjectProperty;

	/**
	 * Correctness of axioms deletions requires that toldSubProperties is a
	 * List.
	 */
	protected List<IndexedPropertyChain> toldSubProperties;

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
	 * @return All told sub object properties of this object property, possibly
	 *         null.
	 */
	@Override
	public List<IndexedPropertyChain> getToldSubProperties() {
		return toldSubProperties;
	}

	protected void addToldSubProperty(IndexedPropertyChain subObjectProperty) {
		if (toldSubProperties == null)
			toldSubProperties = new ArrayList<IndexedPropertyChain>(1);
		toldSubProperties.add(subObjectProperty);
	}

	/**
	 * @param subObjectProperty
	 * @return true if succesfully removed
	 */
	protected boolean removeToldSubProperty(
			IndexedPropertyChain subObjectProperty) {
		boolean success = false;
		if (toldSubProperties != null) {
			success = toldSubProperties.remove(subObjectProperty);
			if (toldSubProperties.isEmpty())
				toldSubProperties = null;
		}
		return success;
	}

	@Override
	protected void updateOccurrenceNumber(int increment) {
		occurrenceNo += increment;
	}

	public <O> O accept(IndexedObjectPropertyVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(IndexedPropertyChainVisitor<O> visitor) {
		return visitor.visit(this);
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
		return '<' + getElkObjectProperty().getIri().getFullIriAsString() + '>';
	}

}