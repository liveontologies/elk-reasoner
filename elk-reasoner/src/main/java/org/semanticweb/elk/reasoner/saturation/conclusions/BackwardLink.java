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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.rules.BackwardLinkRules;
import org.semanticweb.elk.reasoner.indexing.rules.RuleEngine;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class BackwardLink implements Conclusion {

	private final IndexedPropertyChain relation_;

	private final Context target_;

	public BackwardLink(IndexedPropertyChain relation, Context target) {
		this.relation_ = relation;
		this.target_ = target;
	}

	public IndexedPropertyChain getReltaion() {
		return relation_;
	}

	public Context getTarget() {
		return target_;
	}

	@Override
	public void process(Context context, RuleEngine ruleEngine) {

		RuleStatistics statistics = ruleEngine.getRuleStatistics();
		statistics.backLinkInfNo++;

		if (!context.addBackwardLinkByObjectProperty(relation_, target_))
			return;

		statistics.backLinkNo++;

//		final IndexedPropertyChain linkRelation = relation_;
		final Context target = target_;

		// apply all backward link rules of the context
		BackwardLinkRules rules = context.getBackwardLinkRules().getNext();

		while (rules != null) {
			rules.apply(ruleEngine, context, this);
			rules = rules.getNext();
		}

//		for (Conclusion carry : context.getPropagationsByObjectProperty().get(
//				linkRelation))
//			ruleEngine.derive(target, carry);

		// propagate unsatisfiability over the link
		if (!context.isSatisfiable())
			ruleEngine.derive(target, new PositiveSuperClassExpression(
					ruleEngine.getOwlNothing()));

	}

	public String toString() {
		return (relation_ + "<-" + target_.getRoot());
	}

}
