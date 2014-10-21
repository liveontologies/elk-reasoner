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

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectVisitor;
import org.semanticweb.elk.reasoner.saturation.IndexedObjectWithContext;
import org.semanticweb.elk.reasoner.saturation.rules.LinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ChainableSubsumerRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.LinkedSubsumerRule;
import org.semanticweb.elk.util.collections.chains.AbstractChain;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents all occurrences of an {@link ElkClassExpression} in an ontology.
 * 
 * @author "Frantisek Simancik"
 * @author "Markus Kroetzsch"
 * @author "Yevgeny Kazakov"
 * @author Pavel Klinov
 */
abstract public class IndexedClassExpression extends IndexedObjectWithContext
		implements Comparable<IndexedClassExpression> {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(IndexedClassExpression.class);

	/**
	 * The first composition rule assigned to this
	 * {@link IndexedClassExpression}
	 */
	ChainableSubsumerRule compositionRuleHead;

	/**
	 * This counts how often this object occurred positively. Some indexing
	 * operations are only needed when encountering objects positively for the
	 * first time.
	 */
	int positiveOccurrenceNo = 0;

	/**
	 * This counts how often this object occurred negatively. Some indexing
	 * operations are only needed when encountering objects negatively for the
	 * first time.
	 */
	int negativeOccurrenceNo = 0;

	// TODO: replace pointers to contexts by a mapping

	/** Hash code for this object. */
	private final int hashCode_ = HashGenerator.generateNextHashCode();

	/**
	 * This method should always return true apart from intermediate steps
	 * during the indexing.
	 * 
	 * @return true if the represented class expression occurs in the ontology
	 */
	@Override
	public boolean occurs() {
		return positiveOccurrenceNo > 0 || negativeOccurrenceNo > 0;
	}

	/**
	 * @return {@code true} if the represented class expression occurs
	 *         negatively in the ontology
	 */
	public boolean occursNegatively() {
		return negativeOccurrenceNo > 0;
	}

	/**
	 * @return {@code true} if the represented class expression occurs
	 *         positively in the ontology
	 */
	public boolean occursPositively() {
		return positiveOccurrenceNo > 0;
	}

	/**
	 * @return the string representation for the occurrence numbers of this
	 *         {@link IndexedClassExpression}
	 */
	public String printOccurrenceNumbers() {
		return "[pos=" + positiveOccurrenceNo + "; neg="
				+ +negativeOccurrenceNo + "]";
	}

	/**
	 * verifies that occurrence numbers are not negative
	 */
	public void checkOccurrenceNumbers() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(toString() + " occurences: "
					+ printOccurrenceNumbers());
		if (positiveOccurrenceNo < 0 || negativeOccurrenceNo < 0)
			throw new ElkUnexpectedIndexingException(toString()
					+ " has a negative occurrence: " + printOccurrenceNumbers());
	}

	/**
	 * Updates occurrence numbers of this {@link IndexedClassExpression}, making
	 * changes with the {@link ModifiableOntologyIndex} if necessary. Occurrence
	 * numbers of sub-expressions is not affected.
	 * 
	 * @return {@code true} if this operation was successful and {@code false}
	 *         otherwise; if {@code false} is returned, the
	 *         {@link ModifiableOntologyIndex} should not change
	 */
	abstract boolean updateOccurrenceNumbers(ModifiableOntologyIndex index,
			int increment, int positiveIncrement, int negativeIncrement);

	boolean updateAndCheckOccurrenceNumbers(ModifiableOntologyIndex index,
			int increment, int positiveIncrement, int negativeIncrement) {
		if (!updateOccurrenceNumbers(index, increment, positiveIncrement,
				negativeIncrement)) {
			LOGGER_.trace("{}: cannot index!", this);
			return false;
		}
		checkOccurrenceNumbers();
		return true;
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

	@Override
	public int compareTo(IndexedClassExpression o) {
		if (this == o)
			return 0;
		else if (this.hashCode_ == o.hashCode_) {
			/*
			 * hash code collision for different elements should happen very
			 * rarely; in this case we rely on the unique string representation
			 * of indexed objects to compare them
			 */
			return this.toString().compareTo(o.toString());
		} else
			return (this.hashCode_ < o.hashCode_ ? -1 : 1);
	}

	/**
	 * @return the first composition rule assigned to this
	 *         {@link IndexedClassExpression}, or {@code null} if there no such
	 *         rules; all other rules can be obtained by traversing over
	 *         {@link LinkRule#next()}; this method should be used to access the
	 *         rules without modifying them.
	 */
	public LinkedSubsumerRule getCompositionRuleHead() {
		return compositionRuleHead;
	}

	/**
	 * @return the {@link Chain} view of all composition rules assigned to this
	 *         {@link IndexedClassExpression}; this is always not {@code null}.
	 *         This method can be used for convenient search and modification
	 *         (addition and deletion) of the rules using the methods of the
	 *         {@link Chain} interface without worrying about {@code null}
	 *         values.
	 */
	Chain<ChainableSubsumerRule> getCompositionRuleChain() {
		return new AbstractChain<ChainableSubsumerRule>() {
			@Override
			public ChainableSubsumerRule next() {
				return compositionRuleHead;
			}

			@Override
			public void setNext(ChainableSubsumerRule tail) {
				compositionRuleHead = tail;
			}
		};
	}

	@Override
	public <O> O accept(IndexedObjectVisitor<O> visitor) {
		return accept((IndexedClassExpressionVisitor<O>) visitor);
	}

	public abstract <O> O accept(IndexedClassExpressionVisitor<O> visitor);
}
