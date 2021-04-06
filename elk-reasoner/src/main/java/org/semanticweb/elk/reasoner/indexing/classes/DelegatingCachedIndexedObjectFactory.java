package org.semanticweb.elk.reasoner.indexing.classes;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
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
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedPropertyChain;

/**
 * Delegates construction of {@link CachedIndexedSubObject} to a given
 * {@link CachedIndexedSubObject.Factory}. The constructed instances of 
 * {@link CachedIndexedSubObject} are subsequently filtered using a
 * given {@link CachedIndexedSubObject.Filter}.  
 * 
 * @author "Yevgeny Kazakov"
 */
class DelegatingCachedIndexedObjectFactory implements
		CachedIndexedSubObject.Factory {

	private final CachedIndexedSubObject.Factory baseFactory_;
	private final CachedIndexedSubObject.Filter filter_;

	DelegatingCachedIndexedObjectFactory(CachedIndexedSubObject.Factory baseFactory, CachedIndexedSubObject.Filter filter) {
		this.baseFactory_ = baseFactory;
		this.filter_ = filter;
	}

	@Override
	public final CachedIndexedClass getIndexedClass(ElkClass elkClass) {
		return filter_.filter(baseFactory_.getIndexedClass(elkClass));
	}

	@Override
	public final CachedIndexedClassExpressionList getIndexedClassExpressionList(
			List<? extends ModifiableIndexedClassExpression> members) {
		return  filter_.filter(baseFactory_.getIndexedClassExpressionList(members));
	}

	@Override
	public final CachedIndexedComplexPropertyChain getIndexedComplexPropertyChain(
			ModifiableIndexedObjectProperty leftProperty,
			ModifiableIndexedPropertyChain rightProperty) {
		return filter_.filter(baseFactory_.getIndexedComplexPropertyChain(leftProperty,
				rightProperty));
	}

	@Override
	public final CachedIndexedDataHasValue getIndexedDataHasValue(
			ElkDataHasValue elkDataHasValue) {
		return filter_.filter(baseFactory_.getIndexedDataHasValue(elkDataHasValue));
	}

	@Override
	public final CachedIndexedIndividual getIndexedIndividual(
			ElkNamedIndividual elkNamedIndividual) {
		return filter_.filter(baseFactory_.getIndexedIndividual(elkNamedIndividual));
	}

	@Override
	public final CachedIndexedObjectComplementOf getIndexedObjectComplementOf(
			ModifiableIndexedClassExpression negated) {
		return filter_.filter(baseFactory_.getIndexedObjectComplementOf(negated));
	}

	@Override
	public final CachedIndexedObjectHasSelf getIndexedObjectHasSelf(
			ModifiableIndexedObjectProperty property) {
		return filter_.filter(baseFactory_.getIndexedObjectHasSelf(property));
	}

	@Override
	public final CachedIndexedObjectIntersectionOf getIndexedObjectIntersectionOf(
			ModifiableIndexedClassExpression conjunctA,
			ModifiableIndexedClassExpression conjunctB) {
		return filter_.filter(baseFactory_.getIndexedObjectIntersectionOf(conjunctA,
				conjunctB));
	}

	@Override
	public final CachedIndexedObjectProperty getIndexedObjectProperty(
			ElkObjectProperty elkObjectProperty) {
		return filter_.filter(baseFactory_.getIndexedObjectProperty(elkObjectProperty));
	}

	@Override
	public final CachedIndexedObjectSomeValuesFrom getIndexedObjectSomeValuesFrom(
			ModifiableIndexedObjectProperty property,
			ModifiableIndexedClassExpression filler) {
		return filter_.filter(baseFactory_.getIndexedObjectSomeValuesFrom(property,
				filler));
	}

	@Override
	public final CachedIndexedObjectUnionOf getIndexedObjectUnionOf(
			List<? extends ModifiableIndexedClassExpression> disjuncts) {
		return filter_.filter(baseFactory_.getIndexedObjectUnionOf(disjuncts));
	}

}
