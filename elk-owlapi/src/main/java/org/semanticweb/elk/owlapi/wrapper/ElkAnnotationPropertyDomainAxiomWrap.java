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

import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationPropertyDomainAxiom;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.visitors.ElkAnnotationAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkAnnotationPropertyDomainAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkPropertyAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkPropertyDomainAxiomVisitor;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;

/**
 * Implements the {@link ElkAnnotationPropertyDomainAxiom} interface by wrapping
 * instances of {@link OWLAnnotationPropertyDomainAxiom}
 * 
 * @author Frantisek Simancik
 * 
 * @param <T>
 *            the type of the wrapped object
 */
public class ElkAnnotationPropertyDomainAxiomWrap<T extends OWLAnnotationPropertyDomainAxiom>
		extends ElkAnnotationAxiomWrap<T> implements
		ElkAnnotationPropertyDomainAxiom {

	public ElkAnnotationPropertyDomainAxiomWrap(
			T owlAnnotationPropertyDomainAxiom) {
		super(owlAnnotationPropertyDomainAxiom);
	}

	@Override
	public ElkAnnotationProperty getProperty() {
		return converter.convert(this.owlObject.getProperty());
	}

	@Override
	public ElkIri getDomain() {
		return converter.convert(this.owlObject.getDomain());
	}

	@Override
	public <O> O accept(ElkAnnotationPropertyDomainAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(ElkPropertyDomainAxiomVisitor<O> visitor) {
		return accept((ElkAnnotationPropertyDomainAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkPropertyAxiomVisitor<O> visitor) {
		return accept((ElkAnnotationPropertyDomainAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkAnnotationAxiomVisitor<O> visitor) {
		return accept((ElkAnnotationPropertyDomainAxiomVisitor<O>) visitor);
	}
}