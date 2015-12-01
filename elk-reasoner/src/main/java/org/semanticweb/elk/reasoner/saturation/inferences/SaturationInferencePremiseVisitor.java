/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.inferences;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.saturation.conclusions.model.SaturationConclusion;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainExpandedSubObjectPropertyOf;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainTautology;

/**
 * Creates all {@link SaturationConclusion}s of the visited
 * {@link SaturationInference} using the provided
 * {@link SaturationConclusion.Factory}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author Yevgeny Kazakov
 */
public class SaturationInferencePremiseVisitor<O>
		implements
			SaturationInference.Visitor<O> {

	private final SaturationConclusion.Factory conclusionFactory_;

	public SaturationInferencePremiseVisitor(
			SaturationConclusion.Factory conclusionFactory) {
		this.conclusionFactory_ = conclusionFactory;
	}

	@Override
	public O visit(SubClassInclusionTautology conclusion) {
		return null;
	}

	@Override
	public O visit(SubClassInclusionExpandedSubClassOf conclusion) {
		conclusion.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedObjectIntersectionOf conclusion) {
		conclusion.getFirstPremise(conclusionFactory_);
		conclusion.getSecondPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionDecomposedFirstConjunct conclusion) {
		conclusion.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionDecomposedSecondConjunct conclusion) {
		conclusion.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedObjectSomeValuesFrom conclusion) {
		conclusion.getFirstPremise(conclusionFactory_);
		conclusion.getSecondPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(BackwardLinkComposition conclusion) {
		conclusion.getFirstPremise(conclusionFactory_);
		conclusion.getSecondPremise(conclusionFactory_);
		conclusion.getThirdPremise(conclusionFactory_);
		conclusion.getFourthPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(ForwardLinkComposition conclusion) {
		conclusion.getFirstPremise(conclusionFactory_);
		conclusion.getSecondPremise(conclusionFactory_);
		conclusion.getThirdPremise(conclusionFactory_);
		conclusion.getFourthPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(BackwardLinkReversed conclusion) {
		conclusion.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(BackwardLinkReversedExpanded conclusion) {
		conclusion.getFirstPremise(conclusionFactory_);
		conclusion.getSecondPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(BackwardLinkOfObjectSomeValuesFrom conclusion) {
		conclusion.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(ForwardLinkOfObjectSomeValuesFrom conclusion) {
		conclusion.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(BackwardLinkOfObjectHasSelf conclusion) {
		conclusion.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(ForwardLinkOfObjectHasSelf conclusion) {
		conclusion.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(PropagationGenerated conclusion) {
		conclusion.getFirstPremise(conclusionFactory_);
		conclusion.getSecondPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(ContradictionOfDisjointSubsumers conclusion) {
		conclusion.getFirstPremise(conclusionFactory_);
		conclusion.getSecondPremise(conclusionFactory_);

		return null;
	}

	@Override
	public O visit(ContradictionOfObjectComplementOf conclusion) {
		conclusion.getFirstPremise(conclusionFactory_);
		conclusion.getSecondPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(ContradictionOfOwlNothing conclusion) {
		conclusion.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(ContradictionPropagated conclusion) {
		conclusion.getFirstPremise(conclusionFactory_);
		conclusion.getSecondPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(DisjointSubsumerFromSubsumer conclusion) {
		conclusion.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedObjectUnionOf conclusion) {
		conclusion.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedEntity inference) {
		inference.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedDefinedClass inference) {
		inference.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionExpandedDefinition inference) {
		inference.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(SubPropertyChainTautology conclusion) {
		// no premises
		return null;
	}

	@Override
	public O visit(SubPropertyChainExpandedSubObjectPropertyOf inference) {
		inference.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionObjectHasSelfPropertyRange inference) {
		inference.getPremise(conclusionFactory_);
		// TODO: process the property range premise
		return null;
	}

}
