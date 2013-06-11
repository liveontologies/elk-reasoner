/*
 * #%L
 * ELK Reasoner
 * *
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

import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.reasoner.datatypes.handlers.ElkDatatypeHandler;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionFilter;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainFilter;

/**
 * A converter from {@link ElkClassExpression}s,
 * {@link ElkSubObjectPropertyExpression}s, and {@link ElkIndividual}s to
 * corresponding {@link IndexedObject}s with filtering through the provided
 * {@link IndexedClassExpressionFilter} and {@link IndexedPropertyChainFilter}.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class IndexObjectConverter extends AbstractIndexObjectConverter {

	// logger for events
	private static final Logger LOGGER_ = Logger
			.getLogger(IndexObjectConverter.class);

	protected IndexedClassExpressionFilter indexedClassFilter;
	protected IndexedPropertyChainFilter indexedPropertyFilter;
        protected ElkDatatypeHandler datatypeHandler;

	/**
	 * @param objectFilter
	 *            filter that is applied to the indexed objects after
	 *            construction
	 */
	public IndexObjectConverter(
			IndexedClassExpressionFilter indexedClassFilter,
			IndexedPropertyChainFilter indexedPropertyFilter,
                        ElkDatatypeHandler datatypeHandler) {
		this.indexedClassFilter = indexedClassFilter;
		this.indexedPropertyFilter = indexedPropertyFilter;
                this.datatypeHandler = datatypeHandler;
	}

	@Override
	public IndexedClass visit(ElkClass elkClass) {
		return indexedClassFilter.visit(new IndexedClass(elkClass));
	}

	@Override
	public IndexedClassExpression visit(ElkObjectHasValue elkObjectHasValue) {
		IndexedObjectProperty iop = (IndexedObjectProperty) elkObjectHasValue
				.getProperty().accept(this);
		return indexedClassFilter.visit(new IndexedObjectSomeValuesFrom(iop,
				elkObjectHasValue.getFiller().accept(this)));
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectIntersectionOf elkObjectIntersectionOf) {

		// the input conjunction is binarized
		IndexedClassExpression result = null;
		for (ElkClassExpression c : elkObjectIntersectionOf
				.getClassExpressions()) {
			IndexedClassExpression ice = c.accept(this);

			if (result == null)
				result = ice;
			else
				result = indexedClassFilter
						.visit(new IndexedObjectIntersectionOf(result, ice));
		}

		return result;
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectSomeValuesFrom elkObjectSomeValuesFrom) {
		IndexedObjectProperty iop = (IndexedObjectProperty) elkObjectSomeValuesFrom
				.getProperty().accept(this);
		return indexedClassFilter.visit(new IndexedObjectSomeValuesFrom(iop,
				elkObjectSomeValuesFrom.getFiller().accept(this)));
	}

	@Override
	public IndexedClassExpression visit(ElkDataHasValue elkDataHasValue) {
		IndexedDataProperty idp = (IndexedDataProperty) elkDataHasValue
				.getProperty().accept(this);
		ValueSpace vs = elkDataHasValue.getFiller().accept(datatypeHandler);
		return indexedClassFilter.visit(new IndexedDatatypeExpression(idp, vs));
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataSomeValuesFrom elkDataSomeValuesFrom) {
		List<? extends ElkDataPropertyExpression> exps = elkDataSomeValuesFrom
				.getDataPropertyExpressions();
		if (exps != null && exps.size() == 1) {
			IndexedDataProperty idp = (IndexedDataProperty) exps.get(0).accept(this);
			ValueSpace vs = elkDataSomeValuesFrom.getDataRange().accept(datatypeHandler);
			return indexedClassFilter.visit(new IndexedDatatypeExpression(idp, vs));
		} else {
			throw new ElkIndexingException(
					ElkDataSomeValuesFrom.class.getSimpleName()
							+ "with multiple properties not supported");
		}
	}

	@Override
	public IndexedPropertyChain visit(ElkObjectProperty elkObjectProperty) {
		return indexedPropertyFilter.visit(new IndexedObjectProperty(
				elkObjectProperty));
	}

	@Override
	public IndexedDataProperty visit(ElkDataProperty elkDataProperty) {
		return indexedPropertyFilter.visit(new IndexedDataProperty(elkDataProperty));
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

			result = indexedPropertyFilter
					.visit(new IndexedBinaryPropertyChain(iop, result));
		}

		return result;
	}

	@Override
	public IndexedIndividual visit(ElkNamedIndividual elkNamedIndividual) {
		return (IndexedIndividual) indexedClassFilter
				.visit(new IndexedIndividual(elkNamedIndividual));
	}
}
