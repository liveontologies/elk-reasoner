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
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
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
import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor;

public class ElkObjectIndexerVisitor implements
		ElkClassExpressionVisitor<IndexedClassExpression>,
		ElkSubObjectPropertyExpressionVisitor<IndexedPropertyChain> {

	// logger for events
	private static final Logger LOGGER_ = Logger
			.getLogger(ElkObjectIndexerVisitor.class);

	private IndexedObjectFilter objectFilter;

	ElkObjectIndexerVisitor(IndexedObjectFilter subObjectFilter) {
		this.objectFilter = subObjectFilter;
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

	public IndexedClassExpression visit(ElkObjectHasSelf elkObjectHasSelf) {
		throw new IndexingException(ElkObjectHasSelf.class.getSimpleName()
				+ " not supported");
	}

	public IndexedClassExpression visit(ElkObjectHasValue elkObjectHasValue) {
		throw new IndexingException(ElkObjectHasValue.class.getSimpleName()
				+ " not supported");
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
			ElkObjectMinCardinality elkObjectMinCardinality) {
		throw new IndexingException(
				ElkObjectMinCardinality.class.getSimpleName()
						+ " not supported");
	}

	public IndexedClassExpression visit(ElkObjectOneOf elkObjectOneOf) {
		throw new IndexingException(ElkObjectOneOf.class.getSimpleName()
				+ " not supported");
	}

	public IndexedClassExpression visit(
			ElkObjectSomeValuesFrom elkObjectSomeValuesFrom) {
		IndexedObjectProperty iop = (IndexedObjectProperty) elkObjectSomeValuesFrom
				.getObjectPropertyExpression().accept(this);
		return objectFilter.filter(new IndexedObjectSomeValuesFrom(iop,
				elkObjectSomeValuesFrom.getClassExpression().accept(this)));
	}

	public IndexedClassExpression visit(ElkObjectUnionOf elkObjectUnionOf) {
		throw new IndexingException(ElkObjectUnionOf.class.getSimpleName()
				+ " not supported");
	}

	public IndexedClassExpression visit(ElkDataHasValue elkDataHasValue) {
		if (LOGGER_.isEnabledFor(Level.WARN))
			LOGGER_.warn(ElkDataHasValue.class.getSimpleName()
					+ " is supported only partially.");
		return objectFilter.filter(new IndexedDataHasValue(elkDataHasValue));
	}

	public IndexedClassExpression visit(
			ElkDataMaxCardinality elkDataMaxCardinality) {
		throw new IndexingException(ElkDataMaxCardinality.class.getSimpleName()
				+ " not supported");
	}

	public IndexedClassExpression visit(
			ElkDataMinCardinality elkDataMinCardinality) {
		throw new IndexingException(ElkDataMinCardinality.class.getSimpleName()
				+ " not supported");
	}

	public IndexedClassExpression visit(
			ElkDataExactCardinality elkDataExactCardinality) {
		throw new IndexingException(ElkDataMinCardinality.class.getSimpleName()
				+ " not supported");
	}

	public IndexedClassExpression visit(
			ElkDataSomeValuesFrom elkDataSomeValuesFrom) {
		throw new IndexingException(ElkDataSomeValuesFrom.class.getSimpleName()
				+ " not supported");
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

}
