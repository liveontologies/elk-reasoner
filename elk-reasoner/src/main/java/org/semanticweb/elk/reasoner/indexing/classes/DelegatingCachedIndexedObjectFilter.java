package org.semanticweb.elk.reasoner.indexing.classes;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2021 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectHasSelf;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedSubObject;

/**
 * A {@link CachedIndexedSubObject.Filter} that delegates all method calls to a
 * given {@link CachedIndexedSubObject.Filter}. Subclasses can override the
 * methods for pre-processing inputs and post-processing outputs of these
 * methods.
 * 
 * @author Yevgeny Kazakov
 *
 */
public class DelegatingCachedIndexedObjectFilter
		implements CachedIndexedSubObject.Filter {

	private final CachedIndexedSubObject.Filter delegate_;

	public DelegatingCachedIndexedObjectFilter(
			CachedIndexedSubObject.Filter delegate) {
		this.delegate_ = delegate;
	}

	public CachedIndexedSubObject.Filter getDelegate() {
		return delegate_;
	}

	@SuppressWarnings("static-method")
	<T extends CachedIndexedSubObject> T preFilter(T element) {
		return element;
	}

	@SuppressWarnings("static-method")
	<T extends CachedIndexedSubObject> T postFilter(T element) {
		return element;
	}

	@Override
	public CachedIndexedClass filter(CachedIndexedClass element) {
		return postFilter(delegate_.filter(preFilter(element)));
	}

	@Override
	public CachedIndexedObjectHasSelf filter(
			CachedIndexedObjectHasSelf element) {
		return postFilter(delegate_.filter(preFilter(element)));
	}

	@Override
	public CachedIndexedObjectComplementOf filter(
			CachedIndexedObjectComplementOf element) {
		return postFilter(delegate_.filter(preFilter(element)));
	}

	@Override
	public CachedIndexedObjectUnionOf filter(
			CachedIndexedObjectUnionOf element) {
		return postFilter(delegate_.filter(preFilter(element)));
	}

	@Override
	public CachedIndexedIndividual filter(CachedIndexedIndividual element) {
		return postFilter(delegate_.filter(preFilter(element)));
	}

	@Override
	public CachedIndexedObjectSomeValuesFrom filter(
			CachedIndexedObjectSomeValuesFrom element) {
		return postFilter(delegate_.filter(preFilter(element)));
	}

	@Override
	public CachedIndexedObjectIntersectionOf filter(
			CachedIndexedObjectIntersectionOf element) {
		return postFilter(delegate_.filter(preFilter(element)));
	}

	@Override
	public CachedIndexedObjectProperty filter(
			CachedIndexedObjectProperty element) {
		return postFilter(delegate_.filter(preFilter(element)));
	}

	@Override
	public CachedIndexedDataHasValue filter(CachedIndexedDataHasValue element) {
		return postFilter(delegate_.filter(preFilter(element)));
	}

	@Override
	public CachedIndexedClassExpressionList filter(
			CachedIndexedClassExpressionList element) {
		return postFilter(delegate_.filter(preFilter(element)));
	}

	@Override
	public CachedIndexedComplexPropertyChain filter(
			CachedIndexedComplexPropertyChain element) {
		return postFilter(delegate_.filter(preFilter(element)));
	}

}
