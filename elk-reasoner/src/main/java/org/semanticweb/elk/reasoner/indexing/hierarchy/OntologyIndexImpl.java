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

import java.util.Collection;
import java.util.LinkedList;

import org.semanticweb.elk.owl.ElkAxiomProcessor;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.util.collections.Operations;

public class OntologyIndexImpl extends IndexedObjectCache implements
		OntologyIndex {

	private final IndexedClass indexedOwlThing;
	private final IndexedClass indexedOwlNothing;
	
	private final ElkObjectIndexerVisitor elkObjectIndexer;
	private final ElkAxiomIndexerVisitor axiomInserter;
	private final ElkAxiomIndexerVisitor axiomDeleter;

	protected Collection<IndexedObjectProperty> reflexiveObjectProperties;

	public OntologyIndexImpl() {
		this.elkObjectIndexer = new ElkObjectIndexerVisitor(this);
		this.axiomInserter = new ElkAxiomIndexerVisitor(this, true);
		this.axiomDeleter = new ElkAxiomIndexerVisitor(this, false);
		// index predefined entities
		// TODO: what to do if someone tries to delete them?
		this.indexedOwlThing = axiomInserter.indexClassDeclaration(PredefinedElkClass.OWL_THING);
		this.indexedOwlNothing = axiomInserter.indexClassDeclaration(PredefinedElkClass.OWL_NOTHING);
	}

	@Override
	public IndexedClassExpression getIndexed(ElkClassExpression representative) {
		IndexedClassExpression result = representative.accept(elkObjectIndexer);
		if (result.occurs())
			return result;
		else
			return null;
	}

	@Override
	public IndexedPropertyChain getIndexed(
			ElkSubObjectPropertyExpression elkSubObjectPropertyExpression) {
		IndexedPropertyChain result = elkSubObjectPropertyExpression
				.accept(elkObjectIndexer);
		if (result.occurs())
			return result;
		else
			return null;
	}

	@Override
	public Iterable<IndexedClassExpression> getIndexedClassExpressions() {
		return indexedClassExpressionLookup;
	}
	
	@Override 
	public int getIndexedClassExpressionCount() {
		return indexedClassExpressionLookup.size();
	}

	@Override
	public Iterable<IndexedClass> getIndexedClasses() {
		return Operations.filter(getIndexedClassExpressions(),
				IndexedClass.class);
	}

	@Override
	public int getIndexedClassCount() {
		return indexedClassCount;
	}
	
	@Override
	public Iterable<IndexedIndividual> getIndexedIndividuals() {
		return Operations.filter(getIndexedClassExpressions(),
				IndexedIndividual.class);
	}

	@Override
	public int getIndexedIndividualCount() {
		return indexedIndividualCount;
	}

	@Override
	public Iterable<IndexedPropertyChain> getIndexedPropertyChains() {
		return indexedPropertyChainLookup;
	}
	
	@Override 
	public int getIndexedPropertyChainCount() {
		return indexedPropertyChainLookup.size();
	}

	@Override
	public Iterable<IndexedObjectProperty> getIndexedObjectProperties() {
		return Operations.filter(getIndexedPropertyChains(),
				IndexedObjectProperty.class);
	}

	@Override
	public int getIndexedObjectPropertyCount() {
		return indexedObjectPropertyCount;
	}

	@Override
	public ElkAxiomProcessor getAxiomInserter() {
		return axiomInserter;
	}

	@Override
	public ElkAxiomProcessor getAxiomDeleter() {
		return axiomDeleter;
	}

	@Override
	public Iterable<IndexedObjectProperty> getReflexiveObjectProperties() {
		return reflexiveObjectProperties;
	}

	protected void addReflexiveObjectProperty(
			IndexedObjectProperty reflexiveObjectProperty) {
		if (reflexiveObjectProperties == null)
			reflexiveObjectProperties = new LinkedList<IndexedObjectProperty>();
		reflexiveObjectProperties.add(reflexiveObjectProperty);
	}

	protected boolean removeReflexiveObjectProperty(
			IndexedObjectProperty reflexiveObjectProperty) {
		boolean success = reflexiveObjectProperties.remove(reflexiveObjectProperty);
		if (reflexiveObjectProperties.isEmpty())
			reflexiveObjectProperties = null;
		return success;
	}

	@Override
	public IndexedClass getIndexedOwlThing() {
		return indexedOwlThing;
	}

	@Override
	public IndexedClass getIndexedOwlNothing() {
		return indexedOwlNothing;
	}

}
