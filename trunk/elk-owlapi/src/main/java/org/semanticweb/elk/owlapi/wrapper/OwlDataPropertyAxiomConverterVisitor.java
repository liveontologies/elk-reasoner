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

import org.semanticweb.elk.owl.interfaces.ElkDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;

/**
 * An implementation of the visitor pattern for OWL axioms to convert OWL data
 * property axioms to the corresponding ELK data property axioms. Conversion of
 * unsupported axioms throws an {@link IllegalArgumentException}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class OwlDataPropertyAxiomConverterVisitor extends
		AbstractOwlAxiomConverterVisitor<ElkDataPropertyAxiom> {

	private static OwlDataPropertyAxiomConverterVisitor INSTANCE_ = new OwlDataPropertyAxiomConverterVisitor();

	private OwlDataPropertyAxiomConverterVisitor() {
	}

	public static OwlDataPropertyAxiomConverterVisitor getInstance() {
		return INSTANCE_;
	}

	protected static OwlConverter CONVERTER = OwlConverter.getInstance();

	@Override
	protected Class<ElkDataPropertyAxiom> getTargetClass() {
		return ElkDataPropertyAxiom.class;
	}

	@Override
	public ElkDataPropertyAxiom visit(
			OWLDataPropertyDomainAxiom owlDataPropertyDomainAxiom) {
		return CONVERTER.convert(owlDataPropertyDomainAxiom);
	}

	@Override
	public ElkDataPropertyAxiom visit(
			OWLDataPropertyRangeAxiom owlDataPropertyRangeAxiom) {
		return CONVERTER.convert(owlDataPropertyRangeAxiom);
	}

	@Override
	public ElkDataPropertyAxiom visit(
			OWLDisjointDataPropertiesAxiom owlDisjointDataPropertiesAxiom) {
		return CONVERTER.convert(owlDisjointDataPropertiesAxiom);
	}

	@Override
	public ElkDataPropertyAxiom visit(
			OWLEquivalentDataPropertiesAxiom owlEquivalentDataProperties) {
		return CONVERTER.convert(owlEquivalentDataProperties);
	}

	@Override
	public ElkDataPropertyAxiom visit(
			OWLFunctionalDataPropertyAxiom owlFunctionalDataPropertyAxiom) {
		return CONVERTER.convert(owlFunctionalDataPropertyAxiom);
	}

	@Override
	public ElkDataPropertyAxiom visit(
			OWLSubDataPropertyOfAxiom owlSubDataPropertyOfAxiom) {
		return CONVERTER.convert(owlSubDataPropertyOfAxiom);
	}

}
