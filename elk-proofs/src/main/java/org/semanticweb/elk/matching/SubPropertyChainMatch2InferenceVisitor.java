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

import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch1Watch;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch2;
import org.semanticweb.elk.matching.inferences.BackwardLinkCompositionMatch2;
import org.semanticweb.elk.matching.inferences.BackwardLinkCompositionMatch4;
import org.semanticweb.elk.matching.inferences.ForwardLinkCompositionMatch1;
import org.semanticweb.elk.matching.inferences.ForwardLinkCompositionMatch3;
import org.semanticweb.elk.matching.inferences.InferenceMatch;
import org.semanticweb.elk.matching.inferences.PropagationGeneratedMatch1;
import org.semanticweb.elk.matching.inferences.PropertyRangeInheritedMatch2;

class SubPropertyChainMatch2InferenceVisitor
		extends AbstractConclusionMatchInferenceVisitor<SubPropertyChainMatch2>
		implements SubPropertyChainMatch1Watch.Visitor<Void> {

	SubPropertyChainMatch2InferenceVisitor(InferenceMatch.Factory factory,
			SubPropertyChainMatch2 child) {
		super(factory, child);
	}

	@Override
	public Void visit(BackwardLinkCompositionMatch2 inferenceMatch2) {
		factory.getBackwardLinkCompositionMatch3(inferenceMatch2, child);
		return null;
	}

	@Override
	public Void visit(BackwardLinkCompositionMatch4 inferenceMatch4) {
		factory.getBackwardLinkCompositionMatch5(inferenceMatch4, child);
		return null;
	}

	@Override
	public Void visit(ForwardLinkCompositionMatch1 inferenceMatch1) {
		factory.getForwardLinkCompositionMatch2(inferenceMatch1, child);
		return null;
	}

	@Override
	public Void visit(ForwardLinkCompositionMatch3 inferenceMatch3) {
		factory.getForwardLinkCompositionMatch4(inferenceMatch3, child);
		return null;
	}

	@Override
	public Void visit(PropagationGeneratedMatch1 inferenceMatch1) {
		factory.getPropagationGeneratedMatch2(inferenceMatch1, child);
		return null;
	}

	@Override
	public Void visit(PropertyRangeInheritedMatch2 inferenceMatch2) {
		factory.getPropertyRangeInheritedMatch3(inferenceMatch2, child);
		return null;
	}

}