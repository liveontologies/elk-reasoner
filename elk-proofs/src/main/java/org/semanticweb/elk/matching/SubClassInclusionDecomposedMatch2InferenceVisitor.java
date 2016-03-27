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

import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch1Watch;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch2;
import org.semanticweb.elk.matching.inferences.BackwardLinkOfObjectHasSelfMatch1;
import org.semanticweb.elk.matching.inferences.BackwardLinkOfObjectSomeValuesFromMatch1;
import org.semanticweb.elk.matching.inferences.ForwardLinkOfObjectHasSelfMatch1;
import org.semanticweb.elk.matching.inferences.ForwardLinkOfObjectSomeValuesFromMatch1;
import org.semanticweb.elk.matching.inferences.InferenceMatch;
import org.semanticweb.elk.matching.inferences.SubClassInclusionDecomposedFirstConjunctMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionDecomposedSecondConjunctMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionObjectHasSelfPropertyRangeMatch1;

class SubClassInclusionDecomposedMatch2InferenceVisitor extends
		AbstractConclusionMatchInferenceVisitor<SubClassInclusionDecomposedMatch2>
		implements SubClassInclusionDecomposedMatch1Watch.Visitor<Void> {

	SubClassInclusionDecomposedMatch2InferenceVisitor(
			InferenceMatch.Factory factory,
			SubClassInclusionDecomposedMatch2 child) {
		super(factory, child);
	}

	@Override
	public Void visit(BackwardLinkOfObjectHasSelfMatch1 inferenceMatch1) {
		factory.getBackwardLinkOfObjectHasSelfMatch2(inferenceMatch1, child);
		return null;
	}

	@Override
	public Void visit(
			BackwardLinkOfObjectSomeValuesFromMatch1 inferenceMatch1) {
		factory.getBackwardLinkOfObjectSomeValuesFromMatch2(inferenceMatch1,
				child);
		return null;
	}

	@Override
	public Void visit(ForwardLinkOfObjectHasSelfMatch1 inferenceMatch1) {
		factory.getForwardLinkOfObjectHasSelfMatch2(inferenceMatch1, child);
		return null;
	}

	@Override
	public Void visit(ForwardLinkOfObjectSomeValuesFromMatch1 inferenceMatch1) {
		factory.getForwardLinkOfObjectSomeValuesFromMatch2(inferenceMatch1,
				child);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionDecomposedFirstConjunctMatch1 inferenceMatch1) {
		factory.getSubClassInclusionDecomposedFirstConjunctMatch2(
				inferenceMatch1, child);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionDecomposedSecondConjunctMatch1 inferenceMatch1) {
		factory.getSubClassInclusionDecomposedSecondConjunctMatch2(
				inferenceMatch1, child);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionObjectHasSelfPropertyRangeMatch1 inferenceMatch1) {
		factory.getSubClassInclusionObjectHasSelfPropertyRangeMatch2(
				inferenceMatch1, child);
		return null;
	}

}