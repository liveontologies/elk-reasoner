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

import java.util.ListIterator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.*;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkDataPropertyExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkIndividualVisitor;
import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor;

public class ElkObjectIndexerVisitor implements
		ElkClassExpressionVisitor<IndexedClassExpression>,
		ElkSubObjectPropertyExpressionVisitor<IndexedPropertyChain>,
		ElkDataPropertyExpressionVisitor<IndexedDataProperty>,
		ElkIndividualVisitor<IndexedNominal> {

	// logger for events
	private static final Logger LOGGER_ = Logger
			.getLogger(ElkObjectIndexerVisitor.class);

	private IndexedObjectFilter objectFilter;

	public ElkObjectIndexerVisitor(IndexedObjectFilter objectFilter) {
		this.objectFilter = objectFilter;
	}

	public IndexedClassExpression visit(ElkClass elkClass) {
		return objectFilter.filter(new IndexedClass(elkClass));
	}

	public IndexedClassExpression visit(
			ElkObjectAllValuesFrom elkObjectAllValuesFrom) {
		throw new IndexingException(
				ElkObjectAllValuesFrom.class.getSimpleName() + " not supported");
	}

	public IndexedClassExpression visit(
			ElkObjectComplementOf elkObjectComplementOf) {
		throw new IndexingException(ElkObjectComplementOf.class.getSimpleName()
				+ " not supported");
	}

	public IndexedClassExpression visit(
			ElkObjectExactCardinality elkObjectExactCardinality) {
		throw new IndexingException(
				ElkObjectExactCardinality.class.getSimpleName()
						+ " not supported");
	}

	public IndexedClassExpression visit(
			ElkObjectExactCardinalityQualified elkObjectExactCardinalityQualified) {
		throw new IndexingException(
				ElkObjectExactCardinalityQualified.class.getSimpleName()
						+ " not supported");
	}

	public IndexedClassExpression visit(ElkObjectHasSelf elkObjectHasSelf) {
		throw new IndexingException(ElkObjectHasSelf.class.getSimpleName()
				+ " not supported");
	}

	public IndexedClassExpression visit(ElkObjectHasValue elkObjectHasValue) {
		IndexedObjectProperty iop = (IndexedObjectProperty) elkObjectHasValue
			.getProperty().accept(this);
		return objectFilter.filter(new IndexedObjectSomeValuesFrom(iop,
				elkObjectHasValue.getFiller().accept(this)));
	}

	public IndexedClassExpression visit(
			ElkObjectIntersectionOf elkObjectIntersectionOf) {

		IndexedClassExpression result = null;
		for (ElkClassExpression c : elkObjectIntersectionOf
				.getClassExpressions()) {
			IndexedClassExpression ice = c.accept(this);

			if (result == null) {
				result = ice;
				continue;
			}

			// TODO comparison shouldn't be on hash code
			IndexedClassExpression firstConjunct, secondConjunct;
			if (result.hashCode() < ice.hashCode()) {
				firstConjunct = result;
				secondConjunct = ice;
			} else {
				firstConjunct = ice;
				secondConjunct = result;
			}

			result = objectFilter.filter(new IndexedObjectIntersectionOf(
					firstConjunct, secondConjunct));
		}

		return result;
	}

	public IndexedClassExpression visit(
			ElkObjectMaxCardinality elkObjectMaxCardinality) {
		throw new IndexingException(
				ElkObjectMaxCardinality.class.getSimpleName()
						+ " not supported");
	}

	public IndexedClassExpression visit(
			ElkObjectMaxCardinalityQualified elkObjectMaxCardinalityQualified) {
		throw new IndexingException(
				ElkObjectMaxCardinalityQualified.class.getSimpleName()
						+ " not supported");
	}

	public IndexedClassExpression visit(
			ElkObjectMinCardinality elkObjectMinCardinality) {
		throw new IndexingException(
				ElkObjectMinCardinality.class.getSimpleName()
						+ " not supported");
	}

	public IndexedClassExpression visit(
			ElkObjectMinCardinalityQualified elkObjectMinCardinalityQualified) {
		throw new IndexingException(
				ElkObjectMinCardinalityQualified.class.getSimpleName()
						+ " not supported");
	}

	public IndexedClassExpression visit(ElkObjectOneOf elkObjectOneOf) {
		if (elkObjectOneOf.getIndividuals().size() != 1)
			throw new IndexingException(ElkObjectOneOf.class.getSimpleName()
				+ "is supported only for singletons");
		return elkObjectOneOf.getIndividuals().get(0).accept(this);
	}

	public IndexedClassExpression visit(
			ElkObjectSomeValuesFrom elkObjectSomeValuesFrom) {
		IndexedObjectProperty iop = (IndexedObjectProperty) elkObjectSomeValuesFrom
				.getProperty().accept(this);
		return objectFilter.filter(new IndexedObjectSomeValuesFrom(iop,
				elkObjectSomeValuesFrom.getFiller().accept(this)));
	}

	public IndexedClassExpression visit(ElkObjectUnionOf elkObjectUnionOf) {
		throw new IndexingException(ElkObjectUnionOf.class.getSimpleName()
				+ " not supported");
	}

	public IndexedClassExpression visit(ElkDataHasValue elkDataHasValue) {
		if (LOGGER_.isEnabledFor(Level.WARN)) {
			LOGGER_.warn(ElkDataHasValue.class.getSimpleName()
					+ " is supported only partially.");
		}
		IndexedDataProperty idp = (IndexedDataProperty) elkDataHasValue.getProperty().accept(this);
		return objectFilter.filter(new IndexedDataHasValue(idp, elkDataHasValue.getFiller()));
	}

	public IndexedClassExpression visit(
			ElkDataMaxCardinality elkDataMaxCardinality) {
		throw new IndexingException(ElkDataMaxCardinality.class.getSimpleName()
				+ " not supported");
	}

	public IndexedClassExpression visit(
			ElkDataMaxCardinalityQualified elkDataMaxCardinalityQualified) {
		throw new IndexingException(
				ElkDataMaxCardinalityQualified.class.getSimpleName()
						+ " not supported");
	}

	public IndexedClassExpression visit(
			ElkDataMinCardinality elkDataMinCardinality) {
		throw new IndexingException(ElkDataMinCardinality.class.getSimpleName()
				+ " not supported");
	}

	public IndexedClassExpression visit(
			ElkDataMinCardinalityQualified elkDataMinCardinalityQualified) {
		throw new IndexingException(
				ElkDataMinCardinalityQualified.class.getSimpleName()
						+ " not supported");
	}

	public IndexedClassExpression visit(
			ElkDataExactCardinality elkDataExactCardinality) {
		throw new IndexingException(ElkDataMinCardinality.class.getSimpleName()
				+ " not supported");
	}

	public IndexedClassExpression visit(
			ElkDataExactCardinalityQualified elkDataExactCardinalityQualified) {
		throw new IndexingException(
				ElkDataMinCardinalityQualified.class.getSimpleName()
						+ " not supported");
	}

	public IndexedClassExpression visit(
			ElkDataSomeValuesFrom elkDataSomeValuesFrom) {
		if (LOGGER_.isEnabledFor(Level.WARN)) {
			LOGGER_.warn(ElkDataSomeValuesFrom.class.getSimpleName() + " is supported only partially.");
		}
		IndexedDataProperty idp = (IndexedDataProperty) elkDataSomeValuesFrom.getProperty().accept(this);
		return objectFilter.filter(new IndexedDataSomeValuesFrom(idp, elkDataSomeValuesFrom.getFiller()));
	}

	public IndexedClassExpression visit(
			ElkDataAllValuesFrom elkDataAllValuesFrom) {
		throw new IndexingException(ElkDataAllValuesFrom.class.getSimpleName()
				+ " not supported");
	}

	public IndexedObjectProperty visit(ElkObjectInverseOf elkObjectInverseOf) {
		throw new IndexingException(ElkObjectInverseOf.class.getSimpleName()
				+ " not supported");
	}

	public IndexedPropertyChain visit(ElkObjectProperty elkObjectProperty) {
		return objectFilter
				.filter(new IndexedObjectProperty(elkObjectProperty));
	}
        
	public IndexedDataProperty visit(ElkDataProperty elkDataProperty) {
		return objectFilter.filter(new IndexedDataProperty(elkDataProperty));
	}

	public IndexedPropertyChain visit(
			ElkObjectPropertyChain elkObjectPropertyChain) {

		IndexedPropertyChain result = null;
		ListIterator<? extends ElkObjectPropertyExpression> iterator = elkObjectPropertyChain
				.getObjectPropertyExpressions().listIterator(
						elkObjectPropertyChain.getObjectPropertyExpressions()
								.size());

		while (iterator.hasPrevious()) {
			IndexedObjectProperty iop = (IndexedObjectProperty) iterator
					.previous().accept(this);

			if (result == null) {
				result = iop;
				continue;
			}

			result = objectFilter.filter(new IndexedBinaryPropertyChain(iop,
					result));
		}

		return result;
	}

	public IndexedNominal visit(ElkAnonymousIndividual elkAnonymousIndividual) {
		throw new IndexingException(ElkAnonymousIndividual.class.getSimpleName()
				+ " not supported");
	}

	public IndexedNominal visit(ElkNamedIndividual elkNamedIndividual) {
		return (IndexedNominal) objectFilter.filter(
				new IndexedNominal(elkNamedIndividual));
	}
}
