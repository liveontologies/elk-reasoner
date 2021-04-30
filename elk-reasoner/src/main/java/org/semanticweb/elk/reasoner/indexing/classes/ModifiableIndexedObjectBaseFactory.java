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
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedEntity;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedEquivalentClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectHasSelf;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectPropertyRangeAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedSubObjectPropertyOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.StructuralIndexedSubObject;

/**
 * Implements {@link ModifiableIndexedObject.Factory} and
 * {@link StructuralIndexedSubObject.Factory}. The occurrences of the created
 * objects are not modified.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public class ModifiableIndexedObjectBaseFactory
		implements ModifiableIndexedObject.Factory {

	@SuppressWarnings("static-method")
	protected <T extends StructuralIndexedSubObject<T>> T filter(T input) {
		return input;
	}

	@SuppressWarnings("static-method")
	protected <T extends ModifiableIndexedAxiom> T filter(T input) {
		return input;
	}

	@Override
	public ModifiableIndexedClass getIndexedClass(ElkClass elkClass) {
		return filter(new ModifiableIndexedDefinedClassImpl(elkClass));
	}

	@Override
	public ModifiableIndexedClassExpressionList getIndexedClassExpressionList(
			List<? extends ModifiableIndexedClassExpression> elements) {
		return filter(new StructuralIndexedClassExpressionListEntryImpl(elements));
	}

	@Override
	public ModifiableIndexedComplexPropertyChain getIndexedComplexPropertyChain(
			ModifiableIndexedObjectProperty leftProperty,
			ModifiableIndexedPropertyChain rightProperty) {
		return filter(new StructuralIndexedComplexPropertyChainEntryImpl(leftProperty,
				rightProperty));
	}

	@Override
	public ModifiableIndexedDataHasValue getIndexedDataHasValue(
			ElkDataHasValue elkDataHasValue) {
		return filter(new ModifiableIndexedDataHasValueImpl(elkDataHasValue));
	}

	@Override
	public ModifiableIndexedDeclarationAxiom getIndexedDeclarationAxiom(
			ElkAxiom originalAxiom, ModifiableIndexedEntity entity) {
		return filter(new ModifiableIndexedDeclarationAxiomImpl<ElkAxiom>(
				originalAxiom, entity));
	}

	@Override
	public ModifiableIndexedDisjointClassesAxiom getIndexedDisjointClassesAxiom(
			ElkAxiom originalAxiom,
			ModifiableIndexedClassExpressionList members) {
		return filter(new ModifiableIndexedDisjointClassesAxiomImpl<ElkAxiom>(
				originalAxiom, members));
	}

	@Override
	public ModifiableIndexedEquivalentClassesAxiom getIndexedEquivalentClassesAxiom(
			ElkAxiom originalAxiom,
			ModifiableIndexedClassExpression firstMember,
			ModifiableIndexedClassExpression secondMember) {
		return filter(new ModifiableIndexedEquivalentClassesAxiomImpl<ElkAxiom>(
				originalAxiom, firstMember, secondMember));
	}

	@Override
	public ModifiableIndexedIndividual getIndexedIndividual(
			ElkNamedIndividual elkNamedIndividual) {
		return filter(new StructuralIndexedIndividualEntryImpl(elkNamedIndividual));
	}

	@Override
	public ModifiableIndexedObjectComplementOf getIndexedObjectComplementOf(
			ModifiableIndexedClassExpression negated) {
		return filter(new ModifiableIndexedObjectComplementOfImpl(negated));
	}

	@Override
	public ModifiableIndexedObjectHasSelf getIndexedObjectHasSelf(
			ModifiableIndexedObjectProperty property) {
		return filter(new ModifiableIndexedObjectHasSelfImpl(property));
	}

	@Override
	public ModifiableIndexedObjectIntersectionOf getIndexedObjectIntersectionOf(
			ModifiableIndexedClassExpression conjunctA,
			ModifiableIndexedClassExpression conjunctB) {
		return filter(new ModifiableIndexedObjectIntersectionOfImpl(conjunctA,
				conjunctB));
	}

	@Override
	public ModifiableIndexedObjectProperty getIndexedObjectProperty(
			ElkObjectProperty elkObjectProperty) {
		return filter(new StructuralIndexedObjectPropertyEntryImpl(elkObjectProperty));
	}

	@Override
	public ModifiableIndexedObjectPropertyRangeAxiom getIndexedObjectPropertyRangeAxiom(
			ElkAxiom originalAxiom, ModifiableIndexedObjectProperty property,
			ModifiableIndexedClassExpression range) {
		return filter(
				new ModifiableIndexedObjectPropertyRangeAxiomImpl<ElkAxiom>(
						originalAxiom, property, range));
	}

	@Override
	public ModifiableIndexedObjectSomeValuesFrom getIndexedObjectSomeValuesFrom(
			ModifiableIndexedObjectProperty property,
			ModifiableIndexedClassExpression filler) {
		return filter(
				new ModifiableIndexedObjectSomeValuesFromImpl(property, filler));
	}

	@Override
	public ModifiableIndexedObjectUnionOf getIndexedObjectUnionOf(
			List<? extends ModifiableIndexedClassExpression> disjuncts) {
		return filter(new ModifiableIndexedObjectUnionOfImpl(disjuncts));
	}

	@Override
	public ModifiableIndexedSubClassOfAxiom getIndexedSubClassOfAxiom(
			ElkAxiom originalAxiom, ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		return filter(new ModifiableIndexedSubClassOfAxiomImpl<ElkAxiom>(
				originalAxiom, subClass, superClass));
	}

	@Override
	public ModifiableIndexedSubObjectPropertyOfAxiom getIndexedSubObjectPropertyOfAxiom(
			ElkAxiom originalAxiom,
			ModifiableIndexedPropertyChain subPropertyChain,
			ModifiableIndexedObjectProperty superProperty) {
		return filter(
				new ModifiableIndexedSubObjectPropertyOfAxiomImpl<ElkAxiom>(
						originalAxiom, subPropertyChain, superProperty));
	}

}
