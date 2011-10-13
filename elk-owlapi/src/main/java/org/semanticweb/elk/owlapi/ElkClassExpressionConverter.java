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

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkDataAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
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

	private static ElkClassExpressionConverter INSTANCE_ = new ElkClassExpressionConverter();

	private ElkClassExpressionConverter() {
	}

	public static ElkClassExpressionConverter getInstance() {
		return INSTANCE_;
	}

	public OWLClass visit(ElkClass elkClass) {
		String iri = elkClass.getIri().toString();
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

	public OWLClassExpression visit(ElkObjectHasValue elkObjectHasValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLClassExpression visit(ElkObjectOneOf elkObjectOneOf) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLClassExpression visit(ElkObjectHasSelf elkObjectHasSelf) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLClassExpression visit(
			ElkObjectAllValuesFrom elkObjectAllValuesFrom) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLClassExpression visit(ElkObjectComplementOf elkObjectComplementOf) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLClassExpression visit(
			ElkObjectExactCardinality elkObjectExactCardinality) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLClassExpression visit(
			ElkObjectMaxCardinality elkObjectMaxCardinality) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLClassExpression visit(
			ElkObjectMinCardinality elkObjectMaxCardinality) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLClassExpression visit(ElkObjectUnionOf elkObjectUnionOf) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLClassExpression visit(ElkDataHasValue elkDataHasValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLClassExpression visit(ElkDataMaxCardinality elkDataMaxCardinality) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLClassExpression visit(ElkDataMinCardinality elkDataMinCardinality) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLClassExpression visit(
			ElkDataExactCardinality elkDataExactCardinality) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLClassExpression visit(ElkDataSomeValuesFrom elkDataSomeValuesFrom) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLClassExpression visit(ElkDataAllValuesFrom elkDataAllValuesFrom) {
		// TODO Auto-generated method stub
		return null;
	}

}
