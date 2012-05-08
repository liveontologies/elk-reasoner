/*
 * #%L
 * ELK OWL API
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
/**
 * @author Yevgeny Kazakov, Jul 1, 2011
 */
package org.semanticweb.elk.owlapi;

import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

/**
 * Conversion of OWL object properties to Elk object properties.
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class OwlPropertyExpressionConverter implements
		OWLPropertyExpressionVisitorEx<ElkObjectPropertyExpression> {
	
	protected final ElkObjectFactory objectFactory;

	public OwlPropertyExpressionConverter(ElkObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}

	@Override
	public ElkObjectProperty visit(OWLObjectProperty property) {
		if (property.isOWLTopObjectProperty())
			return objectFactory.getOwlTopObjectProperty();
		else if (property.isOWLBottomObjectProperty())
			return objectFactory.getOwlBottomObjectProperty();
		else
			return objectFactory.getObjectProperty(new ElkFullIri(property.getIRI().toString()));
	}

	@Override
	public ElkObjectPropertyExpression visit(OWLObjectInverseOf property) {
		// TODO Support this constructor
		throw new ConverterException("OWLObjectInverseOf not supported");
	}

	@Override
	public ElkObjectPropertyExpression visit(OWLDataProperty property) {
		// TODO Auto-generated method stub
		throw new ConverterException(property.getEntityType().getName()
				+ " not supported");
	}

}
