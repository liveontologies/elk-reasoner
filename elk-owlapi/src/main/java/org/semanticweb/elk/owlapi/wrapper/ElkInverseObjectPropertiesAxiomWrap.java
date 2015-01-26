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

import org.semanticweb.elk.owl.interfaces.ElkInverseObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.visitors.ElkInverseObjectPropertiesAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectPropertyAxiomVisitor;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;

/**
 * Implements the {@link ElkInverseObjectPropertiesAxiom} interface by wrapping
 * instances of {@link OWLInverseObjectPropertiesAxiom}
 * 
 * @author Yevgeny Kazakov
 * 
 * @param <T>
 *            the type of the wrapped object
 */
public class ElkInverseObjectPropertiesAxiomWrap<T extends OWLInverseObjectPropertiesAxiom>
		extends ElkObjectPropertyAxiomWrap<T> implements
		ElkInverseObjectPropertiesAxiom {

	public ElkInverseObjectPropertiesAxiomWrap(T owlInverseObjectPropertiesAxiom) {
		super(owlInverseObjectPropertiesAxiom);
	}

	@Override
	public ElkObjectPropertyExpression getFirstObjectPropertyExpression() {
		return converter.convert(this.owlObject.getFirstProperty());
	}

	@Override
	public ElkObjectPropertyExpression getSecondObjectPropertyExpression() {
		return converter.convert(this.owlObject.getSecondProperty());
	}

	@Override
	public <O> O accept(ElkObjectPropertyAxiomVisitor<O> visitor) {
		return accept((ElkInverseObjectPropertiesAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkInverseObjectPropertiesAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
