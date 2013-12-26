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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.LinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.Rule;

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

	private final ThisCompositionRule thisCompositionRule_ = new ThisCompositionRule();

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
	public void accept(CompositionRuleVisitor ruleAppVisitor,
			SaturationStateWriter writer, Context context) {

		ruleAppVisitor.visit(thisCompositionRule_, this, context, writer);

		// apply all backward link rules of the context
		LinkRule<BackwardLink> backLinkRule = context.getBackwardLinkRuleHead();
		while (backLinkRule != null) {
			backLinkRule.accept(ruleAppVisitor, this, context, writer);
			backLinkRule = backLinkRule.next();
		}
	}

	@Override
	public Context getSourceContext(Context contextWhereStored) {
		return source_;
	}

	@Override
	public String toString() {
		return (relation_ + "<-" + source_);
	}

	@Override
	public <R> R accept(ConclusionVisitor<R> visitor, Context context) {
		return visitor.visit(this, context);
	}

	/**
	 * The composition rule applied when processing when processing this
	 * {@link BackwardLink} producing new {@link Propagation}s that can be
	 * propagated over this {@link BackwardLink} and the corresponding
	 * {@link ForwardLink} if it can be used with property chain axioms.
	 * 
	 * @author "Yevgeny Kazakov"
	 */
	public static class ThisCompositionRule implements Rule<BackwardLink> {

		private static final String NAME_ = "BackwardLink Registration";

		@Override
		public String getName() {
			return NAME_;
		}

		@Override
		public void apply(BackwardLink premise, Context context,
				SaturationStateWriter writer) {
			IndexedPropertyChain premiseRelation = premise.getRelation();
			// if this is the first/last backward link for this relation,
			// generate new propagations for this relation
			if (context.getBackwardLinksByObjectProperty().get(premiseRelation)
					.size() == 1) {
				IndexedObjectSomeValuesFrom.generatePropagations(
						premiseRelation, context, writer);
			}

			/*
			 * convert backward link to a forward link if it can potentially be
			 * composed
			 */
			if (!premiseRelation.getSaturated()
					.getCompositionsByLeftSubProperty().isEmpty()) {
				writer.produce(premise.getSource(), new ForwardLink(
						premiseRelation, context));
			}

		}

	}
}
