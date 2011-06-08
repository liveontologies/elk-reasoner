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
package org.semanticweb.elk.reasoner.indexing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.semanticweb.elk.reasoner.saturation.SaturatedClassExpression;
import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.syntax.ElkClassExpression;
import org.semanticweb.elk.syntax.ElkClassExpressionVisitor;
import org.semanticweb.elk.syntax.ElkObjectIntersectionOf;
import org.semanticweb.elk.syntax.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.util.HashGenerator;
import org.semanticweb.elk.util.HashListMultimap;
import org.semanticweb.elk.util.Multimap;

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
 * @author Frantisek Simancik
 * @author Markus Kroetzsch
 */
abstract public class IndexedClassExpression {
	
	protected final ElkClassExpression elkClassExpression;
	protected List<IndexedClassExpression> toldSuperClassExpressions;
	protected Multimap<IndexedClassExpression, IndexedObjectIntersectionOf> negConjunctionsByConjunct;
	protected List<IndexedObjectSomeValuesFrom> negExistentials;

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
	 * Creates an object that represents the given an ElkClassExpression.
	 */
	protected IndexedClassExpression(ElkClassExpression classExpression) {
		this.elkClassExpression = classExpression;
	}
	
	
	/** 
	 * @return The represented class expression.
	 */ 
	public ElkClassExpression getClassExpression() {
		return elkClassExpression;
	}
	

	/**
	 * @return All told super class expressions of this class expression.
	 */
	public List<IndexedClassExpression> getToldSuperClassExpressions() {
		return toldSuperClassExpressions;
	}
	
	
	/**
	 * @return Indexed conjunctions that occur negatively and contain this class expression,
	 *  indexed by the second conjunct.
	 */
	public Multimap<IndexedClassExpression, IndexedObjectIntersectionOf> getNegConjunctionsByConjunct() {
		return negConjunctionsByConjunct;
	}


	/**
	 * @return Indexed existentials that occur negatively and have this class expression as the filler.
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

	
	protected void addNegativeConjunctionByConjunct(
			IndexedObjectIntersectionOf conjunction,
			IndexedClassExpression conjunct) {
		if (negConjunctionsByConjunct == null)
			negConjunctionsByConjunct = new HashListMultimap<IndexedClassExpression, IndexedObjectIntersectionOf>();
		negConjunctionsByConjunct.add(conjunct, conjunction);
	}

	
	protected void addNegativeExistential(IndexedObjectSomeValuesFrom existential) {
		if (negExistentials == null)
			negExistentials = new ArrayList<IndexedObjectSomeValuesFrom> (1);
		negExistentials.add(existential);
	}

	
	

	
	protected final AtomicReference<SaturatedClassExpression> saturated =
		new AtomicReference<SaturatedClassExpression> ();
	
	
	/**
	 * @return The corresponding saturated object property, 
	 * null if none was assigned.
	 */
	public SaturatedClassExpression getSaturated() {
		return saturated.get();
	}
	
	
	/**
	 * Sets the corresponding saturated class expression if none
	 * was yet assigned. 
	 * 
	 * @return True if the operation succeeded. 
	 */
	public boolean setSaturated(SaturatedClassExpression saturatedClassExpression) {
		return saturated.compareAndSet(null, saturatedClassExpression);
	}
	
	
	/**
	 * Resets the corresponding saturated object property to null.  
	 */
	public void resetSaturated() {
		saturated.set(null);
	}


	
	
	
	
	/**
	 * Represent the object's ElkClassExpression as a string. This
	 * implementation reflects the fact that we generally consider only one
	 * IndexedClassExpression for each ElkClassExpression.
	 * 
	 * @return String representation.
	 */
	public String toString() {
		return "[" + elkClassExpression.toString() + "]";
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

	
	static IndexedClassExpression create(ElkClassExpression classExpression) {
		return classExpression
				.accept(new ElkClassExpressionVisitor<IndexedClassExpression>() {

					public IndexedClassExpression visit(ElkClass elkClass) {
						return new IndexedClass(elkClass);
					}

					public IndexedClassExpression visit(
							ElkObjectIntersectionOf elkObjectIntersectionOf) {
						return new IndexedObjectIntersectionOf(
								elkObjectIntersectionOf);
					}

					public IndexedClassExpression visit(
							ElkObjectSomeValuesFrom elkObjectSomeValuesFrom) {
						return new IndexedObjectSomeValuesFrom(
								elkObjectSomeValuesFrom);
					}
				});
	}

	abstract public <O> O accept(IndexedClassExpressionVisitor<O> visitor);
}
