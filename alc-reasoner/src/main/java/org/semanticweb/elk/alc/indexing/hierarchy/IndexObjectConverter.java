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
package org.semanticweb.elk.alc.indexing.hierarchy;

import org.semanticweb.elk.alc.indexing.visitors.IndexedObjectFilter;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;

/**
 * A converter from {@link ElkClassExpression}s,
 * {@link ElkSubObjectPropertyExpression}s, and {@link ElkIndividual}s to
 * corresponding {@link IndexedObject}s with filtering through the provided
 * {@link IndexedObjectFilter}.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class IndexObjectConverter extends AbstractIndexObjectConverter {

	/**
	 * the filter for produced {@link IndexedClassExpression}s
	 */
	private final IndexedObjectFilter indexedObjectFilter_;
	/**
	 * the converter for {@link IndexedObject}s of the complementary polarity
	 */
	private final IndexObjectConverter complementaryConverter_;

	/**
	 * @param objectFilter
	 *            filter that is applied to the indexed objects after
	 *            construction
	 */

	/**
	 * Creates a new {@link IndexObjectConverter}
	 * 
	 * @param indexedObjectFilter
	 *            the filter for produced {@link IndexedObject}s
	 * @param complementaryConverter
	 *            the converter for {@link IndexedObject}s of the complementary
	 *            polarity
	 */
	public IndexObjectConverter(IndexedObjectFilter indexedObjectFilter,
			IndexObjectConverter complementaryConverter) {
		this.indexedObjectFilter_ = indexedObjectFilter;
		this.complementaryConverter_ = complementaryConverter;
	}

	/**
	 * Creates a new {@link IndexObjectConverter} which is also used for
	 * converting {@link IndexedObject}s of the complementary polarity
	 * 
	 * @param indexedObjectFilter
	 *            the filter for produced {@link IndexedObject}s
	 * @param indexedPropertyFilter
	 *            the filter for produced {@link IndexedPropertyChain}
	 */
	public IndexObjectConverter(IndexedObjectFilter indexedObjectFilter) {
		this.indexedObjectFilter_ = indexedObjectFilter;
		this.complementaryConverter_ = this;
	}

	/**
	 * Creates a new {@link IndexObjectConverter}
	 * 
	 * @param indexedObjectFilter
	 *            the filter for produced {@link IndexedObject}s
	 * @param complementaryConverterFactory
	 *            a {@link IndexObjectConverterFactory}s used to create the
	 *            converter for the complementary polarity, which itself uses
	 *            this converter as complementary
	 */
	public IndexObjectConverter(IndexedObjectFilter indexedObjectFilter,
			IndexObjectConverterFactory complementaryConverterFactory) {
		this.indexedObjectFilter_ = indexedObjectFilter;
		this.complementaryConverter_ = complementaryConverterFactory
				.create(this);
	}

	public IndexObjectConverter getComplementaryConverter() {
		return complementaryConverter_;
	}

	@Override
	public IndexedClass visit(ElkClass elkClass) {
		return indexedObjectFilter_.visit(new IndexedClass(elkClass));
	}

	@Override
	public IndexedObjectProperty visit(ElkObjectProperty elkProperty) {
		return indexedObjectFilter_
				.visit(new IndexedObjectProperty(elkProperty));
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
				result = indexedObjectFilter_
						.visit(new IndexedObjectIntersectionOf(result, ice));
		}

		return result;
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectSomeValuesFrom elkObjectSomeValuesFrom) {
		IndexedObjectProperty iop = elkObjectSomeValuesFrom.getProperty()
				.accept(this);
		return indexedObjectFilter_.visit(new IndexedObjectSomeValuesFrom(iop,
				elkObjectSomeValuesFrom.getFiller().accept(this)));
	}

}
