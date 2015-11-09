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

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDifferentIndividualsAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointUnionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedClass;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectHasSelf;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.factories.ModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.inferences.ModifiableElkClassAssertionAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.inferences.ModifiableElkDeclarationAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.inferences.ModifiableElkDifferentIndividualsAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.inferences.ModifiableElkDifferentIndividualsAxiomNaryConversion;
import org.semanticweb.elk.reasoner.indexing.inferences.ModifiableElkDisjointClassesAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.inferences.ModifiableElkDisjointClassesAxiomNaryConversion;
import org.semanticweb.elk.reasoner.indexing.inferences.ModifiableElkDisjointUnionAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.inferences.ModifiableElkDisjointUnionAxiomDefinitionConversion;
import org.semanticweb.elk.reasoner.indexing.inferences.ModifiableElkDisjointUnionAxiomNaryConversion;
import org.semanticweb.elk.reasoner.indexing.inferences.ModifiableElkDisjointUnionAxiomSubClassConversion;
import org.semanticweb.elk.reasoner.indexing.inferences.ModifiableElkEquivalentClassesAxiomDefinitionConversion;
import org.semanticweb.elk.reasoner.indexing.inferences.ModifiableElkEquivalentClassesAxiomSubClassConversion;
import org.semanticweb.elk.reasoner.indexing.inferences.ModifiableElkEquivalentObjectPropertiesAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.inferences.ModifiableElkObjectPropertyAssertionAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.inferences.ModifiableElkObjectPropertyDomainAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.inferences.ModifiableElkObjectPropertyRangeAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.inferences.ModifiableElkReflexiveObjectPropertyAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.inferences.ModifiableElkSameIndividualAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.inferences.ModifiableElkSubClassOfAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.inferences.ModifiableElkSubObjectPropertyOfAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.inferences.ModifiableElkTransitiveObjectPropertyAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClass;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedEntity;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedPropertyChain;

/**
 * Implements {@link ModifiableIndexedObjectFactory} and
 * {@link CachedIndexedObjectFactory}. The occurrences of the created objects
 * are not modified.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public class ModifiableIndexedObjectFactoryImpl
		implements
			ModifiableIndexedObjectFactory,
			CachedIndexedObjectFactory {

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
	public ModifiableElkClassAssertionAxiomConversion getElkClassAssertionAxiomConversion(
			ElkClassAssertionAxiom originalAxiom,
			ModifiableIndexedIndividual instance,
			ModifiableIndexedClassExpression type) {
		return new ModifiableElkClassAssertionAxiomConversionImpl(originalAxiom,
				instance, type);
	}

	@Override
	public ModifiableElkDeclarationAxiomConversion getElkDeclarationAxiomConversion(
			ElkDeclarationAxiom originalAxiom, ModifiableIndexedEntity entity) {
		return new ModifiableElkDeclarationAxiomConversionImpl(originalAxiom,
				entity);
	}

	@Override
	public ModifiableElkDifferentIndividualsAxiomBinaryConversion getElkDifferentIndividualsAxiomBinaryConversion(
			ElkDifferentIndividualsAxiom originalAxiom,
			int firstIndividualPosition, int secondIndividualPosition,
			ModifiableIndexedObjectIntersectionOf conjunction,
			ModifiableIndexedClass bottom) {
		return new ModifiableElkDifferentIndividualsAxiomBinaryConversionImpl(
				originalAxiom, firstIndividualPosition,
				secondIndividualPosition, conjunction, bottom);
	}

	@Override
	public ModifiableElkDifferentIndividualsAxiomNaryConversion getElkDifferentIndividualsAxiomNaryConversion(
			ElkDifferentIndividualsAxiom originalAxiom,
			ModifiableIndexedClassExpressionList differentIndividuals) {
		return new ModifiableElkDifferentIndividualsAxiomNaryConversionImpl(
				originalAxiom, differentIndividuals);
	}

	@Override
	public ModifiableElkDisjointClassesAxiomBinaryConversion getElkDisjointClassesAxiomBinaryConversion(
			ElkDisjointClassesAxiom originalAxiom, int firstClassPosition,
			int secondClassPosition,
			ModifiableIndexedObjectIntersectionOf conjunction,
			ModifiableIndexedClass bottom) {
		return new ModifiableElkDisjointClassesAxiomBinaryConversionImpl(
				originalAxiom, firstClassPosition, secondClassPosition,
				conjunction, bottom);
	}

	@Override
	public ModifiableElkDisjointClassesAxiomNaryConversion getElkDisjointClassesAxiomNaryConversion(
			ElkDisjointClassesAxiom originalAxiom,
			ModifiableIndexedClassExpressionList disjointClasses) {
		return new ModifiableElkDisjointClassesAxiomNaryConversionImpl(
				originalAxiom, disjointClasses);
	}

	@Override
	public ModifiableElkDisjointUnionAxiomBinaryConversion getElkDisjointUnionAxiomBinaryConversion(
			ElkDisjointUnionAxiom originalAxiom, int firstDisjunctPosition,
			int secondDisjunctPosition,
			ModifiableIndexedObjectIntersectionOf conjunction,
			ModifiableIndexedClass bottom) {
		return new ModifiableElkDisjointUnionAxiomBinaryConversionImpl(
				originalAxiom, firstDisjunctPosition, secondDisjunctPosition,
				conjunction, bottom);
	}

	@Override
	public ModifiableElkDisjointUnionAxiomDefinitionConversion getElkDisjointUnionAxiomDefinitionConversion(
			ElkDisjointUnionAxiom originalAxiom,
			ModifiableIndexedClass definedClass,
			ModifiableIndexedClassExpression definition) {
		return new ModifiableElkDisjointUnionAxiomDefinitionConversionImpl(
				originalAxiom, definedClass, definition);
	}

	@Override
	public ModifiableElkDisjointUnionAxiomNaryConversion getElkDisjointUnionAxiomNaryConversion(
			ElkDisjointUnionAxiom originalAxiom,
			ModifiableIndexedClassExpressionList disjointClasses) {
		return new ModifiableElkDisjointUnionAxiomNaryConversionImpl(
				originalAxiom, disjointClasses);
	}

	@Override
	public ModifiableElkDisjointUnionAxiomSubClassConversion getElkDisjointUnionAxiomSubClassConversion(
			ElkDisjointUnionAxiom originalAxiom, int disjunctPosition,
			ModifiableIndexedClassExpression disjunct,
			ModifiableIndexedClass definedClass) {
		return new ModifiableElkDisjointUnionAxiomSubClassConversionImpl(
				originalAxiom, disjunctPosition, disjunct, definedClass);
	}

	@Override
	public ModifiableElkEquivalentClassesAxiomDefinitionConversion getElkEquivalentClassesAxiomDefinitionConversion(
			ElkEquivalentClassesAxiom originalAxiom, int definedClassPosition,
			int definitionPosition, ModifiableIndexedClass definedClass,
			ModifiableIndexedClassExpression definition) {
		return new ModifiableElkEquivalentClassesAxiomDefinitionConversionImpl(
				originalAxiom, definedClassPosition, definitionPosition,
				definedClass, definition);
	}

	@Override
	public ModifiableElkEquivalentClassesAxiomSubClassConversion getElkEquivalentClassesAxiomSubClassConversion(
			ElkEquivalentClassesAxiom originalAxiom, int subClassPosition,
			int superClassPosition, ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		return new ModifiableElkEquivalentClassesAxiomSubClassConversionImpl(
				originalAxiom, subClassPosition, superClassPosition, subClass,
				superClass);
	}

	@Override
	public ModifiableElkEquivalentObjectPropertiesAxiomConversion getElkEquivalentObjectPropertiesAxiomConversion(
			ElkEquivalentObjectPropertiesAxiom originalAxiom,
			int subPropertyPosition, int superPropertyPosition,
			ModifiableIndexedObjectProperty subProperty,
			ModifiableIndexedObjectProperty superProperty) {
		return new ModifiableElkEquivalentObjectPropertiesAxiomConversionImpl(
				originalAxiom, subPropertyPosition, superPropertyPosition,
				subProperty, superProperty);
	}

	@Override
	public ModifiableElkObjectPropertyAssertionAxiomConversion getElkObjectPropertyAssertionAxiomConversion(
			ElkObjectPropertyAssertionAxiom originalAxiom,
			ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		return new ModifiableElkObjectPropertyAssertionAxiomConversionImpl(
				originalAxiom, subClass, superClass);
	}

	@Override
	public ModifiableElkObjectPropertyDomainAxiomConversion getElkObjectPropertyDomainAxiomConversion(
			ElkObjectPropertyDomainAxiom originalAxiom,
			ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		return new ModifiableElkObjectPropertyDomainAxiomConversionImpl(
				originalAxiom, subClass, superClass);
	}

	@Override
	public ModifiableElkObjectPropertyRangeAxiomConversion getElkObjectPropertyRangeAxiomConversion(
			ElkObjectPropertyRangeAxiom originalAxiom,
			ModifiableIndexedObjectProperty property,
			ModifiableIndexedClassExpression range) {
		return new ModifiableElkObjectPropertyRangeAxiomConversionImpl(
				originalAxiom, property, range);
	}

	@Override
	public ModifiableElkReflexiveObjectPropertyAxiomConversion getElkReflexiveObjectPropertyAxiomConversion(
			ElkReflexiveObjectPropertyAxiom originalAxiom,
			ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		return new ModifiableElkReflexiveObjectPropertyAxiomConversionImpl(
				originalAxiom, subClass, superClass);
	}

	@Override
	public ModifiableElkSameIndividualAxiomConversion getElkSameIndividualAxiomConversion(
			ElkSameIndividualAxiom originalAxiom, int subIndividualPosition,
			int superIndividualPosition,
			ModifiableIndexedIndividual subIndividual,
			ModifiableIndexedIndividual superIndividual) {
		return new ModifiableElkSameIndividualAxiomConversionImpl(originalAxiom,
				subIndividualPosition, superIndividualPosition, subIndividual,
				superIndividual);
	}

	@Override
	public ModifiableElkSubClassOfAxiomConversion getElkSubClassOfAxiomConversion(
			ElkSubClassOfAxiom originalAxiom,
			ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		return new ModifiableElkSubClassOfAxiomConversionImpl(originalAxiom,
				subClass, superClass);
	}

	@Override
	public ModifiableElkSubObjectPropertyOfAxiomConversion getElkSubObjectPropertyOfAxiomConversion(
			ElkSubObjectPropertyOfAxiom originalAxiom,
			ModifiableIndexedPropertyChain subPropertyChain,
			ModifiableIndexedObjectProperty superProperty) {
		return new ModifiableElkSubObjectPropertyOfAxiomConversionImpl(
				originalAxiom, subPropertyChain, superProperty);
	}

	@Override
	public ModifiableElkTransitiveObjectPropertyAxiomConversion getElkTransitiveObjectPropertyAxiomConversion(
			ElkTransitiveObjectPropertyAxiom originalAxiom,
			ModifiableIndexedPropertyChain subPropertyChain,
			ModifiableIndexedObjectProperty superProperty) {
		return new ModifiableElkTransitiveObjectPropertyAxiomConversionImpl(
				originalAxiom, subPropertyChain, superProperty);
	}

}
