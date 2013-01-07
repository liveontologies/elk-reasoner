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
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitorEx;
import org.semanticweb.elk.reasoner.saturation.properties.IndexedPropertyChainSaturation;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedPropertyChain;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Represents all occurrences of an {@link ElkSubObjectPropertyExpression} in an
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
 * @author "Yevgeny Kazakov"
 * 
 */
public abstract class IndexedPropertyChain extends IndexedObject {

	protected static final Logger LOGGER_ = Logger
			.getLogger(IndexedPropertyChain.class);

	/**
	 * This counts how often this object occurred in the ontology.
	 */
	int occurrenceNo = 0;

	/** Hash code for this object. */
	private final int hashCode_ = HashGenerator.generateNextHashCode();

	/**
	 * The {@link SaturatedPropertyChain} object assigned to this
	 * {@link IndexedPropertyChain}
	 */
	private volatile SaturatedPropertyChain saturated_ = null;

	/**
	 * All told super object properties of this
	 * {@link IndexedBinaryPropertyChain}. Should be a List for correctness of
	 * axioms deletions (duplicates matter).
	 */
	private List<IndexedObjectProperty> toldSuperProperties_;

	/**
	 * Collections of all binary role chains in which this
	 * {@link IndexedBinaryPropertyChain} occurs on the right.
	 */
	private Collection<IndexedBinaryPropertyChain> rightChains_;

	/**
	 * @return All told super object properties of this
	 *         {@link IndexedBinaryPropertyChain}, or {@code null} if none is
	 *         assigned
	 */
	public List<IndexedObjectProperty> getToldSuperProperties() {
		return toldSuperProperties_ == null ? Collections
				.<IndexedObjectProperty> emptyList() : toldSuperProperties_;
	}

	/**
	 * @return All told sub object properties of this
	 *         {@link IndexedBinaryPropertyChain}, or {@code null} if none is
	 *         assigned
	 */
	public abstract List<IndexedPropertyChain> getToldSubProperties();

	/**
	 * @return All {@link IndexedBinaryPropertyChain}s in which this
	 *         {@link IndexedPropertyChain} occurs on right, or {@code null} if
	 *         none is assigned
	 */
	public Collection<IndexedBinaryPropertyChain> getRightChains() {
		return rightChains_;
	}

	/**
	 * Adds the given {@link IndexedObjectProperty} as a super-role of this
	 * {@link IndexedPropertyChain}
	 * 
	 * @param superObjectProperty
	 *            the {@link IndexedObjectProperty} to be added
	 */
	void addToldSuperObjectProperty(IndexedObjectProperty superObjectProperty) {
		if (toldSuperProperties_ == null)
			toldSuperProperties_ = new ArrayList<IndexedObjectProperty>(1);
		toldSuperProperties_.add(superObjectProperty);
	}

	/**
	 * Removes the given {@link IndexedObjectProperty} ones from the list of
	 * super-roles of this {@link IndexedPropertyChain}
	 * 
	 * @param superObjectProperty
	 *            the {@link IndexedObjectProperty} to be removed
	 * @return {@code true} if successfully removed
	 */
	protected boolean removeToldSuperObjectProperty(
			IndexedObjectProperty superObjectProperty) {
		boolean success = false;
		if (toldSuperProperties_ != null) {
			success = toldSuperProperties_.remove(superObjectProperty);
			if (toldSuperProperties_.isEmpty())
				toldSuperProperties_ = null;
		}
		return success;
	}

	/**
	 * Adds the given {@link IndexedBinaryPropertyChain} to the list of
	 * {@link IndexedBinaryPropertyChain} that contains this
	 * {@link IndexedPropertyChain} in the right-hand-side
	 * 
	 * @param chain
	 *            the {@link IndexedBinaryPropertyChain} to be added
	 */
	protected void addRightChain(IndexedBinaryPropertyChain chain) {
		if (rightChains_ == null)
			rightChains_ = new ArrayList<IndexedBinaryPropertyChain>(1);
		rightChains_.add(chain);
	}

	/**
	 * Adds the given {@link IndexedBinaryPropertyChain} from the list of
	 * {@link IndexedBinaryPropertyChain} that contain this
	 * {@link IndexedPropertyChain} in the right-hand-side
	 * 
	 * @param chain
	 *            the {@link IndexedBinaryPropertyChain} to be removed
	 * @return {@code true} if successfully removed
	 */
	protected boolean removeRightChain(IndexedBinaryPropertyChain chain) {
		boolean success = false;
		if (rightChains_ != null) {
			success = rightChains_.remove(chain);
			if (rightChains_.isEmpty())
				rightChains_ = null;
		}
		return success;
	}

	/**
	 * Non-recursively. The recursion is implemented in indexing visitors.
	 */
	abstract void updateOccurrenceNumber(int increment);

	@Override
	public boolean occurs() {
		return occurrenceNo > 0;
	}

	/**
	 * @return the string representation for the occurrence numbers of this
	 *         {@link IndexedClassExpression}
	 */
	public String printOccurrenceNumbers() {
		return "[all=" + occurrenceNo + "]";
	}

	/**
	 * verifies that occurrence numbers are not negative
	 */
	public void checkOccurrenceNumbers() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(toStringId() + " occurences: "
					+ printOccurrenceNumbers());
		if (occurrenceNo < 0)
			throw new ElkUnexpectedIndexingException(toStringId()
					+ " has a negative occurrence: " + printOccurrenceNumbers());
	}

	public void updateAndCheckOccurrenceNumbers(int increment) {
		updateOccurrenceNumber(increment);
		checkOccurrenceNumbers();
	}

	/**
	 * @return The corresponding {@code SaturatedObjecProperty} assigned to this
	 *         {@link IndexedPropertyChain}, or {@code null} if none was
	 *         assigned.
	 */
	public SaturatedPropertyChain getSaturated() {
		return getSaturated(true);
	}

	/**
	 * If the parameter is set to false, the saturation object will be returned
	 * "as is", i.e. possibly null or not yet populated. Otherwise, saturation
	 * will be triggered automatically.
	 * 
	 * @param saturate
	 * @return
	 */
	public SaturatedPropertyChain getSaturated(boolean saturate) {
		return saturate && (saturated_ == null || !saturated_.isComputed()) ? saturate()
				: saturated_;
	}

	/**
	 * Sets the corresponding {@code SaturatedObjecProperty} of this
	 * {@link IndexedPropertyChain} if none was yet assigned.
	 * 
	 * @param saturatedObjectProperty
	 *            assign the given {@link SaturatedPropertyChain} to this
	 *            {@link IndexedClassExpression}
	 * 
	 * @return {@code true} if the operation succeeded. If this method is called
	 *         for the same object from different threads with at the same time
	 *         with non-null arguments, only one call returns {@code true}.
	 */
	public synchronized void setSaturated(
			SaturatedPropertyChain saturatedObjectProperty) {
		saturated_ = saturatedObjectProperty;
	}

	/**
	 * Resets the corresponding {@code SaturatedObjecProperty} to {@code null}.
	 */
	public void resetSaturated() {
		if (saturated_ != null)
			synchronized (this) {
				saturated_ = null;
			}
	}

	/**
	 * Get an integer hash code to be used for this object.
	 * 
	 * @return Hash code.
	 */
	@Override
	public final int hashCode() {
		return hashCode_;
	}

	private SaturatedPropertyChain saturate() {
		SaturatedPropertyChain saturated = IndexedPropertyChainSaturation
				.saturate(this);

		setSaturated(saturated);

		return saturated;
	}

	public abstract <O> O accept(IndexedPropertyChainVisitor<O> visitor);

	@Override
	public <O> O accept(IndexedObjectVisitor<O> visitor) {
		return accept((IndexedPropertyChainVisitor<O>) visitor);
	}

	public abstract <O, P> O accept(
			IndexedPropertyChainVisitorEx<O, P> visitor, P parameter);

}
