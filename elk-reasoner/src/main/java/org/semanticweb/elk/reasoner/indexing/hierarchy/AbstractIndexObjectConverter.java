/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.owl.interfaces.ElkAnonymousIndividual;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkIndividualVisitor;
import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor;

/**
 * A converter from {@link ElkClassExpression}s,
 * {@link ElkSubObjectPropertyExpression}s, and {@link ElkIndividual}s to
 * corresponding {@link IndexedObject}s that simply throws an
 * {@link ElkIndexingException} on all arguments.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public abstract class AbstractIndexObjectConverter implements
		ElkClassExpressionVisitor<IndexedClassExpression>,
		ElkSubObjectPropertyExpressionVisitor<IndexedPropertyChain>,
		ElkIndividualVisitor<IndexedIndividual> {

	protected static IndexedClassExpression defaultVisit(
			ElkClassExpression expression) {
		throw new ElkIndexingUnsupportedException(expression);
	}

	protected static IndexedPropertyChain defaultVisit(
			ElkSubObjectPropertyExpression expression) {
		throw new ElkIndexingUnsupportedException(expression);
	}

	protected static IndexedIndividual defaultVisit(ElkIndividual expression) {
		throw new ElkIndexingUnsupportedException(expression);
	}

	@Override
	public IndexedClassExpression visit(ElkClass elkClass) {
		return defaultVisit(elkClass);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectAllValuesFrom elkObjectAllValuesFrom) {
		return defaultVisit(elkObjectAllValuesFrom);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectComplementOf elkObjectComplementOf) {
		return defaultVisit(elkObjectComplementOf);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectExactCardinality elkObjectExactCardinality) {
		return defaultVisit(elkObjectExactCardinality);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectExactCardinalityQualified elkObjectExactCardinalityQualified) {
		return defaultVisit(elkObjectExactCardinalityQualified);
	}

	@Override
	public IndexedClassExpression visit(ElkObjectHasSelf elkObjectHasSelf) {
		return defaultVisit(elkObjectHasSelf);
	}

	@Override
	public IndexedClassExpression visit(ElkObjectHasValue elkObjectHasValue) {
		return defaultVisit(elkObjectHasValue);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectIntersectionOf elkObjectIntersectionOf) {
		return defaultVisit(elkObjectIntersectionOf);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectMaxCardinality elkObjectMaxCardinality) {
		return defaultVisit(elkObjectMaxCardinality);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectMaxCardinalityQualified elkObjectMaxCardinalityQualified) {
		return defaultVisit(elkObjectMaxCardinalityQualified);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectMinCardinality elkObjectMinCardinality) {
		return defaultVisit(elkObjectMinCardinality);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectMinCardinalityQualified elkObjectMinCardinalityQualified) {
		return defaultVisit(elkObjectMinCardinalityQualified);
	}

	@Override
	public IndexedClassExpression visit(ElkObjectOneOf elkObjectOneOf) {
		return defaultVisit(elkObjectOneOf);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectSomeValuesFrom elkObjectSomeValuesFrom) {
		return defaultVisit(elkObjectSomeValuesFrom);
	}

	@Override
	public IndexedClassExpression visit(ElkObjectUnionOf elkObjectUnionOf) {
		return defaultVisit(elkObjectUnionOf);
	}

	@Override
	public IndexedClassExpression visit(ElkDataHasValue elkDataHasValue) {
		return defaultVisit(elkDataHasValue);
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataMaxCardinality elkDataMaxCardinality) {
		return defaultVisit(elkDataMaxCardinality);
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataMaxCardinalityQualified elkDataMaxCardinalityQualified) {
		return defaultVisit(elkDataMaxCardinalityQualified);
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataMinCardinality elkDataMinCardinality) {
		return defaultVisit(elkDataMinCardinality);
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataMinCardinalityQualified elkDataMinCardinalityQualified) {
		return defaultVisit(elkDataMinCardinalityQualified);
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataExactCardinality elkDataExactCardinality) {
		return defaultVisit(elkDataExactCardinality);
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataExactCardinalityQualified elkDataExactCardinalityQualified) {
		return defaultVisit(elkDataExactCardinalityQualified);
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataSomeValuesFrom elkDataSomeValuesFrom) {
		return defaultVisit(elkDataSomeValuesFrom);
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataAllValuesFrom elkDataAllValuesFrom) {
		return defaultVisit(elkDataAllValuesFrom);
	}

	@Override
	public IndexedPropertyChain visit(ElkObjectInverseOf elkObjectInverseOf) {
		return defaultVisit(elkObjectInverseOf);
	}

	@Override
	public IndexedPropertyChain visit(ElkObjectProperty elkObjectProperty) {
		return defaultVisit(elkObjectProperty);
	}

	@Override
	public IndexedPropertyChain visit(
			ElkObjectPropertyChain elkObjectPropertyChain) {
		return defaultVisit(elkObjectPropertyChain);
	}

	@Override
	public IndexedIndividual visit(ElkAnonymousIndividual elkAnonymousIndividual) {
		return defaultVisit(elkAnonymousIndividual);
	}

	@Override
	public IndexedIndividual visit(ElkNamedIndividual elkNamedIndividual) {
		return defaultVisit(elkNamedIndividual);
	}
}
