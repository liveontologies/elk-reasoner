/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.hierarchy;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.reasoner.incremental.NonIncrementalChangeListener;

/**
 * A delegating visitor which notifies the
 * {@link NonIncrementalChangeCheckingVisitor} that some axiom represents a
 * change which cannot be processed incrementally by the reasoner
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class NonIncrementalChangeCheckingVisitor extends
		DelegatingElkAxiomVisitor implements ElkAxiomIndexingVisitor {

	private final NonIncrementalChangeListener<ElkAxiom> listener_;

	public NonIncrementalChangeCheckingVisitor(ElkAxiomIndexingVisitor visitor,
			NonIncrementalChangeListener<ElkAxiom> listener) {
		super(visitor);

		listener_ = listener;
	}

	public ElkAxiomIndexingVisitor getIndexingVisitor() {
		return (ElkAxiomIndexingVisitor) getVisitor();
	}

	@Override
	public Void visit(
			ElkEquivalentObjectPropertiesAxiom elkEquivalentObjectProperties) {
		listener_.notify(elkEquivalentObjectProperties);

		return super.visit(elkEquivalentObjectProperties);
	}

	@Override
	public Void visit(
			ElkReflexiveObjectPropertyAxiom elkReflexiveObjectPropertyAxiom) {
		listener_.notify(elkReflexiveObjectPropertyAxiom);

		return super.visit(elkReflexiveObjectPropertyAxiom);
	}

	@Override
	public Void visit(ElkSubObjectPropertyOfAxiom elkSubObjectPropertyOfAxiom) {
		listener_.notify(elkSubObjectPropertyOfAxiom);

		return super.visit(elkSubObjectPropertyOfAxiom);
	}

	@Override
	public Void visit(
			ElkTransitiveObjectPropertyAxiom elkTransitiveObjectPropertyAxiom) {
		listener_.notify(elkTransitiveObjectPropertyAxiom);

		return super.visit(elkTransitiveObjectPropertyAxiom);
	}

	@Override
	public void indexSubClassOfAxiom(ElkClassExpression subClass,
			ElkClassExpression superClass, ElkAxiom assertedAxiom) {
		getIndexingVisitor().indexSubClassOfAxiom(subClass, superClass, assertedAxiom);
	}

	@Override
	public void indexSubObjectPropertyOfAxiom(
			ElkSubObjectPropertyExpression subProperty,
			ElkObjectPropertyExpression superProperty) {
		getIndexingVisitor().indexSubObjectPropertyOfAxiom(subProperty,
				superProperty);
	}

	@Override
	public void indexClassAssertion(ElkIndividual individual,
			ElkClassExpression type, ElkAxiom assertedAxiom) {
		getIndexingVisitor().indexClassAssertion(individual, type, assertedAxiom);
	}

	@Override
	public void indexDisjointClassExpressions(
			List<? extends ElkClassExpression> list, ElkAxiom assertedAxiom) {
		getIndexingVisitor().indexDisjointClassExpressions(list, assertedAxiom);
	}

	@Override
	public void indexReflexiveObjectProperty(
			ElkObjectPropertyExpression reflexiveProperty) {
		getIndexingVisitor().indexReflexiveObjectProperty(reflexiveProperty);
	}

	@Override
	public IndexedClass indexClassDeclaration(ElkClass ec) {
		return getIndexingVisitor().indexClassDeclaration(ec);
	}

	@Override
	public IndexedObjectProperty indexObjectPropertyDeclaration(
			ElkObjectProperty eop) {
		return getIndexingVisitor().indexObjectPropertyDeclaration(eop);
	}

	@Override
	public IndexedIndividual indexNamedIndividualDeclaration(
			ElkNamedIndividual eni) {
		return getIndexingVisitor().indexNamedIndividualDeclaration(eni);
	}

	@Override
	public int getMultiplicity() {
		return getIndexingVisitor().getMultiplicity();
	}

}
