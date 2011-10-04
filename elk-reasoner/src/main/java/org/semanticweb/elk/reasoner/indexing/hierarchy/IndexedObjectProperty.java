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
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectPropertyVisitable;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectPropertyVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedSubPropertyExpressionVisitor;

/**
 * Represents all occurrences of an ElkObjectProperty in an ontology.
 * 
 * @author Frantisek Simancik
 * @author Markus Kroetzsch
 */

public class IndexedObjectProperty extends IndexedSubPropertyExpression implements
		IndexedObjectPropertyVisitable {
	protected final ElkObjectProperty representative;

	protected List<IndexedObjectProperty> toldSubObjectProperties;
	protected List<IndexedObjectProperty> toldSuperObjectProperties;

	/**
	 * Creates an object representing the given ElkObjectProperty.
	 */
	protected IndexedObjectProperty(ElkObjectProperty elkObjectProperty) {
		representative = elkObjectProperty;
	}

	/**
	 * @return The represented object property expression.
	 */
	public ElkObjectProperty getElkObjectProperty() {
		return representative;
	}

	/**
	 * @return All told sub object properties of this object property, possibly
	 *         null.
	 */
	public List<IndexedObjectProperty> getToldSubObjectProperties() {
		return toldSubObjectProperties;
	}

	/**
	 * @return All told super object properties of this object property,
	 *         possibly null.
	 */
	public List<IndexedObjectProperty> getToldSuperObjectProperties() {
		return toldSuperObjectProperties;
	}

	protected void addToldSubObjectProperty(
			IndexedObjectProperty subObjectProperty) {
		if (toldSubObjectProperties == null)
			toldSubObjectProperties = new ArrayList<IndexedObjectProperty>(1);
		toldSubObjectProperties.add(subObjectProperty);
	}

	protected boolean removeToldSubObjectProperty(
			IndexedObjectProperty subObjectProperty) {
		boolean success = false;
		if (toldSubObjectProperties != null) {
			success = toldSubObjectProperties.remove(subObjectProperty);
			if (toldSubObjectProperties.isEmpty())
				toldSubObjectProperties = null;
		}
		return success;
	}

	protected void addToldSuperObjectProperty(
			IndexedObjectProperty superObjectProperty) {
		if (toldSuperObjectProperties == null)
			toldSuperObjectProperties = new ArrayList<IndexedObjectProperty>(1);
		toldSuperObjectProperties.add(superObjectProperty);
	}

	protected boolean removeToldSuperObjectProperty(
			IndexedObjectProperty superObjectProperty) {
		boolean success = false;
		if (toldSuperObjectProperties != null) {
			success = toldSuperObjectProperties.remove(superObjectProperty);
			if (toldSuperObjectProperties.isEmpty())
				toldSuperObjectProperties = null;
		}
		return success;
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
		return "[" + getElkObjectProperty().toString() + "]";
	}

	public <O> O accept(IndexedObjectPropertyVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(IndexedSubPropertyExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
