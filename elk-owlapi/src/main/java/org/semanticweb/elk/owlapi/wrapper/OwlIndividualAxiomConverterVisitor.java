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

import org.semanticweb.elk.owl.interfaces.ElkAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;

/**
 * 
 * An implementation of the visitor pattern for OWL axioms to convert OWL
 * individual axioms axioms to the corresponding ELK assertion axioms.
 * Conversion of unsupported axioms throws an {@link IllegalArgumentException}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public final class OwlIndividualAxiomConverterVisitor extends
		AbstractOwlAxiomConverterVisitor<ElkAssertionAxiom> {

	private static OwlIndividualAxiomConverterVisitor INSTANCE_ = new OwlIndividualAxiomConverterVisitor();

	private OwlIndividualAxiomConverterVisitor() {
	}

	public static OwlIndividualAxiomConverterVisitor getInstance() {
		return INSTANCE_;
	}

	protected static OwlConverter CONVERTER = OwlConverter.getInstance();

	@Override
	protected Class<ElkAssertionAxiom> getTargetClass() {
		return ElkAssertionAxiom.class;
	}

	@Override
	public ElkAssertionAxiom visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		return CONVERTER.convert(axiom);
	}

	@Override
	public ElkAssertionAxiom visit(OWLDifferentIndividualsAxiom axiom) {
		return CONVERTER.convert(axiom);
	}

	@Override
	public ElkAssertionAxiom visit(OWLObjectPropertyAssertionAxiom axiom) {
		return CONVERTER.convert(axiom);
	}

	@Override
	public ElkAssertionAxiom visit(OWLClassAssertionAxiom axiom) {
		return CONVERTER.convert(axiom);
	}

	@Override
	public ElkAssertionAxiom visit(OWLDataPropertyAssertionAxiom axiom) {
		return CONVERTER.convert(axiom);
	}

	@Override
	public ElkAssertionAxiom visit(OWLSameIndividualAxiom axiom) {
		return CONVERTER.convert(axiom);
	}
}
