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

import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch3Watch;
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch4;
import org.semanticweb.elk.matching.inferences.BackwardLinkCompositionMatch8;
import org.semanticweb.elk.matching.inferences.ClassInconsistencyPropagatedMatch3;
import org.semanticweb.elk.matching.inferences.ForwardLinkCompositionMatch7;
import org.semanticweb.elk.matching.inferences.InferenceMatch;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedObjectSomeValuesFromMatch3;

class BackwardLinkMatch4InferenceVisitor
		extends AbstractConclusionMatchInferenceVisitor<BackwardLinkMatch4>
		implements BackwardLinkMatch3Watch.Visitor<Void> {

	BackwardLinkMatch4InferenceVisitor(InferenceMatch.Factory factory,
			BackwardLinkMatch4 child) {
		super(factory, child);
	}

	@Override
	public Void visit(BackwardLinkCompositionMatch8 inferenceMatch8) {
		factory.getBackwardLinkCompositionMatch9(inferenceMatch8, child);
		return null;
	}

	@Override
	public Void visit(ClassInconsistencyPropagatedMatch3 inferenceMatch3) {
		factory.getClassInconsistencyPropagatedMatch4(inferenceMatch3, child);
		return null;
	}

	@Override
	public Void visit(ForwardLinkCompositionMatch7 inferenceMatch7) {
		factory.getForwardLinkCompositionMatch8(inferenceMatch7, child);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedObjectSomeValuesFromMatch3 inferenceMatch3) {
		factory.getSubClassInclusionComposedObjectSomeValuesFromMatch4(
				inferenceMatch3, child);
		return null;
	}

}