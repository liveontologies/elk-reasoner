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

import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.syntax.ElkClassExpressionVisitor;
import org.semanticweb.elk.syntax.ElkObjectIntersectionOf;
import org.semanticweb.elk.syntax.ElkObjectSomeValuesFrom;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;

/**
 * @author Yevgeny Kazakov
 * 
 */
public final class ElkClassExpressionConverter implements
		ElkClassExpressionVisitor<OWLClassExpression> {

	final OWLDataFactory owlDataFactory = OWLManager.getOWLDataFactory();
	private static final ElkClassExpressionConverter converter = new ElkClassExpressionConverter();

	private ElkClassExpressionConverter() {
	}

	static ElkClassExpressionConverter getInstance() {
		return converter;
	}

	public OWLClass visit(ElkClass elkClass) {
		String iri = elkClass.getIri();
		if (elkClass.equals(ElkClass.ELK_OWL_THING))
			return owlDataFactory.getOWLThing();
		else if (elkClass.equals(ElkClass.ELK_OWL_NOTHING))
			return owlDataFactory.getOWLNothing();
		else
			return owlDataFactory.getOWLClass(IRI.create(iri));
	}

	public OWLObjectIntersectionOf visit(
			ElkObjectIntersectionOf elkObjectIntersectionOf) {
		// TODO Support this constructor
		throw new ConverterException("Not yet implemented.");
	}

	public OWLObjectSomeValuesFrom visit(
			ElkObjectSomeValuesFrom elkObjectSomeValuesFrom) {
		// TODO Support this constructor
		throw new ConverterException("Not yet implemented.");
	}

}
