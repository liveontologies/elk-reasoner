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
package org.semanticweb.elk.reasoner.indexing;

import org.semanticweb.elk.owl.ElkAxiomProcessor;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkAxiomIndexerVisitor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkObjectIndexerVisitor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexChange;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectLookup;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.util.collections.Operations;

public class OntologyIndexImpl extends IndexedObjectLookup implements
		OntologyIndex {

	private final ElkObjectIndexerVisitor elkObjectIndexer;
	private final ElkAxiomIndexerVisitor directAxiomInserter;
	private final ElkAxiomIndexerVisitor directAxiomDeleter;
	private final ElkAxiomIndexerVisitor incrementalAxiomInserter;
	private final ElkAxiomIndexerVisitor incrementalAxiomDeleter;
	private final IndexChange indexChange;

	public OntologyIndexImpl() {
		this.elkObjectIndexer = new ElkObjectIndexerVisitor(this);
		this.directAxiomInserter = ElkAxiomIndexerVisitor
				.getDirectAxiomIndexer(this, true);
		this.directAxiomDeleter = ElkAxiomIndexerVisitor.getDirectAxiomIndexer(
				this, false);
		this.indexChange = new IndexChange();
		this.incrementalAxiomInserter = ElkAxiomIndexerVisitor
				.getIncrementalAxiomIndexer(this, indexChange, true);
		this.incrementalAxiomDeleter = ElkAxiomIndexerVisitor
				.getIncrementalAxiomIndexer(this, indexChange, false);
		// index predefined entities
		// TODO: what to do if someone tries to delete them?
		directAxiomInserter.indexClassDeclaration(PredefinedElkClass.OWL_THING);
		directAxiomInserter
				.indexClassDeclaration(PredefinedElkClass.OWL_NOTHING);
	}

	public IndexedClassExpression getIndexed(ElkClassExpression representative) {
		IndexedClassExpression result = representative.accept(elkObjectIndexer);
		if (result.occurs())
			return result;
		else
			return null;
	}

	public IndexedPropertyChain getIndexed(
			ElkSubObjectPropertyExpression elkSubObjectPropertyExpression) {
		IndexedPropertyChain result = elkSubObjectPropertyExpression
				.accept(elkObjectIndexer);
		if (result.occurs())
			return result;
		else
			return null;
	}

	public Iterable<IndexedClassExpression> getIndexedClassExpressions() {
		return indexedClassExpressionLookup;
	}

	public Iterable<IndexedClass> getIndexedClasses() {
		return Operations.filter(getIndexedClassExpressions(),
				IndexedClass.class);
	}

	public int getIndexedClassCount() {
		return indexedClassCount;
	}

	public Iterable<IndexedPropertyChain> getIndexedPropertyChains() {
		return indexedPropertyChainLookup;
	}

	public Iterable<IndexedObjectProperty> getIndexedObjectProperties() {
		return Operations.filter(getIndexedPropertyChains(),
				IndexedObjectProperty.class);
	}

	public int getIndexedObjectPropertyCount() {
		return indexedObjectPropertyCount;
	}

	public ElkAxiomProcessor getDirectAxiomInserter() {
		return directAxiomInserter;
	}

	public ElkAxiomProcessor getDirectAxiomDeleter() {
		return directAxiomDeleter;
	}

	public ElkAxiomProcessor getIncrementalAxiomInserter() {
		return incrementalAxiomInserter;
	}

	public ElkAxiomProcessor getIncrementalAxiomDeleter() {
		return incrementalAxiomDeleter;
	}

	public IndexChange getIndexChange() {
		return this.indexChange;
	}

}
