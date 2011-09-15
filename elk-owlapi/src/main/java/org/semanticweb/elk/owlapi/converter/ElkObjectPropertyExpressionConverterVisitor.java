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
package org.semanticweb.elk.owlapi.converter;

import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owlapi.wrapper.ElkObjectPropertyWrap;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

/**
 * Converting instances of {@link OWLObjectProperty} to the corresponding instances of
 * {@link ElkObjectProperty}
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ElkObjectPropertyExpressionConverterVisitor implements
		OWLPropertyExpressionVisitorEx<ElkObjectProperty> {

	private static ElkObjectPropertyExpressionConverterVisitor instance_ = new ElkObjectPropertyExpressionConverterVisitor();

	private ElkObjectPropertyExpressionConverterVisitor() {
	}

	public static ElkObjectPropertyExpressionConverterVisitor getInstance() {
		return instance_;
	}

	public ElkObjectProperty visit(OWLObjectProperty property) {		
		return new ElkObjectPropertyWrap<OWLObjectProperty>(property);
	}

	public ElkObjectProperty visit(OWLObjectInverseOf property) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkObjectProperty visit(OWLDataProperty property) {
		throw new IllegalArgumentException(property.getClass()
				+ " cannot be converted to "
				+ ElkObjectProperty.class);
	}

}
