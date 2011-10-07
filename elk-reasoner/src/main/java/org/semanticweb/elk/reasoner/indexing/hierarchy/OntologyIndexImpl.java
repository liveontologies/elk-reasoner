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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.NoSuchElementException;

import org.semanticweb.elk.owl.ElkAxiomProcessor;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.util.collections.Iterables;

public class OntologyIndexImpl extends IndexedObjectCanonizer 
		implements OntologyIndex {
	
	private final ElkObjectIndexerVisitor failingIndexer;
	private final ElkAxiomProcessor axiomInserter;
	private final ElkAxiomProcessor axiomDeleter;
	
	public OntologyIndexImpl() {
		this.failingIndexer = new ElkObjectIndexerVisitor(
				new FailingIndexedObjectFilter(this));
		this.axiomInserter = new ElkAxiomInserterVisitor(this);
		this.axiomDeleter = new ElkAxiomDeleterVisitor(this);
	}
	
	public IndexedClassExpression getIndexed(
			ElkClassExpression representative) {
		try {
			return representative.accept(failingIndexer);
		}
		catch (NoSuchElementException e) {
			return null;
		}
	}
	
	public IndexedPropertyChain getIndexed(
			ElkSubObjectPropertyExpression elkSubObjectPropertyExpression) {
		try {
			return elkSubObjectPropertyExpression.accept(failingIndexer);
		}
		catch (NoSuchElementException e) {
			return null;
		}
	}

	public Iterable<IndexedClassExpression> getIndexedClassExpressions() {
		return indexedClassExpressionLookup.values();
	}
	
	public Iterable<IndexedClass> getIndexedClasses() {
		return Iterables.filter(getIndexedClassExpressions(), IndexedClass.class);
	}
	
	public int getIndexedClassCount() {
		return indexedClassCount;
	}

	public Iterable<IndexedPropertyChain> getIndexedPropertyChains() {
		return indexedPropertyChainLookup.values();
	}
	
	public Iterable<IndexedObjectProperty> getIndexedObjectProperties() {
		return Iterables.filter(getIndexedPropertyChains(), IndexedObjectProperty.class);
	}

	public int getIndexedObjectPropertyCount() {
		return indexedObjectPropertyCount;
	}

	public ElkAxiomProcessor getAxiomInserter() {
		return axiomInserter;
	}

	public ElkAxiomProcessor getAxiomDeleter() {
		return axiomDeleter;
	}
}
