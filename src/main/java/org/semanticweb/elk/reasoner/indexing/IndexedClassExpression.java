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

import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.syntax.ElkClassExpression;
import org.semanticweb.elk.syntax.ElkClassExpressionVisitor;
import org.semanticweb.elk.syntax.ElkObjectIntersectionOf;
import org.semanticweb.elk.syntax.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.util.HashListMultimap;
import org.semanticweb.elk.util.Multimap;
import org.semanticweb.elk.util.Pair;

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
	/** The represented class expression. */
	public final ElkClassExpression classExpression;
	
	/**
	 * A list of all indexed class expressions for (told) superclasses of this
	 * class expression.
	 */
	public List<IndexedClassExpression> superClassExpressions;

	/**
	 * A list of Conjunction objects that have this class expression in their
	 * premise.
	 */
	public Multimap<IndexedClassExpression, IndexedObjectIntersectionOf> negConjunctionsByConjunct;

	/**
	 * A list of Quantifier objects representing all existential role
	 * restrictions that this class expressions is a told superclass of.
	 */
	public List<Pair<IndexedObjectSomeValuesFrom, IndexedObjectProperty>> negExistentialsWithRelation;

	/**
	 * This counts how often this object occurred positively. Some indexing
	 * operations are only needed when encountering objects positively for the
	 * first time.
	 */
	int positiveOccurrenceNo = 0;
	
	public boolean occursPositively() {
		return positiveOccurrenceNo > 0;
	}

	/**
	 * This counts how often this object occurred negatively. Some indexing
	 * operations are only needed when encountering objects negatively for the
	 * first time.
	 */
	int negativeOccurrenceNo = 0;
	
	public boolean occursNegatively() {
		return negativeOccurrenceNo > 0;
	}

	/**
	 * Creates a Concept that represents an ElkClassExpression.
	 * 
	 * @param classExpression
	 */
	public IndexedClassExpression(ElkClassExpression classExpression) {
		this.classExpression = classExpression;
		this.superClassExpressions = null;
		this.negConjunctionsByConjunct = null;
		this.negExistentialsWithRelation = null;
	}
	
	public void addSuperClassExpression(IndexedClassExpression superClassExpression) {
		if (superClassExpressions == null)
			superClassExpressions = new ArrayList<IndexedClassExpression> (1);
		superClassExpressions.add(superClassExpression);
	}
	
	public void addNegativeConjunctionByConjunct(IndexedObjectIntersectionOf conjunctions, 
			IndexedClassExpression conjunct) {
		if (negConjunctionsByConjunct == null)
			negConjunctionsByConjunct = 
				new HashListMultimap<IndexedClassExpression, IndexedObjectIntersectionOf>();
		negConjunctionsByConjunct.add(conjunct, conjunctions);
	}
	
	public void addNegativeExistentialWithRelation(IndexedObjectSomeValuesFrom existential,
			IndexedObjectProperty relation) {
		if (negExistentialsWithRelation == null)
			negExistentialsWithRelation = new ArrayList<Pair<IndexedObjectSomeValuesFrom,IndexedObjectProperty>> (1);
		negExistentialsWithRelation.add(new Pair<IndexedObjectSomeValuesFrom, IndexedObjectProperty> (existential, relation));
	}

	/**
	 * Represent the object's ElkClassExpression as a string. This
	 * implementation reflects the fact that we generally consider only one
	 * IndexedClassExpression for each ElkClassExpression.
	 * 
	 * @return string representation
	 */
	public String toString() {
		return "[" + classExpression.toString() + "]";
	}

	/** Global register for calculating hash codes. */
	private static int lastHashCode_ = 0;

	/** Hash code for this object. */
	private final int hashCode_ = ++lastHashCode_;

	/**
	 * Get an integer hash code to be used for this object. 
	 */
	@Override
	public final int hashCode() {
		return hashCode_;
	}
	
	static IndexedClassExpression create(ElkClassExpression classExpression) {
		return classExpression.accept(new ElkClassExpressionVisitor<IndexedClassExpression> () {

			public IndexedClassExpression visit(ElkClass elkClass) {
				return new IndexedClass(elkClass);
			}

			public IndexedClassExpression visit(
					ElkObjectIntersectionOf elkObjectIntersectionOf) {
				return new IndexedObjectIntersectionOf(elkObjectIntersectionOf);
			}

			public IndexedClassExpression visit(
					ElkObjectSomeValuesFrom elkObjectSomeValuesFrom) {
				return new IndexedObjectSomeValuesFrom(elkObjectSomeValuesFrom);
			}
		});
	}
	
	abstract public <O> O accept(IndexedClassExpressionVisitor<O> visitor);
}