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
package org.semanticweb.elk.reasoner.saturation.classes;

import java.util.Collection;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.rules.Conclusion;
import org.semanticweb.elk.reasoner.indexing.rules.NewContext;
import org.semanticweb.elk.reasoner.indexing.rules.RuleEngine;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * @author Frantisek Simancik
 * 
 */
public class BackwardLink implements Conclusion {

	private final IndexedPropertyChain relation_;

	private final NewContext target_;

	public BackwardLink(IndexedPropertyChain relation, NewContext target) {
		this.relation_ = relation;
		this.target_ = target;
	}

	public IndexedPropertyChain getRelation() {
		return relation_;
	}

	public NewContext getTarget() {
		return target_;
	}

	@Override
	public void applyInContext(NewContext context, RuleEngine ruleEngine) {
		RuleStatistics statistics = ruleEngine.getRuleStatistics();
		statistics.backLinkInfNo++;

		if (!context.addBackwardLinkByObjectProperty(relation_, target_))
			return;

		statistics.backLinkNo++;

		final IndexedPropertyChain linkRelation = getRelation();
		final NewContext target = getTarget();

		// apply all propagations over the link
		final Multimap<IndexedPropertyChain, Conclusion> props = context
				.getPropagationsByObjectProperty();

		if (props != null) {

			Collection<Conclusion> carrys = props.get(linkRelation);

			if (carrys != null)
				for (Conclusion carry : carrys)
					ruleEngine.derive(target, carry);
		}

		// propagate unsatisfiability over the link
		if (!context.isSatisfiable())
			ruleEngine.derive(target, new PositiveSuperClassExpression(
					ruleEngine.getOwlNothing()));

	}

	public String toString() {
		return (relation_ + "<-" + target_.getRoot());
	}

}
