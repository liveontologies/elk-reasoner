package org.semanticweb.elk.reasoner.saturation.rules.subsumers;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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
import java.util.Collection;
import java.util.List;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDefinitionAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ComposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ChainableSubsumerRule} producing {@link Subsumer} for an
 * {@link IndexedClass} when processing its defined
 * {@link IndexedClassExpression}
 * 
 * @author "Yevgeny Kazakov"
 */
public class IndexedClassFromDefinitionRule extends
		AbstractChainableSubsumerRule {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(IndexedClassFromDefinitionRule.class);

	public static final String NAME = "Defined Class Introduction";

	private final List<IndexedClass> definedClasses_;

	private IndexedClassFromDefinitionRule(ChainableSubsumerRule tail) {
		super(tail);
		this.definedClasses_ = new ArrayList<IndexedClass>(1);
	}

	private IndexedClassFromDefinitionRule(IndexedClass defined) {
		this((ChainableSubsumerRule) null);
		this.definedClasses_.add(defined);
	}

	@Override
	public String getName() {
		return NAME;
	}

	public static void addRulesFor(IndexedDefinitionAxiom axiom,
			ModifiableOntologyIndex index) {
		IndexedClass defined = axiom.getDefinedClass();
		IndexedClassExpression definition = axiom.getDefinition();
		index.add(definition, new IndexedClassFromDefinitionRule(defined));
	}

	public static void removeRulesFor(IndexedDefinitionAxiom axiom,
			ModifiableOntologyIndex index) {
		index.remove(axiom.getDefinedClass(),
				new IndexedClassFromDefinitionRule(axiom.getDefinedClass()));
	}

	// TODO: hide this method
	public Collection<IndexedClass> getDefinedClasses() {
		return definedClasses_;
	}

	@Override
	public void apply(IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		for (IndexedClassExpression defined : definedClasses_) {
			producer.produce(premises.getRoot(),
					new ComposedSubsumerImpl<IndexedClassExpression>(defined));
			// TODO: introduce inference for this rule
		}
	}

	@Override
	public boolean addTo(Chain<ChainableSubsumerRule> ruleChain) {
		IndexedClassFromDefinitionRule rule = ruleChain.getCreate(
				IndexedClassFromDefinitionRule.MATCHER_,
				IndexedClassFromDefinitionRule.FACTORY_);
		boolean changed = false;

		for (IndexedClass defined : definedClasses_) {
			LOGGER_.trace("Adding {} to {}", defined, NAME);

			changed |= rule.addDefinedClass(defined);
		}

		return changed;

	}

	@Override
	public boolean removeFrom(Chain<ChainableSubsumerRule> ruleChain) {
		IndexedClassFromDefinitionRule rule = ruleChain
				.find(IndexedClassFromDefinitionRule.MATCHER_);
		boolean changed = false;

		if (rule != null) {
			for (IndexedClass defined : definedClasses_) {
				LOGGER_.trace("Removing {} from {}", defined, NAME);

				changed |= rule.removeDefinedClass(defined);
			}

			if (rule.isEmpty()) {
				ruleChain.remove(IndexedClassFromDefinitionRule.MATCHER_);

				LOGGER_.trace("{}: removed ", NAME);

				return true;
			}
		}

		return changed;

	}

	@Override
	public void accept(LinkedSubsumerRuleVisitor visitor,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

	protected boolean addDefinedClass(IndexedClass definedClass) {
		return definedClasses_.add(definedClass);
	}

	protected boolean removeDefinedClass(IndexedClass definedClass) {
		return definedClasses_.remove(definedClass);
	}

	/**
	 * @return {@code true} if this rule never does anything
	 */
	private boolean isEmpty() {
		return definedClasses_.isEmpty();
	}

	@Override
	public String toString() {
		return getName() + ": " + definedClasses_;
	}

	private static final Matcher<ChainableSubsumerRule, IndexedClassFromDefinitionRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableSubsumerRule, IndexedClassFromDefinitionRule>(
			IndexedClassFromDefinitionRule.class);

	private static final ReferenceFactory<ChainableSubsumerRule, IndexedClassFromDefinitionRule> FACTORY_ = new ReferenceFactory<ChainableSubsumerRule, IndexedClassFromDefinitionRule>() {
		@Override
		public IndexedClassFromDefinitionRule create(ChainableSubsumerRule tail) {
			return new IndexedClassFromDefinitionRule(tail);
		}
	};

}
