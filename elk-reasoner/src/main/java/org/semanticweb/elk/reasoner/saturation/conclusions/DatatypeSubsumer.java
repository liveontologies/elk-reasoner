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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDatatypeExpression;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationVisitor;

/**
 * A special subsumer implementation for datatypes since they may require
 * special handling, e.g., storing separately from other subsumers.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class DatatypeSubsumer extends Subsumer {

	public DatatypeSubsumer(IndexedDatatypeExpression expression) {
		super(expression);
	}
	
	@Override
	public IndexedDatatypeExpression getExpression() {
		return (IndexedDatatypeExpression) super.getExpression();
	}
	
	public void apply(BasicSaturationStateWriter writer, Context context,
			RuleApplicationVisitor ruleAppVisitor) {
		applyCompositionRules(writer, context, ruleAppVisitor);
	}

	@Override
	public <R> R accept(ConclusionVisitor<R> visitor, Context context) {
		return visitor.visit(this, context);
	}

	@Override
	public String toString() {
		return expression.toString() + " (for IDE handling)";
	}

	
}
