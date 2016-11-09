package org.semanticweb.elk.matching;

/*-
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.matching.conclusions.PropagationMatch1Watch;
import org.semanticweb.elk.matching.conclusions.PropagationMatch2;
import org.semanticweb.elk.matching.inferences.InferenceMatch.Factory;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedObjectSomeValuesFromMatch2;

class PropagationMatch2InferenceVisitor
		extends AbstractConclusionMatchInferenceVisitor<PropagationMatch2>
		implements PropagationMatch1Watch.Visitor<Void> {

	PropagationMatch2InferenceVisitor(Factory factory,
			PropagationMatch2 child) {
		super(factory, child);
	}

	@Override
	public Void visit(
			SubClassInclusionComposedObjectSomeValuesFromMatch2 inferenceMatch2) {
		factory.getSubClassInclusionComposedObjectSomeValuesFromMatch3(
				inferenceMatch2, child);
		return null;
	}

}
