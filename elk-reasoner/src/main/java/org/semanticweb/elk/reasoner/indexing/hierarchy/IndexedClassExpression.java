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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.saturation.rulesystem.Context;
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
/**
 * @author Frantisek Simancik
 * 
 */
abstract public class IndexedClassExpression {

	/**
	 * Correctness of axioms deletions requires that toldSuperClassExpressions
	 * is a List.
	 */
	protected List<IndexedClassExpression> toldSuperClassExpressions;

	protected Map<IndexedClassExpression, IndexedObjectIntersectionOf> negConjunctionsByConjunct;
	protected Collection<IndexedObjectSomeValuesFrom> negExistentials;

	/**
	 * ICEs that appear in binary disjointess axioms with this object.
	 */
	protected Set<IndexedClassExpression> disjointClasses;

	/**
	 * List of all larger (non-binary) disjointness axioms in which this
	 * object appears.
	 * 
	 */
	protected List<IndexedDisjointnessAxiom> disjointnessAxioms;

	/**
	 * This counts how often this object occurred positively. Some indexing
	 * operations are only needed when encountering objects positively for the
	 * first time.
	 */
	protected int positiveOccurrenceNo = 0;

	/**
	 * This counts how often this object occurred negatively. Some indexing
	 * operations are only needed when encountering objects negatively for the
	 * first time.
	 */
	protected int negativeOccurrenceNo = 0;

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
	 * @return true if the represented class expression occurs negatively in the
	 *         ontology
	 */
	public boolean occursNegatively() {
		return negativeOccurrenceNo > 0;
	}

	/**
	 * @return true if the represented class expression occurs positively in the
	 *         ontologu
	 */
	public boolean occursPositively() {
		return positiveOccurrenceNo > 0;
	}

	/**
	 * Non-recursively. The recursion is implemented in indexing visitors.
	 */
	protected abstract void updateOccurrenceNumbers(int increment,
			int positiveIncrement, int negativeIncrement);

	/**
	 * @return All told super class expressions of this class expression,
	 *         possibly null.
	 */
	public List<IndexedClassExpression> getToldSuperClassExpressions() {
		return toldSuperClassExpressions;
	}

	/**
	 * @return Indexed conjunctions that occur negatively and contain this class
	 *         expression, indexed by the second conjunct, possibly null.
	 */
	public Map<IndexedClassExpression, IndexedObjectIntersectionOf> getNegConjunctionsByConjunct() {
		return negConjunctionsByConjunct;
	}

	/**
	 * @return Indexed existentials that occur negatively and have this class
	 *         expression as the filler, possibly null.
	 */
	public Collection<IndexedObjectSomeValuesFrom> getNegExistentials() {
		return negExistentials;
	}

	/**
	 * @return Indexed Class Expression that occur with this object in binary
	 *         disjointness axioms, possibly null.
	 */
	public Set<IndexedClassExpression> getDisjointClasses() {
		return disjointClasses;
	}

	/**
	 * @return Collection of all larger (non-binary) disjointness axioms in
	 *         which this object appears, possibly null.
	 */
	public List<IndexedDisjointnessAxiom> getDisjointnessAxioms() {
		return disjointnessAxioms;
	}

	protected void addToldSuperClassExpression(
			IndexedClassExpression superClassExpression) {
		if (toldSuperClassExpressions == null)
			toldSuperClassExpressions = new ArrayList<IndexedClassExpression>(1);
		toldSuperClassExpressions.add(superClassExpression);
	}

	/**
	 * @param superClassExpression
	 * @return true if successfully removed
	 */
	protected boolean removeToldSuperClassExpression(
			IndexedClassExpression superClassExpression) {
		boolean success = false;
		if (toldSuperClassExpressions != null) {
			success = toldSuperClassExpressions.remove(superClassExpression);
			if (toldSuperClassExpressions.isEmpty())
				toldSuperClassExpressions = null;
		}
		return success;
	}

	protected void addNegConjunctionByConjunct(
			IndexedObjectIntersectionOf conjunction,
			IndexedClassExpression conjunct) {
		if (negConjunctionsByConjunct == null)
			// TODO possibly replace by ArrayHashMap when it supports removal
			negConjunctionsByConjunct = new HashMap<IndexedClassExpression, IndexedObjectIntersectionOf>(
					4);
		negConjunctionsByConjunct.put(conjunct, conjunction);
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
		if (negConjunctionsByConjunct != null) {
			success = (negConjunctionsByConjunct.remove(conjunct) != null);
			if (negConjunctionsByConjunct.isEmpty())
				negConjunctionsByConjunct = null;
		}
		return success;
	}

	protected void addNegExistential(IndexedObjectSomeValuesFrom existential) {
		if (negExistentials == null)
			negExistentials = new ArrayList<IndexedObjectSomeValuesFrom>(1);
		negExistentials.add(existential);
	}

	/**
	 * @param existential
	 * @return true if successfully removed
	 */
	protected boolean removeNegExistential(
			IndexedObjectSomeValuesFrom existential) {
		boolean success = false;
		if (negExistentials != null) {
			success = negExistentials.remove(existential);
			if (negExistentials.isEmpty())
				negExistentials = null;
		}
		return success;
	}

	protected void addDisjointClass(IndexedClassExpression disjointClass) {
		if (disjointClasses == null)
			disjointClasses = new ArrayHashSet<IndexedClassExpression>();
		disjointClasses.add(disjointClass);
	}

	/**
	 * @param disjointClass
	 * @return true if successfully removed
	 */
	protected boolean removeDisjointClass(IndexedClassExpression disjointClass) {
		boolean success = false;
		if (disjointClasses != null) {
			success = disjointClasses.remove(disjointClass);
			if (disjointClasses.isEmpty())
				disjointClasses = null;
		}
		return success;
	}

	protected void addDisjointnessAxiom(IndexedDisjointnessAxiom disjointnessAxiom) {
		if (disjointnessAxioms == null)
			disjointnessAxioms = new LinkedList<IndexedDisjointnessAxiom>();
		disjointnessAxioms.add(disjointnessAxiom);
	}

	/**
	 * @param disjointnessAxiom
	 * @return true if successfully removed
	 */
	protected boolean removeDisjointnessAxiom(IndexedDisjointnessAxiom disjointnessAxiom) {
		boolean success = false;
		
		if (disjointnessAxioms != null) {
			Iterator<IndexedDisjointnessAxiom> i = disjointnessAxioms.iterator();
			while (i.hasNext())
				if (i.next().getMembers().equals(disjointnessAxiom.getMembers())) {
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
	 * @return True if the operation succeeded.
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
