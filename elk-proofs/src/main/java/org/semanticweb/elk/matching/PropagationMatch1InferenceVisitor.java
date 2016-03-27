package org.semanticweb.elk.matching;

/*
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

import org.semanticweb.elk.matching.conclusions.PropagationMatch1;
import org.semanticweb.elk.matching.inferences.InferenceMatch;
import org.semanticweb.elk.reasoner.saturation.inferences.PropagationGenerated;
import org.semanticweb.elk.reasoner.saturation.inferences.PropagationInference;

class PropagationMatch1InferenceVisitor
		extends
			AbstractConclusionMatchInferenceVisitor<PropagationMatch1>
		implements
			PropagationInference.Visitor<Void> {

	PropagationMatch1InferenceVisitor(InferenceMatch.Factory factory,
			PropagationMatch1 child) {
		super(factory, child);
	}

	@Override
	public Void visit(PropagationGenerated inference) {
		factory.getPropagationGeneratedMatch1(inference, child);
		return null;
	}

}
