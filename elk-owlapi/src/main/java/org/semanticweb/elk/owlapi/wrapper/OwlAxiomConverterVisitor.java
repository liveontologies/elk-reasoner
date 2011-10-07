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
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;

/**
 * An implementation of the visitor pattern for OWL axioms to convert OWL axioms
 * to ELK axioms. Conversion of unsupported axioms throws an
 * {@link IllegalArgumentException}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class OwlAxiomConverterVisitor extends
		AbstractOwlAxiomConverterVisitor<ElkAxiom> {

	private static OwlAxiomConverterVisitor INSTANCE_ = new OwlAxiomConverterVisitor();

	protected static OwlConverter CONVERTER = OwlConverter.getInstance();

	public static OwlAxiomConverterVisitor getInstance() {
		return INSTANCE_;
	}

	private OwlAxiomConverterVisitor() {
	}

	@Override
	protected Class<ElkAxiom> getTargetClass() {
		return ElkAxiom.class;
	}

	@Override
	public ElkAxiom visit(OWLClassAssertionAxiom axiom) {
		return CONVERTER.convert(axiom);
	}

	@Override
	public ElkAxiom visit(OWLDataPropertyAssertionAxiom axiom) {
		return CONVERTER.convert(axiom);
	}

	@Override
	public ElkAxiom visit(OWLDataPropertyDomainAxiom owlDataPropertyDomainAxiom) {
		return CONVERTER.convert(owlDataPropertyDomainAxiom);
	}

	@Override
	public ElkAxiom visit(OWLDataPropertyRangeAxiom owlDataPropertyRangeAxiom) {
		return CONVERTER.convert(owlDataPropertyRangeAxiom);
	}

	@Override
	public ElkAxiom visit(OWLDifferentIndividualsAxiom axiom) {
		return CONVERTER.convert(axiom);
	}

	@Override
	public ElkAxiom visit(OWLDisjointClassesAxiom owlDisjointClasses) {
		return CONVERTER.convert(owlDisjointClasses);
	}

	@Override
	public ElkAxiom visit(
			OWLDisjointDataPropertiesAxiom owlDisjointDataPropertiesAxiom) {
		return CONVERTER.convert(owlDisjointDataPropertiesAxiom);
	}

	@Override
	public ElkAxiom visit(OWLDisjointUnionAxiom owlDisjointUnionAxiom) {
		return CONVERTER.convert(owlDisjointUnionAxiom);
	}

	@Override
	public ElkAxiom visit(OWLEquivalentClassesAxiom owlEquivalentClassesAxiom) {
		return CONVERTER.convert(owlEquivalentClassesAxiom);
	}

	@Override
	public ElkAxiom visit(
			OWLEquivalentDataPropertiesAxiom owlEquivalentDataProperties) {
		return CONVERTER.convert(owlEquivalentDataProperties);
	}

	@Override
	public ElkAxiom visit(
			OWLFunctionalDataPropertyAxiom owlFunctionalDataPropertyAxiom) {
		return CONVERTER.convert(owlFunctionalDataPropertyAxiom);
	}

	@Override
	public ElkAxiom visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		return CONVERTER.convert(axiom);
	}

	@Override
	public ElkAxiom visit(OWLObjectPropertyAssertionAxiom axiom) {
		return CONVERTER.convert(axiom);
	}

	@Override
	public ElkAxiom visit(OWLSameIndividualAxiom axiom) {
		return CONVERTER.convert(axiom);
	}

	@Override
	public ElkAxiom visit(OWLSubClassOfAxiom owlSubClassOfAxiom) {
		return CONVERTER.convert(owlSubClassOfAxiom);
	}

	@Override
	public ElkAxiom visit(OWLSubDataPropertyOfAxiom owlSubDataPropertyOfAxiom) {
		return CONVERTER.convert(owlSubDataPropertyOfAxiom);
	}

}
