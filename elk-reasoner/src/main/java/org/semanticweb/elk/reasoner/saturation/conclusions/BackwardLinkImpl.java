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
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.LinkRule;

/**
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class BackwardLinkImpl implements BackwardLink {

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

	BackwardLinkImpl(Context source, IndexedPropertyChain relation) {
		this.relation_ = relation;
		this.source_ = source;
	}

	@Override
	public IndexedPropertyChain getRelation() {
		return relation_;
	}

	/**
	 * @return the source of this {@link BackwardLink}, that is, the
	 *         {@link Context} from which the existential restriction
	 *         corresponding to this {@link BackwardLink} follows
	 */
	@Override
	public Context getSource() {
		return source_;
	}

	@Override
	public void apply(BasicSaturationStateWriter writer, Context context,
			CompositionRuleApplicationVisitor ruleAppVisitor) {

		// if this is the first/last backward link for this relation,
		// generate new propagations for this relation
		if (context.getBackwardLinksByObjectProperty().get(relation_).size() == 1) {
			IndexedObjectSomeValuesFrom.generatePropagations(writer, relation_, this, 
					context);
		}

		// apply all backward link rules of the context
		LinkRule<BackwardLink, Context> backLinkRule = context.getBackwardLinkRuleHead();
		
		while (backLinkRule != null) {
			backLinkRule.accept(ruleAppVisitor, writer, this, context);
			backLinkRule = backLinkRule.next();
		}

		/*
		 * convert backward link to a forward link if it can potentially be
		 * composed
		 */
		if (!relation_.getSaturated().getCompositionsByLeftSubProperty()
				.isEmpty()) {
			writer.produce(source_, writer.getConclusionFactory().createForwardLink(this, context));
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
}
