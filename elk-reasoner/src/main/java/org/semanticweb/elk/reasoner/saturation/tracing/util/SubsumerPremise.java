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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.DecompositionRuleApplicationVisitor;

/**
 * Used only to create temporary subsumers when returning indexed class expressions as premises of inferences. 
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
class SubsumerPremise implements PositiveSubsumer {

	private final IndexedClassExpression ice_;
	
	/**
	 * 
	 */
	public SubsumerPremise(IndexedClassExpression ice) {
		ice_ = ice;
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
	public IndexedClassExpression getExpression() {
		return ice_;
	}

	@Override
	public void apply(BasicSaturationStateWriter writer, Context context,
			CompositionRuleApplicationVisitor ruleAppVisitor,
			DecompositionRuleApplicationVisitor decompVisitor) {
		//no-op
	}
	
	@Override
	public String toString() {
		return ice_.toString();
	}

}
