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

import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;

/**
 * An implementation of the visitor pattern for OWL axioms to convert OWL object
 * property axioms to the corresponding ELK object property axioms. Conversion
 * of unsupported axioms throws an {@link IllegalArgumentException}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class OwlObjectPropertyAxiomConverterVisitor extends
		AbstractOwlAxiomConverterVisitor<ElkObjectPropertyAxiom> {

	private static OwlObjectPropertyAxiomConverterVisitor INSTANCE_ = new OwlObjectPropertyAxiomConverterVisitor();

	protected static OwlConverter CONVERTER = OwlConverter.getInstance();

	public static OwlObjectPropertyAxiomConverterVisitor getInstance() {
		return INSTANCE_;
	}

	private OwlObjectPropertyAxiomConverterVisitor() {
	}

	@Override
	protected Class<ElkObjectPropertyAxiom> getTargetClass() {
		return ElkObjectPropertyAxiom.class;
	}

	@Override
	public ElkObjectPropertyAxiom visit(
			OWLAsymmetricObjectPropertyAxiom owlAsymmetricObjectPropertyAxiom) {
		return CONVERTER.convert(owlAsymmetricObjectPropertyAxiom);
	}

	@Override
	public ElkObjectPropertyAxiom visit(
			OWLDisjointObjectPropertiesAxiom owlDisjointObjectPropertiesAxiom) {
		return CONVERTER.convert(owlDisjointObjectPropertiesAxiom);
	}

	@Override
	public ElkObjectPropertyAxiom visit(
			OWLEquivalentObjectPropertiesAxiom owlEquivalentObjectProperties) {
		return CONVERTER.convert(owlEquivalentObjectProperties);
	}

	@Override
	public ElkObjectPropertyAxiom visit(
			OWLFunctionalObjectPropertyAxiom owlFunctionalObjectPropertyAxiom) {
		return CONVERTER.convert(owlFunctionalObjectPropertyAxiom);
	}

	@Override
	public ElkObjectPropertyAxiom visit(
			OWLInverseFunctionalObjectPropertyAxiom owlInverseFunctionalObjectPropertyAxiom) {
		return CONVERTER.convert(owlInverseFunctionalObjectPropertyAxiom);
	}

	@Override
	public ElkObjectPropertyAxiom visit(
			OWLInverseObjectPropertiesAxiom owlInverseObjectPropertiesAxiom) {
		return CONVERTER.convert(owlInverseObjectPropertiesAxiom);
	}

	@Override
	public ElkObjectPropertyAxiom visit(
			OWLIrreflexiveObjectPropertyAxiom owlIrreflexiveObjectPropertyAxiom) {
		return CONVERTER.convert(owlIrreflexiveObjectPropertyAxiom);
	}

	@Override
	public ElkObjectPropertyAxiom visit(
			OWLObjectPropertyDomainAxiom owlObjectPropertyDomainAxiom) {
		return CONVERTER.convert(owlObjectPropertyDomainAxiom);
	}

	@Override
	public ElkObjectPropertyAxiom visit(
			OWLObjectPropertyRangeAxiom owlObjectPropertyRangeAxiom) {
		return CONVERTER.convert(owlObjectPropertyRangeAxiom);
	}

	@Override
	public ElkObjectPropertyAxiom visit(
			OWLReflexiveObjectPropertyAxiom owlReflexiveObjectPropertyAxiom) {
		return CONVERTER.convert(owlReflexiveObjectPropertyAxiom);
	}

	@Override
	public ElkObjectPropertyAxiom visit(
			OWLSubObjectPropertyOfAxiom owlSubObjectPropertyOfAxiom) {
		return CONVERTER.convert(owlSubObjectPropertyOfAxiom);
	}

	@Override
	public ElkObjectPropertyAxiom visit(
			OWLSubPropertyChainOfAxiom owlSubPropertyChainOfAxiom) {
		return new ElkSubObjectPropertyChainOfAxiomWrap<OWLSubPropertyChainOfAxiom>(
				owlSubPropertyChainOfAxiom);
	}

	@Override
	public ElkObjectPropertyAxiom visit(
			OWLSymmetricObjectPropertyAxiom owlSymmetricObjectPropertyAxiom) {
		return CONVERTER.convert(owlSymmetricObjectPropertyAxiom);
	}

	@Override
	public ElkObjectPropertyAxiom visit(
			OWLTransitiveObjectPropertyAxiom owlTransitiveObjectPropertyAxiom) {
		return CONVERTER.convert(owlTransitiveObjectPropertyAxiom);
	}

}
