package org.semanticweb.elk.reasoner.indexing.conversion;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkAnnotationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAsymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeDefinitionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDifferentIndividualsAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointDataPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointUnionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentDataPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkFunctionalDataPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkHasKeyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkInverseObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIrreflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNegativeDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSWRLRule;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubDataPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedPropertyChain;

/**
 * A {@link ElkAxiomConverter} that does nothing for {@link ElkAnnotationAxiom}s
 * and throws {@link ElkIndexingUnsupportedException} for other types of axioms.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public class FailingElkAxiomConverter extends
		NoOpElkAnnotationAxiomConverter<Void> implements ElkAxiomConverter,
		ElkSubObjectPropertyExpressionVisitor<ModifiableIndexedPropertyChain> {

	private static <E> E fail(ElkObject expression) {
		throw new ElkIndexingUnsupportedException(expression);
	}

	@Override
	public Void visit(ElkClassAssertionAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkDifferentIndividualsAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkDataPropertyAssertionAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkNegativeDataPropertyAssertionAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkNegativeObjectPropertyAssertionAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkObjectPropertyAssertionAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkSameIndividualAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkDisjointClassesAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkDisjointUnionAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkEquivalentClassesAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkSubClassOfAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkDataPropertyDomainAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkDataPropertyRangeAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkDisjointDataPropertiesAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkEquivalentDataPropertiesAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkFunctionalDataPropertyAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkSubDataPropertyOfAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkDatatypeDefinitionAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkDeclarationAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkHasKeyAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkAsymmetricObjectPropertyAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkDisjointObjectPropertiesAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkEquivalentObjectPropertiesAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkFunctionalObjectPropertyAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkInverseFunctionalObjectPropertyAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkInverseObjectPropertiesAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkIrreflexiveObjectPropertyAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkObjectPropertyDomainAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkObjectPropertyRangeAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkReflexiveObjectPropertyAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkSubObjectPropertyOfAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkSymmetricObjectPropertyAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkTransitiveObjectPropertyAxiom axiom) {
		return fail(axiom);
	}

	@Override
	public Void visit(ElkSWRLRule axiom) {
		return fail(axiom);
	}

	@Override
	public ModifiableIndexedPropertyChain visit(ElkObjectProperty expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedPropertyChain visit(
			ElkObjectPropertyChain expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedPropertyChain visit(ElkObjectInverseOf expression) {
		return fail(expression);
	}

}
