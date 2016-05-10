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

import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch1Watch;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch2;
import org.semanticweb.elk.matching.inferences.BackwardLinkReversedMatch1;
import org.semanticweb.elk.matching.inferences.ForwardLinkCompositionMatch1;
import org.semanticweb.elk.matching.inferences.InferenceMatch;

class ForwardLinkMatch2InferenceVisitor
		extends AbstractConclusionMatchInferenceVisitor<ForwardLinkMatch2>
		implements ForwardLinkMatch1Watch.Visitor<Void> {

	ForwardLinkMatch2InferenceVisitor(InferenceMatch.Factory factory,
			ForwardLinkMatch2 child) {
		super(factory, child);
	}

	@Override
	public Void visit(BackwardLinkReversedMatch1 inferenceMatch1) {
		factory.getBackwardLinkReversedMatch2(inferenceMatch1, child);
		return null;
	}

	@Override
	public Void visit(ForwardLinkCompositionMatch1 inferenceMatch1) {
		factory.getForwardLinkCompositionMatch2(inferenceMatch1, child);
		return null;
	}

}
