/*
 * #%L
 * ELK OWL API Binding
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
/**
 * 
 */
package org.semanticweb.elk.owlapi.wrapper;

import org.semanticweb.elk.owl.interfaces.ElkAnnotationSubject;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationValue;
import org.semanticweb.elk.owl.interfaces.ElkAnonymousIndividual;
import org.semanticweb.elk.owl.interfaces.literals.ElkLiteral;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationSubjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAnnotationValueVisitorEx;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

/**
 * Converts OWL annotation subjects and values (IRIs, anonymous individuals, and literals)
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public class OwlAnnotationSubjectValueVisitor implements OWLAnnotationSubjectVisitorEx<ElkAnnotationSubject>, OWLAnnotationValueVisitorEx<ElkAnnotationValue> {

	private final static OwlAnnotationSubjectValueVisitor INSTANCE_ = new OwlAnnotationSubjectValueVisitor();
	protected static OwlConverter CONVERTER = OwlConverter.getInstance();
	
	static OwlAnnotationSubjectValueVisitor getInstance() {
		return INSTANCE_;
	}
	
	public ElkAnnotationSubject visit(OWLAnnotationSubject subject) {
		return subject.accept(this);
	}
	
	public ElkAnnotationValue visit(OWLAnnotationValue value) {
		return value.accept(this);
	}
	
	@Override
	public ElkIri visit(IRI iri) {
		return CONVERTER.convert(iri);
	}

	@Override
	public ElkAnonymousIndividual visit(OWLAnonymousIndividual anon) {
		return CONVERTER.convert(anon);
	}

	@Override
	public ElkLiteral visit(OWLLiteral literal) {
		return CONVERTER.convert(literal);
	}
}
