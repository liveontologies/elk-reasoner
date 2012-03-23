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

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;
import org.semanticweb.elk.reasoner.rules.SaturatedPropertyChain;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Represents all occurrences of an ElkSubObjectPropertyExpression in an
 * ontology. To this end, objects of this class keeps a list of sub and super
 * property expressions. The data structures are optimized for quickly
 * retrieving the relevant relationships during inferencing.
 * 
 * This class is mainly a data container that provides direct public access to
 * its content. The task of updating index structures consistently in a global
 * sense is left to callers.
 * 
 * @author Frantisek Simancik
 * @author Markus Kroetzsch
 */

public abstract class IndexedPropertyChain {

	/**
	 * Correctness of axioms deletions requires that toldSuperProperties is a
	 * List.
	 */
	protected List<IndexedObjectProperty> toldSuperProperties;

	/**
	 * @return All told super object properties of this object property,
	 *         possibly null.
	 */
	public List<IndexedObjectProperty> getToldSuperProperties() {
		return toldSuperProperties;
	}

	/**
	 * @return All told sub object properties of this object property, possibly
	 *         null.
	 */
	public abstract List<IndexedPropertyChain> getToldSubProperties();

	/**
	 * This counts how often this object occurred in the ontology.
	 */
	protected int occurrenceNo = 0;

	protected final AtomicReference<SaturatedPropertyChain> saturated = new AtomicReference<SaturatedPropertyChain>();

	/**
	 * Non-recursively. The recursion is implemented in indexing visitors.
	 */
	protected void updateOccurrenceNumber(int increment) {
		occurrenceNo += increment;
	}

	public boolean occurs() {
		return occurrenceNo > 0;
	}

	/**
	 * @return The corresponding SaturatedObjecProperty, null if none was
	 *         assigned.
	 */
	public SaturatedPropertyChain getSaturated() {
		return saturated.get();
	}

	/**
	 * Sets the corresponding SaturatedObjectProperty if none was yet assigned.
	 * 
	 * @return True if the operation succeeded.
	 */
	public boolean setSaturated(SaturatedPropertyChain saturatedObjectProperty) {
		return saturated.compareAndSet(null, saturatedObjectProperty);
	}

	/**
	 * Resets the corresponding SaturatedObjectProperty to null.
	 */
	public void resetSaturated() {
		saturated.set(null);
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

	public abstract <O> O accept(IndexedPropertyChainVisitor<O> visitor);

	@Override
	public abstract String toString();

}
