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

import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch1;
import org.semanticweb.elk.matching.inferences.InferenceMatch;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkComposition;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkInference;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkOfObjectHasSelf;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkOfObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkReversedExpanded;

class BackwardLinkMatch1InferenceVisitor
		extends
			AbstractConclusionMatchInferenceVisitor<BackwardLinkMatch1>
		implements
			BackwardLinkInference.Visitor<Void> {

	BackwardLinkMatch1InferenceVisitor(InferenceMatch.Factory factory,
			BackwardLinkMatch1 child) {
		super(factory, child);
	}

	@Override
	public Void visit(BackwardLinkComposition inference) {
		factory.getBackwardLinkCompositionMatch1(inference, child);
		return null;
	}

	@Override
	public Void visit(BackwardLinkOfObjectHasSelf inference) {
		factory.getBackwardLinkOfObjectHasSelfMatch1(inference, child);
		return null;
	}

	@Override
	public Void visit(BackwardLinkOfObjectSomeValuesFrom inference) {
		factory.getBackwardLinkOfObjectSomeValuesFromMatch1(inference, child);
		return null;
	}

	@Override
	public Void visit(BackwardLinkReversedExpanded inference) {
		factory.getBackwardLinkReversedExpandedMatch1(inference, child);
		return null;
	}

}
