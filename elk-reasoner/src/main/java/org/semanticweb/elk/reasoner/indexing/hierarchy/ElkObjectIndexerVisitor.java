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
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkIndividualVisitor;
import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor;

/**
 * Visitor for Elk classes, properties, and individuals that returns the
 * corresponding indexed objects, already filtered through the
 * IndexedObjectFilter provided in the constructor.
 * 
 * @author Frantisek Simancik
 * 
 */
public class ElkObjectIndexerVisitor implements
		ElkClassExpressionVisitor<IndexedClassExpression>,
		ElkSubObjectPropertyExpressionVisitor<IndexedPropertyChain>,
		ElkIndividualVisitor<IndexedIndividual> {

	// logger for events
	private static final Logger LOGGER_ = Logger
			.getLogger(ElkObjectIndexerVisitor.class);

	private IndexedObjectFilter objectFilter;

	/**
	 * @param objectFilter
	 *            filter that is applied to the indexed objects after
	 *            construction
	 */
	public ElkObjectIndexerVisitor(IndexedObjectFilter objectFilter) {
		this.objectFilter = objectFilter;
	}

	@Override
	public IndexedClassExpression visit(ElkClass elkClass) {
		return objectFilter.filter(new IndexedClass(elkClass));
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectAllValuesFrom elkObjectAllValuesFrom) {
		throw new IndexingException(
				ElkObjectAllValuesFrom.class.getSimpleName() + " not supported");
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectComplementOf elkObjectComplementOf) {
		throw new IndexingException(ElkObjectComplementOf.class.getSimpleName()
				+ " not supported");
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectExactCardinality elkObjectExactCardinality) {
		throw new IndexingException(
				ElkObjectExactCardinality.class.getSimpleName()
						+ " not supported");
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectExactCardinalityQualified elkObjectExactCardinalityQualified) {
		throw new IndexingException(
				ElkObjectExactCardinalityQualified.class.getSimpleName()
						+ " not supported");
	}

	@Override
	public IndexedClassExpression visit(ElkObjectHasSelf elkObjectHasSelf) {
		throw new IndexingException(ElkObjectHasSelf.class.getSimpleName()
				+ " not supported");
	}

	@Override
	public IndexedClassExpression visit(ElkObjectHasValue elkObjectHasValue) {
		IndexedObjectProperty iop = (IndexedObjectProperty) elkObjectHasValue
				.getProperty().accept(this);
		return objectFilter.filter(new IndexedObjectSomeValuesFrom(iop,
				elkObjectHasValue.getFiller().accept(this)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor#visit(org.
	 * semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf)
	 * 
	 * Binarization of conjunctions. To be able to use a map instead of a
	 * multimap in IndexedClassExpression.negConjunctionsByConjunct It is
	 * important to ensure that we never create both (A & B) and (B & A). This
	 * is achieved by ordering conjucts so that A < B in each binary
	 * conjunction.
	 */
	@Override
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

	@Override
	public IndexedClassExpression visit(
			ElkObjectMaxCardinality elkObjectMaxCardinality) {
		throw new IndexingException(
				ElkObjectMaxCardinality.class.getSimpleName()
						+ " not supported");
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectMaxCardinalityQualified elkObjectMaxCardinalityQualified) {
		throw new IndexingException(
				ElkObjectMaxCardinalityQualified.class.getSimpleName()
						+ " not supported");
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectMinCardinality elkObjectMinCardinality) {
		throw new IndexingException(
				ElkObjectMinCardinality.class.getSimpleName()
						+ " not supported");
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectMinCardinalityQualified elkObjectMinCardinalityQualified) {
		throw new IndexingException(
				ElkObjectMinCardinalityQualified.class.getSimpleName()
						+ " not supported");
	}

	@Override
	public IndexedClassExpression visit(ElkObjectOneOf elkObjectOneOf) {
		throw new IndexingException(ElkObjectOneOf.class.getSimpleName()
				+ " not supported");
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectSomeValuesFrom elkObjectSomeValuesFrom) {
		IndexedObjectProperty iop = (IndexedObjectProperty) elkObjectSomeValuesFrom
				.getProperty().accept(this);
		return objectFilter.filter(new IndexedObjectSomeValuesFrom(iop,
				elkObjectSomeValuesFrom.getFiller().accept(this)));
	}

	@Override
	public IndexedClassExpression visit(ElkObjectUnionOf elkObjectUnionOf) {
		throw new IndexingException(ElkObjectUnionOf.class.getSimpleName()
				+ " not supported");
	}

	@Override
	public IndexedClassExpression visit(ElkDataHasValue elkDataHasValue) {
		if (LOGGER_.isEnabledFor(Level.WARN))
			LOGGER_.warn(ElkDataHasValue.class.getSimpleName()
					+ " is supported only partially.");
		return objectFilter.filter(new IndexedDataHasValue(elkDataHasValue));
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataMaxCardinality elkDataMaxCardinality) {
		throw new IndexingException(ElkDataMaxCardinality.class.getSimpleName()
				+ " not supported");
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataMaxCardinalityQualified elkDataMaxCardinalityQualified) {
		throw new IndexingException(
				ElkDataMaxCardinalityQualified.class.getSimpleName()
						+ " not supported");
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataMinCardinality elkDataMinCardinality) {
		throw new IndexingException(ElkDataMinCardinality.class.getSimpleName()
				+ " not supported");
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataMinCardinalityQualified elkDataMinCardinalityQualified) {
		throw new IndexingException(
				ElkDataMinCardinalityQualified.class.getSimpleName()
						+ " not supported");
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataExactCardinality elkDataExactCardinality) {
		throw new IndexingException(ElkDataMinCardinality.class.getSimpleName()
				+ " not supported");
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataExactCardinalityQualified elkDataExactCardinalityQualified) {
		throw new IndexingException(
				ElkDataMinCardinalityQualified.class.getSimpleName()
						+ " not supported");
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataSomeValuesFrom elkDataSomeValuesFrom) {
		throw new IndexingException(ElkDataSomeValuesFrom.class.getSimpleName()
				+ " not supported");
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataAllValuesFrom elkDataAllValuesFrom) {
		throw new IndexingException(ElkDataAllValuesFrom.class.getSimpleName()
				+ " not supported");
	}

	@Override
	public IndexedObjectProperty visit(ElkObjectInverseOf elkObjectInverseOf) {
		throw new IndexingException(ElkObjectInverseOf.class.getSimpleName()
				+ " not supported");
	}

	@Override
	public IndexedPropertyChain visit(ElkObjectProperty elkObjectProperty) {
		return objectFilter
				.filter(new IndexedObjectProperty(elkObjectProperty));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor
	 * #visit(org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain)
	 * 
	 * Binarization of role chains. Order must be preserved.
	 */
	@Override
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

	@Override
	public IndexedIndividual visit(ElkAnonymousIndividual elkAnonymousIndividual) {
		throw new IndexingException(
				ElkAnonymousIndividual.class.getSimpleName() + " not supported");
	}

	@Override
	public IndexedIndividual visit(ElkNamedIndividual elkNamedIndividual) {
		return (IndexedIndividual) objectFilter.filter(new IndexedIndividual(
				elkNamedIndividual));
	}
}
