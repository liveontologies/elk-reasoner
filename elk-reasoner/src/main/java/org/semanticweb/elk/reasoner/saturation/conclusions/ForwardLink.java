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
package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.NonReflexiveBackwardLinkCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.ReflexiveBackwardLinkCompositionRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link Conclusion} representing derived existential restrictions from this
 * source {@link IndexedClassExpression} to a target
 * {@link IndexedClassExpression}. Intuitively, if a subclass axiom
 * {@code SubClassOf(:A ObjectSomeValuesFrom(:r :B))} is derived by inference
 * rules, then a {@link ForwardLink} with the relation {@code :r} and the target
 * {@code :B} can be produced for {@code :A}.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class ForwardLink extends AbstractConclusion {

	static final Logger LOGGER_ = LoggerFactory.getLogger(ForwardLink.class);

	/**
	 * the {@link IndexedPropertyChain} in the existential restriction
	 * corresponding to this {@link ForwardLink}
	 */
	final IndexedPropertyChain relation_;

	/**
	 * the {@link IndexedClassExpression}, which root is the filler of the
	 * existential restriction corresponding to this {@link ForwardLink}
	 */
	final IndexedClassExpression target_;

	public ForwardLink(IndexedPropertyChain relation,
			IndexedClassExpression target) {
		this.relation_ = relation;
		this.target_ = target;
	}

	public IndexedPropertyChain getRelation() {
		return relation_;
	}

	public IndexedClassExpression getTarget() {
		return target_;
	}

	@Override
	public void applyNonRedundantRules(RuleVisitor ruleAppVisitor,
			ContextPremises premises, ConclusionProducer producer) {
		// compose with all backward links
		ruleAppVisitor.visit(
				NonReflexiveBackwardLinkCompositionRule.getRuleFor(this), this,
				premises, producer);
		ruleAppVisitor.visit(
				ReflexiveBackwardLinkCompositionRule.getRuleFor(this), this,
				premises, producer);
	}

	@Override
	public void applyRedundantRules(RuleVisitor ruleAppVisitor,
			ContextPremises premises, ConclusionProducer producer) {
		// no redundant rules
	}

	@Override
	public void applyNonRedundantLocalRules(RuleVisitor ruleAppVisitor,
			ContextPremises premises, ConclusionProducer producer) {
		// compose only with reflexive backward links
		ruleAppVisitor.visit(
				ReflexiveBackwardLinkCompositionRule.getRuleFor(this), this,
				premises, producer);
	}

	@Override
	public <I, O> O accept(ConclusionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public String toString() {
		return relation_ + "->" + target_;
	}
}
