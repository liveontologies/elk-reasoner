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
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.ForwardLinkFromBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.LinkedBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.PropagationFromBackwardLinkRule;

/**
 * A {@link Conclusion} representing derived existential restrictions from a
 * source {@link IndexedClassExpression} to this target
 * {@link IndexedClassExpression}. Intuitively, if a subclass axiom
 * {@code SubClassOf(:A ObjectSomeValuesFrom(:r :B))} is derived by inference
 * rules, then a {@link BackwardLink} with the source {@code :A} and the
 * relation {@code :r} can be produced for the target {@code :B}.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class BackwardLink extends AbstractConclusion {

	/**
	 * the source {@link IndexedClassExpression} of this {@link BackwardLink};
	 * the root of the source implies this link.
	 */
	private final IndexedClassExpression source_;

	/**
	 * the {@link IndexedPropertyChain} in the existential restriction
	 * corresponding to this link
	 */
	private final IndexedPropertyChain relation_;

	public BackwardLink(IndexedClassExpression source,
			IndexedPropertyChain relation) {
		this.relation_ = relation;
		this.source_ = source;
	}

	/**
	 * @return the {@link IndexedPropertyChain} that is the relation of this
	 *         {@link BackwardLink}
	 */
	public IndexedPropertyChain getRelation() {
		return relation_;
	}

	/**
	 * @return the source of this {@link BackwardLink}, that is, the
	 *         {@link IndexedClassExpression} from which the existential
	 *         restriction corresponding to this {@link BackwardLink} follows
	 */
	public IndexedClassExpression getSource() {
		return source_;
	}

	@Override
	public void applyNonRedundantRules(RuleVisitor ruleAppVisitor,
			ContextPremises premises, ConclusionProducer producer) {

		ruleAppVisitor.visit(PropagationFromBackwardLinkRule.getInstance(),
				this, premises, producer);
		ruleAppVisitor.visit(ForwardLinkFromBackwardLinkRule.getInstance(),
				this, premises, producer);

		// apply all backward link rules of the context
		LinkedBackwardLinkRule backLinkRule = premises
				.getBackwardLinkRuleHead();
		while (backLinkRule != null) {
			backLinkRule.accept(ruleAppVisitor, this, premises, producer);
			backLinkRule = backLinkRule.next();
		}
	}

	@Override
	public void applyRedundantRules(RuleVisitor ruleAppVisitor,
			ContextPremises premises, ConclusionProducer producer) {
		// no redundant rules for BackwardLink
	}

	@Override
	public void applyNonRedundantLocalRules(RuleVisitor ruleAppVisitor,
			ContextPremises premises, ConclusionProducer producer) {
		if (isLocalFor(premises.getRoot()))
			// generate propagations only if this link is local
			ruleAppVisitor.visit(PropagationFromBackwardLinkRule.getInstance(),
					this, premises, producer);
		ruleAppVisitor.visit(ForwardLinkFromBackwardLinkRule.getInstance(),
				this, premises, producer);

		// apply all backward link rules of the context
		LinkedBackwardLinkRule backLinkRule = premises
				.getBackwardLinkRuleHead();
		while (backLinkRule != null) {
			backLinkRule.accept(ruleAppVisitor, this, premises, producer);
			backLinkRule = backLinkRule.next();
		}
	}

	@Override
	public IndexedClassExpression getSourceRoot(
			IndexedClassExpression rootWhereStored) {
		return source_;
	}

	@Override
	public String toString() {
		return (relation_ + "<-" + source_);
	}

	@Override
	public <I, O> O accept(ConclusionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}
}
