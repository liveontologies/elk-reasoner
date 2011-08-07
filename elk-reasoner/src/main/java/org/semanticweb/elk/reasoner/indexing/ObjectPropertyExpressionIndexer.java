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

/**
 * For indexing object properties.
 * 
 * @author Frantisek Simancik
 * 
 */
class ObjectPropertyExpressionIndexer
		implements IndexedPropertyExpressionVisitor<Void> {

	protected AxiomIndexer axiomIndexer;
	
	public ObjectPropertyExpressionIndexer(AxiomIndexer axiomIndexer) {
		this.axiomIndexer = axiomIndexer;
	}

	public Void visit(IndexedObjectProperty indexedObjectProperty) {
		indexedObjectProperty.occurrenceNo += axiomIndexer.multiplicity;
		assert indexedObjectProperty.occurrenceNo >= 0;
		if (indexedObjectProperty.occurrenceNo == 0)
			axiomIndexer.ontologyIndex.remove(indexedObjectProperty);

		return null;
	}

	public Void visit(IndexedPropertyComposition indexedPropertyComposition) {
		indexedPropertyComposition.occurrenceNo += axiomIndexer.multiplicity;
		assert indexedPropertyComposition.occurrenceNo >= 0;
		if (indexedPropertyComposition.occurrenceNo == 0)
			axiomIndexer.ontologyIndex.remove(indexedPropertyComposition);
		
		indexedPropertyComposition.leftProperty.accept(this);
		indexedPropertyComposition.rightProperty.accept(this);
		if (!indexedPropertyComposition.isAuxiliary())
			indexedPropertyComposition.superProperty.accept(this);
		
		return null;
	}
}
