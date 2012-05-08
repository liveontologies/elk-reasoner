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

import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubDataPropertyOfAxiom;
import org.semanticweb.elk.owl.visitors.ElkDataPropertyAxiomVisitor;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;

/**
 * Implements the {@link ElkSubDataPropertyOfAxiom} interface by wrapping
 * instances of {@link OWLSubDataPropertyOfAxiom}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped object
 */
public class ElkSubDataPropertyOfAxiomWrap<T extends OWLSubDataPropertyOfAxiom>
		extends ElkDataPropertyAxiomWrap<T> implements
		ElkSubDataPropertyOfAxiom {

	public ElkSubDataPropertyOfAxiomWrap(T owlSubDataPropertyOfAxiom) {
		super(owlSubDataPropertyOfAxiom);
	}

	@Override
	public ElkDataPropertyExpression getSubDataPropertyExpression() {
		return converter.convert(this.owlObject.getSubProperty());
	}

	@Override
	public ElkDataPropertyExpression getSuperDataPropertyExpression() {
		return converter.convert(this.owlObject.getSuperProperty());
	}

	@Override
	public <O> O accept(ElkDataPropertyAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}
}