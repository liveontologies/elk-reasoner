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
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

/**
 * A visitor class for converting the inverse of the given
 * {@link OWLObjectPropertyExpression} to the corresponding
 * {@link ElkObjectPropertyExpression}. This is needed because instances of
 * {@link OWLObjectInverseOf} cannot be directly converted to
 * {@link ElkObjectInverseOf} since inverse of object properties can be nested
 * in OWL API but not in OWL 2 and thus in ELK OWL model.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class OwlObjectInverseOfConverterVisitor implements
		OWLPropertyExpressionVisitorEx<ElkObjectPropertyExpression> {

	private static OwlObjectInverseOfConverterVisitor INSTANCE_ = new OwlObjectInverseOfConverterVisitor();

	private static OwlObjectPropertyExpressionConverterVisitor OWL_OBJECT_PROPERTRY_CONVERTER_ = OwlObjectPropertyExpressionConverterVisitor
			.getInstance();

	private OwlObjectInverseOfConverterVisitor() {
	}

	public static OwlObjectInverseOfConverterVisitor getInstance() {
		return INSTANCE_;
	}

	@Override
	public ElkObjectPropertyExpression visit(OWLObjectProperty property) {
		return new ElkObjectInverseOfWrap<OWLObjectProperty>(property);
	}

	@Override
	public ElkObjectPropertyExpression visit(OWLObjectInverseOf property) {
		return property.getInverse().accept(OWL_OBJECT_PROPERTRY_CONVERTER_);
	}

	@Override
	public ElkObjectPropertyExpression visit(OWLDataProperty property) {
		throw new IllegalArgumentException("invers of "
				+ property.getClass().getSimpleName()
				+ " cannot be converted to "
				+ ElkObjectPropertyExpression.class.getSimpleName());
	}

	@Override
	public ElkObjectPropertyExpression visit(OWLAnnotationProperty property) {
		throw new IllegalArgumentException("invers of "
				+ property.getClass().getSimpleName()
				+ " cannot be converted to "
				+ ElkObjectPropertyExpression.class.getSimpleName());
	}

}
