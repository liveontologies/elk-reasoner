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
import org.semanticweb.elk.owl.visitors.ElkDataPropertyExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkIndividualVisitor;
import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor;

/**
 * Visitor for ElkObjects classes, properties, and individuals that simply throws an
 * unsupported IndexingException on all arguments.
 * 
 * @author Frantisek Simancik
 * 
 */
public abstract class AbstractElkObjectIndexerVisitor implements
		ElkClassExpressionVisitor<IndexedClassExpression>,
		ElkSubObjectPropertyExpressionVisitor<IndexedPropertyChain>,
		ElkDataPropertyExpressionVisitor<IndexedDataProperty>,
		ElkIndividualVisitor<IndexedIndividual> {

	@Override
	public IndexedClassExpression visit(ElkClass elkClass) {
		throw new IndexingException(elkClass);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectAllValuesFrom elkObjectAllValuesFrom) {
		throw new IndexingException(elkObjectAllValuesFrom);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectComplementOf elkObjectComplementOf) {
		throw new IndexingException(elkObjectComplementOf);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectExactCardinality elkObjectExactCardinality) {
		throw new IndexingException(elkObjectExactCardinality);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectExactCardinalityQualified elkObjectExactCardinalityQualified) {
		throw new IndexingException(elkObjectExactCardinalityQualified);
	}

	@Override
	public IndexedClassExpression visit(ElkObjectHasSelf elkObjectHasSelf) {
		throw new IndexingException(elkObjectHasSelf);
	}

	@Override
	public IndexedClassExpression visit(ElkObjectHasValue elkObjectHasValue) {
		throw new IndexingException(elkObjectHasValue);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectIntersectionOf elkObjectIntersectionOf) {
		throw new IndexingException(elkObjectIntersectionOf);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectMaxCardinality elkObjectMaxCardinality) {
		throw new IndexingException(elkObjectMaxCardinality);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectMaxCardinalityQualified elkObjectMaxCardinalityQualified) {
		throw new IndexingException(elkObjectMaxCardinalityQualified);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectMinCardinality elkObjectMinCardinality) {
		throw new IndexingException(elkObjectMinCardinality);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectMinCardinalityQualified elkObjectMinCardinalityQualified) {
		throw new IndexingException(elkObjectMinCardinalityQualified);
	}

	@Override
	public IndexedClassExpression visit(ElkObjectOneOf elkObjectOneOf) {
		throw new IndexingException(elkObjectOneOf);
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectSomeValuesFrom elkObjectSomeValuesFrom) {
		throw new IndexingException(elkObjectSomeValuesFrom);
	}

	@Override
	public IndexedClassExpression visit(ElkObjectUnionOf elkObjectUnionOf) {
		throw new IndexingException(elkObjectUnionOf);
	}

	@Override
	public IndexedClassExpression visit(ElkDataHasValue elkDataHasValue) {
		throw new IndexingException(elkDataHasValue);
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataMaxCardinality elkDataMaxCardinality) {
		throw new IndexingException(elkDataMaxCardinality);
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataMaxCardinalityQualified elkDataMaxCardinalityQualified) {
		throw new IndexingException(elkDataMaxCardinalityQualified);
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataMinCardinality elkDataMinCardinality) {
		throw new IndexingException(elkDataMinCardinality);
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataMinCardinalityQualified elkDataMinCardinalityQualified) {
		throw new IndexingException(elkDataMinCardinalityQualified);
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataExactCardinality elkDataExactCardinality) {
		throw new IndexingException(elkDataExactCardinality);
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataExactCardinalityQualified elkDataExactCardinalityQualified) {
		throw new IndexingException(elkDataExactCardinalityQualified);
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataSomeValuesFrom elkDataSomeValuesFrom) {
		throw new IndexingException(elkDataSomeValuesFrom);
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataAllValuesFrom elkDataAllValuesFrom) {
		throw new IndexingException(elkDataAllValuesFrom);
	}

	@Override
	public IndexedObjectProperty visit(ElkObjectInverseOf elkObjectInverseOf) {
		throw new IndexingException(elkObjectInverseOf);
	}

	@Override
	public IndexedPropertyChain visit(ElkObjectProperty elkObjectProperty) {
		throw new IndexingException(elkObjectProperty);
	}

	@Override
	public IndexedPropertyChain visit(
			ElkObjectPropertyChain elkObjectPropertyChain) {
		throw new IndexingException(elkObjectPropertyChain);
	}

	@Override
	public IndexedIndividual visit(ElkAnonymousIndividual elkAnonymousIndividual) {
		throw new IndexingException(elkAnonymousIndividual);
	}

	@Override
	public IndexedIndividual visit(ElkNamedIndividual elkNamedIndividual) {
		throw new IndexingException(elkNamedIndividual);
	}
}
