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
package org.semanticweb.elk.alc.indexing.hierarchy;

import java.util.List;

import org.semanticweb.elk.alc.indexing.visitors.IndexedAxiomVisitor;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;

/**
 * Creating indexed axioms for testing without changing occurrence counters
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class TestAxiomIndexerVisitor extends AbstractElkAxiomIndexerVisitor
		implements ElkAxiomIndexingVisitor {

	/**
	 * A {@link IndexObjectConverter}s to convert axioms
	 */
	private final IndexObjectConverter converter_;

	private final IndexedAxiomVisitor<Void> testAxiomVisitor_;

	/**
	 * @param index
	 *            the {@link ModifiableOntologyIndex} used for indexing axioms
	 */
	public TestAxiomIndexerVisitor(final OntologyIndex index,
			IndexedAxiomVisitor<Void> testAxiomVisitor) {
		this.converter_ = new IndexObjectConverter(
				index.getIndexedObjectCache());
		this.testAxiomVisitor_ = testAxiomVisitor;
	}

	@Override
	public int getMultiplicity() {
		return 0;
	}

	@Override
	public void indexSubClassOfAxiom(ElkClassExpression subElkClass,
			ElkClassExpression superElkClass) {

		IndexedClassExpression subIndexedClass = subElkClass.accept(converter_);

		IndexedClassExpression superIndexedClass = superElkClass
				.accept(converter_);
		testAxiomVisitor_.visit(new IndexedSubClassOfAxiom(subIndexedClass,
				superIndexedClass));
	}

	@Override
	public IndexedClass indexClassDeclaration(ElkClass ec) {
		return (IndexedClass) ec.accept(converter_);
	}

	@Override
	public IndexedObjectProperty indexObjectPropertyDeclaration(
			ElkObjectProperty ep) {
		return ep.accept(converter_);
	}

	@Override
	public void indexSubObjectPropertyOfAxiom(ElkObjectProperty subProperty,
			ElkObjectProperty superProperty) {
		// no-op
	}

	@Override
	public void indexTransitiveProperty(ElkObjectProperty property) {
		// no-op
	}

	@Override
	public void indexDisjointClassesAxiom(
			List<? extends ElkClassExpression> disjointClasses) {
		// TODO
	}

	
}
