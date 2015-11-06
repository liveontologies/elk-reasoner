package org.semanticweb.elk.reasoner.indexing.caching;

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
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedPropertyChain;

/**
 * Delegates construction of {@link CachedIndexedObject} to a given
 * {@link CachedIndexedObjectFactory}. Subclasses can redefine the the method
 * {@link #filter(CachedIndexedObject)} which can additionally post-process the
 * created object.
 * 
 * @author "Yevgeny Kazakov"
 */
public class DelegatingCachedIndexedObjectFactory implements
		CachedIndexedObjectFactory {

	private final CachedIndexedObjectFactory baseFactory_;

	DelegatingCachedIndexedObjectFactory(CachedIndexedObjectFactory baseFactory) {
		this.baseFactory_ = baseFactory;
	}

	/**
	 * Filters the sub-objects created by the factory; can be overridden in
	 * subclasses
	 * 
	 * @param input
	 * @return
	 */
	@SuppressWarnings("static-method")
	<T extends CachedIndexedSubObject<T>> T filter(T input) {
		return input;
	}

	@Override
	public final CachedIndexedClass getIndexedClass(ElkClass elkClass) {
		return filter(baseFactory_.getIndexedClass(elkClass));
	}

	@Override
	public final CachedIndexedClassExpressionList getIndexedClassExpressionList(
			List<? extends ModifiableIndexedClassExpression> members) {
		return filter(baseFactory_.getIndexedClassExpressionList(members));
	}

	@Override
	public final CachedIndexedComplexPropertyChain getIndexedComplexPropertyChain(
			ModifiableIndexedObjectProperty leftProperty,
			ModifiableIndexedPropertyChain rightProperty) {
		return filter(baseFactory_.getIndexedComplexPropertyChain(leftProperty,
				rightProperty));
	}

	@Override
	public final CachedIndexedDataHasValue getIndexedDataHasValue(
			ElkDataHasValue elkDataHasValue) {
		return filter(baseFactory_.getIndexedDataHasValue(elkDataHasValue));
	}

	@Override
	public final CachedIndexedIndividual getIndexedIndividual(
			ElkNamedIndividual elkNamedIndividual) {
		return filter(baseFactory_.getIndexedIndividual(elkNamedIndividual));
	}

	@Override
	public final CachedIndexedObjectComplementOf getIndexedObjectComplementOf(
			ModifiableIndexedClassExpression negated) {
		return filter(baseFactory_.getIndexedObjectComplementOf(negated));
	}

	@Override
	public final CachedIndexedObjectHasSelf getIndexedObjectHasSelf(
			ModifiableIndexedObjectProperty property) {
		return filter(baseFactory_.getIndexedObjectHasSelf(property));
	}

	@Override
	public final CachedIndexedObjectIntersectionOf getIndexedObjectIntersectionOf(
			ModifiableIndexedClassExpression conjunctA,
			ModifiableIndexedClassExpression conjunctB) {
		return filter(baseFactory_.getIndexedObjectIntersectionOf(conjunctA,
				conjunctB));
	}

	@Override
	public final CachedIndexedObjectProperty getIndexedObjectProperty(
			ElkObjectProperty elkObjectProperty) {
		return filter(baseFactory_.getIndexedObjectProperty(elkObjectProperty));
	}

	@Override
	public final CachedIndexedObjectSomeValuesFrom getIndexedObjectSomeValuesFrom(
			ModifiableIndexedObjectProperty property,
			ModifiableIndexedClassExpression filler) {
		return filter(baseFactory_.getIndexedObjectSomeValuesFrom(property,
				filler));
	}

	@Override
	public final CachedIndexedObjectUnionOf getIndexedObjectUnionOf(
			List<? extends ModifiableIndexedClassExpression> disjuncts) {
		return filter(baseFactory_.getIndexedObjectUnionOf(disjuncts));
	}

}
