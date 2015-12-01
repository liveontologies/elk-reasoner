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

import org.semanticweb.elk.reasoner.saturation.conclusions.classes.SaturationConclusionBaseFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SaturationConclusion;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainExpandedSubObjectPropertyOf;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainTautology;

/**
 * A utility to pretty-print {@link ClassInference}s.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class InferencePrinter
		implements
			ClassInference.Visitor<String>,
			ObjectPropertyInference.Visitor<String> {

	private static InferencePrinter DEFAULT_PRINTER_ = new InferencePrinter(
			new SaturationConclusionBaseFactory());

	private final SaturationConclusion.Factory factory_;

	public InferencePrinter(SaturationConclusion.Factory factory) {
		this.factory_ = factory;
	}

	public static String print(ClassInference conclusion) {
		return conclusion.accept(DEFAULT_PRINTER_);
	}

	@Override
	public String visit(SubClassInclusionTautology conclusion) {
		return "Root Initialization";
	}

	@Override
	public String visit(SubClassInclusionExpandedSubClassOf conclusion) {
		return "SubClassOf( " + conclusion.getPremise(factory_) + " "
				+ conclusion.getSuperExpression() + " )";
	}

	@Override
	public String visit(SubClassInclusionComposedObjectIntersectionOf conclusion) {
		return "Conjuncting " + conclusion.getFirstPremise(factory_) + " and "
				+ conclusion.getSecondPremise(factory_);

	}

	@Override
	public String visit(SubClassInclusionDecomposedFirstConjunct conclusion) {
		return "Decomposing " + conclusion.getPremise(factory_);

	}

	@Override
	public String visit(SubClassInclusionDecomposedSecondConjunct conclusion) {
		return "Decomposing " + conclusion.getPremise(factory_);

	}

	@Override
	public String visit(SubClassInclusionComposedObjectSomeValuesFrom conclusion) {
		return "Existential inference from "
				+ conclusion.getSecondPremise(factory_) + " and "
				+ conclusion.getFirstPremise(factory_);
	}

	@Override
	public String visit(BackwardLinkComposition conclusion) {
		BackwardLink bwLink = conclusion.getFirstPremise(factory_);
		ForwardLink fwLink = conclusion.getThirdPremise(factory_);
		return "Composed backward link from " + bwLink + " and " + fwLink;
	}

	@Override
	public String visit(ForwardLinkComposition conclusion) {
		BackwardLink bwLink = conclusion.getFirstPremise(factory_);
		ForwardLink fwLink = conclusion.getThirdPremise(factory_);
		return "Composed forward link from " + bwLink + " and " + fwLink;
	}

	@Override
	public String visit(BackwardLinkReversed conclusion) {
		return "Reversing forward link " + conclusion.getPremise(factory_);
	}

	@Override
	public String visit(BackwardLinkReversedExpanded conclusion) {
		return "Reversing forward link " + conclusion.getFirstPremise(factory_)
				+ " and unfolding under "
				+ conclusion.getSecondPremise(factory_);
	}

	@Override
	public String visit(BackwardLinkOfObjectSomeValuesFrom conclusion) {
		return "Creating backward link from " + conclusion.getPremise(factory_);
	}

	@Override
	public String visit(BackwardLinkOfObjectHasSelf conclusion) {
		return "Creating forward link from " + conclusion.getPremise(factory_);
	}

	@Override
	public String visit(ForwardLinkOfObjectSomeValuesFrom conclusion) {
		return "Creating forward link from " + conclusion.getPremise(factory_);
	}

	@Override
	public String visit(ForwardLinkOfObjectHasSelf conclusion) {
		return "Creating forward link from " + conclusion.getPremise(factory_);
	}

	@Override
	public String visit(PropagationGenerated conclusion) {
		return "Creating propagation from "
				+ conclusion.getFirstPremise(factory_);
	}

	@Override
	public String visit(ContradictionOfDisjointSubsumers conclusion) {
		return conclusion.toString();
	}

	@Override
	public String visit(ContradictionOfObjectComplementOf conclusion) {
		return "Contradiction due to derived " + conclusion.getFirstPremise(factory_)
				+ " and " + conclusion.getSecondPremise(factory_);
	}

	@Override
	public String visit(ContradictionOfOwlNothing conclusion) {
		return conclusion.toString();
	}

	@Override
	public String visit(ContradictionPropagated conclusion) {
		return "Contradiction propagated over "
				+ conclusion.getFirstPremise(factory_);
	}

	@Override
	public String visit(DisjointSubsumerFromSubsumer conclusion) {
		return "Disjoint subsumer " + conclusion + " derived from "
				+ conclusion.getPremise(factory_);
	}

	@Override
	public String visit(SubClassInclusionComposedObjectUnionOf conclusion) {
		return "Composed disjunction " + conclusion.getSuperExpression() + " from "
				+ conclusion.getPremise(factory_);
	}

	@Override
	public String visit(SubPropertyChainTautology inference) {
		return "Initialization (" + inference.getChain() + " => "
				+ inference.getSuperChain() + ")";
	}

	@Override
	public String visit(SubPropertyChainExpandedSubObjectPropertyOf inference) {
		return "Told sub-chain: " + inference.getSubChain() + " => "
				+ inference.getSuperChain() + ", premise: "
				+ inference.getPremise(factory_);
	}

	@Override
	public String visit(SubClassInclusionObjectHasSelfPropertyRange inference) {
		return "Property range of " + inference.getPremise(factory_);
	}

	@Override
	public String visit(SubClassInclusionComposedEntity inference) {
		return "Composed decomposition " + inference.getSuperExpression();
	}

	@Override
	public String visit(SubClassInclusionComposedDefinedClass inference) {
		return "Composed definition " + inference.getSuperExpression() + " from "
				+ inference.getPremise(factory_);
	}

	@Override
	public String visit(SubClassInclusionExpandedDefinition inference) {
		return "Decomposed definition " + inference.getSuperExpression() + " of "
				+ inference.getPremise(factory_);
	}

}
