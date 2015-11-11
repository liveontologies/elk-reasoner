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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedDefinitionAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedDefinition;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;
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

	private final ArrayList<IndexedClass> definedClasses_;

	private final ArrayList<ElkAxiom> reasons_;

	private IndexedClassFromDefinitionRule(ChainableSubsumerRule tail) {
		super(tail);
		this.definedClasses_ = new ArrayList<IndexedClass>(1);
		this.reasons_ = new ArrayList<ElkAxiom>(1);
	}

	private IndexedClassFromDefinitionRule(IndexedClass defined, ElkAxiom reason) {
		this((ChainableSubsumerRule) null);
		this.definedClasses_.add(defined);
		this.reasons_.add(reason);
	}

	public static boolean addRuleFor(ModifiableIndexedDefinitionAxiom axiom,
			ModifiableOntologyIndex index, ElkAxiom reason) {
		return index.add(axiom.getDefinition(),
				new IndexedClassFromDefinitionRule(axiom.getDefinedClass(),
						reason));
	}

	public static boolean removeRuleFor(ModifiableIndexedDefinitionAxiom axiom,
			ModifiableOntologyIndex index, ElkAxiom reason) {
		return index.remove(axiom.getDefinition(),
				new IndexedClassFromDefinitionRule(axiom.getDefinedClass(),
						reason));
	}

	@Override
	public String toString() {
		return NAME;
	}

	@Deprecated
	public Collection<IndexedClass> getDefinedClasses() {
		return definedClasses_;
	}

	@Override
	public void apply(IndexedClassExpression premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		for (int i = 0; i < definedClasses_.size(); i++) {
			producer.produce(new ComposedDefinition(premises.getRoot(),
					definedClasses_.get(i), premise, reasons_.get(i)));
		}
	}

	@Override
	public boolean isLocal() {
		return true;
	}

	@Override
	public boolean addTo(Chain<ChainableSubsumerRule> ruleChain) {
		if (isEmpty())
			return true;
		IndexedClassFromDefinitionRule rule = ruleChain.getCreate(
				IndexedClassFromDefinitionRule.MATCHER_,
				IndexedClassFromDefinitionRule.FACTORY_);
		boolean success = true;
		int added = 0;
		for (int i = 0; i < definedClasses_.size(); i++) {
			IndexedClass defined = definedClasses_.get(i);
			ElkAxiom reason = reasons_.get(i);
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("{}: adding to {} reason: {}", defined, NAME,
						reason);
			}
			if (rule.definedClasses_.add(defined)) {
				rule.reasons_.add(reason);
				added++;
			} else {
				success = false;
				break;
			}
		}
		if (success) {
			return true;
		}
		// else revert all changes
		for (int i = 0; i < definedClasses_.size(); i++) {
			if (added == 0)
				break;
			added--;
			IndexedClass defined = definedClasses_.get(i);
			ElkAxiom reason = reasons_.get(i);
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("{}: removing from {} reason: {}", defined, NAME,
						reason);
			}
			int j = rule.indexOf(defined, reason);
			rule.definedClasses_.remove(j);
			rule.reasons_.remove(j);
		}
		return false;
	}

	@Override
	public boolean removeFrom(Chain<ChainableSubsumerRule> ruleChain) {
		if (isEmpty())
			return true;
		IndexedClassFromDefinitionRule rule = ruleChain
				.find(IndexedClassFromDefinitionRule.MATCHER_);
		if (rule == null)
			return false;
		// else
		boolean success = true;
		int removed = 0;
		for (int i = 0; i < definedClasses_.size(); i++) {
			IndexedClass defined = definedClasses_.get(i);
			ElkAxiom reason = reasons_.get(i);
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("{}: removing from {} reason: {}", defined, NAME,
						reason);
			}
			int j = rule.indexOf(defined, reason);
			if (j >= 0) {
				rule.definedClasses_.remove(j);
				rule.reasons_.remove(j);
				removed++;
			} else {
				success = false;
				break;
			}
		}
		if (success) {
			if (rule.isEmpty()) {
				ruleChain.remove(IndexedClassFromDefinitionRule.MATCHER_);
				LOGGER_.trace("{}: removed ", NAME);
			}
			return true;
		}
		// else revert all changes
		for (int i = 0; i < definedClasses_.size(); i++) {
			if (removed == 0)
				break;
			removed--;
			IndexedClass defined = definedClasses_.get(i);
			ElkAxiom reason = reasons_.get(i);
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("{}: adding to {} reason: {} [revert]", defined,
						NAME, reason);
			}
			rule.definedClasses_.add(defined);
			rule.reasons_.add(reason);
		}
		return false;
	}

	private int indexOf(IndexedClass defined, ElkAxiom reason) {
		for (int i = 0; i < definedClasses_.size(); i++) {
			if (definedClasses_.get(i).equals(defined)
					&& reasons_.get(i).equals(reason))
				return i;
		}
		// else not found
		return -1;
	}

	@Override
	public void accept(LinkedSubsumerRuleVisitor<?> visitor,
			IndexedClassExpression premise, ContextPremises premises,
			ClassConclusionProducer producer) {
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

	private static final Matcher<ChainableSubsumerRule, IndexedClassFromDefinitionRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableSubsumerRule, IndexedClassFromDefinitionRule>(
			IndexedClassFromDefinitionRule.class);

	private static final ReferenceFactory<ChainableSubsumerRule, IndexedClassFromDefinitionRule> FACTORY_ = new ReferenceFactory<ChainableSubsumerRule, IndexedClassFromDefinitionRule>() {
		@Override
		public IndexedClassFromDefinitionRule create(ChainableSubsumerRule tail) {
			return new IndexedClassFromDefinitionRule(tail);
		}
	};

}
