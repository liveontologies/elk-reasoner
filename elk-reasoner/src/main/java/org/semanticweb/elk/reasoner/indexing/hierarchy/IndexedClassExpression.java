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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.saturation.Derivable;
import org.semanticweb.elk.reasoner.saturation.QueueableVisitor;
import org.semanticweb.elk.reasoner.saturation.SaturatedClassExpression;
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
abstract public class IndexedClassExpression implements Derivable {

	protected List<IndexedClassExpression> toldSuperClassExpressions;
	protected Map<IndexedClassExpression, IndexedObjectIntersectionOf> negConjunctionsByConjunct;
	protected List<IndexedObjectSomeValuesFrom> negExistentials;

	// TODO: some of the occurrence counters are relevant only for subclasses

	/**
	 * This counts how often this object occurred in the ontology.
	 */
	protected int occurrenceNo = 0;

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

	public boolean occurs() {
		return occurrenceNo > 0;
	}

	public boolean occursNegatively() {
		return negativeOccurrenceNo > 0;
	}

	public boolean occursPositively() {
		return positiveOccurrenceNo > 0;
	}

	protected abstract void updateOccurrenceNumbers(int increment,
			int positiveIncrement, int negativeIncrement,
			IndexedObjectCanonizer canonizer);
	

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
	public List<IndexedObjectSomeValuesFrom> getNegExistentials() {
		return negExistentials;
	}

	protected void addToldSuperClassExpression(
			IndexedClassExpression superClassExpression) {
		if (toldSuperClassExpressions == null)
			toldSuperClassExpressions = new ArrayList<IndexedClassExpression>(1);
		toldSuperClassExpressions.add(superClassExpression);
	}

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

	/**
	 * 
	 */
	protected final AtomicReference<SaturatedClassExpression> saturated = new AtomicReference<SaturatedClassExpression>();

	/**
	 * @return The corresponding SaturatedClassExpression, null if none was
	 *         assigned.
	 */
	public SaturatedClassExpression getSaturated() {
		return saturated.get();
	}

	/**
	 * Sets the corresponding SaturatedClassExpression if none was yet assigned.
	 * 
	 * @return True if the operation succeeded.
	 */
	public boolean setSaturated(
			SaturatedClassExpression saturatedClassExpression) {
		return saturated.compareAndSet(null, saturatedClassExpression);
	}

	/**
	 * Resets the corresponding SaturatedClassExpression to null.
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

	public abstract <O> O accept(IndexedClassExpressionVisitor<O> visitor);
	
	public <O> O accept(QueueableVisitor<O> visitor) {
		return visitor.visit(this);
	}
}
