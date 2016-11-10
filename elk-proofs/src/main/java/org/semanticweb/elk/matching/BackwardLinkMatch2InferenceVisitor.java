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

import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch1Watch;
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch2;
import org.semanticweb.elk.matching.inferences.BackwardLinkCompositionMatch2;
import org.semanticweb.elk.matching.inferences.ClassInconsistencyPropagatedMatch1;
import org.semanticweb.elk.matching.inferences.ForwardLinkCompositionMatch1;
import org.semanticweb.elk.matching.inferences.InferenceMatch;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedObjectSomeValuesFromMatch1;

class BackwardLinkMatch2InferenceVisitor
		extends AbstractConclusionMatchInferenceVisitor<BackwardLinkMatch2>
		implements BackwardLinkMatch1Watch.Visitor<Void> {

	BackwardLinkMatch2InferenceVisitor(InferenceMatch.Factory factory,
			BackwardLinkMatch2 child) {
		super(factory, child);
	}

	@Override
	public Void visit(BackwardLinkCompositionMatch2 inferenceMatch2) {
		factory.getBackwardLinkCompositionMatch3(inferenceMatch2, child);
		return null;
	}

	@Override
	public Void visit(ClassInconsistencyPropagatedMatch1 inferenceMatch1) {
		factory.getClassInconsistencyPropagatedMatch2(inferenceMatch1, child);
		return null;
	}

	@Override
	public Void visit(ForwardLinkCompositionMatch1 inferenceMatch1) {
		factory.getForwardLinkCompositionMatch2(inferenceMatch1, child);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedObjectSomeValuesFromMatch1 inferenceMatch1) {
		factory.getSubClassInclusionComposedObjectSomeValuesFromMatch2(
				inferenceMatch1, child);
		return null;
	}

}