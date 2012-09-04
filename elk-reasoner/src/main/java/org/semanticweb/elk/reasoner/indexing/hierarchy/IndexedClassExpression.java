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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.saturation.rulesystem.Context;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Represents all occurrences of an ElkClassExpression in an ontology. To this
 * end, objects of this class keeps a number of lists to describe the
 * relationships to other (indexed) class expressions. The data structures are
 * optimized for quickly retrieving the relevant relationships during
 * inferencing.
 * 
 * This class is mainly a data container that provides direct public access to
 * its content. The task of updating index structures consistently in a global
 * sense is left to callers.
 * 
 * @author "Frantisek Simancik"
 * @author "Markus Kroetzsch"
 * @author "Yevgeny Kazakov"
 */
abstract public class IndexedClassExpression {

	/**
	 * Correctness of axioms deletions requires that toldSuperClassExpressions
	 * is a List.
	 */
	private List<IndexedClassExpression> toldSuperClassExpressions_;

	private Map<IndexedClassExpression, IndexedObjectIntersectionOf> negConjunctionsByConjunct_;

	private Collection<IndexedObjectSomeValuesFrom> negExistentials_;

	private Set<IndexedPropertyChain> posPropertiesInExistentials_;

	/**
	 * {@link IndexedClassExpression} that appear in binary disjointess axioms
	 * with this object.
	 */
	private Set<IndexedClassExpression> disjointClasses_;

	/**
	 * List of all larger (non-binary) disjointness axioms in which this object
	 * appears.
	 * 
	 */
	protected List<IndexedDisjointnessAxiom> disjointnessAxioms;

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
	 * This method should always return true apart from intermediate steps
	 * during the indexing.
	 * 
	 * @return true if the represented class expression occurs in the ontology
	 */
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
	 * Non-recursively. The recursion is implemented in indexing visitors.
	 */
	abstract void updateOccurrenceNumbers(int increment, int positiveIncrement,
			int negativeIncrement);

	/**
	 * @return All told super class expressions of this class expression,
	 *         possibly null.
	 */
	public List<IndexedClassExpression> getToldSuperClassExpressions() {
		return toldSuperClassExpressions_;
	}

	/**
	 * @return the {@link IndexedObjectIntersectionOf} objects that occur
	 *         negatively and contain this {@link IndexedClassExpression},
	 *         indexed by the other {@link IndexedClassExpression} in the
	 *         conjunction, or {@code null} if none is assigned
	 */
	public Map<IndexedClassExpression, IndexedObjectIntersectionOf> getNegConjunctionsByConjunct() {
		return negConjunctionsByConjunct_;
	}

	/**
	 * @return the {@link IndexedObjectSomeValuesFrom} objects that occur
	 *         negatively and have this {@link IndexedClassExpression} as the
	 *         filler, or {@code null} if none is assigned
	 */
	public Collection<IndexedObjectSomeValuesFrom> getNegExistentials() {
		return negExistentials_;
	}

	/**
	 * @return the {@link IndexedObjectProperty} objects that occur in positive
	 *         {@link IndexedObjectSomeValuesFrom} that have this
	 *         {@link IndexedClassExpression} as the filler, or {@code null} if
	 *         none is assigned
	 */
	public Set<IndexedPropertyChain> getPosPropertiesInExistentials() {
		return posPropertiesInExistentials_;
	}

	/**
	 * @return the {@link IndexedClassExpression} objects that occur with this
	 *         object in binary disjointness axioms, or {@code null} if none is
	 *         assigned
	 */
	public Set<IndexedClassExpression> getDisjointClasses() {
		return disjointClasses_;
	}

	/**
	 * @return Collection of all (non-binary) {@link IndexedDisjointnessAxiom}s
	 *         in which this object appears, or {@code null} if none is assigned
	 */
	public List<IndexedDisjointnessAxiom> getDisjointnessAxioms() {
		return disjointnessAxioms;
	}

	protected void addToldSuperClassExpression(
			IndexedClassExpression superClassExpression) {
		if (toldSuperClassExpressions_ == null)
			toldSuperClassExpressions_ = new ArrayList<IndexedClassExpression>(
					1);
		toldSuperClassExpressions_.add(superClassExpression);
	}

	/**
	 * @param superClassExpression
	 * @return true if successfully removed
	 */
	protected boolean removeToldSuperClassExpression(
			IndexedClassExpression superClassExpression) {
		boolean success = false;
		if (toldSuperClassExpressions_ != null) {
			success = toldSuperClassExpressions_.remove(superClassExpression);
			if (toldSuperClassExpressions_.isEmpty())
				toldSuperClassExpressions_ = null;
		}
		return success;
	}

	protected void addNegConjunctionByConjunct(
			IndexedObjectIntersectionOf conjunction,
			IndexedClassExpression conjunct) {

		if (negConjunctionsByConjunct_ == null) {
			negConjunctionsByConjunct_ = new ArrayHashMap<IndexedClassExpression, IndexedObjectIntersectionOf>(
					4);
		}

		if (negConjunctionsByConjunct_.put(conjunct, conjunction) != null) {
			// Can be caused e.g. when ElkObjectIndexerVisitor indexed conjuncts
			// with equals hashCodes.
			throw new RuntimeException(
					"Internal error: duplicate indexing in IndexedClassExpression.addNegConjunctionByConjunct.");
		}

	}

	/**
	 * @param conjunction
	 * @param conjunct
	 * @return true if successfully removed
	 */
	protected boolean removeNegConjunctionByConjunct(
			IndexedObjectIntersectionOf conjunction,
			IndexedClassExpression conjunct) {
		boolean success = false;
		if (negConjunctionsByConjunct_ != null) {
			success = (negConjunctionsByConjunct_.remove(conjunct) != null);
			if (negConjunctionsByConjunct_.isEmpty())
				negConjunctionsByConjunct_ = null;
		}
		return success;
	}

	protected void addNegExistential(IndexedObjectSomeValuesFrom existential) {
		if (negExistentials_ == null)
			negExistentials_ = new ArrayList<IndexedObjectSomeValuesFrom>(1);
		negExistentials_.add(existential);
	}

	/**
	 * @param existential
	 * @return true if successfully removed
	 */
	protected boolean removeNegExistential(
			IndexedObjectSomeValuesFrom existential) {
		boolean success = false;
		if (negExistentials_ != null) {
			success = negExistentials_.remove(existential);
			if (negExistentials_.isEmpty())
				negExistentials_ = null;
		}
		return success;
	}

	protected void addPosPropertyInExistential(IndexedPropertyChain property) {
		if (posPropertiesInExistentials_ == null)
			posPropertiesInExistentials_ = new ArrayHashSet<IndexedPropertyChain>(
					1);
		posPropertiesInExistentials_.add(property);
	}

	protected boolean removePosPropertyInExistential(
			IndexedPropertyChain property) {
		boolean success = false;
		if (posPropertiesInExistentials_ != null) {
			success = posPropertiesInExistentials_.remove(property);
			if (posPropertiesInExistentials_.isEmpty())
				posPropertiesInExistentials_ = null;
		}
		return success;
	}

	protected void addDisjointClass(IndexedClassExpression disjointClass) {
		if (disjointClasses_ == null)
			disjointClasses_ = new ArrayHashSet<IndexedClassExpression>();
		disjointClasses_.add(disjointClass);
	}

	/**
	 * @param disjointClass
	 * @return true if successfully removed
	 */
	protected boolean removeDisjointClass(IndexedClassExpression disjointClass) {
		boolean success = false;
		if (disjointClasses_ != null) {
			success = disjointClasses_.remove(disjointClass);
			if (disjointClasses_.isEmpty())
				disjointClasses_ = null;
		}
		return success;
	}

	protected void addDisjointnessAxiom(
			IndexedDisjointnessAxiom disjointnessAxiom) {
		if (disjointnessAxioms == null)
			disjointnessAxioms = new LinkedList<IndexedDisjointnessAxiom>();
		disjointnessAxioms.add(disjointnessAxiom);
	}

	/**
	 * @param disjointnessAxiom
	 * @return true if successfully removed
	 */
	protected boolean removeDisjointnessAxiom(
			IndexedDisjointnessAxiom disjointnessAxiom) {
		boolean success = false;

		if (disjointnessAxioms != null) {
			Iterator<IndexedDisjointnessAxiom> i = disjointnessAxioms
					.iterator();
			while (i.hasNext())
				if (i.next().getMembers()
						.equals(disjointnessAxiom.getMembers())) {
					i.remove();
					break;
				}

			if (disjointnessAxioms.isEmpty())
				disjointnessAxioms = null;
		}
		return success;
	}

	// TODO: replace pointers to contexts by a mapping

	/**
	 * Used for efficient retrieval of the Context corresponding to this class
	 * expression.
	 */
	protected final AtomicReference<Context> context = new AtomicReference<Context>();

	/**
	 * @return The corresponding context, null if none was assigned.
	 */
	public Context getContext() {
		return context.get();
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
	public boolean setContext(Context context) {
		return this.context.compareAndSet(null, context);
	}

	/**
	 * Resets the corresponding context to null.
	 */
	public void resetContext() {
		context.set(null);
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

	public abstract <O> O accept(IndexedClassExpressionVisitor<O> visitor);

	@Override
	public abstract String toString();
}
