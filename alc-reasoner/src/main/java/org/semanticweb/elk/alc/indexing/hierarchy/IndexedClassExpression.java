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
package org.semanticweb.elk.alc.indexing.hierarchy;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.alc.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.alc.indexing.visitors.IndexedObjectVisitor;
import org.semanticweb.elk.alc.saturation.ConclusionProducer;
import org.semanticweb.elk.alc.saturation.Context;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ClashImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ComposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DecomposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.PropagationImpl;
import org.semanticweb.elk.util.collections.LazySetIntersection;
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
abstract public class IndexedClassExpression extends IndexedObject implements
		Comparable<IndexedClassExpression> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(IndexedClassExpression.class);

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

	/**
	 * {@link IndexedObjectIntersectionOf} in which this
	 * {@link IndexedClassExpression} is one of the conjuncts indexed by the
	 * other conjunct
	 */
	Map<IndexedClassExpression, IndexedObjectIntersectionOf> conjunctionsByConjunct_;

	/**
	 * Negatively occurred {@link IndexedObjectSomeValuesFrom} in which this
	 * {@link IndexedClassExpression} is the filler.
	 */
	Set<IndexedObjectSomeValuesFrom> negativeExistentials_;

	/**
	 * {@link IndexedClassExpression} occurred as super-classes in
	 * {@link IndexedSubClassOfAxiom}s in which this
	 * {@link IndexedClassExpression} is a subclass.
	 */
	Set<IndexedClassExpression> toldSuperClasses_;

	Map<IndexedClassExpression, IndexedObjectIntersectionOf> getConjunctionsByConjunct() {
		if (conjunctionsByConjunct_ == null)
			return Collections.emptyMap();
		// else
		return conjunctionsByConjunct_;
	}

	Set<IndexedObjectSomeValuesFrom> getNegativeExistentials() {
		if (negativeExistentials_ == null)
			return Collections.emptySet();
		// else
		return negativeExistentials_;
	}

	Set<IndexedClassExpression> getToldSuperClasses() {
		if (toldSuperClasses_ == null)
			return Collections.emptySet();
		// else
		return toldSuperClasses_;
	}

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
	abstract void updateOccurrenceNumbers(OntologyIndex index, int increment,
			int positiveIncrement, int negativeIncrement);

	void updateAndCheckOccurrenceNumbers(OntologyIndex index, int increment,
			int positiveIncrement, int negativeIncrement) {
		updateOccurrenceNumbers(index, increment, positiveIncrement,
				negativeIncrement);
		checkOccurrenceNumbers();
	}

	@Override
	public int compareTo(IndexedClassExpression o) {
		if (this == o)
			return 0;
		else if (this.hashCode() == o.hashCode()) {
			/*
			 * hash code collision for different elements should happen very
			 * rarely; in this case we rely on the unique string representation
			 * of indexed objects to compare them
			 */
			return this.toString().compareTo(o.toString());
		} else
			return (this.hashCode() < o.hashCode() ? -1 : 1);
	}

	@Override
	public <O> O accept(IndexedObjectVisitor<O> visitor) {
		return accept((IndexedClassExpressionVisitor<O>) visitor);
	}

	public abstract <O> O accept(IndexedClassExpressionVisitor<O> visitor);

	public static void applyCompositionRules(IndexedClassExpression subsumer,
			Context premises, ConclusionProducer producer) {
		if (premises.getNegativeSubsumers().contains(subsumer)) {
			// generate clash
			producer.produce(ClashImpl.getInstance());
			// nothing else should be derived
			return;
		}
		if (subsumer.conjunctionsByConjunct_ != null) {
			// conjunction introduction
			for (IndexedClassExpression common : new LazySetIntersection<IndexedClassExpression>(
					premises.getSubsumers(),
					subsumer.conjunctionsByConjunct_.keySet())) {
				producer.produce(new ComposedSubsumerImpl(
						subsumer.conjunctionsByConjunct_.get(common)));
			}
		}
		if (subsumer.negativeExistentials_ != null) {
			// generate propagations
			for (IndexedObjectSomeValuesFrom existential : subsumer.negativeExistentials_) {
				IndexedObjectProperty relation = existential.getRelation();
				producer.produce(new PropagationImpl(relation, existential));
			}
		}
		if (subsumer.toldSuperClasses_ != null) {
			// expand under told super-classes
			for (IndexedClassExpression toldSuper : subsumer.toldSuperClasses_) {
				producer.produce(new DecomposedSubsumerImpl(toldSuper));
			}

		}
	}
}
