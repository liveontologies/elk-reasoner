package org.semanticweb.elk.owlapi.query;

/*-
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;

/**
 * An {@link OWLAxiomVisitor} that always fails
 * 
 * @author Yevgeny Kazakov
 */
public class FailingOwlAxiomVisitor implements OWLAxiomVisitor {

	public <O> O defaultVisit(OWLAxiom axiom) {
		throw new RuntimeException("Unexpected axiom: " + axiom);
	}

	@Override
	public void visit(OWLAnnotationAssertionAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLAnnotationPropertyDomainAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLSubClassOfAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLDisjointClassesAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLDataPropertyDomainAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLObjectPropertyDomainAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLDifferentIndividualsAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLDisjointDataPropertiesAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLObjectPropertyRangeAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLObjectPropertyAssertionAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLSubObjectPropertyOfAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLDisjointUnionAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLDataPropertyRangeAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLFunctionalDataPropertyAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLClassAssertionAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLEquivalentClassesAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLDataPropertyAssertionAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLSubDataPropertyOfAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLSameIndividualAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLSubPropertyChainOfAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLInverseObjectPropertiesAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLHasKeyAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(SWRLRule rule) {
		defaultVisit(rule);
	}

	@Override
	public void visit(OWLDeclarationAxiom axiom) {
		defaultVisit(axiom);
	}

	@Override
	public void visit(OWLDatatypeDefinitionAxiom axiom) {
		defaultVisit(axiom);
	}

}
