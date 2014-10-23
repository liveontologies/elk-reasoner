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
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;

/**
 * Converter of ElkClassExpressions to OWL API class expressions.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
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

	@Override
	public OWLClass visit(ElkClass elkClass) {
		return ElkEntityConverter.getInstance().visit(elkClass);
	}

	@Override
	public OWLClassExpression visit(ElkDataAllValuesFrom elkDataAllValuesFrom) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLClassExpression visit(
			ElkDataExactCardinalityUnqualified elkDataExactCardinality) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLClassExpression visit(
			ElkDataExactCardinalityQualified elkDataExactCardinalityQualified) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLClassExpression visit(ElkDataHasValue elkDataHasValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLClassExpression visit(ElkDataMaxCardinalityUnqualified elkDataMaxCardinality) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLClassExpression visit(
			ElkDataMaxCardinalityQualified elkDataMaxCardinalityQualified) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLClassExpression visit(ElkDataMinCardinalityUnqualified elkDataMinCardinality) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLClassExpression visit(
			ElkDataMinCardinalityQualified elkDataMinCardinalityQualified) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLClassExpression visit(ElkDataSomeValuesFrom elkDataSomeValuesFrom) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLClassExpression visit(
			ElkObjectAllValuesFrom elkObjectAllValuesFrom) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLClassExpression visit(ElkObjectComplementOf elkObjectComplementOf) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLClassExpression visit(
			ElkObjectExactCardinalityUnqualified elkObjectExactCardinality) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLClassExpression visit(
			ElkObjectExactCardinalityQualified elkObjectExactCardinalityQualified) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLClassExpression visit(ElkObjectHasSelf elkObjectHasSelf) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLClassExpression visit(ElkObjectHasValue elkObjectHasValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLObjectIntersectionOf visit(
			ElkObjectIntersectionOf elkObjectIntersectionOf) {
		// TODO Support this constructor
		throw new ConverterException("Not yet implemented.");
	}

	@Override
	public OWLClassExpression visit(
			ElkObjectMaxCardinalityUnqualified elkObjectMaxCardinality) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLClassExpression visit(
			ElkObjectMaxCardinalityQualified elkObjectMaxCardinalityQualified) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLClassExpression visit(
			ElkObjectMinCardinalityUnqualified elkObjectMaxCardinality) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLClassExpression visit(
			ElkObjectMinCardinalityQualified elkObjectMinCardinalityQualified) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLClassExpression visit(ElkObjectOneOf elkObjectOneOf) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLObjectSomeValuesFrom visit(
			ElkObjectSomeValuesFrom elkObjectSomeValuesFrom) {
		// TODO Support this constructor
		throw new ConverterException("Not yet implemented.");
	}

	@Override
	public OWLClassExpression visit(ElkObjectUnionOf elkObjectUnionOf) {
		// TODO Auto-generated method stub
		return null;
	}

}
