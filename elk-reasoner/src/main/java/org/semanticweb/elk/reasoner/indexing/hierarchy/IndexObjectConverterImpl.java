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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionFilter;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainFilter;
import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class IndexObjectConverterImpl extends AbstractIndexObjectConverter {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(IndexObjectConverterImpl.class);

	/**
	 * the filter for produced {@link IndexedClassExpression}s
	 */
	private final IndexedClassExpressionFilter indexedClassFilter_;
	/**
	 * the filter for produced {@link IndexedPropertyChain}
	 */
	private final IndexedPropertyChainFilter indexedPropertyFilter_;
	/**
	 * the converter for {@link IndexedObject}s of the complementary polarity
	 */
	private final IndexObjectConverterImpl complementaryConverter_;

	/**
	 * @param objectFilter
	 *            filter that is applied to the indexed objects after
	 *            construction
	 */

	/**
	 * Creates a new {@link IndexObjectConverterImpl}
	 * 
	 * @param indexedClassFilter
	 *            the filter for produced {@link IndexedClassExpression}s
	 * @param indexedPropertyFilter
	 *            the filter for produced {@link IndexedPropertyChain}
	 * @param complementaryConverter
	 *            the converter for {@link IndexedObject}s of the complementary
	 *            polarity
	 */
	public IndexObjectConverterImpl(
			IndexedClassExpressionFilter indexedClassFilter,
			IndexedPropertyChainFilter indexedPropertyFilter,
			IndexObjectConverterImpl complementaryConverter) {
		this.indexedClassFilter_ = indexedClassFilter;
		this.indexedPropertyFilter_ = indexedPropertyFilter;
		this.complementaryConverter_ = complementaryConverter;
	}

	/**
	 * Creates a new {@link IndexObjectConverterImpl} which is also used for
	 * converting {@link IndexedObject}s of the complementary polarity
	 * 
	 * @param indexedClassFilter
	 *            the filter for produced {@link IndexedClassExpression}s
	 * @param indexedPropertyFilter
	 *            the filter for produced {@link IndexedPropertyChain}
	 */
	public IndexObjectConverterImpl(
			IndexedClassExpressionFilter indexedClassFilter,
			IndexedPropertyChainFilter indexedPropertyFilter) {
		this.indexedClassFilter_ = indexedClassFilter;
		this.indexedPropertyFilter_ = indexedPropertyFilter;
		this.complementaryConverter_ = this;
	}

	/**
	 * Creates a new {@link IndexObjectConverterImpl}
	 * 
	 * @param indexedClassFilter
	 *            the filter for produced {@link IndexedClassExpression}s
	 * @param indexedPropertyFilter
	 *            the filter for produced {@link IndexedPropertyChain}
	 * @param complementaryConverterFactory
	 *            a {@link IndexObjectConverterFactory}s used to create the
	 *            converter for the complementary polarity, which itself uses
	 *            this converter as complementary
	 */
	public IndexObjectConverterImpl(
			IndexedClassExpressionFilter indexedClassFilter,
			IndexedPropertyChainFilter indexedPropertyFilter,
			IndexObjectConverterFactory complementaryConverterFactory) {
		this.indexedClassFilter_ = indexedClassFilter;
		this.indexedPropertyFilter_ = indexedPropertyFilter;
		this.complementaryConverter_ = complementaryConverterFactory
				.create(this);
	}

	public IndexObjectConverterImpl getComplementaryConverter() {
		return complementaryConverter_;
	}

	@Override
	public IndexedClass visit(ElkClass elkClass) {
		return indexedClassFilter_.visit(new IndexedClass(elkClass));
	}

	@Override
	public IndexedClassExpression visit(ElkObjectHasValue elkObjectHasValue) {
		IndexedObjectProperty iop = (IndexedObjectProperty) elkObjectHasValue
				.getProperty().accept(this);
		return indexedClassFilter_.visit(new IndexedObjectSomeValuesFrom(iop,
				elkObjectHasValue.getFiller().accept(this)));
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectComplementOf elkObjectComplementOf) {
		IndexedClassExpression negated = elkObjectComplementOf
				.getClassExpression().accept(complementaryConverter_);
		return indexedClassFilter_
				.visit(new IndexedObjectComplementOf(negated));
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
				result = indexedClassFilter_
						.visit(new IndexedObjectIntersectionOf(result, ice));
		}

		return result;
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectSomeValuesFrom elkObjectSomeValuesFrom) {
		IndexedObjectProperty iop = (IndexedObjectProperty) elkObjectSomeValuesFrom
				.getProperty().accept(this);
		return indexedClassFilter_.visit(new IndexedObjectSomeValuesFrom(iop,
				elkObjectSomeValuesFrom.getFiller().accept(this)));
	}

	@Override
	public IndexedClassExpression visit(ElkObjectUnionOf elkObjectUnionOf) {
		List<IndexedClassExpression> conjuncts = new ArrayList<IndexedClassExpression>(
				elkObjectUnionOf.getClassExpressions().size());
		for (ElkClassExpression conjunct : elkObjectUnionOf
				.getClassExpressions()) {
			conjuncts.add(conjunct.accept(this));
		}
		return indexedClassFilter_.visit(new IndexedObjectUnionOf(conjuncts));
	}

	@Override
	public IndexedClassExpression visit(ElkDataHasValue elkDataHasValue) {
		if (LOGGER_.isWarnEnabled()) {
			LoggerWrap
					.log(LOGGER_, LogLevel.WARN,
							"reasoner.indexing.dataHasValue",
							"ELK supports DataHasValue only partially. Reasoning might be incomplete!");
		}

		return indexedClassFilter_.visit(new IndexedDataHasValue(
				elkDataHasValue));
	}

	@Override
	public IndexedPropertyChain visit(ElkObjectProperty elkObjectProperty) {
		return indexedPropertyFilter_.visit(new IndexedObjectProperty(
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

			result = indexedPropertyFilter_
					.visit(new IndexedBinaryPropertyChain(iop, result));
		}

		return result;
	}

	@Override
	public IndexedIndividual visit(ElkNamedIndividual elkNamedIndividual) {
		return indexedClassFilter_.visit(new IndexedIndividual(
				elkNamedIndividual));
	}
}
