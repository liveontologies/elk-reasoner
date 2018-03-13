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

import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.visitors.ElkObjectInverseOfVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectPropertyExpressionVisitor;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * Implements the {@link ElkObjectInverseOf} interface by wrapping instances of
 * {@link OWLObjectProperty}. We cannot wrap {@link OWLObjectInverseOf} because
 * it can be nested. For example, object property expressions such as
 * {@code ObjectInverseOf(ObjectInverseOf r)} are allowed in OWL API, but are
 * not valid in OWL 2 and for this reason are not allowed to be constructed for
 * {@link ElkObjectPropertyExpression}.
 * 
 * @author Yevgeny Kazakov
 * 
 * @param <T>
 *            the type of the wrapped object
 */
public class ElkObjectInverseOfWrap<T extends OWLObjectProperty> extends
		ElkObjectPropertyExpressionWrap<T> implements ElkObjectInverseOf {

	public ElkObjectInverseOfWrap(T owlObjectInverseOf) {
		super(owlObjectInverseOf);
	}

	@Override
	public ElkObjectProperty getObjectProperty() {
		return new ElkObjectPropertyWrap<OWLObjectProperty>(owlObject);
	}

	@Override
	public <O> O accept(ElkObjectPropertyExpressionVisitor<O> visitor) {
		return accept((ElkObjectInverseOfVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkObjectInverseOfVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
