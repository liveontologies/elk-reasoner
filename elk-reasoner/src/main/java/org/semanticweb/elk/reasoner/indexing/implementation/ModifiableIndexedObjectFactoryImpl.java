package org.semanticweb.elk.reasoner.indexing.implementation;

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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedClass;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.factories.ModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedEntity;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedSubObjectPropertyOfAxiom;

/**
 * Implements {@link ModifiableIndexedObjectFactory} and
 * {@link CachedIndexedObjectFactory}. The occurrences of the created objects
 * are not modified.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public class ModifiableIndexedObjectFactoryImpl implements
		ModifiableIndexedObjectFactory, CachedIndexedObjectFactory {

	@Override
	public CachedIndexedComplexPropertyChain getIndexedBinaryPropertyChain(
			ModifiableIndexedObjectProperty leftProperty,
			ModifiableIndexedPropertyChain rightProperty) {
		return new CachedIndexedComplexPropertyChainImpl(leftProperty,
				rightProperty);
	}

	@Override
	public CachedIndexedClass getIndexedClass(ElkClass elkClass) {
		return new CachedIndexedClassImpl(elkClass);
	}

	@Override
	public CachedIndexedDataHasValue getIndexedDataHasValue(
			ElkDataHasValue elkDataHasValue) {
		return new CachedIndexedDataHasValueImpl(elkDataHasValue);
	}

	@Override
	public ModifiableIndexedDeclarationAxiom getIndexedDeclarationAxiom(
			ModifiableIndexedEntity entity, ElkAxiom reason) {
		return new ModifiableIndexedDeclarationAxiomImpl(entity);
	}

	@Override
	public CachedIndexedDisjointClassesAxiom getIndexedDisjointClassesAxiom(
			List<? extends ModifiableIndexedClassExpression> members,
			ElkAxiom reason) {
		return new CachedIndexedDisjointClassesAxiomImpl(members);
	}

	@Override
	public CachedIndexedIndividual getIndexedIndividual(
			ElkNamedIndividual elkNamedIndividual) {
		return new CachedIndexedIndividualImpl(elkNamedIndividual);
	}

	@Override
	public CachedIndexedObjectComplementOf getIndexedObjectComplementOf(
			ModifiableIndexedClassExpression negated) {
		return new CachedIndexedObjectComplementOfImpl(negated);
	}

	@Override
	public CachedIndexedObjectIntersectionOf getIndexedObjectIntersectionOf(
			ModifiableIndexedClassExpression conjunctA,
			ModifiableIndexedClassExpression conjunctB) {
		return new CachedIndexedObjectIntersectionOfImpl(conjunctA, conjunctB);
	}

	@Override
	public CachedIndexedObjectProperty getIndexedObjectProperty(
			ElkObjectProperty elkObjectProperty) {
		return new CachedIndexedObjectPropertyImpl(elkObjectProperty);
	}

	@Override
	public CachedIndexedObjectSomeValuesFrom getIndexedObjectSomeValuesFrom(
			ModifiableIndexedObjectProperty property,
			ModifiableIndexedClassExpression filler) {
		return new CachedIndexedObjectSomeValuesFromImpl(property, filler);
	}

	@Override
	public CachedIndexedObjectUnionOf getIndexedObjectUnionOf(
			List<? extends ModifiableIndexedClassExpression> disjuncts) {
		return new CachedIndexedObjectUnionOfImpl(disjuncts);
	}

	@Override
	public ModifiableIndexedReflexiveObjectPropertyAxiom getIndexedReflexiveObjectPropertyAxiom(
			ModifiableIndexedObjectProperty property, ElkAxiom reason) {
		return new ModifiableIndexedReflexiveObjectPropertyAxiomImpl(property);
	}

	@Override
	public ModifiableIndexedSubClassOfAxiom getIndexedSubClassOfAxiom(
			ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass, ElkAxiom reason) {
		return new ModifiableIndexedSubClassOfAxiomImpl(subClass, superClass);
	}

	@Override
	public ModifiableIndexedSubObjectPropertyOfAxiom getIndexedSubObjectPropertyOfAxiom(
			ModifiableIndexedPropertyChain subPropertyChain,
			ModifiableIndexedObjectProperty superProperty, ElkAxiom reason) {
		return new ModifiableIndexedSubObjectPropertyOfAxiomImpl(
				subPropertyChain, superProperty);
	}

}
