/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.util;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
class BackwardLinkPremise implements BackwardLink {

	private final Context source_;

	private final IndexedPropertyChain relation_;

	BackwardLinkPremise(Context source, IndexedPropertyChain relation) {
		this.relation_ = relation;
		this.source_ = source;
	}

	@Override
	public IndexedPropertyChain getRelation() {
		return relation_;
	}

	@Override
	public Context getSource() {
		return source_;
	}

	@Override
	public void apply(BasicSaturationStateWriter writer, Context context,
			CompositionRuleApplicationVisitor ruleAppVisitor) {
		//no-op
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
	public <R, C> R accept(ConclusionVisitor<R, C> visitor, C context) {
		return visitor.visit(this, context);
	}

	@Override
	public void applyLocally(BasicSaturationStateWriter writer,
			Context context, CompositionRuleApplicationVisitor ruleAppVisitor) {
		//no-op
	}

}
