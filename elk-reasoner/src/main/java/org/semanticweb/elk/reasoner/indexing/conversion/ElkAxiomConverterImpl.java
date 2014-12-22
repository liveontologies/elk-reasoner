package org.semanticweb.elk.reasoner.indexing.conversion;

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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.caching.ModifiableIndexedObjectCache;
import org.semanticweb.elk.reasoner.indexing.caching.ResolvingModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.caching.UpdatingModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.factories.ModifiableIndexedAxiomFactory;
import org.semanticweb.elk.reasoner.indexing.factories.ModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.implementation.ModifiableIndexedObjectFactoryImpl;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClass;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;

public class ElkAxiomConverterImpl extends FailingElkAxiomConverter {

	private final ModifiableIndexedAxiomFactory axiomFactory_;

	private final ModifiableIndexedObjectFactory positiveFactory_,
			negativeFactory_;

	private final ElkPolarityExpressionConverter positiveConverter_,
			negativeConverter_, dualConverter_;

	private final ElkEntityConverter entityConverter_;

	ElkAxiomConverterImpl(ModifiableIndexedAxiomFactory factory,
			ModifiableIndexedObjectFactory positiveFactory,
			ModifiableIndexedObjectFactory negativeFactory,
			ElkPolarityExpressionConverter positiveConverter,
			ElkPolarityExpressionConverter negativeConverter,
			ElkPolarityExpressionConverter dualConverter,
			ElkEntityConverter entityConverter) {
		this.axiomFactory_ = factory;
		this.positiveFactory_ = positiveFactory;
		this.negativeFactory_ = negativeFactory;
		this.positiveConverter_ = positiveConverter;
		this.negativeConverter_ = negativeConverter;
		this.dualConverter_ = dualConverter;
		this.entityConverter_ = entityConverter;
	}

	public ElkAxiomConverterImpl(ModifiableIndexedObjectFactory neutralFactory,
			ModifiableIndexedObjectFactory positiveFactory,
			ModifiableIndexedObjectFactory negativeFactory,
			ModifiableIndexedObjectFactory dualFactory) {
		this.axiomFactory_ = neutralFactory;
		this.positiveFactory_ = positiveFactory;
		this.negativeFactory_ = negativeFactory;
		this.positiveConverter_ = new ElkPolarityExpressionConverterImpl(
				positiveFactory, negativeFactory);
		this.negativeConverter_ = positiveConverter_
				.getComplementaryConverter();
		this.dualConverter_ = new ElkPolarityExpressionConverterImpl(
				dualFactory);
		this.entityConverter_ = new ElkEntityConverterImpl(neutralFactory);
	}

	public ElkAxiomConverterImpl(ModifiableIndexedObjectFactory factory) {
		this(factory, factory, factory, factory);
	}

	public ElkAxiomConverterImpl(ModifiableIndexedObjectCache cache) {
		this(new ResolvingModifiableIndexedObjectFactory(cache));
	}

	<F extends CachedIndexedObjectFactory & ModifiableIndexedObjectFactory> ElkAxiomConverterImpl(
			F baseFactory, ModifiableOntologyIndex index, int increment) {
		this(new UpdatingModifiableIndexedObjectFactory(baseFactory, index,
				increment, 0, 0),// neutralFactory
				new UpdatingModifiableIndexedObjectFactory(baseFactory, index,
						increment, increment, 0),// positiveFactory
				new UpdatingModifiableIndexedObjectFactory(baseFactory, index,
						increment, 0, increment),// negative Factory
				new UpdatingModifiableIndexedObjectFactory(baseFactory, index,
						increment, increment, increment)// dualFactory
		);
	}

	public ElkAxiomConverterImpl(ModifiableOntologyIndex index, int increment) {
		this(new ModifiableIndexedObjectFactoryImpl(), index, increment);
	}

	@Override
	public Void visit(ElkObjectPropertyDomainAxiom axiom) {
		ModifiableIndexedClass indexedOwlThing = negativeFactory_
				.getIndexedClass(PredefinedElkClass.OWL_THING);
		axiomFactory_.getIndexedSubClassOfAxiom(negativeFactory_
				.getIndexedObjectSomeValuesFrom(
						axiom.getProperty().accept(negativeConverter_),
						indexedOwlThing),
				axiom.getDomain().accept(positiveConverter_));
		return null;
	}

	@Override
	public Void visit(ElkReflexiveObjectPropertyAxiom axiom) {
		axiomFactory_.getIndexedReflexiveObjectPropertyAxiom(axiom
				.getProperty().accept(positiveConverter_));
		return null;
	}

	@Override
	public Void visit(ElkSubObjectPropertyOfAxiom axiom) {
		axiomFactory_.getIndexedSubObjectPropertyOfAxiom(axiom
				.getSubObjectPropertyExpression().accept(this), axiom
				.getSuperObjectPropertyExpression().accept(positiveConverter_));
		return null;
	}

	@Override
	public Void visit(ElkTransitiveObjectPropertyAxiom axiom) {
		ModifiableIndexedObjectProperty p = axiom.getProperty().accept(
				dualConverter_);
		axiomFactory_.getIndexedSubObjectPropertyOfAxiom(
				negativeFactory_.getIndexedBinaryPropertyChain(p, p), p);
		return null;
	}

	@Override
	public Void visit(ElkEquivalentClassesAxiom axiom) {
		// reduces equivalence to sub-class axioms
		ModifiableIndexedClassExpression first = null;
		for (ElkClassExpression c : axiom.getClassExpressions()) {
			if (first == null)
				first = c.accept(dualConverter_);
			else {
				ModifiableIndexedClassExpression other = c
						.accept(dualConverter_);
				axiomFactory_.getIndexedSubClassOfAxiom(first, other);
				axiomFactory_.getIndexedSubClassOfAxiom(other, first);
			}
		}
		return null;
	}

	@Override
	public Void visit(ElkSubClassOfAxiom axiom) {
		axiomFactory_.getIndexedSubClassOfAxiom(axiom.getSubClassExpression()
				.accept(negativeConverter_), axiom.getSuperClassExpression()
				.accept(positiveConverter_));
		return null;
	}

	/**
	 * With at most how many disjoint classes transform to binary disjointness
	 * axioms
	 */
	private final static int DISJOINT_AXIOM_BINARIZATION_THRESHOLD = 2;

	@Override
	public Void visit(ElkDisjointClassesAxiom axiom) {
		List<? extends ElkClassExpression> members = axiom
				.getClassExpressions();
		/*
		 * if the axiom contains sufficiently many disjoint classes, convert it
		 * natively
		 */
		if (members.size() > DISJOINT_AXIOM_BINARIZATION_THRESHOLD) {
			List<ModifiableIndexedClassExpression> indexedMembers = new ArrayList<ModifiableIndexedClassExpression>(
					members.size());
			for (ElkClassExpression member : members)
				indexedMembers.add(member.accept(negativeConverter_));
			axiomFactory_.getIndexedDisjointnessAxiom(indexedMembers);
			return null;
		}
		/*
		 * otherwise create a binary disjointness axiom for all pairs (member,
		 * otherMember) where otherMember occurs after member in members
		 */
		ModifiableIndexedClass indexedOwlNothing = positiveFactory_
				.getIndexedClass(PredefinedElkClass.OWL_NOTHING);
		for (final ElkClassExpression member : members) {
			ModifiableIndexedClassExpression indexedMember = member
					.accept(negativeConverter_);
			boolean selfFound = false;
			for (ElkClassExpression otherMember : members) {
				if (!selfFound) {
					if (otherMember == member)
						selfFound = true;
					continue;
				}
				// else otherMember appears after member
				ModifiableIndexedClassExpression indexedOtherMember = otherMember
						.accept(negativeConverter_);
				axiomFactory_.getIndexedSubClassOfAxiom(negativeFactory_
						.getIndexedObjectIntersectionOf(indexedMember,
								indexedOtherMember), indexedOwlNothing);
			}
		}
		return null;
	}

	@Override
	public Void visit(ElkClassAssertionAxiom axiom) {
		axiomFactory_.getIndexedSubClassOfAxiom(
				axiom.getIndividual().accept(negativeConverter_), axiom
						.getClassExpression().accept(positiveConverter_));
		return null;
	}

	@Override
	public Void visit(ElkObjectPropertyAssertionAxiom axiom) {
		axiomFactory_.getIndexedSubClassOfAxiom(
				axiom.getSubject().accept(negativeConverter_), positiveFactory_
						.getIndexedObjectSomeValuesFrom(axiom.getProperty()
								.accept(positiveConverter_), axiom.getSubject()
								.accept(positiveConverter_)));
		return null;
	}

	@Override
	public Void visit(ElkDeclarationAxiom axiom) {
		axiomFactory_.getIndexedDeclarationAxiom(axiom.getEntity().accept(
				entityConverter_));
		return null;
	}

	@Override
	public ModifiableIndexedPropertyChain visit(ElkObjectProperty expression) {
		return expression.accept(negativeConverter_);
	}

	@Override
	public ModifiableIndexedPropertyChain visit(
			ElkObjectPropertyChain elkObjectPropertyChain) {
		ModifiableIndexedPropertyChain result = null;
		// iterating over the chain backwards
		ListIterator<? extends ElkObjectPropertyExpression> iterator = elkObjectPropertyChain
				.getObjectPropertyExpressions().listIterator(
						elkObjectPropertyChain.getObjectPropertyExpressions()
								.size());
		while (iterator.hasPrevious()) {
			ModifiableIndexedObjectProperty iop = iterator.previous().accept(
					negativeConverter_);
			if (result == null) {
				result = iop;
				continue;
			}
			result = negativeFactory_
					.getIndexedBinaryPropertyChain(iop, result);
		}
		return result;
	}

}
