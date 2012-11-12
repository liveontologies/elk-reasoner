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
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkIndividualVisitor;
import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor;

/**
 * Visitor for {@link ElkClassExpression}s,
 * {@link ElkSubObjectPropertyExpression}s, and {@link ElkIndividual}s that
 * simply throws an {@link IndexingException} on all arguments.
 * 
 * @author Frantisek Simancik
 * 
 */
public abstract class AbstractElkObjectIndexerVisitor implements
		ElkClassExpressionVisitor<IndexedClassExpression>,
		ElkSubObjectPropertyExpressionVisitor<IndexedPropertyChain>,
		ElkIndividualVisitor<IndexedIndividual> {

	@Override
	public IndexedClassExpression visit(ElkClass elkClass) {
		throw new ElkIndexingException(elkClass);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectAllValuesFrom elkObjectAllValuesFrom) {
		throw new ElkIndexingException(elkObjectAllValuesFrom);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectComplementOf elkObjectComplementOf) {
		throw new ElkIndexingException(elkObjectComplementOf);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectExactCardinality elkObjectExactCardinality) {
		throw new ElkIndexingException(elkObjectExactCardinality);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectExactCardinalityQualified elkObjectExactCardinalityQualified) {
		throw new ElkIndexingException(elkObjectExactCardinalityQualified);
	}

	@Override
	public IndexedClassExpression visit(ElkObjectHasSelf elkObjectHasSelf) {
		throw new ElkIndexingException(elkObjectHasSelf);
	}

	@Override
	public IndexedClassExpression visit(ElkObjectHasValue elkObjectHasValue) {
		throw new ElkIndexingException(elkObjectHasValue);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectIntersectionOf elkObjectIntersectionOf) {
		throw new ElkIndexingException(elkObjectIntersectionOf);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectMaxCardinality elkObjectMaxCardinality) {
		throw new ElkIndexingException(elkObjectMaxCardinality);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectMaxCardinalityQualified elkObjectMaxCardinalityQualified) {
		throw new ElkIndexingException(elkObjectMaxCardinalityQualified);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectMinCardinality elkObjectMinCardinality) {
		throw new ElkIndexingException(elkObjectMinCardinality);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectMinCardinalityQualified elkObjectMinCardinalityQualified) {
		throw new ElkIndexingException(elkObjectMinCardinalityQualified);
	}

	@Override
	public IndexedClassExpression visit(ElkObjectOneOf elkObjectOneOf) {
		throw new ElkIndexingException(elkObjectOneOf);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectSomeValuesFrom elkObjectSomeValuesFrom) {
		throw new ElkIndexingException(elkObjectSomeValuesFrom);
	}

	@Override
	public IndexedClassExpression visit(ElkObjectUnionOf elkObjectUnionOf) {
		throw new ElkIndexingException(elkObjectUnionOf);
	}

	@Override
	public IndexedClassExpression visit(ElkDataHasValue elkDataHasValue) {
		throw new ElkIndexingException(elkDataHasValue);
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataMaxCardinality elkDataMaxCardinality) {
		throw new ElkIndexingException(elkDataMaxCardinality);
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataMaxCardinalityQualified elkDataMaxCardinalityQualified) {
		throw new ElkIndexingException(elkDataMaxCardinalityQualified);
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataMinCardinality elkDataMinCardinality) {
		throw new ElkIndexingException(elkDataMinCardinality);
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataMinCardinalityQualified elkDataMinCardinalityQualified) {
		throw new ElkIndexingException(elkDataMinCardinalityQualified);
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataExactCardinality elkDataExactCardinality) {
		throw new ElkIndexingException(elkDataExactCardinality);
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataExactCardinalityQualified elkDataExactCardinalityQualified) {
		throw new ElkIndexingException(elkDataExactCardinalityQualified);
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataSomeValuesFrom elkDataSomeValuesFrom) {
		throw new ElkIndexingException(elkDataSomeValuesFrom);
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataAllValuesFrom elkDataAllValuesFrom) {
		throw new ElkIndexingException(elkDataAllValuesFrom);
	}

	@Override
	public IndexedObjectProperty visit(ElkObjectInverseOf elkObjectInverseOf) {
		throw new ElkIndexingException(elkObjectInverseOf);
	}

	@Override
	public IndexedPropertyChain visit(ElkObjectProperty elkObjectProperty) {
		throw new ElkIndexingException(elkObjectProperty);
	}

	@Override
	public IndexedPropertyChain visit(
			ElkObjectPropertyChain elkObjectPropertyChain) {
		throw new ElkIndexingException(elkObjectPropertyChain);
	}

	@Override
	public IndexedIndividual visit(ElkAnonymousIndividual elkAnonymousIndividual) {
		throw new ElkIndexingException(elkAnonymousIndividual);
	}

	@Override
	public IndexedIndividual visit(ElkNamedIndividual elkNamedIndividual) {
		throw new ElkIndexingException(elkNamedIndividual);
	}
}
