/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
 * @author Yevgeny Kazakov, Jul 3, 2011
 */
package org.semanticweb.elk.reasoner.indexing;

/**
 * For indexing all occurrences of class expressions
 * 
 * @author Yevgeny Kazakov
 * 
 */
class ClassExpressionIndexer implements
		IndexedClassExpressionVisitor<Void> {

	protected final AxiomIndexer axiomIndexer;

	/**
	 * @param axiomIndexer
	 */
	public ClassExpressionIndexer(AxiomIndexer axiomIndexer) {
		this.axiomIndexer = axiomIndexer;
	}
	
	protected void updateOccurrenceNo(IndexedClassExpression ice) {
		ice.occurrenceNo += axiomIndexer.multiplicity;
		assert ice.occurrenceNo >= 0;
		if (ice.occurrenceNo == 0)
			axiomIndexer.ontologyIndex.remove(ice);
	}

	public Void visit(IndexedClass indexedClass) {
		updateOccurrenceNo(indexedClass);
		
		return null;
	}

	public Void visit(IndexedObjectIntersectionOf indexedObjectIntersectionOf) {
		updateOccurrenceNo(indexedObjectIntersectionOf);

		indexedObjectIntersectionOf.firstConjunct.accept(this);
		indexedObjectIntersectionOf.secondConjunct.accept(this);

		return null;
	}

	public Void visit(IndexedObjectSomeValuesFrom indexedObjectSomeValuesFrom) {
		updateOccurrenceNo(indexedObjectSomeValuesFrom);
		
		indexedObjectSomeValuesFrom.relation.accept(axiomIndexer.objectPropertyExpressionIndexer);
		indexedObjectSomeValuesFrom.filler.accept(this);

		return null;
	}

}
