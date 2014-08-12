/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.AbstractConclusionVisitor;
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
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.PropertyChainInitialization;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexivePropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexivePropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexiveToldSubObjectProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.RightReflexiveSubPropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.SubPropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ToldReflexiveProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ToldSubPropertyChain;

/**
 * Visits all premises for the given {@link ClassInference} or {@link ObjectPropertyInference}. Each premise implements
 * {@link Conclusion} or {@link ObjectPropertyConclusion}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class PremiseVisitor<I, O> extends AbstractConclusionVisitor<I, O>
		implements ClassInferenceVisitor<I, O>, ObjectPropertyInferenceVisitor<I, O>, ObjectPropertyConclusionVisitor<I, O> {

	@Override
	protected O defaultVisit(Conclusion conclusion, I input) {
		// no-op
		return null;
	}
	
	protected O defaultVisit(@SuppressWarnings("unused") ObjectPropertyConclusion conclusion) {
		// no-op
		return null;
	}	
	
	@Override
	public O visit(InitializationSubsumer<?> conclusion, I parameter) {
		return null;
	}

	@Override
	public O visit(SubClassOfSubsumer<?> conclusion, I cxt) {
		conclusion.getPremise().accept(this, cxt);
		return null;
	}

	@Override
	public O visit(ComposedConjunction conclusion, I parameter) {
		conclusion.getFirstConjunct().accept(this, parameter);
		conclusion.getSecondConjunct().accept(this, parameter);
		return null;
	}

	@Override
	public O visit(DecomposedConjunction conclusion, I parameter) {
		conclusion.getConjunction().accept(this, parameter);
		return null;
	}

	@Override
	public O visit(PropagatedSubsumer conclusion, I parameter) {
		conclusion.getBackwardLink().accept(this, parameter);
		conclusion.getPropagation().accept(this, parameter);
		conclusion.getSubPropertyInference().accept(this, parameter);
		return null;
	}

	@Override
	public O visit(ReflexiveSubsumer<?> conclusion, I parameter) {
		conclusion.getReflexivityPremise().accept(this, parameter);
		return null;
	}

	@Override
	public O visit(ComposedBackwardLink conclusion, I parameter) {
		conclusion.getBackwardLink().accept(this, parameter);
		conclusion.getForwardLink().accept(this, parameter);
		conclusion.getSubPropertyChain().accept(this, parameter);
		conclusion.getLeftSubObjectProperty().accept(this, parameter);
		conclusion.getRightSubObjectPropertyChain().accept(this, parameter);
		return null;
	}

	@Override
	public O visit(ComposedForwardLink conclusion, I parameter) {
		conclusion.getBackwardLink().accept(this, parameter);
		conclusion.getForwardLink().accept(this, parameter);
		conclusion.getLeftSubObjectProperty().accept(this, parameter);
		conclusion.getRightSubObjectPropertyChain().accept(this, parameter);
		return null;
	}

	@Override
	public O visit(ReversedForwardLink conclusion, I parameter) {
		conclusion.getSourceLink().accept(this, parameter);
		conclusion.getSubPropertyChain().accept(this, parameter);
		return null;
	}

	@Override
	public O visit(DecomposedExistentialBackwardLink conclusion, I parameter) {
		conclusion.getExistential().accept(this, parameter);
		return null;
	}

	@Override
	public O visit(DecomposedExistentialForwardLink conclusion, I parameter) {
		conclusion.getExistential().accept(this, parameter);
		return null;
	}

	@Override
	public O visit(TracedPropagation conclusion, I parameter) {
		conclusion.getPremise().accept(this, parameter);
		return null;
	}

	@Override
	public O visit(ContradictionFromInconsistentDisjointnessAxiom conclusion,
			I input) {
		conclusion.getPremise().accept(this, input);
		return null;
	}

	@Override
	public O visit(ContradictionFromDisjointSubsumers conclusion, I input) {
		for (DisjointSubsumer subsumer : conclusion.getPremises()) {
			subsumer.accept(this, input);
		}

		return null;
	}

	@Override
	public O visit(ContradictionFromNegation conclusion, I input) {
		conclusion.getPremise().accept(this, input); 
		conclusion.getPositivePremise().accept(this, input);
		return null;
	}

	@Override
	public O visit(ContradictionFromOwlNothing conclusion, I input) {
		conclusion.getPremise().accept(this, input);
		return null;
	}

	@Override
	public O visit(PropagatedContradiction conclusion, I input) {
		conclusion.getLinkPremise().accept(this, input);
		conclusion.getContradictionPremise().accept(this, input);
		return null;
	}

	@Override
	public O visit(DisjointSubsumerFromSubsumer conclusion, I input) {
		conclusion.getPremise().accept(this, input);
		return null;
	}
	
	@Override
	public O visit(DisjunctionComposition conclusion, I input) {
		conclusion.getPremise().accept(this, input);
		return null;
	}
	
	@Override
	public O visit(PropertyChainInitialization conclusion, I input) {
		// no premises
		return null;
	}

	@Override
	public O visit(ReflexivePropertyChain<?> conclusion, I input) {
		return defaultVisit(conclusion);
	}

	@Override
	public O visit(SubPropertyChain<?, ?> conclusion, I input) {
		return defaultVisit(conclusion);
	}

	@Override
	public O visit(ToldReflexiveProperty inference, I input) {
		inference.getPropertyInitialization().accept(this, input);
		return null;
	}

	@Override
	public O visit(ReflexiveToldSubObjectProperty inference, I input) {
		inference.getSubProperty().accept(this, input);
		return null;
	}

	@Override
	public O visit(ReflexivePropertyChainInference inference, I input) {
		inference.getLeftReflexiveProperty().accept(this, input);
		inference.getRightReflexivePropertyChain().accept(this, input);
		return null;
	}

	@Override
	public O visit(LeftReflexiveSubPropertyChainInference inference, I input) {
		inference.getReflexivePremise().accept(this, input);
		return null;
	}

	@Override
	public O visit(RightReflexiveSubPropertyChainInference inference, I input) {
		inference.getReflexivePremise().accept(this, input);
		return null;
	}

	@Override
	public O visit(ToldSubPropertyChain inference, I input) {
		inference.getPremise().accept(this, input);
		return null;
	}

}
