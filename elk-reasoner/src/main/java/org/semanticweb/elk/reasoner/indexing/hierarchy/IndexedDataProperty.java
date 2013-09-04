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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.reasoner.datatypes.index.AdaptableDatatypeIndex;
import org.semanticweb.elk.reasoner.datatypes.index.DatatypeIndex;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedDataPropertyVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectVisitor;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedDataProperty;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * TODO: documentation
 * 
 * @author Olexandr Pospishnyi
 */
public class IndexedDataProperty extends IndexedObject {

	/**
	 * The corresponding {@link ElkIri}
	 */
	private ElkIri dataPropertyIri;

	/**
	 * The list of super-properties of this data property
	 */
	private List<IndexedDataProperty> toldSuperProperties_;

	/**
	 * This counts how often this object occurred in the ontology.
	 */
	protected int occurrenceNo = 0;

	/**
	 * TODO: documentation
	 */
	private final DatatypeIndex datatypeIndex_;

	/**
	 * the reference to a {@link SaturatedDataProperty} assigned to this
	 * {@link IndexedDataProperty}
	 */
	private final AtomicReference<SaturatedDataProperty> saturated_ = new AtomicReference<SaturatedDataProperty>();

	public IndexedDataProperty(ElkDataProperty property) {
		this.dataPropertyIri = property.getIri();
		this.datatypeIndex_ = new AdaptableDatatypeIndex();
	}

	/**
	 * TODO: documentation
	 */
	public ElkIri getIri() {
		return this.dataPropertyIri;
	}

	/**
	 * @return All told super properties of this {@link IndexedDataProperty}, or
	 *         {@code null} if none is assigned
	 */
	public List<IndexedDataProperty> getToldSuperProperties() {
		return toldSuperProperties_;
	}

	/**
	 * TODO: documentation
	 */
	protected void addDatatypeExpression(IndexedDatatypeExpression ide) {
		datatypeIndex_.addDatatypeExpression(ide);
	}

	/**
	 * TODO: documentation
	 */
	protected void addDatatypeExpressions(DatatypeIndex index) {
		index.appendTo(datatypeIndex_);
	}

	/**
	 * TODO: documentation
	 */
	protected boolean removeDatatypeExpression(IndexedDatatypeExpression ide) {
		return datatypeIndex_.removeDatatypeExpression(ide);
	}

	/**
	 * TODO: documentation
	 */
	public Collection<IndexedDatatypeExpression> getSubsumersFor(
			IndexedDatatypeExpression ide) {
		return datatypeIndex_.getSubsumersFor(ide);
	}

	/**
	 * TODO: clarify: assigned by whom?
	 * 
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
	 * TODO: clarify: assigned by whom?
	 * 
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

	public <O> O accept(IndexedDataPropertyVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(IndexedObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

	protected void updateOccurrenceNumber(int increment) {
		occurrenceNo += increment;
	}

	@Override
	public boolean occurs() {
		return occurrenceNo > 0;
	}

	@Override
	public String toStringStructural() {
		return '<' + dataPropertyIri.getFullIriAsString() + '>';
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
