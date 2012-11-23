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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.LinkRule;
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

	public IndexedPropertyChain getRelation() {
		return relation_;
	}

	/**
	 * @return the source of this {@link BackwardLink}, that is, the
	 *         {@link Context} from which the existential restriction
	 *         corresponding to this {@link BackwardLink} follows
	 */
	public Context getSource() {
		return source_;
	}

	@Override
	public void apply(SaturationState.Writer engine, Context context) {

		// ConclusionsCounter statistics = ruleEngine.getConclusionsCounter();
		// statistics.backLinkTime -= CachedTimeThread.currentTimeMillis;
		try {

			// if this is the first/last backward link for this relation,
			// generate new propagations for this relation
			if (context.getBackwardLinksByObjectProperty().get(relation_)
					.size() == 1) {
				IndexedObjectSomeValuesFrom.generatePropagations(engine,
						relation_, context);
			}

			// apply all backward link rules of the context
			LinkRule<BackwardLink> backLinkRule = context
					.getBackwardLinkRuleHead();
			while (backLinkRule != null) {
				backLinkRule.apply(engine, this);
				backLinkRule = backLinkRule.next();
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
				// if
				// (!relation_.getSaturated().getLeftComposableProperties().isEmpty())
				// {
				engine.produce(source_, new ForwardLink(relation_, context));
			}
		} finally {
			// statistics.backLinkTime += CachedTimeThread.currentTimeMillis;
		}
	}

	@Override
	public void deapply(SaturationState.Writer engine, Context context) {
		apply(engine, context);
	}

	@Override
	public String toString() {
		return (relation_ + "<-" + source_.getRoot());
	}

	@Override
	public <R> R accept(ConclusionVisitor<R> visitor, Context context) {
		return visitor.visit(this, context);
	}
}