/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.indexing.classes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkUnexpectedIndexingException;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedSubObject;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedPropertyChain;
import org.semanticweb.elk.util.collections.entryset.Entry;
import org.semanticweb.elk.util.collections.entryset.EntryCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements {@link CachedIndexedPropertyChain} and {@link Entry} so that these
 * objects can be stored in {@link EntryCollection} together with other
 * elements.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of objects this object can be structurally equal to
 * 
 * @param <N>
 *            The type of the elements in the set where this entry is used
 * 
 */
abstract class CachedIndexedPropertyChainImpl<T extends CachedIndexedPropertyChain<T> & Entry<T, N>, N>
		extends CachedIndexedSubObjectImpl<T, N>
		implements CachedIndexedPropertyChain<T>, ModifiableIndexedPropertyChain, Entry<T, N> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(CachedIndexedPropertyChainImpl.class);

	// TODO: move to elk-utils-collections
	private static ArrayList<?> EMPTY_ARRAY_LIST_ = new ArrayList<Object>(0);

	@SuppressWarnings("unchecked")
	protected static <T> ArrayList<T> emptyArrayList() {
		return (ArrayList<T>) EMPTY_ARRAY_LIST_;
	}

	/**
	 * This counts how often this object occurred in the ontology.
	 */
	int totalOccurrenceNo = 0;

	/**
	 * The {@link SaturatedPropertyChain} object assigned to this
	 * {@link IndexedPropertyChain}
	 */
	private final SaturatedPropertyChain saturated_;

	/**
	 * All told super object properties of this
	 * {@link IndexedComplexPropertyChain}. Should be a List for correctness of
	 * axioms deletions (duplicates matter).
	 */
	private ArrayList<IndexedObjectProperty> toldSuperProperties_;

	/**
	 * The {@link ElkAxiom}s responsible for the corresponding told
	 * super-properties
	 */
	private ArrayList<ElkAxiom> toldSuperPropertiesReasons_;

	/**
	 * Collections of all binary role chains in which this
	 * {@link IndexedComplexPropertyChain} occurs on the right.
	 */
	private Collection<IndexedComplexPropertyChain> rightChains_;

	CachedIndexedPropertyChainImpl(int structuralHash) {
		super(structuralHash);
		this.saturated_ = new SaturatedPropertyChain(this);
	}

	@Override
	public final boolean occurs() {
		return totalOccurrenceNo > 0;
	}

	@Override
	public final ArrayList<IndexedObjectProperty> getToldSuperProperties() {
		if (toldSuperProperties_ == null) {
			final ArrayList<IndexedObjectProperty> result = CachedIndexedPropertyChainImpl
					.emptyArrayList();
			return result;
		}
		// else
		return toldSuperProperties_;
	}

	@Override
	public ArrayList<ElkAxiom> getToldSuperPropertiesReasons() {
		if (toldSuperPropertiesReasons_ == null) {
			final ArrayList<ElkAxiom> result = CachedIndexedPropertyChainImpl
					.emptyArrayList();
			return result;
		}
		// else
		return toldSuperPropertiesReasons_;
	}

	@Override
	public final Collection<IndexedComplexPropertyChain> getRightChains() {
		return rightChains_ == null
				? Collections.<IndexedComplexPropertyChain> emptySet()
				: Collections.unmodifiableCollection(rightChains_);
	}

	@Override
	public final SaturatedPropertyChain getSaturated() {
		return saturated_;
	}

	@Override
	public final int compareTo(ModifiableIndexedPropertyChain o) {
		if (this == o)
			return 0;
		// else
		int thisHash = hashCode();
		int otherHash = o.hashCode();
		if (thisHash == otherHash) {
			/*
			 * hash code collision for different elements should happen very
			 * rarely; in this case we rely on the unique string representation
			 * of indexed objects to compare them
			 */
			return this.toString().compareTo(o.toString());
		}
		// else
		return (thisHash < otherHash ? -1 : 1);
	}

	@Override
	public final boolean addToldSuperObjectProperty(
			IndexedObjectProperty superObjectProperty, ElkAxiom reason) {
		if (toldSuperProperties_ == null) {
			toldSuperProperties_ = new ArrayList<IndexedObjectProperty>(1);
		}
		if (toldSuperPropertiesReasons_ == null) {
			toldSuperPropertiesReasons_ = new ArrayList<ElkAxiom>(1);
		}
		toldSuperProperties_.add(superObjectProperty);
		toldSuperPropertiesReasons_.add(reason);
		return true;
	}

	@Override
	public final boolean removeToldSuperObjectProperty(
			IndexedObjectProperty superObjectProperty, ElkAxiom reason) {
		int i = indexOf(superObjectProperty, reason);
		if (i < 0)
			return false;
		// else success
		toldSuperProperties_.remove(i);
		toldSuperPropertiesReasons_.remove(i);
		if (toldSuperProperties_.isEmpty())
			toldSuperProperties_ = null;
		if (toldSuperPropertiesReasons_.isEmpty())
			toldSuperPropertiesReasons_ = null;
		return true;
	}

	private int indexOf(IndexedPropertyChain superObjectProperty,
			ElkAxiom reason) {
		for (int i = 0; i < toldSuperProperties_.size(); i++) {
			if (toldSuperProperties_.get(i).equals(superObjectProperty)
					&& toldSuperPropertiesReasons_.get(i).equals(reason))
				return i;
		}
		// else not found
		return -1;
	}

	@Override
	public final boolean addRightChain(IndexedComplexPropertyChain chain) {
		if (rightChains_ == null)
			rightChains_ = new ArrayList<IndexedComplexPropertyChain>(1);
		return rightChains_.add(chain);
	}

	@Override
	public final boolean removeRightChain(IndexedComplexPropertyChain chain) {
		boolean success = false;
		if (rightChains_ != null) {
			success = rightChains_.remove(chain);
			if (rightChains_.isEmpty())
				rightChains_ = null;
		}
		return success;
	}

	/**
	 * @return the string representation for the occurrence numbers of this
	 *         {@link IndexedClassExpression}
	 */
	public final String printOccurrenceNumbers() {
		return "[all=" + totalOccurrenceNo + "]";
	}

	/**
	 * verifies that occurrence numbers are not negative
	 */
	public final void checkOccurrenceNumbers() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(this + " occurences: " + printOccurrenceNumbers());
		if (totalOccurrenceNo < 0)
			throw new ElkUnexpectedIndexingException(
					this + " has a negative occurrence: "
							+ printOccurrenceNumbers());
	}

	public final boolean updateAndCheckOccurrenceNumbers(
			ModifiableOntologyIndex index, OccurrenceIncrement increment) {
		if (!updateOccurrenceNumbers(index, increment))
			return false;
		checkOccurrenceNumbers();
		return true;
	}

	@Override
	public final <O> O accept(IndexedSubObject.Visitor<O> visitor) {
		return accept((IndexedPropertyChain.Visitor<O>) visitor);
	}

	@Override
	public final T accept(CachedIndexedSubObject.Filter filter) {
		return accept((CachedIndexedPropertyChain.Filter) filter);
	}

}
