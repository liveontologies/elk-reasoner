package org.semanticweb.elk.reasoner.saturation.inferences;

/*
 * #%L
 * ELK Reasoner
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

import org.semanticweb.elk.reasoner.saturation.conclusions.classes.SaturationConclusionBaseFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;

public class ClassInferenceConclusionVisitor
		implements
			ClassInference.Visitor<Boolean> {

	private final ClassConclusion.Factory conclusionFactory_;

	private final ClassConclusion.Visitor<Boolean> conclusionVisitor_;

	public ClassInferenceConclusionVisitor(
			ClassConclusion.Factory conclusionFactory,
			ClassConclusion.Visitor<Boolean> conclusionVisitor) {
		this.conclusionFactory_ = conclusionFactory;
		this.conclusionVisitor_ = conclusionVisitor;
	}

	public ClassInferenceConclusionVisitor(
			ClassConclusion.Visitor<Boolean> conclusionVisitor) {
		this(new SaturationConclusionBaseFactory(), conclusionVisitor);
	}

	@Override
	public Boolean visit(ContradictionOfOwlNothing inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(ContradictionOfDisjointSubsumers inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(ContradictionOfObjectComplementOf inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(ContradictionPropagated inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(DisjointSubsumerFromSubsumer inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(ForwardLinkComposition inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(ForwardLinkOfObjectHasSelf inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(ForwardLinkOfObjectSomeValuesFrom inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(ContextInitializationNoPremises inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(SubContextInitializationNoPremises inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(BackwardLinkComposition inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(BackwardLinkOfObjectHasSelf inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(BackwardLinkOfObjectSomeValuesFrom inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(BackwardLinkReversed inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(BackwardLinkReversedExpanded inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(PropagationGenerated inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(SubClassInclusionComposedDefinedClass inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(SubClassInclusionComposedEntity inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(
			SubClassInclusionComposedObjectIntersectionOf inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(
			SubClassInclusionComposedObjectSomeValuesFrom inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(SubClassInclusionComposedObjectUnionOf inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(SubClassInclusionDecomposedFirstConjunct inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(SubClassInclusionDecomposedSecondConjunct inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(SubClassInclusionExpandedDefinition inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(SubClassInclusionExpandedSubClassOf inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(
			SubClassInclusionObjectHasSelfPropertyRange inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(SubClassInclusionOwlThing inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(SubClassInclusionRange inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public Boolean visit(SubClassInclusionTautology inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

}
