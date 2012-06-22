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

import org.semanticweb.elk.owl.interfaces.ElkDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.visitors.ElkAssertionAxiomVisitor;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;

/**
 * Implements the {@link ElkDataPropertyAssertionAxiom} interface by wrapping
 * instances of {@link OWLDataPropertyAssertionAxiom}
 * 
 * @author Yevgeny Kazakov
 * 
 * @param <T>
 *            the type of the wrapped object
 */
public class ElkDataPropertyAssertionAxiomWrap<T extends OWLDataPropertyAssertionAxiom>
		extends ElkAssertionAxiomWrap<T> implements
		ElkDataPropertyAssertionAxiom {

	public ElkDataPropertyAssertionAxiomWrap(T owlObjectPropertyAssertionAxiom) {
		super(owlObjectPropertyAssertionAxiom);
	}

	@Override
	public ElkIndividual getSubject() {
		return converter.convert(this.owlObject.getSubject());
	}

	@Override
	public ElkLiteral getObject() {
		return converter.convert(this.owlObject.getObject());
	}

	@Override
	public ElkDataPropertyExpression getProperty() {
		return converter.convert(this.owlObject.getProperty());
	}

	@Override
	public <O> O accept(ElkAssertionAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}
}
