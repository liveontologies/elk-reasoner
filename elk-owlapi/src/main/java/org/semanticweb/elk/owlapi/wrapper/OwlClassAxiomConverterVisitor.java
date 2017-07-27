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

import org.semanticweb.elk.owl.interfaces.ElkClassAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

/**
 * 
 * An implementation of the visitor pattern for OWL axioms to convert OWL class
 * axioms to the corresponding ELK class axioms.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public final class OwlClassAxiomConverterVisitor extends
		AbstractOwlAxiomConverterVisitor<ElkClassAxiom> {

	private static OwlClassAxiomConverterVisitor INSTANCE_ = new OwlClassAxiomConverterVisitor();

	private OwlClassAxiomConverterVisitor() {
	}

	public static OwlClassAxiomConverterVisitor getInstance() {
		return INSTANCE_;
	}

	protected static OwlConverter CONVERTER = OwlConverter.getInstance();

	@Override
	protected Class<ElkClassAxiom> getTargetClass() {
		return ElkClassAxiom.class;
	}

	@Override
	public ElkClassAxiom visit(OWLDisjointClassesAxiom owlDisjointClasses) {
		return CONVERTER.convert(owlDisjointClasses);
	}

	@Override
	public ElkClassAxiom visit(OWLDisjointUnionAxiom owlDisjointUnionAxiom) {
		return CONVERTER.convert(owlDisjointUnionAxiom);
	}

	@Override
	public ElkClassAxiom visit(
			OWLEquivalentClassesAxiom owlEquivalentClassesAxiom) {
		return CONVERTER.convert(owlEquivalentClassesAxiom);
	}

	@Override
	public ElkClassAxiom visit(OWLSubClassOfAxiom owlSubClassOfAxiom) {
		return CONVERTER.convert(owlSubClassOfAxiom);
	}

}
