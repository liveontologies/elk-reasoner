/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules;
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Used for decomposing class expressions when rules are de-applied.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class BackwardDecompositionRuleApplicationVisitor extends
		BasicDecompositionRuleApplicationVisitor {
	
	private final BasicSaturationStateWriter writer_;

	public BackwardDecompositionRuleApplicationVisitor(BasicSaturationStateWriter writer) {
		writer_ = writer;
	}
	
	@Override
	public void visit(IndexedObjectSomeValuesFrom ice, Context context) {
		if (ice.getFiller().getContext() != null) {
			/*writer_.produce(ice.getFiller().getContext(),
					new BackwardLink(context, ice.getRelation()));*/	
			writer_.produce(ice.getFiller().getContext(),
					writer_.getConclusionFactory().backwardLinkInference(ice, context));
		}
	}

	@Override
	protected BasicSaturationStateWriter getSaturationStateWriter() {
		return writer_;
	}

}
