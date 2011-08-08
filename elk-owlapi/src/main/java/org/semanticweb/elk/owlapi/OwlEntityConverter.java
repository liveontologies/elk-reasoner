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

import org.semanticweb.elk.syntax.interfaces.ElkEntity;
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
	
	private static final OwlEntityConverter converter_ = new OwlEntityConverter();

	private OwlEntityConverter() {
	}

	static OwlEntityConverter getInstance() {
		return converter_;
	}
	
	public ElkEntity visit(OWLClass cls) {
		return OwlClassExpressionConverter.getInstance().visit(cls);
	}

	public ElkEntity visit(OWLObjectProperty property) {
		return OwlPropertyExpressionConverter.getInstance().visit(property);
	}

	public ElkEntity visit(OWLDataProperty property) {
		// TODO Support this constructor
		return null;
	}
	
	public ElkEntity visit(OWLNamedIndividual individual) {
		// TODO Support this constructor
		return null;
	}
	
	public ElkEntity visit(OWLDatatype datatype) {
		// TODO Support this constructor
		return null;
	}
	
	public ElkEntity visit(OWLAnnotationProperty property) {
		// TODO Support this constructor
		return null;
	}

}
