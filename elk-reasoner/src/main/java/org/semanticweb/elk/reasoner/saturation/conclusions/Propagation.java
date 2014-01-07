/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

import java.util.Collection;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.util.collections.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class Propagation extends AbstractConclusion {

	// logger for this class
	static final Logger LOGGER_ = LoggerFactory.getLogger(Propagation.class);

	private final IndexedPropertyChain relation_;

	private final IndexedClassExpression carry_;

	public Propagation(final IndexedPropertyChain relation,
			final IndexedClassExpression carry) {
		relation_ = relation;
		carry_ = carry;
	}

	/**
	 * @return the {@link IndexedPropertyChain} that is the relation over which
	 *         this {@link Propagation} is applied
	 */
	public IndexedPropertyChain getRelation() {
		return this.relation_;
	}

	/**
	 * @return the {@link IndexedClassExpression} that is propagated by this
	 *         {@link Propagation}
	 */
	public IndexedClassExpression getCarry() {
		return this.carry_;
	}

	@Override
	public String toString() {
		return "Propagation " + relation_ + "->" + carry_;
	}

	@Override
	public void applyNonRedundantRules(RuleVisitor ruleAppVisitor,
			Context context, ConclusionProducer producer) {
		// propagate over all backward links
		final Multimap<IndexedPropertyChain, Context> backLinks = context
				.getBackwardLinksByObjectProperty();

		Collection<Context> targets = backLinks.get(relation_);

		for (Context target : targets) {
			producer.produce(target, new ComposedSubsumer(carry_));
		}
	}

	@Override
	public void applyRedundantRules(RuleVisitor ruleAppVisitor,
			Context context, ConclusionProducer producer) {
		// no redundant rules
	}

	@Override
	public <R> R accept(ConclusionVisitor<R> visitor, Context context) {
		return visitor.visit(this, context);
	}

}
