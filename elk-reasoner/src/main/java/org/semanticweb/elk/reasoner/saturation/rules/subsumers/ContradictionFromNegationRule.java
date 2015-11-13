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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionOfObjectComplementOf;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;

/**
 * A {@link ChainableSubsumerRule} producing {@link Contradiction} when
 * processing an {@link IndexedClassExpression} that is the negation of
 * {@link IndexedObjectComplementOf} that is contained in the {@code Context} .
 * 
 * @see IndexedObjectComplementOf#getNegated()
 * @see IndexedObjectIntersectionOfDecomposition
 * 
 * @author "Yevgeny Kazakov"
 */
public class ContradictionFromNegationRule extends
		AbstractChainableSubsumerRule {

	public static final String NAME = "ObjectComplementOf Clash";

	private IndexedObjectComplementOf negation_;

	private ContradictionFromNegationRule(ChainableSubsumerRule tail) {
		super(tail);
	}

	private ContradictionFromNegationRule(IndexedObjectComplementOf negation) {
		this((ChainableSubsumerRule) null);
		this.negation_ = negation;
	}

	public static boolean addRulesFor(
			ModifiableIndexedObjectComplementOf negation,
			ModifiableOntologyIndex index) {
		return index.add(negation.getNegated(),
				new ContradictionFromNegationRule(negation));
	}

	public static boolean removeRulesFor(
			ModifiableIndexedObjectComplementOf negation,
			ModifiableOntologyIndex index) {
		return index.remove(negation.getNegated(),
				new ContradictionFromNegationRule(negation));
	}

	@Override
	public String toString() {
		return NAME;
	}

	@Deprecated
	public IndexedClassExpression getNegation() {
		return negation_;
	}

	@Override
	public void apply(IndexedClassExpression premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		if (negation_ != null
				&& premises.getDecomposedSubsumers().contains(negation_)) {
			producer.produce(new ContradictionOfObjectComplementOf(premises.getRoot(),
					negation_));
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
		ContradictionFromNegationRule rule = ruleChain.getCreate(MATCHER_,
				FACTORY_);
		if (rule.negation_ != null && rule.negation_ != negation_) {
			return false;
		}
		// else
		rule.negation_ = negation_;
		return true;
	}

	@Override
	public boolean removeFrom(Chain<ChainableSubsumerRule> ruleChain) {
		if (isEmpty())
			return true;
		ContradictionFromNegationRule rule = ruleChain.find(MATCHER_);
		if (rule == null) {
			return false;
		}
		// else
		if (rule.negation_ != negation_) {
			return false;
		}
		// else
		rule.negation_ = null;
		if (rule.isEmpty()) {
			ruleChain.remove(MATCHER_);
		}
		return true;
	}

	@Override
	public void accept(LinkedSubsumerRuleVisitor<?> visitor,
			IndexedClassExpression premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

	/**
	 * @return {@code true} if this rule never does anything
	 */
	private boolean isEmpty() {
		return negation_ == null;
	}

	private static final Matcher<ChainableSubsumerRule, ContradictionFromNegationRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableSubsumerRule, ContradictionFromNegationRule>(
			ContradictionFromNegationRule.class);

	private static final ReferenceFactory<ChainableSubsumerRule, ContradictionFromNegationRule> FACTORY_ = new ReferenceFactory<ChainableSubsumerRule, ContradictionFromNegationRule>() {
		@Override
		public ContradictionFromNegationRule create(ChainableSubsumerRule tail) {
			return new ContradictionFromNegationRule(tail);
		}
	};

}
