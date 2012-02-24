/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.saturation.expressions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.expressions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.expressions.NegativeSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.expressions.PositiveSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.expressions.QueueableVisitor;

abstract class InferenceSystem  {
	
	UnaryRule<? super NegativeSuperClassExpression> negSuperClassExpressionRule = null;
	UnaryRule<? super PositiveSuperClassExpression> posSuperClassExpressionRule = null; 
	UnaryRule<BackwardLink> backwardLinkRule = null;
	UnaryRule<ForwardLink> forwardLinkRule = null;

	void add(InferenceRule inferenceRule) {
		for (RegistrableRule componentRule : inferenceRule.getComponentRules())
			componentRule.register(this);
	}
	
	void visit(SuperClassExpressionRule rule) {
		if (negSuperClassExpressionRule == null)
			negSuperClassExpressionRule = rule;
		else
			negSuperClassExpressionRule = new ComposedRule<NegativeSuperClassExpression>(negSuperClassExpressionRule, rule);
		
		if (posSuperClassExpressionRule == null)
			posSuperClassExpressionRule = rule;
		else
			posSuperClassExpressionRule = new ComposedRule<PositiveSuperClassExpression>(posSuperClassExpressionRule, rule);
	}
	
	void visit(PositiveSuperClassExpressionRule rule) {
		if (posSuperClassExpressionRule == null)
			posSuperClassExpressionRule = rule;
		else
			posSuperClassExpressionRule = new ComposedRule<PositiveSuperClassExpression>(posSuperClassExpressionRule, rule);
	}

	void visit(BackwardLinkRule rule) {
		if (backwardLinkRule == null)
			backwardLinkRule = rule;
		else
			backwardLinkRule = new ComposedRule<BackwardLink>(backwardLinkRule, rule);
	}

	void visit(ForwardLinkRule rule) {
		if (forwardLinkRule == null)
			forwardLinkRule = rule;
		else
			forwardLinkRule = new ComposedRule<ForwardLink>(forwardLinkRule, rule);
	}
	
	RuleApplicatorInContext getRuleApplicatorInContext(Context context, RuleApplicationEngine engine) {
		return new RuleApplicatorInContext(context, engine);
	}
	
	class RuleApplicatorInContext implements QueueableVisitor<Void> {

		final Context context;
		final RuleApplicationEngine engine;

		protected RuleApplicatorInContext(Context context, RuleApplicationEngine engine) {
			this.context = context;
			this.engine = engine;
		}

		public Void visit(BackwardLink backwardLink) {
			backwardLinkRule.apply(backwardLink, context, engine);
			return null;
		}

		public Void visit(ForwardLink forwardLink) {
			forwardLinkRule.apply(forwardLink, context, engine);
			return null;
		}

		public Void visit(NegativeSuperClassExpression negSuperClassExpression) {
			negSuperClassExpressionRule.apply(negSuperClassExpression, context, engine);
			return null;
		}

		public Void visit(PositiveSuperClassExpression posSuperClassExpression) {
			posSuperClassExpressionRule.apply(posSuperClassExpression, context, engine);
			return null;
		}
	}
}
