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

import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * An implementation of the visitor pattern for OWL entities to convert OWL
 * entities ranges to ELK entities.
 * 
 * @author "Yevgeny Kazakov"
 */
public class OwlEntityConverterVisitor
		implements OWLEntityVisitorEx<ElkEntity> {

	protected static OwlConverter CONVERTER = OwlConverter.getInstance();

	private static OwlEntityConverterVisitor INSTANCE_ = new OwlEntityConverterVisitor();

	public static OwlEntityConverterVisitor getInstance() {
		return INSTANCE_;
	}

	private OwlEntityConverterVisitor() {
	}

	@Override
	public ElkAnnotationProperty visit(
			OWLAnnotationProperty owlAnnotationproperty) {
		return CONVERTER.convert(owlAnnotationproperty);
	}

	@Override
	public ElkClass visit(OWLClass owlClass) {
		return CONVERTER.convert(owlClass);
	}

	@Override
	public ElkDataProperty visit(OWLDataProperty owlDataProperty) {
		return CONVERTER.convert(owlDataProperty);
	}

	@Override
	public ElkDatatype visit(OWLDatatype owlDatatype) {
		return CONVERTER.convert(owlDatatype);
	}

	@Override
	public ElkObjectProperty visit(OWLObjectProperty owlObjectProperty) {
		return CONVERTER.convert(owlObjectProperty);
	}

	@Override
	public ElkNamedIndividual visit(OWLNamedIndividual owlNamedIndividual) {
		return CONVERTER.convert(owlNamedIndividual);
	}

}
