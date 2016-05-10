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

import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch2Watch;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch3;
import org.semanticweb.elk.matching.inferences.BackwardLinkCompositionMatch4;
import org.semanticweb.elk.matching.inferences.BackwardLinkReversedExpandedMatch2;
import org.semanticweb.elk.matching.inferences.BackwardLinkReversedMatch2;
import org.semanticweb.elk.matching.inferences.ForwardLinkCompositionMatch4;
import org.semanticweb.elk.matching.inferences.InferenceMatch;

class ForwardLinkMatch3InferenceVisitor
		extends AbstractConclusionMatchInferenceVisitor<ForwardLinkMatch3>
		implements ForwardLinkMatch2Watch.Visitor<Void> {

	ForwardLinkMatch3InferenceVisitor(InferenceMatch.Factory factory,
			ForwardLinkMatch3 child) {
		super(factory, child);
	}

	@Override
	public Void visit(BackwardLinkCompositionMatch4 inferenceMatch4) {
		factory.getBackwardLinkCompositionMatch5(inferenceMatch4, child);
		return null;
	}

	@Override
	public Void visit(BackwardLinkReversedExpandedMatch2 inferenceMatch2) {
		factory.getBackwardLinkReversedExpandedMatch3(inferenceMatch2, child);
		return null;
	}

	@Override
	public Void visit(BackwardLinkReversedMatch2 inferenceMatch2) {
		factory.getBackwardLinkReversedMatch3(inferenceMatch2, child);
		return null;
	}

	@Override
	public Void visit(ForwardLinkCompositionMatch4 inferenceMatch4) {
		factory.getForwardLinkCompositionMatch5(inferenceMatch4, child);
		return null;
	}

}
