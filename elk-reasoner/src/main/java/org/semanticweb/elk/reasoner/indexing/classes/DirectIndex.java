/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.indexing.classes;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.predefined.PredefinedElkDeclaration;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkAxiomConverter;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkAxiomConverterImpl;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkIndexingUnsupportedException;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.ChainableContextInitRule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.LinkedContextInitRule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.RootContextInitializationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ChainableSubsumerRule;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.chains.AbstractChain;
import org.semanticweb.elk.util.collections.chains.Chain;

/**
 * An implementation of {@link ModifiableOntologyIndex}
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public class DirectIndex extends ModifiableIndexedObjectCacheImpl implements
		ModifiableOntologyIndex {

	private ChainableContextInitRule contextInitRules_;

	private final Multimap<IndexedObjectProperty, ElkAxiom> reflexiveObjectProperties_;

	private int negativeOwlThingOccurrenceNo_ = 0,
			positiveOwlNothingOccurrenceNo_ = 0;

	public DirectIndex() {
		this.reflexiveObjectProperties_ = new HashListMultimap<IndexedObjectProperty, ElkAxiom>(
				64);

		// the context root initialization rule is always registered
		RootContextInitializationRule.addRuleFor(this);

		// index build-in declarations TODO: move somewhere else
		ElkAxiomConverter tmpConverter = new ElkAxiomConverterImpl(this, 1);
		for (ElkDeclarationAxiom declaration : PredefinedElkDeclaration
				.values()) {
			try {
				declaration.accept(tmpConverter);
			} catch (ElkIndexingUnsupportedException e) {
				// ignore unsupported declarations
				continue;
			}
		}
	}

	/* read-only methods required by the interface */

	@Override
	public LinkedContextInitRule getContextInitRuleHead() {
		return contextInitRules_;
	}

	@Override
	public Multimap<IndexedObjectProperty, ElkAxiom> getReflexiveObjectProperties() {
		// TODO: make unmodifiable
		return reflexiveObjectProperties_;
	}

	/* read-write methods required by the interface */

	@Override
	public boolean addContextInitRule(ChainableContextInitRule newRule) {
		return newRule.addTo(getContextInitRuleChain());
	}

	@Override
	public boolean removeContextInitRule(ChainableContextInitRule oldRule) {
		return oldRule.removeFrom(getContextInitRuleChain());
	}

	@Override
	public boolean add(ModifiableIndexedClassExpression target,
			ChainableSubsumerRule rule) {
		return rule.addTo(target.getCompositionRuleChain());
	}

	@Override
	public boolean remove(ModifiableIndexedClassExpression target,
			ChainableSubsumerRule rule) {
		return rule.removeFrom(target.getCompositionRuleChain());
	}

	@Override
	public boolean addReflexiveProperty(IndexedObjectProperty property,
			ElkAxiom reason) {
		return reflexiveObjectProperties_.add(property, reason);
	}

	@Override
	public boolean removeReflexiveProperty(IndexedObjectProperty property,
			ElkAxiom reason) {
		return reflexiveObjectProperties_.remove(property, reason);
	}

	@Override
	public boolean hasNegativeOwlThing() {
		return negativeOwlThingOccurrenceNo_ > 0;
	}

	@Override
	public boolean updateNegativeOwlThingOccurrenceNo(int increment) {
		negativeOwlThingOccurrenceNo_ += increment;
		return true;
	}

	@Override
	public boolean hasPositivelyOwlNothing() {
		return positiveOwlNothingOccurrenceNo_ > 0;
	}

	@Override
	public boolean tryAddDefinition(ModifiableIndexedClass target,
			ModifiableIndexedClassExpression definition, ElkAxiom reason) {
		return target.setDefinition(definition, reason);
	}

	@Override
	public boolean tryRemoveDefinition(ModifiableIndexedClass target,
			ModifiableIndexedClassExpression definition, ElkAxiom reason) {
		if (target.getDefinition() != definition
				|| !target.getDefinitionReason().equals(reason))
			// it was not defined by this definition
			return false;
		// else
		target.removeDefinition();
		return true;
	}

	@Override
	public boolean updatePositiveOwlNothingOccurrenceNo(int increment) {
		positiveOwlNothingOccurrenceNo_ += increment;
		return true;
	}

	/* class-specific methods */

	/**
	 * @return a {@link Chain} view of context initialization rules assigned to
	 *         this {@link OntologyIndex}; it can be used for inserting new
	 *         rules or deleting existing ones
	 */
	public Chain<ChainableContextInitRule> getContextInitRuleChain() {
		return new AbstractChain<ChainableContextInitRule>() {

			@Override
			public ChainableContextInitRule next() {
				return contextInitRules_;
			}

			@Override
			public void setNext(ChainableContextInitRule tail) {
				contextInitRules_ = tail;
			}
		};
	}

}
