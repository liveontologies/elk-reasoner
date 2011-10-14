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
import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.predefined.PredefinedElkDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkAxiomDeleterVisitor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkAxiomInserterVisitor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkObjectIndexerVisitor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectCache;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.util.collections.Iterables;

public class OntologyIndexImpl extends IndexedObjectCache implements
		OntologyIndex {

	private final ElkObjectIndexerVisitor elkObjectIndexer;
	private final ElkAxiomProcessor axiomInserter;
	private final ElkAxiomProcessor axiomDeleter;

	public OntologyIndexImpl() {
		this.elkObjectIndexer = new ElkObjectIndexerVisitor(this);
		this.axiomInserter = new ElkAxiomInserterVisitor(this);
		this.axiomDeleter = new ElkAxiomDeleterVisitor(this);
		// index predefined axioms
		// TODO: what to do if someone tries to delete them?
		for (ElkDeclarationAxiom builtInDeclaration : PredefinedElkDeclarationAxiom.DECLARATIONS) {
			this.axiomInserter.process(builtInDeclaration);
		}
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
		IndexedPropertyChain result = elkSubObjectPropertyExpression.accept(elkObjectIndexer);
		if (result.occurs())
			return result;
		else
			return null;
	}

	public Iterable<IndexedClassExpression> getIndexedClassExpressions() {
		return indexedClassExpressionLookup.values();
	}

	public Iterable<IndexedClass> getIndexedClasses() {
		return Iterables.filter(getIndexedClassExpressions(),
				IndexedClass.class);
	}

	public int getIndexedClassCount() {
		return indexedClassCount;
	}

	public Iterable<IndexedPropertyChain> getIndexedPropertyChains() {
		return indexedPropertyChainLookup.values();
	}

	public Iterable<IndexedObjectProperty> getIndexedObjectProperties() {
		return Iterables.filter(getIndexedPropertyChains(),
				IndexedObjectProperty.class);
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
