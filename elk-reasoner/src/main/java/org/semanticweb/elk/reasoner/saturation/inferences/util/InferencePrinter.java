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
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedBackwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedConjunction;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionFromDisjointSubsumers;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionFromInconsistentDisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionFromNegation;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionFromOwlNothing;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedExistentialBackwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedExistentialForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedFirstConjunct;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedReflexiveBackwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedReflexiveForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedSecondConjunct;
import org.semanticweb.elk.reasoner.saturation.inferences.DisjointSubsumerFromSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.DisjunctionComposition;
import org.semanticweb.elk.reasoner.saturation.inferences.GeneratedPropagation;
import org.semanticweb.elk.reasoner.saturation.inferences.InitializationSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.ObjectHasSelfPropertyRangeSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.PropagatedContradiction;
import org.semanticweb.elk.reasoner.saturation.inferences.PropagatedSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.ReflexiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.ReversedForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassOfSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.SuperReversedForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.ComposedReflexivePropertyChain;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.LeftReflexiveSubPropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.ObjectPropertyInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.PropertyChainInitialization;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.ReflexiveToldSubObjectProperty;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.RightReflexiveSubPropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.ToldReflexiveProperty;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.ToldSubProperty;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ClassInferenceVisitor;

/**
 * A utility to pretty-print {@link ClassInference}s.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class InferencePrinter implements ClassInferenceVisitor<Void, String>,
		ObjectPropertyInferenceVisitor<Void, String> {

	public static String print(ClassInference conclusion) {
		return conclusion.accept(new InferencePrinter(), null);
	}

	@Override
	public String visit(InitializationSubsumer conclusion, Void parameter) {
		return "Root Initialization";
	}

	@Override
	public String visit(SubClassOfSubsumer conclusion, Void parameter) {
		return "SubClassOf( " + conclusion.getPremise() + " "
				+ conclusion.getExpression() + " )";
	}

	@Override
	public String visit(ComposedConjunction conclusion, Void parameter) {
		return "Conjuncting " + conclusion.getFirstConjunct() + " and "
				+ conclusion.getSecondConjunct();

	}

	@Override
	public String visit(DecomposedFirstConjunct conclusion, Void parameter) {
		return "Decomposing " + conclusion.getPremise();

	}

	@Override
	public String visit(DecomposedSecondConjunct conclusion, Void parameter) {
		return "Decomposing " + conclusion.getPremise();

	}

	@Override
	public String visit(PropagatedSubsumer conclusion, Void parameter) {
		return "Existential inference from " + conclusion.getPropagation()
				+ " and " + conclusion.getBackwardLink();
	}

	@Override
	public String visit(ReflexiveSubsumer conclusion, Void parameter) {
		return "Reflexive inference: owl:Thing => " + conclusion.getRelation()
				+ " some owl:Thing";
	}

	@Override
	public String visit(ComposedBackwardLink conclusion, Void parameter) {
		BackwardLink bwLink = conclusion.getBackwardLink();
		ForwardLink fwLink = conclusion.getForwardLink();
		return "Composed backward link from " + bwLink + " and " + fwLink;
	}

	@Override
	public String visit(ComposedForwardLink conclusion, Void input) {
		BackwardLink bwLink = conclusion.getBackwardLink();
		ForwardLink fwLink = conclusion.getForwardLink();
		return "Composed forward link from " + bwLink + " and " + fwLink;
	}

	@Override
	public String visit(ReversedForwardLink conclusion, Void parameter) {
		return "Reversing forward link " + conclusion.getPremise();
	}

	@Override
	public String visit(SuperReversedForwardLink conclusion, Void input) {
		return "Reversing forward link " + conclusion.getPremise()
				+ " and unfolding under " + conclusion.getReason();
	}

	@Override
	public String visit(DecomposedExistentialBackwardLink conclusion,
			Void parameter) {
		return "Creating backward link from " + conclusion.getPremise();
	}

	@Override
	public String visit(DecomposedReflexiveBackwardLink conclusion, Void input) {
		return "Creating forward link from " + conclusion.getPremise();
	}

	@Override
	public String visit(DecomposedExistentialForwardLink conclusion, Void input) {
		return "Creating forward link from " + conclusion.getExistential();
	}

	@Override
	public String visit(DecomposedReflexiveForwardLink conclusion, Void input) {
		return "Creating forward link from " + conclusion.getExistential();
	}

	@Override
	public String visit(GeneratedPropagation conclusion, Void parameter) {
		return "Creating propagation from " + conclusion.getPremise();
	}

	@Override
	public String visit(
			ContradictionFromInconsistentDisjointnessAxiom conclusion,
			Void input) {
		return "Contradiction since " + conclusion.getPremise()
				+ " is disjoint with itself";
	}

	@Override
	public String visit(ContradictionFromDisjointSubsumers conclusion,
			Void input) {
		return conclusion.toString();
	}

	@Override
	public String visit(ContradictionFromNegation conclusion, Void input) {
		return "Contradiction due to derived " + conclusion.getPremise()
				+ " and " + conclusion.getNegatedPremise();
	}

	@Override
	public String visit(ContradictionFromOwlNothing conclusion, Void input) {
		return conclusion.toString();
	}

	@Override
	public String visit(PropagatedContradiction conclusion, Void input) {
		return "Contradiction propagated over " + conclusion.getLinkPremise();
	}

	@Override
	public String visit(DisjointSubsumerFromSubsumer conclusion, Void input) {
		return "Disjoint subsumer " + conclusion + " derived from "
				+ conclusion.getPremise();
	}

	@Override
	public String visit(DisjunctionComposition conclusion, Void input) {
		return "Composed disjunction " + conclusion.getExpression() + " from "
				+ conclusion.getPremise();
	}

	@Override
	public String visit(PropertyChainInitialization inference, Void input) {
		return "Initialization (" + inference.getPropertyChain() + ")";
	}

	@Override
	public String visit(ToldReflexiveProperty inference, Void input) {
		return "Told reflexive property " + inference.getPropertyChain();
	}

	@Override
	public String visit(ReflexiveToldSubObjectProperty inference, Void input) {
		return inference.getPropertyChain()
				+ "is reflexive because its told sub-property "
				+ inference.getSubProperty() + " is reflexive";
	}

	@Override
	public String visit(ComposedReflexivePropertyChain inference, Void input) {
		return inference.getPropertyChain()
				+ "is reflexive because of composition of reflexive chains";
	}

	@Override
	public String visit(LeftReflexiveSubPropertyChainInference inference,
			Void input) {
		return "Left part of " + inference.getSuperPropertyChain()
				+ " is reflexive";
	}

	@Override
	public String visit(RightReflexiveSubPropertyChainInference inference,
			Void input) {
		return "Right part of " + inference.getSuperPropertyChain()
				+ " is reflexive";
	}

	@Override
	public String visit(ToldSubProperty inference, Void input) {
		return "Told sub-chain: " + inference.getSubPropertyChain() + " => "
				+ inference.getSuperPropertyChain() + ", premise: "
				+ inference.getPremise();
	}

	@Override
	public String visit(ObjectHasSelfPropertyRangeSubsumer inference, Void input) {
		return "Property range of " + inference.getPremise();
	}

}
