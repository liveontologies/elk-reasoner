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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataSomeValuesFrom;
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
	private final IndexObjectConverter complementaryConverter_;
	
	protected ElkDatatypeHandler datatypeHandler;

	/**
	 * @param objectFilter
	 *            filter that is applied to the indexed objects after
	 *            construction
	 */

	/**
	 * Creates a new {@link IndexObjectConverter}
	 * 
	 * @param indexedClassFilter
	 *            the filter for produced {@link IndexedClassExpression}s
	 * @param indexedPropertyFilter
	 *            the filter for produced {@link IndexedPropertyChain}
	 * @param complementaryConverter
	 *            the converter for {@link IndexedObject}s of the complementary
	 *            polarity
	 */
	public IndexObjectConverter(
			IndexedClassExpressionFilter indexedClassFilter,
			IndexedPropertyChainFilter indexedPropertyFilter,
			IndexObjectConverter complementaryConverter,
			ElkDatatypeHandler datatypeHandler) {
		this.indexedClassFilter_ = indexedClassFilter;
		this.indexedPropertyFilter_ = indexedPropertyFilter;
		this.complementaryConverter_ = complementaryConverter;
		this.datatypeHandler = datatypeHandler;
	}

	/**
	 * Creates a new {@link IndexObjectConverter} which is also used for
	 * converting {@link IndexedObject}s of the complementary polarity
	 * 
	 * @param indexedClassFilter
	 *            the filter for produced {@link IndexedClassExpression}s
	 * @param indexedPropertyFilter
	 *            the filter for produced {@link IndexedPropertyChain}
	 */
	public IndexObjectConverter(
			IndexedClassExpressionFilter indexedClassFilter,
			IndexedPropertyChainFilter indexedPropertyFilter,
			ElkDatatypeHandler datatypeHandler) {
		this.indexedClassFilter_ = indexedClassFilter;
		this.indexedPropertyFilter_ = indexedPropertyFilter;
		this.complementaryConverter_ = this;
		this.datatypeHandler = datatypeHandler;
	}

	/**
	 * Creates a new {@link IndexObjectConverter}
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
	public IndexObjectConverter(
			IndexedClassExpressionFilter indexedClassFilter,
			IndexedPropertyChainFilter indexedPropertyFilter,
			IndexObjectConverterFactory complementaryConverterFactory,
			ElkDatatypeHandler datatypeHandler) {
		this.indexedClassFilter_ = indexedClassFilter;
		this.indexedPropertyFilter_ = indexedPropertyFilter;
		this.complementaryConverter_ = complementaryConverterFactory
				.create(this);
		this.datatypeHandler = datatypeHandler;
	}

	public IndexObjectConverter getComplementaryConverter() {
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
		IndexedDataProperty idp = (IndexedDataProperty) elkDataHasValue
				.getProperty().accept(this);
		ValueSpace vs = elkDataHasValue.getFiller().accept(datatypeHandler);
		return indexedClassFilter_.visit(new IndexedDatatypeExpression(idp, vs));
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataSomeValuesFrom elkDataSomeValuesFrom) {
		List<? extends ElkDataPropertyExpression> exps = elkDataSomeValuesFrom
				.getDataPropertyExpressions();
		if (exps != null && exps.size() == 1) {
			IndexedDataProperty idp = (IndexedDataProperty) exps.get(0).accept(this);
			ValueSpace vs = elkDataSomeValuesFrom.getDataRange().accept(datatypeHandler);
			return indexedClassFilter_.visit(new IndexedDatatypeExpression(idp, vs));
		} else {
			throw new ElkIndexingException(
					ElkDataSomeValuesFrom.class.getSimpleName()
							+ "with multiple properties not supported");
		}
	}

	@Override
	public IndexedPropertyChain visit(ElkObjectProperty elkObjectProperty) {
		return indexedPropertyFilter_.visit(new IndexedObjectProperty(
				elkObjectProperty));
	}

	@Override
	public IndexedDataProperty visit(ElkDataProperty elkDataProperty) {
		return indexedPropertyFilter_.visit(new IndexedDataProperty(elkDataProperty));
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
		return (IndexedIndividual) indexedClassFilter_
				.visit(new IndexedIndividual(elkNamedIndividual));
	}
}
