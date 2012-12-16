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
import java.util.Collection;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectPropertyVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitorEx;

/**
 * Represents all occurrences of an ElkObjectProperty in an ontology.
 * 
 * @author Frantisek Simancik
 * @author Markus Kroetzsch
 */

public class IndexedObjectProperty extends IndexedPropertyChain {
	protected final ElkObjectProperty elkObjectProperty;

	/**
	 * Collections of all binary role chains in which this
	 * {@link IndexedBinaryPropertyChain} occurs on the left.
	 */
	private Collection<IndexedBinaryPropertyChain> leftChains_;	
	
	/**
	 * Correctness of axioms deletions requires that toldSubProperties is a
	 * List.
	 */
	protected List<IndexedPropertyChain> toldSubProperties;

	/**
	 * Number of occurrence in reflexivity axioms
	 */
	int reflexiveAxiomOccurrenceNo = 0;

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
	 *         {@code null}.
	 */
	@Override
	public List<IndexedPropertyChain> getToldSubProperties() {
		return toldSubProperties;
	}

	/**
	 * @return All {@link IndexedBinaryPropertyChain}s in which this
	 *         {@link IndexedPropertyChain} occurs on the left, or {@code null}
	 *         if none is assigned
	 */
	public Collection<IndexedBinaryPropertyChain> getLeftChains() {
		return leftChains_;
	}

	/**
	 * Adds the given {@link IndexedBinaryPropertyChain} to the list of
	 * {@link IndexedBinaryPropertyChain} that contains this
	 * {@link IndexedPropertyChain} in the left-hand-side
	 * 
	 * @param chain
	 *            the {@link IndexedBinaryPropertyChain} to be added
	 */
	protected void addLeftChain(IndexedBinaryPropertyChain chain) {
		if (leftChains_ == null)
			leftChains_ = new ArrayList<IndexedBinaryPropertyChain>(1);
		leftChains_.add(chain);
	}

	/**
	 * Adds the given {@link IndexedBinaryPropertyChain} from the list of
	 * {@link IndexedBinaryPropertyChain} that contain this
	 * {@link IndexedPropertyChain} in the left-hand-side
	 * 
	 * @param chain
	 *            the {@link IndexedBinaryPropertyChain} to be removed
	 * @return {@code true} if successfully removed
	 */
	protected boolean removeLeftChain(IndexedBinaryPropertyChain chain) {
		boolean success = false;
		if (leftChains_ != null) {
			success = leftChains_.remove(chain);
			if (leftChains_.isEmpty())
				leftChains_ = null;
		}
		return success;
	}
	
	
	/**
	 * @return {@code true} if this object property occurs in a reflexivity axiom.
	 */
	public boolean isToldReflexive() {
		return reflexiveAxiomOccurrenceNo > 0;
	}

	protected void addToldSubObjectProperty(
			IndexedPropertyChain subObjectProperty) {
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

	@Override
	public <O, P> O accept(IndexedPropertyChainVisitorEx<O, P> visitor, P parameter) {
		return visitor.visit(this, parameter);
	}
}
