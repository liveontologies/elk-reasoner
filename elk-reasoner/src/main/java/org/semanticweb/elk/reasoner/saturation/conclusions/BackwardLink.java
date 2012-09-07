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

import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.BackwardLinkRules;
import org.semanticweb.elk.reasoner.saturation.rules.RuleEngine;
import org.semanticweb.elk.util.collections.LazySetIntersection;

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

		// apply all backward link rules of the context
		BackwardLinkRules rules = context.getBackwardLinkRules();

		while (rules != null) {
			rules.apply(ruleEngine, this);
			rules = rules.getNext();
		}

		/*
		 * convert backward link to a forward link if it can potentially be
		 * composed
		 */
		Set<IndexedPropertyChain> toldProperties = target_.getRoot()
				.getPosPropertiesInExistentials();
		if (toldProperties != null
				&& !new LazySetIntersection<IndexedPropertyChain>(
						toldProperties, relation_.getSaturated()
								.getLeftComposableProperties()).isEmpty()) {
			ruleEngine.produce(target_, new ForwardLink(relation_, context));
		}

	}

	public String toString() {
		return (relation_ + "<-" + target_.getRoot());
	}

}
