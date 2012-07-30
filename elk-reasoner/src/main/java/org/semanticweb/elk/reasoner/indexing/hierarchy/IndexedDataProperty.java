/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
import java.util.concurrent.atomic.AtomicReference;

import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedDataPropertyVisitor;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedDataProperty;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * 
 * @author Pospishnyi Olexandr
 */
public class IndexedDataProperty {

	/**
	 * The corresponding {@link ElkDataProperty}
	 */
	private ElkDataProperty property_;

	/**
	 * The list of super-properties of this data property
	 */
	private List<IndexedDataProperty> toldSuperProperties_;

	/**
	 * The list of negative occurrences of {@link IndexedDatatypeExpression}s
	 * with this {@link ElkDataProperty}
	 */
	private List<IndexedDatatypeExpression> negativeDatatypeExpressions_;

	/**
	 * This counts how often this object occurred in the ontology.
	 */
	protected int occurrenceNo = 0;

	/**
	 * the reference to a {@link SaturatedDataProperty} assigned to this
	 * {@link IndexedDataProperty}
	 */
	private final AtomicReference<SaturatedDataProperty> saturated_ = new AtomicReference<SaturatedDataProperty>();

	public IndexedDataProperty(ElkDataProperty property) {
		this.property_ = property;
	}

	public ElkIri getIri() {
		return property_.getIri();
	}

	public ElkDataProperty getProperty() {
		return property_;
	}

	/**
	 * @return All told super properties of this {@link IndexedDataProperty}, or
	 *         {@code null} if none is assigned
	 */
	public List<IndexedDataProperty> getToldSuperProperties() {
		return toldSuperProperties_;
	}

	/**
	 * @return all negative occurrences of {@link IndexedDatatypeExpression}s
	 *         with this {@link ElkDataProperty}, or {@code null} if there are
	 *         none
	 */
	public List<IndexedDatatypeExpression> getNegativeDatatypeExpressions() {
		return negativeDatatypeExpressions_;
	}

	/**
	 * @return The corresponding {@code SaturatedDataProperty} assigned to this
	 *         {@link IndexedDataProperty}, or {@code null} if none was
	 *         assigned.
	 */
	public SaturatedDataProperty getSaturated() {
		return saturated_.get();
	}

	/**
	 * Sets the corresponding {@code SaturatedDataProperty} of this
	 * {@link IndexedDataProperty} if none was yet assigned.
	 * 
	 * @param saturatedDataProperty
	 *            assign the given {@link SaturatedDataProperty} to this
	 *            {@link IndexedDataProperty}
	 * 
	 * @return {@code true} if the operation succeeded.
	 */
	public boolean setSaturated(SaturatedDataProperty saturatedDataProperty) {
		return saturated_.compareAndSet(null, saturatedDataProperty);
	}

	/**
	 * Resets the assigned {@code SaturatedDataProperty} to {@code null}.
	 */
	public void resetSaturated() {
		saturated_.set(null);
	}

	protected void addToldSuperProperty(IndexedDataProperty subProperty) {
		if (toldSuperProperties_ == null)
			toldSuperProperties_ = new ArrayList<IndexedDataProperty>(1);
		toldSuperProperties_.add(subProperty);
	}

	protected boolean removeToldSuperProperty(IndexedDataProperty subProperty) {
		boolean success = false;
		if (toldSuperProperties_ != null) {
			success = toldSuperProperties_.remove(subProperty);
			if (toldSuperProperties_.isEmpty())
				toldSuperProperties_ = null;
		}
		return success;
	}

	protected void addNegativeDatatypeExpression(
			IndexedDatatypeExpression datatypeExpression) {
		if (negativeDatatypeExpressions_ == null)
			negativeDatatypeExpressions_ = new ArrayList<IndexedDatatypeExpression>(
					1);
		negativeDatatypeExpressions_.add(datatypeExpression);
	}

	protected boolean removeNegativeDatatypeExpression(
			IndexedDatatypeExpression datatypeExpression) {
		boolean success = false;
		if (negativeDatatypeExpressions_ != null) {
			success = negativeDatatypeExpressions_.remove(datatypeExpression);
			if (negativeDatatypeExpressions_.isEmpty())
				negativeDatatypeExpressions_ = null;
		}
		return success;
	}

	public <O> O accept(IndexedDataPropertyVisitor<O> visitor) {
		return visitor.visit(this);
	}

	protected void updateOccurrenceNumber(int increment) {
		occurrenceNo += increment;
	}

	public boolean occurs() {
		return occurrenceNo > 0;
	}

	@Override
	public String toString() {
		return '<' + property_.getIri().getFullIriAsString() + '>';
	}

	/**
	 * Hash code for this object.
	 */
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
