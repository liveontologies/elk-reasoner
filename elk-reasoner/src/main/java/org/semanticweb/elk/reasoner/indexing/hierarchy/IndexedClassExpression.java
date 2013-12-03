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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.reasoner.saturation.rules.DecompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.LinkRule;
import org.semanticweb.elk.util.collections.chains.AbstractChain;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Represents all occurrences of an {@link ElkClassExpression} in an ontology.
 * 
 * @author "Frantisek Simancik"
 * @author "Markus Kroetzsch"
 * @author "Yevgeny Kazakov"
 * @author Pavel Klinov
 */
abstract public class IndexedClassExpression extends IndexedObject implements
		Comparable<IndexedClassExpression> {

	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(IndexedClassExpression.class);

	/**
	 * The first composition rule assigned to this
	 * {@link IndexedClassExpression}
	 */
	ChainableRule<Conclusion, Context> compositionRuleHead;

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
	 * /** the reference to a {@link Context} assigned to this
	 * {@link IndexedClassExpression}
	 */
	private volatile Context context_ = null;

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
	 * Non-recursively. The recursion is implemented in indexing visitors.
	 */
	abstract void updateOccurrenceNumbers(ModifiableOntologyIndex index,
			int increment, int positiveIncrement, int negativeIncrement);

	void updateAndCheckOccurrenceNumbers(ModifiableOntologyIndex index,
			int increment, int positiveIncrement, int negativeIncrement) {
		updateOccurrenceNumbers(index, increment, positiveIncrement,
				negativeIncrement);
		checkOccurrenceNumbers();
	}

	/**
	 * @return The corresponding context, null if none was assigned.
	 */
	public Context getContext() {
		return context_;
	}

	/**
	 * Sets the corresponding context if none was yet assigned.
	 * 
	 * @param context
	 *            the {@link Context} which will be assigned to this
	 *            {@link IndexedClassExpression}
	 * 
	 * @return {@code true} if the operation succeeded.
	 */
	public synchronized boolean setContext(Context context) {
		if (context_ != null)
			return false;
		context_ = context;
		return true;
	}

	/**
	 * Resets the corresponding context to null.
	 */
	public synchronized void resetContext() {
		context_ = null;
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
	public LinkRule<Conclusion, Context> getCompositionRuleHead() {
		return compositionRuleHead;
	}

	/**
	 * @return the {@link Chain} view of all composition rules assigned to this
	 *         {@link IndexedClassExpression}; this is always not {@code null}.
	 *         This method can be used for convenient search and modification
	 *         (addition and deletion) of the rules using the methods of the
	 *         {@link Chain} interface without without worrying about
	 *         {@code null} values.
	 */
	Chain<ChainableRule<Conclusion, Context>> getCompositionRuleChain() {
		return new AbstractChain<ChainableRule<Conclusion, Context>>() {
			@Override
			public ChainableRule<Conclusion, Context> next() {
				return compositionRuleHead;
			}

			@Override
			public void setNext(ChainableRule<Conclusion, Context> tail) {
				compositionRuleHead = tail;
			}
		};
	}

	@Override
	public <O> O accept(IndexedObjectVisitor<O> visitor) {
		return accept((IndexedClassExpressionVisitor<O>) visitor);
	}

	public abstract <O> O accept(IndexedClassExpressionVisitor<O> visitor);

	public abstract void accept(DecompositionRuleApplicationVisitor visitor,
			Context context);
}
