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

import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch2Watch;
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch3;
import org.semanticweb.elk.matching.inferences.BackwardLinkCompositionMatch5;
import org.semanticweb.elk.matching.inferences.BackwardLinkOfObjectHasSelfMatch2;
import org.semanticweb.elk.matching.inferences.BackwardLinkOfObjectSomeValuesFromMatch2;
import org.semanticweb.elk.matching.inferences.BackwardLinkReversedExpandedMatch3;
import org.semanticweb.elk.matching.inferences.InferenceMatch;

class BackwardLinkMatch3InferenceVisitor
		extends AbstractConclusionMatchInferenceVisitor<BackwardLinkMatch3>
		implements BackwardLinkMatch2Watch.Visitor<Void> {

	BackwardLinkMatch3InferenceVisitor(InferenceMatch.Factory factory,
			BackwardLinkMatch3 child) {
		super(factory, child);
	}

	@Override
	public Void visit(BackwardLinkCompositionMatch5 inferenceMatch5) {
		factory.getBackwardLinkCompositionMatch6(inferenceMatch5, child);
		return null;
	}

	@Override
	public Void visit(BackwardLinkOfObjectHasSelfMatch2 inferenceMatch2) {
		factory.getBackwardLinkOfObjectHasSelfMatch3(inferenceMatch2, child);
		return null;
	}

	@Override
	public Void visit(
			BackwardLinkOfObjectSomeValuesFromMatch2 inferenceMatch2) {
		factory.getBackwardLinkOfObjectSomeValuesFromMatch3(inferenceMatch2,
				child);
		return null;
	}

	@Override
	public Void visit(BackwardLinkReversedExpandedMatch3 inferenceMatch3) {
		factory.getBackwardLinkReversedExpandedMatch4(inferenceMatch3, child);
		return null;
	}

}