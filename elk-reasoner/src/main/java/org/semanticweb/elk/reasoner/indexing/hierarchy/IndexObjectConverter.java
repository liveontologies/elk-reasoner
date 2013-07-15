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
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionFilter;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainFilter;
import org.semanticweb.elk.util.logging.ElkMessage;

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

	/**
	 * @param objectFilter
	 *            filter that is applied to the indexed objects after
	 *            construction
	 */
	public IndexObjectConverter(
			IndexedClassExpressionFilter indexedClassFilter,
			IndexedPropertyChainFilter indexedPropertyFilter) {
		this.indexedClassFilter = indexedClassFilter;
		this.indexedPropertyFilter = indexedPropertyFilter;
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
			ElkObjectComplementOf elkObjectComplementOf) {
		IndexedClassExpression ice = elkObjectComplementOf.getClassExpression()
				.accept(this);
		return indexedClassFilter.visit(new IndexedObjectComplementOf(ice));
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
		if (LOGGER_.isEnabledFor(Level.WARN))
			LOGGER_.warn(new ElkMessage(
					"ELK supports DataHasValue only partially. Reasoning might be incomplete.",
					"reasoner.indexing.dataHasValue"));
		return indexedClassFilter
				.visit(new IndexedDataHasValue(elkDataHasValue));
	}

	@Override
	public IndexedPropertyChain visit(ElkObjectProperty elkObjectProperty) {
		return indexedPropertyFilter.visit(new IndexedObjectProperty(
				elkObjectProperty));
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
