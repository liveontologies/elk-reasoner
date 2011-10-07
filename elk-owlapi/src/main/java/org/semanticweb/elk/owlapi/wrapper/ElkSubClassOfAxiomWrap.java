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

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.visitors.ElkClassAxiomVisitor;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

/**
 * Implements the {@link ElkSubClassOfAxiom} interface by wrapping instances of
 * {@link OWLSubClassOfAxiom}
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ElkSubClassOfAxiomWrap<T extends OWLSubClassOfAxiom> extends
		ElkClassAxiomWrap<T> implements ElkSubClassOfAxiom {

	public ElkSubClassOfAxiomWrap(T owlSubClassOfAxiom) {
		super(owlSubClassOfAxiom);
	}

	public ElkClassExpression getSubClassExpression() {
		return converter.convert(this.owlObject.getSubClass());
	}

	public ElkClassExpression getSuperClassExpression() {
		return converter.convert(this.owlObject.getSuperClass());
	}

	@Override
	public <O> O accept(ElkClassAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
