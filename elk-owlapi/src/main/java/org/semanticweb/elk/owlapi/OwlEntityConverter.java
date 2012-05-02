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
 * @author Yevgeny Kazakov, Jul 4, 2011
 */
package org.semanticweb.elk.owlapi;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * @author Yevgeny Kazakov
 * 
 */
public class OwlEntityConverter implements OWLEntityVisitorEx<ElkEntity> {

	protected final ElkObjectFactory objectFactory;
	protected final OwlClassExpressionConverter classExpressionConverter;
	protected final OwlPropertyExpressionConverter propertyExpressionConverter;

	public OwlEntityConverter(ElkObjectFactory objectFactory,
			OwlClassExpressionConverter classExpressionConverter,
			OwlPropertyExpressionConverter propertyExpressionConverter) {
		this.objectFactory = objectFactory;
		this.classExpressionConverter = classExpressionConverter;
		this.propertyExpressionConverter = propertyExpressionConverter;
	}

	@Override
	public ElkEntity visit(OWLClass cls) {
		return classExpressionConverter.visit(cls);
	}

	@Override
	public ElkEntity visit(OWLObjectProperty property) {
		return propertyExpressionConverter.visit(property);
	}

	@Override
	public ElkEntity visit(OWLDataProperty property) {
		// TODO Support this constructor
		return null;
	}

	@Override
	public ElkEntity visit(OWLNamedIndividual individual) {
		// TODO Support this constructor
		return null;
	}

	@Override
	public ElkEntity visit(OWLDatatype datatype) {
		// TODO Support this constructor
		return null;
	}

	@Override
	public ElkEntity visit(OWLAnnotationProperty property) {
		// TODO Support this constructor
		return null;
	}

}
