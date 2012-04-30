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
package org.semanticweb.elk.reasoner.saturation.classes;

import org.semanticweb.elk.reasoner.saturation.rulesystem.Context;
import org.semanticweb.elk.reasoner.saturation.rulesystem.IllegalInferenceMethodException;
import org.semanticweb.elk.reasoner.saturation.rulesystem.InferenceRule;
import org.semanticweb.elk.reasoner.saturation.rulesystem.InferenceSystem;
import org.semanticweb.elk.reasoner.saturation.rulesystem.InferenceSystemInvocationManager;
import org.semanticweb.elk.reasoner.saturation.rulesystem.Queueable;
import org.semanticweb.elk.reasoner.saturation.rulesystem.RuleApplicationEngine;

/**
 * An optimized implementation of the InferenceSystemInvocationManager that
 * does not use Java reflection for inference rules with SuperClassExpressions.
 * Instead, all methods applicable to negative and positive SuperClassExpressions
 * are stored in separate fields.
 * 
 * @author Frantisek Simancik
 *
 */
public class InferenceSystemInvocationManagerSCE<C extends ContextElClassSaturation> extends
		InferenceSystemInvocationManager {

	public InferenceSystemInvocationManagerSCE(RuleApplicationEngine engine) {
		super(engine);
	}
	
	protected class RuleMethodListNegSCE {
		InferenceRuleNegSCE<C> firstInferenceRule;
		RuleMethodListNegSCE rest;

		public RuleMethodListNegSCE(InferenceRuleNegSCE<C> firstInferenceRule,
				RuleMethodListNegSCE rest) {
			this.firstInferenceRule = firstInferenceRule;
			this.rest = rest;
		}
		
		public RuleMethodListNegSCE(final InferenceRuleSCE<C> firstInferenceRule,
				RuleMethodListNegSCE rest) {
			this.firstInferenceRule = new InferenceRuleNegSCE<C> () {

				public void applySCE(NegativeSuperClassExpression<C> argument,
						C context, RuleApplicationEngine engine) {
					firstInferenceRule.applySCE(argument, context, engine);
				}
			};
			this.rest = rest;
		}

		public void invoke(NegativeSuperClassExpression<C> argument, C context) {
			firstInferenceRule.applySCE(argument, context, engine);
			if (rest != null) {
				rest.invoke(argument, context);
			}
		}
	}
	
	protected class RuleMethodListPosSCE {
		InferenceRulePosSCE<C> firstInferenceRule;
		RuleMethodListPosSCE rest;

		public RuleMethodListPosSCE(InferenceRulePosSCE<C> firstInferenceRule,
				RuleMethodListPosSCE rest) {
			this.firstInferenceRule = firstInferenceRule;
			this.rest = rest;
		}
		
		public RuleMethodListPosSCE(final InferenceRuleSCE<C> firstInferenceRule,
				RuleMethodListPosSCE rest) {
			this.firstInferenceRule = new InferenceRulePosSCE<C> () {

				public void applySCE(PositiveSuperClassExpression<C> argument,
						C context, RuleApplicationEngine engine) {
					firstInferenceRule.applySCE(argument, context, engine);
				}
			};
			this.rest = rest;
		}

		public void invoke(PositiveSuperClassExpression<C> argument, C context) {
			firstInferenceRule.applySCE(argument, context, engine);
			if (rest != null) {
				rest.invoke(argument, context);
			}
		}
	}
	
	protected RuleMethodListNegSCE rulesNegSCE = null;
	protected RuleMethodListPosSCE rulesPosSCE = null;
	
	protected void addInferenceRuleNegSCE(InferenceRuleNegSCE<C> inferenceRule) {
		rulesNegSCE = new RuleMethodListNegSCE(inferenceRule, rulesNegSCE);
	}
		
	
	protected void addInferenceRulePosSCE(InferenceRulePosSCE<C> inferenceRule) {
		rulesPosSCE = new RuleMethodListPosSCE(inferenceRule, rulesPosSCE);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void addInferenceSystem(InferenceSystem<?> inferenceSystem)
			throws IllegalInferenceMethodException, NoSuchMethodException {
		for (InferenceRule<?> inferenceRule : inferenceSystem
				.getInferenceRules()) {
			addInferenceRule(inferenceRule);
			
			if (inferenceRule instanceof InferenceRuleNegSCE)
				rulesNegSCE = new RuleMethodListNegSCE((InferenceRuleNegSCE<C>) inferenceRule, rulesNegSCE);
				
			if (inferenceRule instanceof InferenceRulePosSCE)
				rulesPosSCE = new RuleMethodListPosSCE((InferenceRulePosSCE<C>) inferenceRule, rulesPosSCE);
			
			if (inferenceRule instanceof InferenceRuleSCE) {
				rulesNegSCE = new RuleMethodListNegSCE((InferenceRuleSCE<C>) inferenceRule, rulesNegSCE);
				rulesPosSCE = new RuleMethodListPosSCE((InferenceRuleSCE<C>) inferenceRule, rulesPosSCE);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void applyAdditionalMethodsToItem(Queueable<?> queueable, Context context) {
		if (rulesNegSCE != null && queueable instanceof NegativeSuperClassExpression)
			rulesNegSCE.invoke((NegativeSuperClassExpression<C>) queueable, (C) context);
		if (rulesPosSCE != null && queueable instanceof PositiveSuperClassExpression)
			rulesPosSCE.invoke((PositiveSuperClassExpression<C>) queueable, (C) context);
	}

}
