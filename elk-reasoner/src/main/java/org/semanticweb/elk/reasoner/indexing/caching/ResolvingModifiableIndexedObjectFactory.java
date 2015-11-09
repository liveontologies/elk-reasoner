package org.semanticweb.elk.reasoner.indexing.caching;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDifferentIndividualsAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointUnionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.reasoner.indexing.factories.ModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.implementation.ModifiableIndexedObjectFactoryImpl;
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
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClass;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedEntity;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedPropertyChain;

/**
 * A {@link ModifiableIndexedObjectFactory} which can only create object present
 * in the provided {@link ModifiableIndexedObjectCache} or (new) not cacheable
 * objects. If a created object is cacheable and there is no structurally
 * equivalent object in the provided {@link ModifiableIndexedObjectCache},
 * {@code null} is returned.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public class ResolvingModifiableIndexedObjectFactory
		extends
			ResolvingCachedIndexedObjectFactory
		implements
			ModifiableIndexedObjectFactory {

	private final ModifiableIndexedObjectFactory baseFactory_;

	public <F extends CachedIndexedObjectFactory & ModifiableIndexedObjectFactory> ResolvingModifiableIndexedObjectFactory(
			F baseFactory, ModifiableIndexedObjectCache cache) {
		super(baseFactory, cache);
		this.baseFactory_ = baseFactory;
	}

	public ResolvingModifiableIndexedObjectFactory(
			ModifiableIndexedObjectCache cache) {
		this(new ModifiableIndexedObjectFactoryImpl(), cache);
	}

	@SuppressWarnings("static-method")
	<T extends ModifiableIndexedAxiom> T filter(T input) {
		return input;
	}

	@Override
	public ModifiableElkClassAssertionAxiomConversion getElkClassAssertionAxiomConversion(
			ElkClassAssertionAxiom originalAxiom,
			ModifiableIndexedIndividual instance,
			ModifiableIndexedClassExpression type) {
		return filter(baseFactory_.getElkClassAssertionAxiomConversion(
				originalAxiom, instance, type));
	}

	@Override
	public ModifiableElkDeclarationAxiomConversion getElkDeclarationAxiomConversion(
			ElkDeclarationAxiom originalAxiom, ModifiableIndexedEntity entity) {
		return filter(baseFactory_
				.getElkDeclarationAxiomConversion(originalAxiom, entity));
	}

	@Override
	public ModifiableElkDifferentIndividualsAxiomBinaryConversion getElkDifferentIndividualsAxiomBinaryConversion(
			ElkDifferentIndividualsAxiom originalAxiom,
			int firstIndividualPosition, int secondIndividualPosition,
			ModifiableIndexedObjectIntersectionOf conjunction,
			ModifiableIndexedClass bottom) {
		return filter(
				baseFactory_.getElkDifferentIndividualsAxiomBinaryConversion(
						originalAxiom, firstIndividualPosition,
						secondIndividualPosition, conjunction, bottom));
	}

	@Override
	public ModifiableElkDifferentIndividualsAxiomNaryConversion getElkDifferentIndividualsAxiomNaryConversion(
			ElkDifferentIndividualsAxiom originalAxiom,
			ModifiableIndexedClassExpressionList differentIndividuals) {
		return filter(
				baseFactory_.getElkDifferentIndividualsAxiomNaryConversion(
						originalAxiom, differentIndividuals));
	}

	@Override
	public ModifiableElkDisjointClassesAxiomBinaryConversion getElkDisjointClassesAxiomBinaryConversion(
			ElkDisjointClassesAxiom originalAxiom, int firstClassPosition,
			int secondClassPosition,
			ModifiableIndexedObjectIntersectionOf conjunction,
			ModifiableIndexedClass bottom) {
		return filter(baseFactory_.getElkDisjointClassesAxiomBinaryConversion(
				originalAxiom, firstClassPosition, secondClassPosition,
				conjunction, bottom));
	}

	@Override
	public ModifiableElkDisjointClassesAxiomNaryConversion getElkDisjointClassesAxiomNaryConversion(
			ElkDisjointClassesAxiom originalAxiom,
			ModifiableIndexedClassExpressionList disjointClasses) {
		return filter(baseFactory_.getElkDisjointClassesAxiomNaryConversion(
				originalAxiom, disjointClasses));
	}

	@Override
	public ModifiableElkDisjointUnionAxiomBinaryConversion getElkDisjointUnionAxiomBinaryConversion(
			ElkDisjointUnionAxiom originalAxiom, int firstDisjunctPosition,
			int secondDisjunctPosition,
			ModifiableIndexedObjectIntersectionOf conjunction,
			ModifiableIndexedClass bottom) {
		return filter(baseFactory_.getElkDisjointUnionAxiomBinaryConversion(
				originalAxiom, firstDisjunctPosition, secondDisjunctPosition,
				conjunction, bottom));
	}

	@Override
	public ModifiableElkDisjointUnionAxiomDefinitionConversion getElkDisjointUnionAxiomDefinitionConversion(
			ElkDisjointUnionAxiom originalAxiom,
			ModifiableIndexedClass definedClass,
			ModifiableIndexedClassExpression definition) {
		return filter(baseFactory_.getElkDisjointUnionAxiomDefinitionConversion(
				originalAxiom, definedClass, definition));
	}

	@Override
	public ModifiableElkDisjointUnionAxiomNaryConversion getElkDisjointUnionAxiomNaryConversion(
			ElkDisjointUnionAxiom originalAxiom,
			ModifiableIndexedClassExpressionList disjointClasses) {
		return filter(baseFactory_.getElkDisjointUnionAxiomNaryConversion(
				originalAxiom, disjointClasses));
	}

	@Override
	public ModifiableElkDisjointUnionAxiomSubClassConversion getElkDisjointUnionAxiomSubClassConversion(
			ElkDisjointUnionAxiom originalAxiom, int disjunctPosition,
			ModifiableIndexedClassExpression disjunct,
			ModifiableIndexedClass definedClass) {
		return filter(baseFactory_.getElkDisjointUnionAxiomSubClassConversion(
				originalAxiom, disjunctPosition, disjunct, definedClass));
	}

	@Override
	public ModifiableElkEquivalentClassesAxiomDefinitionConversion getElkEquivalentClassesAxiomDefinitionConversion(
			ElkEquivalentClassesAxiom originalAxiom, int definedClassPosition,
			int definitionPosition, ModifiableIndexedClass definedClass,
			ModifiableIndexedClassExpression definition) {
		return filter(
				baseFactory_.getElkEquivalentClassesAxiomDefinitionConversion(
						originalAxiom, definedClassPosition, definitionPosition,
						definedClass, definition));
	}

	@Override
	public ModifiableElkEquivalentClassesAxiomSubClassConversion getElkEquivalentClassesAxiomSubClassConversion(
			ElkEquivalentClassesAxiom originalAxiom, int subClassPosition,
			int superClassPosition, ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		return filter(
				baseFactory_.getElkEquivalentClassesAxiomSubClassConversion(
						originalAxiom, subClassPosition, superClassPosition,
						subClass, superClass));
	}

	@Override
	public ModifiableElkEquivalentObjectPropertiesAxiomConversion getElkEquivalentObjectPropertiesAxiomConversion(
			ElkEquivalentObjectPropertiesAxiom originalAxiom,
			int subPropertyPosition, int superPropertyPosition,
			ModifiableIndexedObjectProperty subProperty,
			ModifiableIndexedObjectProperty superProperty) {
		return filter(
				baseFactory_.getElkEquivalentObjectPropertiesAxiomConversion(
						originalAxiom, subPropertyPosition,
						superPropertyPosition, subProperty, superProperty));
	}

	@Override
	public ModifiableElkObjectPropertyAssertionAxiomConversion getElkObjectPropertyAssertionAxiomConversion(
			ElkObjectPropertyAssertionAxiom originalAxiom,
			ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		return filter(baseFactory_.getElkObjectPropertyAssertionAxiomConversion(
				originalAxiom, subClass, superClass));
	}

	@Override
	public ModifiableElkObjectPropertyDomainAxiomConversion getElkObjectPropertyDomainAxiomConversion(
			ElkObjectPropertyDomainAxiom originalAxiom,
			ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		return filter(baseFactory_.getElkObjectPropertyDomainAxiomConversion(
				originalAxiom, subClass, superClass));
	}

	@Override
	public ModifiableElkObjectPropertyRangeAxiomConversion getElkObjectPropertyRangeAxiomConversion(
			ElkObjectPropertyRangeAxiom originalAxiom,
			ModifiableIndexedObjectProperty property,
			ModifiableIndexedClassExpression range) {
		return filter(baseFactory_.getElkObjectPropertyRangeAxiomConversion(
				originalAxiom, property, range));
	}

	@Override
	public ModifiableElkReflexiveObjectPropertyAxiomConversion getElkReflexiveObjectPropertyAxiomConversion(
			ElkReflexiveObjectPropertyAxiom originalAxiom,
			ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		return filter(baseFactory_.getElkReflexiveObjectPropertyAxiomConversion(
				originalAxiom, subClass, superClass));
	}

	@Override
	public ModifiableElkSameIndividualAxiomConversion getElkSameIndividualAxiomConversion(
			ElkSameIndividualAxiom originalAxiom, int subIndividualPosition,
			int superIndividualPosition,
			ModifiableIndexedIndividual subIndividual,
			ModifiableIndexedIndividual superIndividual) {
		return filter(baseFactory_.getElkSameIndividualAxiomConversion(
				originalAxiom, subIndividualPosition, superIndividualPosition,
				subIndividual, superIndividual));
	}

	@Override
	public ModifiableElkSubClassOfAxiomConversion getElkSubClassOfAxiomConversion(
			ElkSubClassOfAxiom originalAxiom,
			ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		return filter(baseFactory_.getElkSubClassOfAxiomConversion(
				originalAxiom, subClass, superClass));
	}

	@Override
	public ModifiableElkSubObjectPropertyOfAxiomConversion getElkSubObjectPropertyOfAxiomConversion(
			ElkSubObjectPropertyOfAxiom originalAxiom,
			ModifiableIndexedPropertyChain subPropertyChain,
			ModifiableIndexedObjectProperty superProperty) {
		return filter(baseFactory_.getElkSubObjectPropertyOfAxiomConversion(
				originalAxiom, subPropertyChain, superProperty));
	}

	@Override
	public ModifiableElkTransitiveObjectPropertyAxiomConversion getElkTransitiveObjectPropertyAxiomConversion(
			ElkTransitiveObjectPropertyAxiom originalAxiom,
			ModifiableIndexedPropertyChain subPropertyChain,
			ModifiableIndexedObjectProperty superProperty) {
		return filter(
				baseFactory_.getElkTransitiveObjectPropertyAxiomConversion(
						originalAxiom, subPropertyChain, superProperty));
	}

}
