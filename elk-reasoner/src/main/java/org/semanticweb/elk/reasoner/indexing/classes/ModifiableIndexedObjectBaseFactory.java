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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedSubObject;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectHasSelf;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedEntity;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedEquivalentClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectPropertyRangeAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedSubObjectPropertyOfAxiom;

/**
 * Implements {@link ModifiableIndexedObject.Factory} and
 * {@link CachedIndexedSubObject.Factory}. The occurrences of the created objects
 * are not modified.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public class ModifiableIndexedObjectBaseFactory implements
		ModifiableIndexedObject.Factory, CachedIndexedSubObject.Factory {

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
			ElkAxiom originalAxiom, ModifiableIndexedEntity entity) {
		return new ModifiableIndexedDeclarationAxiomImpl<ElkAxiom>(
				originalAxiom, entity);
	}

	@Override
	public ModifiableIndexedDisjointClassesAxiom getIndexedDisjointClassesAxiom(
			ElkAxiom originalAxiom,
			ModifiableIndexedClassExpressionList members) {
		return new ModifiableIndexedDisjointClassesAxiomImpl<ElkAxiom>(
				originalAxiom, members);
	}

	@Override
	public ModifiableIndexedEquivalentClassesAxiom getIndexedEquivalentClassesAxiom(
			ElkAxiom originalAxiom,
			ModifiableIndexedClassExpression firstMember,
			ModifiableIndexedClassExpression secondMember) {
		return new ModifiableIndexedEquivalentClassesAxiomImpl<ElkAxiom>(
				originalAxiom, firstMember, secondMember);
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
			ElkAxiom originalAxiom, ModifiableIndexedObjectProperty property,
			ModifiableIndexedClassExpression range) {
		return new ModifiableIndexedObjectPropertyRangeAxiomImpl<ElkAxiom>(
				originalAxiom, property, range);
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
			ElkAxiom originalAxiom, ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		return new ModifiableIndexedSubClassOfAxiomImpl<ElkAxiom>(originalAxiom,
				subClass, superClass);
	}

	@Override
	public ModifiableIndexedSubObjectPropertyOfAxiom getIndexedSubObjectPropertyOfAxiom(
			ElkAxiom originalAxiom,
			ModifiableIndexedPropertyChain subPropertyChain,
			ModifiableIndexedObjectProperty superProperty) {
		return new ModifiableIndexedSubObjectPropertyOfAxiomImpl<ElkAxiom>(
				originalAxiom, subPropertyChain, superProperty);
	}

}
