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

import org.semanticweb.elk.reasoner.saturation.conclusions.classes.ConclusionBaseFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SaturationConclusion;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainExpandedSubObjectPropertyOf;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainTautology;

/**
 * Visits all premises for the given {@link SaturationInference} using the
 * provided {@link SaturationConclusion.Visitor}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SaturationPremiseVisitor<O>
		implements
			SaturationInference.Visitor<O> {

	private final SaturationConclusion.Visitor<O> premiseVisitor_;

	private final SaturationConclusion.Factory factory_ = new ConclusionBaseFactory();

	public SaturationPremiseVisitor(
			SaturationConclusion.Visitor<O> premiseVisitor) {
		this.premiseVisitor_ = premiseVisitor;
	}

	@Override
	public O visit(SubClassInclusionTautology conclusion) {
		return null;
	}

	@Override
	public O visit(SubClassInclusionExpandedSubClassOf conclusion) {
		conclusion.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedObjectIntersectionOf conclusion) {
		conclusion.getFirstPremise(factory_).accept(premiseVisitor_);
		conclusion.getSecondPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionDecomposedFirstConjunct conclusion) {
		conclusion.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionDecomposedSecondConjunct conclusion) {
		conclusion.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedObjectSomeValuesFrom conclusion) {
		conclusion.getFirstPremise(factory_).accept(premiseVisitor_);
		conclusion.getSecondPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(BackwardLinkComposition conclusion) {
		conclusion.getFirstPremise(factory_).accept(premiseVisitor_);
		conclusion.getSecondPremise(factory_).accept(premiseVisitor_);
		conclusion.getThirdPremise(factory_).accept(premiseVisitor_);
		conclusion.getFourthPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(ForwardLinkComposition conclusion) {
		conclusion.getFirstPremise(factory_).accept(premiseVisitor_);
		conclusion.getSecondPremise(factory_).accept(premiseVisitor_);
		conclusion.getThirdPremise(factory_).accept(premiseVisitor_);
		conclusion.getFourthPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(BackwardLinkReversed conclusion) {
		conclusion.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(BackwardLinkReversedExpanded conclusion) {
		conclusion.getFirstPremise(factory_).accept(premiseVisitor_);
		conclusion.getSecondPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(BackwardLinkOfObjectSomeValuesFrom conclusion) {
		conclusion.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(ForwardLinkOfObjectSomeValuesFrom conclusion) {
		conclusion.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(BackwardLinkOfObjectHasSelf conclusion) {
		conclusion.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(ForwardLinkOfObjectHasSelf conclusion) {
		conclusion.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(PropagationGenerated conclusion) {
		conclusion.getFirstPremise(factory_).accept(premiseVisitor_);
		conclusion.getSecondPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(ContradictionOfDisjointSubsumers conclusion) {
		conclusion.getFirstPremise(factory_).accept(premiseVisitor_);
		conclusion.getSecondPremise(factory_).accept(premiseVisitor_);

		return null;
	}

	@Override
	public O visit(ContradictionOfObjectComplementOf conclusion) {
		conclusion.getFirstPremise(factory_).accept(premiseVisitor_);
		conclusion.getSecondPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(ContradictionOfOwlNothing conclusion) {
		conclusion.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(ContradictionPropagated conclusion) {
		conclusion.getFirstPremise(factory_).accept(premiseVisitor_);
		conclusion.getSecondPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(DisjointSubsumerFromSubsumer conclusion) {
		conclusion.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedObjectUnionOf conclusion) {
		conclusion.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedEntity inference) {
		inference.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedDefinedClass inference) {
		inference.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionExpandedDefinition inference) {
		inference.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(SubPropertyChainTautology conclusion) {
		// no premises
		return null;
	}

	@Override
	public O visit(SubPropertyChainExpandedSubObjectPropertyOf inference) {
		inference.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionObjectHasSelfPropertyRange inference) {
		inference.getPremise(factory_).accept(premiseVisitor_);
		// TODO: process the property range premise
		return null;
	}

}
