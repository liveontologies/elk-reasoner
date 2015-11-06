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
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectHasSelf;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.factories.ModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClass;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedDefinitionAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedEntity;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectPropertyRangeAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedPropertyChain;
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
	public CachedIndexedClass getIndexedClass(ElkClass elkClass) {
		return new CachedIndexedClassImpl(elkClass);
	}

	@Override
	public CachedIndexedClassExpressionList getIndexedClassExpressionList(
			List<? extends ModifiableIndexedClassExpression> elements) {
		return new CachedIndexedClassExpressionListImpl(elements);
	}

	@Override
	public CachedIndexedComplexPropertyChain getIndexedComplexPropertyChain(
			ModifiableIndexedObjectProperty leftProperty,
			ModifiableIndexedPropertyChain rightProperty) {
		return new CachedIndexedComplexPropertyChainImpl(leftProperty,
				rightProperty);
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
	public ModifiableIndexedDefinitionAxiom getIndexedDefinitionAxiom(
			ModifiableIndexedClass definedClass,
			ModifiableIndexedClassExpression definition, ElkAxiom reason) {
		return new ModifiableIndexedDefinitionAxiomImpl(definedClass,
				definition);
	}

	
	@Override
	public ModifiableIndexedDisjointClassesAxiom getIndexedDisjointClassesAxiom(
			ModifiableIndexedClassExpressionList members,
			ElkAxiom reason) {
		return new ModifiableIndexedDisjointClassesAxiomImpl(members);
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
	public CachedIndexedObjectHasSelf getIndexedObjectHasSelf(
			ModifiableIndexedObjectProperty property) {
		return new CachedIndexedObjectHasSelfImpl(property);
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
	public ModifiableIndexedObjectPropertyRangeAxiom getIndexedObjectPropertyRangeAxiom(
			ModifiableIndexedObjectProperty property,
			ModifiableIndexedClassExpression range, ElkAxiom reason) {
		return new ModifiableIndexedObjectPropertyRangeAxiomImpl(property,
				range);
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
