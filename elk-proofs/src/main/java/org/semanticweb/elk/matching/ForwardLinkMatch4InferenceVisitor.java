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

import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch3Watch;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch4;
import org.semanticweb.elk.matching.inferences.BackwardLinkCompositionMatch7;
import org.semanticweb.elk.matching.inferences.BackwardLinkReversedExpandedMatch4;
import org.semanticweb.elk.matching.inferences.ForwardLinkCompositionMatch6;
import org.semanticweb.elk.matching.inferences.InferenceMatch;

class ForwardLinkMatch4InferenceVisitor
		extends AbstractConclusionMatchInferenceVisitor<ForwardLinkMatch4>
		implements ForwardLinkMatch3Watch.Visitor<Void> {

	ForwardLinkMatch4InferenceVisitor(InferenceMatch.Factory factory,
			ForwardLinkMatch4 child) {
		super(factory, child);
	}

	@Override
	public Void visit(BackwardLinkCompositionMatch7 inferenceMatch7) {
		factory.getBackwardLinkCompositionMatch8(inferenceMatch7, child);
		return null;
	}

	@Override
	public Void visit(BackwardLinkReversedExpandedMatch4 inferenceMatch4) {
		factory.getBackwardLinkReversedExpandedMatch5(inferenceMatch4, child);
		return null;
	}

	@Override
	public Void visit(ForwardLinkCompositionMatch6 inferenceMatch6) {
		factory.getForwardLinkCompositionMatch7(inferenceMatch6, child);
		return null;
	}

}
