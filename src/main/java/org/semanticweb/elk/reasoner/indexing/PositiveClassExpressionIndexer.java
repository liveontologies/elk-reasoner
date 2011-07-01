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
/**
 * @author Yevgeny Kazakov, May 13, 2011
 */
package org.semanticweb.elk.reasoner.indexing;


/**
 * For indexing positive occurrences of class expressions.
 * 
 * @author Yevgeny Kazakov
 * @author Frantisek Simancik
 * 
 */
class PositiveClassExpressionIndexer implements
		IndexedClassExpressionVisitor<Void> {

	protected final AxiomIndexer axiomIndexer;

	protected PositiveClassExpressionIndexer(AxiomIndexer axiomIndexer) {
		this.axiomIndexer = axiomIndexer;
	}

	public Void visit(IndexedClass indexedClass) {
		indexedClass.positiveOccurrenceNo += axiomIndexer.multiplicity;
		assert indexedClass.negativeOccurrenceNo >= 0;
		
		axiomIndexer.ontologyIndex.removeIfNoOccurrence(indexedClass);
		return null;
	}

	public Void visit(IndexedObjectIntersectionOf indexedObjectIntersectionOf) {
		indexedObjectIntersectionOf.positiveOccurrenceNo += axiomIndexer.multiplicity;
		assert indexedObjectIntersectionOf.negativeOccurrenceNo >= 0;
		
		indexedObjectIntersectionOf.firstConjunct.accept(this);
		indexedObjectIntersectionOf.secondConjunct.accept(this);
		
		axiomIndexer.ontologyIndex.removeIfNoOccurrence(indexedObjectIntersectionOf);
		return null;
	}

	public Void visit(IndexedObjectSomeValuesFrom indexedObjectSomeValuesFrom) {
		indexedObjectSomeValuesFrom.positiveOccurrenceNo += axiomIndexer.multiplicity;
		assert indexedObjectSomeValuesFrom.negativeOccurrenceNo >= 0;
		
		axiomIndexer.objectPropertyExpressionIndexer.visit(indexedObjectSomeValuesFrom.relation);
		indexedObjectSomeValuesFrom.filler.accept(this);
		
		axiomIndexer.ontologyIndex.removeIfNoOccurrence(indexedObjectSomeValuesFrom);
		return null;
	}
}
