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
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
class ForwardLinkPremise implements ForwardLink {

	private final Context target_;

	private final IndexedPropertyChain relation_;

	ForwardLinkPremise(Context target, IndexedPropertyChain relation) {
		this.relation_ = relation;
		this.target_ = target;
	}

	@Override
	public IndexedPropertyChain getRelation() {
		return relation_;
	}

	@Override
	public Context getTarget() {
		return target_;
	}

	@Override
	public <R, C> R accept(ConclusionVisitor<R, C> visitor, C parameter) {
		return visitor.visit(this, parameter);
	}

	@Override
	public Context getSourceContext(Context contextWhereStored) {
		return contextWhereStored;
	}

	@Override
	public boolean addToContextBackwardLinkRule(Context context) {
		//no-op
		return false;
	}

	@Override
	public boolean removeFromContextBackwardLinkRule(Context context) {
		//no-op
		return false;
	}

	@Override
	public boolean containsBackwardLinkRule(Context context) {
		//no-op
		return false;
	}

	@Override
	public void apply(BasicSaturationStateWriter writer, Context context) {
		//no-op
	}

	

}
