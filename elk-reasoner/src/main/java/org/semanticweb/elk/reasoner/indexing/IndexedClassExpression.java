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

import org.semanticweb.elk.syntax.ElkClassExpression;
import org.semanticweb.elk.util.HashMultimap;
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
public class IndexedClassExpression {

	/** The represented class expression. */
	public final ElkClassExpression classExpression;
	
	/**
	 * A list of all indexed class expressions for (told) superclasses of this
	 * class expression.
	 */
	public final List<IndexedClassExpression> superClassExpressions;

	/**
	 * A list of Conjunction objects that have this class expression in their
	 * premise.
	 */
	public final Multimap<IndexedClassExpression, IndexedClassExpression> negConjunctionsByConjunct;

	/**
	 * A list of Quantifier objects representing all existential role
	 * restrictions that this class expressions is a told subclass of.
	 */
	public final List<Quantifier> posExistentials;

	/**
	 * A list of Quantifier objects representing all existential role
	 * restrictions that this class expressions is a told superclass of.
	 */
	public final Multimap<IndexedObjectProperty, IndexedClassExpression> negExistentialsByObjectProperty;

	/**
	 * This counts how often this object occurred positively. Some indexing
	 * operations are only needed when encountering objects positively for the
	 * first time.
	 */
	public int positiveOccurrenceNo = 0;

	/**
	 * This counts how often this object occurred negatively. Some indexing
	 * operations are only needed when encountering objects negatively for the
	 * first time.
	 */
	public int negativeOccurrenceNo = 0;

	/**
	 * Creates a Concept that represents an ElkClassExpression.
	 * 
	 * @param classExpression
	 */
	public IndexedClassExpression(ElkClassExpression classExpression) {
		this.classExpression = classExpression;
		this.superClassExpressions = new ArrayList<IndexedClassExpression>(0);
		this.negConjunctionsByConjunct = 
			new HashMultimap<IndexedClassExpression, IndexedClassExpression>(1);
		this.posExistentials = new ArrayList<Quantifier>(0);
		this.negExistentialsByObjectProperty = 
			new HashMultimap<IndexedObjectProperty, IndexedClassExpression>(1);
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
}
