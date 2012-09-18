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
 * A {@link Conclusion} representing derived existential restrictions from a
 * source {@link Context} to this target {@link Context}. Intuitively, if a
 * subclass axiom {@code SubClassOf(:A ObjectSomeValuesFrom(:r :B))} is derived
 * by inference rules, then a {@link BackwardLink} with the source {@code :A}
 * and the relation {@code :r} can be produced for the target context with root
 * {@code :B}.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class BackwardLink implements Conclusion {

	/**
	 * the source {@link Context} of this {@link BackwardLink}; the root of the
	 * source implies this link.
	 */
	private final Context source_;

	/**
	 * the {@link IndexedPropertyChain} in the existential restriction
	 * corresponding to this link
	 */
	private final IndexedPropertyChain relation_;

	public BackwardLink(Context source, IndexedPropertyChain relation) {
		this.relation_ = relation;
		this.source_ = source;
	}

	/**
	 * @return the source of this {@link BackwardLink}, that is, the
	 *         {@link Context} from which the existential restriction
	 *         corresponding to this {@link BackwardLink} follows
	 */
	public Context getSource() {
		return source_;
	}

	/**
	 * @return the {@link IndexedPropertyChain} of this {@link BackwardLink}
	 *         which is used in the existential restriction corresponding to
	 *         this {@link BackwardLink}
	 * 
	 */
	public IndexedPropertyChain getReltaion() {
		return relation_;
	}

	@Override
	public void apply(RuleEngine ruleEngine, Context context) {

		ConclusionsCounter statistics = ruleEngine.getConclusionsCounter();
		statistics.backLinkInfNo++;

		if (!context.addBackwardLink(this))
			return;

		statistics.backLinkNo++;

		// apply all backward link rules of the context
		BackwardLinkRules rules = context.getBackwardLinkRules();

		while (rules != null) {
			rules.apply(ruleEngine, this);
			rules = rules.next();
		}

		/*
		 * convert backward link to a forward link if it can potentially be
		 * composed
		 */
		Set<IndexedPropertyChain> toldProperties = source_.getRoot()
				.getPosPropertiesInExistentials();
		if (toldProperties != null
				&& !new LazySetIntersection<IndexedPropertyChain>(
						toldProperties, relation_.getSaturated()
								.getLeftComposableProperties()).isEmpty()) {
			ruleEngine.produce(source_, new ForwardLink(relation_, context));
		}

	}

	@Override
	public String toString() {
		return (relation_ + "<-" + source_.getRoot());
	}

}
