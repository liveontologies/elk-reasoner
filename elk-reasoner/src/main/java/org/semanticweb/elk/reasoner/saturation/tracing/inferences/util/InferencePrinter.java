/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.util;

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
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedBackwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedConjunction;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedForwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ContradictionFromDisjointSubsumers;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ContradictionFromInconsistentDisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ContradictionFromNegation;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ContradictionFromOwlNothing;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DecomposedConjunction;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DecomposedExistentialBackwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DecomposedExistentialForwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DisjointSubsumerFromSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DisjunctionComposition;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.InitializationSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.PropagatedContradiction;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.PropagatedSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ReflexiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ReversedForwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.SubClassOfSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.TracedPropagation;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.LeftReflexiveSubPropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.PropertyChainInitialization;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexivePropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexiveToldSubObjectProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.RightReflexiveSubPropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ToldReflexiveProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ToldSubPropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;

/**
 * A utility to pretty-print {@link ClassInference}s.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class InferencePrinter implements ClassInferenceVisitor<Void, String>, ObjectPropertyInferenceVisitor<Void, String> {

	public static String print(ClassInference conclusion) {
		return conclusion.acceptTraced(new InferencePrinter(), null);
	}

	@Override
	public String visit(InitializationSubsumer<?> conclusion, Void parameter) {
		return "Root Initialization";
	}

	@Override
	public String visit(SubClassOfSubsumer<?> conclusion, Void parameter) {
		return "SubClassOf( " + conclusion.getPremise() + " "
				+ conclusion.getExpression() + " )";
	}

	@Override
	public String visit(ComposedConjunction conclusion, Void parameter) {
		return "Conjuncting " + conclusion.getFirstConjunct() + " and "
				+ conclusion.getSecondConjunct();

	}

	@Override
	public String visit(DecomposedConjunction conclusion, Void parameter) {
		return "Decomposing " + conclusion.getConjunction();

	}

	@Override
	public String visit(PropagatedSubsumer conclusion, Void parameter) {
		return "Existential inference from " + conclusion.getPropagation()
				+ " and " + conclusion.getBackwardLink();
	}

	@Override
	public String visit(ReflexiveSubsumer<?> conclusion, Void parameter) {
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
		return "Reversing forward link " + conclusion.getSourceLink();
	}

	@Override
	public String visit(DecomposedExistentialBackwardLink conclusion,
			Void parameter) {
		return "Creating backward link from " + conclusion.getExistential();
	}

	@Override
	public String visit(DecomposedExistentialForwardLink conclusion, Void input) {
		return "Creating forward link from " + conclusion.getExistential();
	}

	@Override
	public String visit(TracedPropagation conclusion, Void parameter) {
		return "Creating propagation from " + conclusion.getPremise();
	}

	@Override
	public String visit(
			ContradictionFromInconsistentDisjointnessAxiom conclusion,
			Void input) {
		return "Contradiction since " + conclusion.getPremise() + " is disjoint with itself";
	}

	@Override
	public String visit(ContradictionFromDisjointSubsumers conclusion,
			Void input) {
		return "Contradiction due to " + conclusion.getAxiom() + ", derived through " + conclusion.getPremises()[0].getMember();
	}

	@Override
	public String visit(ContradictionFromNegation conclusion, Void input) {
		return "Contradiction due to derived " + conclusion.getPremise() + " and " + conclusion.getPositivePremise();
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
		return "Disjoint subsumer " + conclusion + " derived from " + conclusion.getPremise(); 
	}

	@Override
	public String visit(DisjunctionComposition conclusion, Void input) {
		return "Composed disjunction " + conclusion.getExpression() + " from " + conclusion.getPremise();
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
		return inference.getPropertyChain() + "is reflexive because its told sub-property " + inference.getSubProperty() + " is reflexive";
	}

	@Override
	public String visit(ReflexivePropertyChainInference inference, Void input) {
		return inference.getPropertyChain() + "is reflexive because of composition of reflexive chains";
	}

	@Override
	public String visit(LeftReflexiveSubPropertyChainInference inference, Void input) {
		return "Left part of " + inference.getSuperPropertyChain() + " is reflexive";
	}

	@Override
	public String visit(RightReflexiveSubPropertyChainInference inference, 	Void input) {
		return "Right part of " + inference.getSuperPropertyChain() + " is reflexive";
	}

	@Override
	public String visit(ToldSubPropertyChain inference, Void input) {
		return "Told super-property " + inference.getSubPropertyChain() + " -> " + inference.getSuperPropertyChain();
	}

}
