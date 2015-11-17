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
public class SaturationPremiseVisitor<I, O>
		implements
			SaturationInference.Visitor<I, O> {

	private final SaturationConclusion.Visitor<O> premiseVisitor_;

	private final SaturationConclusion.Factory factory_ = new ConclusionBaseFactory();

	public SaturationPremiseVisitor(
			SaturationConclusion.Visitor<O> premiseVisitor) {
		this.premiseVisitor_ = premiseVisitor;
	}

	@Override
	public O visit(SubClassInclusionTautology conclusion, I input) {
		return null;
	}

	@Override
	public O visit(SubClassInclusionExpandedSubClassOf conclusion, I input) {
		conclusion.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedObjectIntersectionOf conclusion,
			I input) {
		conclusion.getFirstPremise(factory_).accept(premiseVisitor_);
		conclusion.getSecondPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionDecomposedFirstConjunct conclusion,
			I input) {
		conclusion.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionDecomposedSecondConjunct conclusion,
			I input) {
		conclusion.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedObjectSomeValuesFrom conclusion,
			I input) {
		conclusion.getFirstPremise(factory_).accept(premiseVisitor_);
		conclusion.getSecondPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(BackwardLinkComposition conclusion, I input) {
		conclusion.getFirstPremise(factory_).accept(premiseVisitor_);
		conclusion.getSecondPremise(factory_).accept(premiseVisitor_);
		conclusion.getThirdPremise(factory_).accept(premiseVisitor_);
		conclusion.getFourthPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(ForwardLinkComposition conclusion, I input) {
		conclusion.getFirstPremise(factory_).accept(premiseVisitor_);
		conclusion.getSecondPremise(factory_).accept(premiseVisitor_);
		conclusion.getThirdPremise(factory_).accept(premiseVisitor_);
		conclusion.getFourthPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(BackwardLinkReversed conclusion, I input) {
		conclusion.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(BackwardLinkReversedExpanded conclusion, I input) {
		conclusion.getFirstPremise(factory_).accept(premiseVisitor_);
		conclusion.getSecondPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(BackwardLinkOfObjectSomeValuesFrom conclusion, I input) {
		conclusion.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(ForwardLinkOfObjectSomeValuesFrom conclusion, I input) {
		conclusion.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(BackwardLinkOfObjectHasSelf conclusion, I input) {
		conclusion.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(ForwardLinkOfObjectHasSelf conclusion, I input) {
		conclusion.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(PropagationGenerated conclusion, I input) {
		conclusion.getFirstPremise(factory_).accept(premiseVisitor_);
		conclusion.getSecondPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(ContradictionOfDisjointSubsumers conclusion, I input) {
		conclusion.getFirstPremise(factory_).accept(premiseVisitor_);
		conclusion.getSecondPremise(factory_).accept(premiseVisitor_);

		return null;
	}

	@Override
	public O visit(ContradictionOfObjectComplementOf conclusion, I input) {
		conclusion.getFirstPremise(factory_).accept(premiseVisitor_);
		conclusion.getSecondPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(ContradictionOfOwlNothing conclusion, I input) {
		conclusion.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(ContradictionPropagated conclusion, I input) {
		conclusion.getFirstPremise(factory_).accept(premiseVisitor_);
		conclusion.getSecondPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(DisjointSubsumerFromSubsumer conclusion, I input) {
		conclusion.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedObjectUnionOf conclusion, I input) {
		conclusion.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedEntity inference, I input) {
		inference.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedDefinedClass inference, I input) {
		inference.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionExpandedDefinition inference, I input) {
		inference.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(SubPropertyChainTautology conclusion, I input) {
		// no premises
		return null;
	}

	@Override
	public O visit(SubPropertyChainExpandedSubObjectPropertyOf inference,
			I input) {
		inference.getPremise(factory_).accept(premiseVisitor_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionObjectHasSelfPropertyRange inference,
			I input) {
		inference.getPremise(factory_).accept(premiseVisitor_);
		// TODO: process the property range premise
		return null;
	}

}
