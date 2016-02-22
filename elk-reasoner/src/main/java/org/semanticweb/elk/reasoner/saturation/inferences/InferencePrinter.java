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

import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.PropertyRangeInherited;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainExpandedSubObjectPropertyOf;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainTautology;
import org.semanticweb.elk.reasoner.tracing.Conclusion;
import org.semanticweb.elk.reasoner.tracing.ConclusionBaseFactory;

/**
 * TODO: improve and extend to all inferences
 * 
 * A utility to pretty-print {@link ClassInference}s.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class InferencePrinter implements ClassInference.Visitor<String>,
		ObjectPropertyInference.Visitor<String> {

	private static InferencePrinter DEFAULT_PRINTER_ = new InferencePrinter(
			new ConclusionBaseFactory());

	private final Conclusion.Factory conclusionFactory_;

	InferencePrinter(Conclusion.Factory conclusionFactory) {
		this.conclusionFactory_ = conclusionFactory;
	}

	public static String print(ClassInference inference) {
		return inference.accept(DEFAULT_PRINTER_);
	}

	@Override
	public String visit(BackwardLinkComposition inference) {
		BackwardLink bwLink = inference.getFirstPremise(conclusionFactory_);
		ForwardLink fwLink = inference.getThirdPremise(conclusionFactory_);
		return "Composed backward link from " + bwLink + " and " + fwLink;
	}

	@Override
	public String visit(BackwardLinkOfObjectHasSelf inference) {
		return "Creating forward link from "
				+ inference.getPremise(conclusionFactory_);
	}

	@Override
	public String visit(BackwardLinkOfObjectSomeValuesFrom inference) {
		return "Creating backward link from "
				+ inference.getPremise(conclusionFactory_);
	}

	@Override
	public String visit(BackwardLinkReversed inference) {
		return "Reversing forward link "
				+ inference.getPremise(conclusionFactory_);
	}

	@Override
	public String visit(BackwardLinkReversedExpanded inference) {
		return "Reversing forward link "
				+ inference.getFirstPremise(conclusionFactory_)
				+ " and unfolding under "
				+ inference.getSecondPremise(conclusionFactory_);
	}

	@Override
	public String visit(ContextInitializationNoPremises inference) {
		return "Init (" + inference.getDestination() + ")";
	}

	@Override
	public String visit(ClassInconsistencyOfDisjointSubsumers inference) {
		return inference.toString();
	}

	@Override
	public String visit(ClassInconsistencyOfObjectComplementOf inference) {
		return "Contradiction due to derived "
				+ inference.getFirstPremise(conclusionFactory_) + " and "
				+ inference.getSecondPremise(conclusionFactory_);
	}

	@Override
	public String visit(ClassInconsistencyOfOwlNothing inference) {
		return inference.toString();
	}

	@Override
	public String visit(ClassInconsistencyPropagated inference) {
		return "Contradiction propagated over "
				+ inference.getFirstPremise(conclusionFactory_);
	}

	@Override
	public String visit(DisjointSubsumerFromSubsumer inference) {
		return "Disjoint subsumer " + inference + " derived from "
				+ inference.getFirstPremise(conclusionFactory_);
	}

	@Override
	public String visit(ForwardLinkComposition inference) {
		BackwardLink bwLink = inference.getFirstPremise(conclusionFactory_);
		ForwardLink fwLink = inference.getThirdPremise(conclusionFactory_);
		return "Composed forward link from " + bwLink + " and " + fwLink;
	}

	@Override
	public String visit(ForwardLinkOfObjectHasSelf inference) {
		return "Creating forward link from "
				+ inference.getPremise(conclusionFactory_);
	}

	@Override
	public String visit(ForwardLinkOfObjectSomeValuesFrom inference) {
		return "Creating forward link from "
				+ inference.getPremise(conclusionFactory_);
	}

	@Override
	public String visit(PropagationGenerated inference) {
		return "Creating propagation from "
				+ inference.getSecondPremise(conclusionFactory_);
	}

	@Override
	public String visit(PropertyRangeInherited inference) {
		return "Property Range Inherited (" + inference.getProperty() + " : "
				+ inference.getRange() + ", premise: "
				+ inference.getFirstPremise(conclusionFactory_) + ", reason: "
				+ inference.getReason();
	}

	@Override
	public String visit(SubClassInclusionComposedDefinedClass inference) {
		return "Composed definition " + inference.getSubsumer() + " from "
				+ inference.getFirstPremise(conclusionFactory_);
	}

	@Override
	public String visit(SubClassInclusionComposedEntity inference) {
		return "Composed decomposition " + inference.getSubsumer();
	}

	@Override
	public String visit(
			SubClassInclusionComposedObjectIntersectionOf inference) {
		return "Conjuncting " + inference.getFirstPremise(conclusionFactory_)
				+ " and " + inference.getSecondPremise(conclusionFactory_);

	}

	@Override
	public String visit(
			SubClassInclusionComposedObjectSomeValuesFrom inference) {
		return "Existential inference from "
				+ inference.getSecondPremise(conclusionFactory_) + " and "
				+ inference.getFirstPremise(conclusionFactory_);
	}

	@Override
	public String visit(SubClassInclusionComposedObjectUnionOf inference) {
		return "Composed disjunction " + inference.getSubsumer() + " from "
				+ inference.getPremise(conclusionFactory_);
	}

	@Override
	public String visit(SubClassInclusionDecomposedFirstConjunct inference) {
		return "Decomposing " + inference.getPremise(conclusionFactory_);

	}

	@Override
	public String visit(SubClassInclusionDecomposedSecondConjunct inference) {
		return "Decomposing " + inference.getPremise(conclusionFactory_);

	}

	@Override
	public String visit(SubClassInclusionExpandedDefinition inference) {
		return "Decomposed definition " + inference.getSubsumer() + " of "
				+ inference.getFirstPremise(conclusionFactory_);
	}

	@Override
	public String visit(SubClassInclusionExpandedSubClassOf inference) {
		return "SubClassOf( " + inference.getFirstPremise(conclusionFactory_)
				+ " " + inference.getSubsumer() + " )";
	}

	@Override
	public String visit(SubClassInclusionObjectHasSelfPropertyRange inference) {
		return "Property range of "
				+ inference.getFirstPremise(conclusionFactory_);
	}

	@Override
	public String visit(SubClassInclusionOwlThing inference) {
		return "SubClassOf( " + inference.getDestination() + " "
				+ inference.getSubsumer() + " ) [owl:Thing]";
	}

	@Override
	public String visit(SubClassInclusionRange inference) {
		return "SubClassOf( " + inference.getDestination() + " "
				+ inference.getSubsumer() + " ) [PropertyRange]";
	}

	@Override
	public String visit(SubClassInclusionTautology inference) {
		return "Root Initialization";
	}

	@Override
	public String visit(SubContextInitializationNoPremises inference) {
		return "Init (" + inference.getDestination() + ":"
				+ inference.getSubDestination() + ")";
	}

	@Override
	public String visit(SubPropertyChainExpandedSubObjectPropertyOf inference) {
		return "Expanded sub-chain: " + inference.getSubChain() + " => "
				+ inference.getSuperChain() + ", premise: "
				+ inference.getFirstPremise(conclusionFactory_) + ", reason: "
				+ inference.getReason();
	}

	@Override
	public String visit(SubPropertyChainTautology inference) {
		return "Initialization (" + inference.getChain() + " => "
				+ inference.getSuperChain() + ")";
	}

}
