/*
 * #%L
 * ELK OWL API Binding
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
package org.semanticweb.elk.owlapi.wrapper;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
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
 * A prototype implementation of {@link OWLAxiomVisitorEx} interface for
 * conversion of owl axioms. All visitor methods throw exceptions and relevant
 * methods should be overridden in subclasses. This addresses the problem for
 * the lack of corresponding visitor interfaces for subclasses of
 * {@link OWLAxiom} in OWL API, such as {@link OWLClassAxiom}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the output of the visitor
 */
public abstract class AbstractOwlAxiomConverterVisitor<T extends ElkAxiom>
		implements OWLAxiomVisitorEx<T> {

	protected abstract Class<T> getTargetClass();

	public T visit(OWLSubAnnotationPropertyOfAxiom axiom) {
		throw new IllegalArgumentException(
				OWLSubAnnotationPropertyOfAxiom.class
						+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLAnnotationPropertyDomainAxiom axiom) {
		throw new IllegalArgumentException(
				OWLAnnotationPropertyDomainAxiom.class
						+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLAnnotationPropertyRangeAxiom axiom) {
		throw new IllegalArgumentException(
				OWLAnnotationPropertyRangeAxiom.class
						+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLSubClassOfAxiom axiom) {
		throw new IllegalArgumentException(OWLSubClassOfAxiom.class
				+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
		throw new IllegalArgumentException(
				OWLNegativeObjectPropertyAssertionAxiom.class
						+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLAsymmetricObjectPropertyAxiom axiom) {
		throw new IllegalArgumentException(
				OWLAsymmetricObjectPropertyAxiom.class
						+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLReflexiveObjectPropertyAxiom axiom) {
		throw new IllegalArgumentException(
				OWLReflexiveObjectPropertyAxiom.class
						+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLDisjointClassesAxiom axiom) {
		throw new IllegalArgumentException(OWLDisjointClassesAxiom.class
				+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLDataPropertyDomainAxiom axiom) {
		throw new IllegalArgumentException(OWLDataPropertyDomainAxiom.class
				+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLObjectPropertyDomainAxiom axiom) {
		throw new IllegalArgumentException(OWLObjectPropertyDomainAxiom.class
				+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLEquivalentObjectPropertiesAxiom axiom) {
		throw new IllegalArgumentException(
				OWLEquivalentObjectPropertiesAxiom.class
						+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		throw new IllegalArgumentException(
				OWLNegativeDataPropertyAssertionAxiom.class
						+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLDifferentIndividualsAxiom axiom) {
		throw new IllegalArgumentException(OWLDifferentIndividualsAxiom.class
				+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLDisjointDataPropertiesAxiom axiom) {
		throw new IllegalArgumentException(OWLDisjointDataPropertiesAxiom.class
				+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLDisjointObjectPropertiesAxiom axiom) {
		throw new IllegalArgumentException(
				OWLDisjointObjectPropertiesAxiom.class
						+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLObjectPropertyRangeAxiom axiom) {
		throw new IllegalArgumentException(OWLObjectPropertyRangeAxiom.class
				+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLObjectPropertyAssertionAxiom axiom) {
		throw new IllegalArgumentException(
				OWLObjectPropertyAssertionAxiom.class
						+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLFunctionalObjectPropertyAxiom axiom) {
		throw new IllegalArgumentException(
				OWLFunctionalObjectPropertyAxiom.class
						+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLSubObjectPropertyOfAxiom axiom) {
		throw new IllegalArgumentException(OWLSubObjectPropertyOfAxiom.class
				+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLDisjointUnionAxiom axiom) {
		throw new IllegalArgumentException(OWLDisjointUnionAxiom.class
				+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLDeclarationAxiom axiom) {
		throw new IllegalArgumentException(OWLDeclarationAxiom.class
				+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLAnnotationAssertionAxiom axiom) {
		throw new IllegalArgumentException(OWLAnnotationAssertionAxiom.class
				+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLSymmetricObjectPropertyAxiom axiom) {
		throw new IllegalArgumentException(
				OWLSymmetricObjectPropertyAxiom.class
						+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLDataPropertyRangeAxiom axiom) {
		throw new IllegalArgumentException(OWLDataPropertyRangeAxiom.class
				+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLFunctionalDataPropertyAxiom axiom) {
		throw new IllegalArgumentException(OWLFunctionalDataPropertyAxiom.class
				+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLEquivalentDataPropertiesAxiom axiom) {
		throw new IllegalArgumentException(
				OWLEquivalentDataPropertiesAxiom.class
						+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLClassAssertionAxiom axiom) {
		throw new IllegalArgumentException(OWLClassAssertionAxiom.class
				+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLEquivalentClassesAxiom axiom) {
		throw new IllegalArgumentException(OWLEquivalentClassesAxiom.class
				+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLDataPropertyAssertionAxiom axiom) {
		throw new IllegalArgumentException(OWLDataPropertyAssertionAxiom.class
				+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLTransitiveObjectPropertyAxiom axiom) {
		throw new IllegalArgumentException(
				OWLTransitiveObjectPropertyAxiom.class
						+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		throw new IllegalArgumentException(
				OWLIrreflexiveObjectPropertyAxiom.class
						+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLSubDataPropertyOfAxiom axiom) {
		throw new IllegalArgumentException(OWLSubDataPropertyOfAxiom.class
				+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		throw new IllegalArgumentException(
				OWLInverseFunctionalObjectPropertyAxiom.class
						+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLSameIndividualAxiom axiom) {
		throw new IllegalArgumentException(OWLSameIndividualAxiom.class
				+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLSubPropertyChainOfAxiom axiom) {
		throw new IllegalArgumentException(OWLSubPropertyChainOfAxiom.class
				+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLInverseObjectPropertiesAxiom axiom) {
		throw new IllegalArgumentException(
				OWLInverseObjectPropertiesAxiom.class
						+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLHasKeyAxiom axiom) {
		throw new IllegalArgumentException(OWLHasKeyAxiom.class
				+ " cannot be converted to " + getTargetClass());
	}

	public T visit(OWLDatatypeDefinitionAxiom axiom) {
		throw new IllegalArgumentException(OWLDatatypeDefinitionAxiom.class
				+ " cannot be converted to " + getTargetClass());
	}

	public T visit(SWRLRule rule) {
		throw new IllegalArgumentException(SWRLRule.class
				+ " cannot be converted to " + getTargetClass());
	}

}
