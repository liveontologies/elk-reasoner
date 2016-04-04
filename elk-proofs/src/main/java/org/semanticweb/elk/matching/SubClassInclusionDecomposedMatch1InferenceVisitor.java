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

import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch1;
import org.semanticweb.elk.matching.inferences.InferenceMatch;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionDecomposedFirstConjunct;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionDecomposedInference;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionDecomposedSecondConjunct;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionExpandedDefinition;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionExpandedSubClassOf;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionObjectHasSelfPropertyRange;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionRange;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionTautology;

class SubClassInclusionDecomposedMatch1InferenceVisitor
		extends
			AbstractConclusionMatchInferenceVisitor<SubClassInclusionDecomposedMatch1>
		implements
			SubClassInclusionDecomposedInference.Visitor<Void> {

	SubClassInclusionDecomposedMatch1InferenceVisitor(
			InferenceMatch.Factory factory,
			SubClassInclusionDecomposedMatch1 child) {
		super(factory, child);
	}

	@Override
	public Void visit(SubClassInclusionDecomposedFirstConjunct inference) {
		factory.getSubClassInclusionDecomposedFirstConjunctMatch1(inference,
				child);
		return null;
	}

	@Override
	public Void visit(SubClassInclusionDecomposedSecondConjunct inference) {
		factory.getSubClassInclusionDecomposedSecondConjunctMatch1(inference,
				child);
		return null;
	}

	@Override
	public Void visit(SubClassInclusionExpandedDefinition inference) {
		factory.getSubClassInclusionExpandedDefinitionMatch1(inference, child);
		return null;
	}

	@Override
	public Void visit(SubClassInclusionExpandedSubClassOf inference) {
		factory.getSubClassInclusionExpandedSubClassOfMatch1(inference, child);
		return null;
	}

	@Override
	public Void visit(SubClassInclusionObjectHasSelfPropertyRange inference) {
		factory.getSubClassInclusionObjectHasSelfPropertyRangeMatch1(inference,
				child);
		return null;
	}

	@Override
	public Void visit(SubClassInclusionRange inference) {
		factory.getSubClassInclusionRangeMatch1(inference, child);
		return null;
	}

	@Override
	public Void visit(SubClassInclusionTautology inference) {
		factory.getSubClassInclusionTautologyMatch1(inference, child);
		return null;
	}

}
