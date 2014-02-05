package org.semanticweb.elk.reasoner.saturation.rules.contradiction;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.util.Collection;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.util.collections.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ContradictionRule} applied when processing {@link Contradiction}
 * producing {@link Contradiction} in all contexts linked by
 * {@link BackwardLink}s in a {@code Context}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ContradictionPropagationRule extends AbstractContradictionRule {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ContradictionPropagationRule.class);

	private static final ContradictionPropagationRule INSTANCE_ = new ContradictionPropagationRule();

	private static final String NAME_ = "Contradiction Propagation over Backward Links";

	private ContradictionPropagationRule() {
		// do not allow creation of instances outside of this class
	}

	public static ContradictionPropagationRule getInstance() {
		return INSTANCE_;
	}

	@Override
	public String getName() {
		return NAME_;
	}

	@Override
	public void apply(Contradiction premise, ContextPremises premises,
			ConclusionProducer producer) {
		final Multimap<IndexedPropertyChain, IndexedClassExpression> backLinks = premises
				.getBackwardLinksByObjectProperty();
		for (IndexedPropertyChain propRelation : backLinks.keySet()) {

			Collection<IndexedClassExpression> targets = backLinks
					.get(propRelation);

			for (IndexedClassExpression target : targets) {
				producer.produce(target, premise);
			}
		}
	}

	@Override
	public void accept(ContradictionRuleVisitor visitor, Contradiction premise,
			ContextPremises premises, ConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

}