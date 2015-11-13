/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.inferences.util;

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
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkComposition;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedEntity;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedDefinedClass;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectUnionOf;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkComposition;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionOfDisjointSubsumers;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionOfObjectComplementOf;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionOfOwlNothing;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionExpandedDefinition;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkOfObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkOfObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionDecomposedFirstConjunct;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkOfObjectHasSelf;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkOfObjectHasSelf;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionDecomposedSecondConjunct;
import org.semanticweb.elk.reasoner.saturation.inferences.DisjointSubsumerFromSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.PropagationGenerated;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionTautology;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionObjectHasSelfPropertyRange;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionPropagated;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkReversed;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionExpandedSubClassOf;
import org.semanticweb.elk.reasoner.saturation.inferences.SuperReversedForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.ObjectPropertyInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.SubPropertyChainExpandedSubObjectPropertyOf;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.SubPropertyChainTautology;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ClassInferenceVisitor;

/**
 * A utility to pretty-print {@link ClassInference}s.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class InferencePrinter
		implements
			ClassInferenceVisitor<Void, String>,
			ObjectPropertyInferenceVisitor<Void, String> {

	private static InferencePrinter DEFAULT_PRINTER_ = new InferencePrinter(
			new ConclusionBaseFactory());

	private final Conclusion.Factory factory_;

	public InferencePrinter(Conclusion.Factory factory) {
		this.factory_ = factory;
	}

	public static String print(ClassInference conclusion) {
		return conclusion.accept(DEFAULT_PRINTER_, null);
	}

	@Override
	public String visit(SubClassInclusionTautology conclusion, Void parameter) {
		return "Root Initialization";
	}

	@Override
	public String visit(SubClassInclusionExpandedSubClassOf conclusion, Void parameter) {
		return "SubClassOf( " + conclusion.getPremise(factory_) + " "
				+ conclusion.getSuperExpression() + " )";
	}

	@Override
	public String visit(SubClassInclusionComposedObjectIntersectionOf conclusion, Void parameter) {
		return "Conjuncting " + conclusion.getFirstPremise(factory_) + " and "
				+ conclusion.getSecondPremise(factory_);

	}

	@Override
	public String visit(SubClassInclusionDecomposedFirstConjunct conclusion, Void parameter) {
		return "Decomposing " + conclusion.getPremise(factory_);

	}

	@Override
	public String visit(SubClassInclusionDecomposedSecondConjunct conclusion, Void parameter) {
		return "Decomposing " + conclusion.getPremise(factory_);

	}

	@Override
	public String visit(SubClassInclusionComposedObjectSomeValuesFrom conclusion, Void parameter) {
		return "Existential inference from "
				+ conclusion.getSecondPremise(factory_) + " and "
				+ conclusion.getFirstPremise(factory_);
	}

	@Override
	public String visit(BackwardLinkComposition conclusion, Void parameter) {
		BackwardLink bwLink = conclusion.getFirstPremise(factory_);
		ForwardLink fwLink = conclusion.getThirdPremise(factory_);
		return "Composed backward link from " + bwLink + " and " + fwLink;
	}

	@Override
	public String visit(ForwardLinkComposition conclusion, Void input) {
		BackwardLink bwLink = conclusion.getFirstPremise(factory_);
		ForwardLink fwLink = conclusion.getThirdPremise(factory_);
		return "Composed forward link from " + bwLink + " and " + fwLink;
	}

	@Override
	public String visit(BackwardLinkReversed conclusion, Void parameter) {
		return "Reversing forward link " + conclusion.getPremise(factory_);
	}

	@Override
	public String visit(SuperReversedForwardLink conclusion, Void input) {
		return "Reversing forward link " + conclusion.getFirstPremise(factory_)
				+ " and unfolding under "
				+ conclusion.getSecondPremise(factory_);
	}

	@Override
	public String visit(BackwardLinkOfObjectSomeValuesFrom conclusion,
			Void parameter) {
		return "Creating backward link from " + conclusion.getPremise(factory_);
	}

	@Override
	public String visit(BackwardLinkOfObjectHasSelf conclusion,
			Void input) {
		return "Creating forward link from " + conclusion.getPremise(factory_);
	}

	@Override
	public String visit(ForwardLinkOfObjectSomeValuesFrom conclusion,
			Void input) {
		return "Creating forward link from " + conclusion.getPremise(factory_);
	}

	@Override
	public String visit(ForwardLinkOfObjectHasSelf conclusion, Void input) {
		return "Creating forward link from " + conclusion.getPremise(factory_);
	}

	@Override
	public String visit(PropagationGenerated conclusion, Void parameter) {
		return "Creating propagation from "
				+ conclusion.getFirstPremise(factory_);
	}

	@Override
	public String visit(ContradictionOfDisjointSubsumers conclusion,
			Void input) {
		return conclusion.toString();
	}

	@Override
	public String visit(ContradictionOfObjectComplementOf conclusion, Void input) {
		return "Contradiction due to derived " + conclusion.getFirstPremise(factory_)
				+ " and " + conclusion.getSecondPremise(factory_);
	}

	@Override
	public String visit(ContradictionOfOwlNothing conclusion, Void input) {
		return conclusion.toString();
	}

	@Override
	public String visit(ContradictionPropagated conclusion, Void input) {
		return "Contradiction propagated over "
				+ conclusion.getFirstPremise(factory_);
	}

	@Override
	public String visit(DisjointSubsumerFromSubsumer conclusion, Void input) {
		return "Disjoint subsumer " + conclusion + " derived from "
				+ conclusion.getPremise(factory_);
	}

	@Override
	public String visit(SubClassInclusionComposedObjectUnionOf conclusion, Void input) {
		return "Composed disjunction " + conclusion.getSuperExpression() + " from "
				+ conclusion.getPremise(factory_);
	}

	@Override
	public String visit(SubPropertyChainTautology inference, Void input) {
		return "Initialization (" + inference.getChain() + " => "
				+ inference.getSuperChain() + ")";
	}

	@Override
	public String visit(SubPropertyChainExpandedSubObjectPropertyOf inference, Void input) {
		return "Told sub-chain: " + inference.getSubChain() + " => "
				+ inference.getSuperChain() + ", premise: "
				+ inference.getPremise(factory_);
	}

	@Override
	public String visit(SubClassInclusionObjectHasSelfPropertyRange inference,
			Void input) {
		return "Property range of " + inference.getPremise(factory_);
	}

	@Override
	public String visit(SubClassInclusionComposedEntity inference, Void input) {
		return "Composed decomposition " + inference.getSuperExpression();
	}

	@Override
	public String visit(SubClassInclusionComposedDefinedClass inference, Void input) {
		return "Composed definition " + inference.getSuperExpression() + " from "
				+ inference.getPremise(factory_);
	}

	@Override
	public String visit(SubClassInclusionExpandedDefinition inference, Void input) {
		return "Decomposed definition " + inference.getSuperExpression() + " of "
				+ inference.getPremise(factory_);
	}

}
