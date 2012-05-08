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

import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.visitors.ElkAssertionAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;

/**
 * Implements the {@link ElkClassAssertionAxiom} interface by wrapping instances
 * of {@link OWLClassAssertionAxiom}
 * 
 * @author Yevgeny Kazakov
 * 
 * @param <T>
 *            the type of the wrapped object
 */
public class ElkClassAssertionAxiomWrap<T extends OWLClassAssertionAxiom>
		extends ElkAssertionAxiomWrap<T> implements ElkClassAssertionAxiom {

	public ElkClassAssertionAxiomWrap(T owlClassAssertionAxiom) {
		super(owlClassAssertionAxiom);
	}

	@Override
	public ElkIndividual getIndividual() {
		return converter.convert(this.owlObject.getIndividual());
	}

	@Override
	public ElkClassExpression getClassExpression() {
		return converter.convert(this.owlObject.getClassExpression());
	}

	@Override
	public <O> O accept(ElkAssertionAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
