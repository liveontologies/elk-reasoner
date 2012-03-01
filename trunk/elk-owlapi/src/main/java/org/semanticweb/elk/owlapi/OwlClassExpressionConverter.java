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

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

/**
 * Conversion of OWL class expressions to Elk class expressions.
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class OwlClassExpressionConverter implements
		OWLClassExpressionVisitorEx<ElkClassExpression> {

	protected final ElkObjectFactory objectFactory;
	protected final OwlPropertyExpressionConverter propertyExpressionConverter;

	public OwlClassExpressionConverter(ElkObjectFactory objectFactory,
			OwlPropertyExpressionConverter propertyExpressionConverter) {
		this.objectFactory = objectFactory;
		this.propertyExpressionConverter = propertyExpressionConverter;
	}

	public ElkClass visit(OWLClass ce) {
		// TODO is this needed?
		if (ce.isOWLThing())
			return objectFactory.getOwlThing();
		else if (ce.isOWLNothing())
			return objectFactory.getOwlNothing();
		else
			return objectFactory.getClass(new ElkFullIri(ce.getIRI().toString()));
	}

	public ElkObjectIntersectionOf visit(OWLObjectIntersectionOf ce) {
		List<OWLClassExpression> owlConjuncts = ce.getOperandsAsList();
		List<ElkClassExpression> elkConjuncts = new ArrayList<ElkClassExpression>();
		for (OWLClassExpression cce : owlConjuncts) {
			elkConjuncts.add(cce.accept(this));
		}
		return objectFactory.getObjectIntersectionOf(elkConjuncts);
	}

	public ElkClassExpression visit(OWLObjectUnionOf ce) {
		// TODO Support this constructor
		throw new ConverterException(ce.getClassExpressionType().getName()
				+ " not supported");
	}

	public ElkClassExpression visit(OWLObjectComplementOf ce) {
		// TODO Support this constructor
		throw new ConverterException(ce.getClassExpressionType().getName()
				+ " not supported");
	}

	public ElkObjectSomeValuesFrom visit(OWLObjectSomeValuesFrom ce) {
		return objectFactory.getObjectSomeValuesFrom(
				ce.getProperty().accept(propertyExpressionConverter), ce
						.getFiller().accept(this));
	}

	public ElkClassExpression visit(OWLObjectAllValuesFrom ce) {
		// TODO Support this constructor
		throw new ConverterException(ce.getClassExpressionType().getName()
				+ " not supported");
	}

	public ElkClassExpression visit(OWLObjectHasValue ce) {
		// TODO Support this constructor
		throw new ConverterException(ce.getClassExpressionType().getName()
				+ " not supported");
	}

	public ElkClassExpression visit(OWLObjectMinCardinality ce) {
		// TODO Support this constructor
		throw new ConverterException(ce.getClassExpressionType().getName()
				+ " not supported");
	}

	public ElkClassExpression visit(OWLObjectExactCardinality ce) {
		// TODO Support this constructor
		throw new ConverterException(ce.getClassExpressionType().getName()
				+ " not supported");
	}

	public ElkClassExpression visit(OWLObjectMaxCardinality ce) {
		// TODO Support this constructor
		throw new ConverterException(ce.getClassExpressionType().getName()
				+ " not supported");
	}

	public ElkClassExpression visit(OWLObjectHasSelf ce) {
		// TODO Support this constructor
		throw new ConverterException(ce.getClassExpressionType().getName()
				+ " not supported");
	}

	public ElkClassExpression visit(OWLObjectOneOf ce) {
		// TODO Support this constructor
		throw new ConverterException(ce.getClassExpressionType().getName()
				+ " not supported");
	}

	public ElkClassExpression visit(OWLDataSomeValuesFrom ce) {
		// TODO Support this constructor
		throw new ConverterException(ce.getClassExpressionType().getName()
				+ " not supported");
	}

	public ElkClassExpression visit(OWLDataAllValuesFrom ce) {
		// TODO Support this constructor
		throw new ConverterException(ce.getClassExpressionType().getName()
				+ " not supported");
	}

	public ElkClassExpression visit(OWLDataHasValue ce) {
		// TODO Support this constructor
		throw new ConverterException(ce.getClassExpressionType().getName()
				+ " not supported");
	}

	public ElkClassExpression visit(OWLDataMinCardinality ce) {
		// TODO Support this constructor
		throw new ConverterException(ce.getClassExpressionType().getName()
				+ " not supported");
	}

	public ElkClassExpression visit(OWLDataExactCardinality ce) {
		// TODO Support this constructor
		throw new ConverterException(ce.getClassExpressionType().getName()
				+ " not supported");
	}

	public ElkClassExpression visit(OWLDataMaxCardinality ce) {
		// TODO Support this constructor
		throw new ConverterException(ce.getClassExpressionType().getName()
				+ " not supported");
	}

}
