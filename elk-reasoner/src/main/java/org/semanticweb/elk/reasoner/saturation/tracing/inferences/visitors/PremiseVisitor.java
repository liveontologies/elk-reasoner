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
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.DummyConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.DummyObjectPropertyConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ObjectPropertyConclusionVisitor;
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
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.GeneralSubPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.LeftReflexiveSubPropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.PropertyChainInitialization;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexivePropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexiveToldSubObjectProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.RightReflexiveSubPropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ToldReflexiveProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ToldSubPropertyInference;

/**
 * Visits all premises for the given {@link ClassInference} or {@link ObjectPropertyInference}. Each premise implements
 * {@link Conclusion} or {@link ObjectPropertyConclusion}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class PremiseVisitor<I, O> implements ClassInferenceVisitor<I, O>, ObjectPropertyInferenceVisitor<I, O> {
	
	private final ConclusionVisitor<I, O> classConclusionVisitor_;
	
	private final ObjectPropertyConclusionVisitor<I, O> propertyConclusionVisitor_;
	
	public PremiseVisitor(ConclusionVisitor<I, O> classConclusionVisitor, ObjectPropertyConclusionVisitor<I, O> propConclusionVisitor) {
		classConclusionVisitor_ = classConclusionVisitor;
		propertyConclusionVisitor_ = propConclusionVisitor;
	}
	
	public PremiseVisitor(ConclusionVisitor<I, O> classConclusionVisitor) {
		this(classConclusionVisitor, new DummyObjectPropertyConclusionVisitor<I, O>());
	}
	
	public PremiseVisitor(ObjectPropertyConclusionVisitor<I, O> propConclusionVisitor) {
		this(new DummyConclusionVisitor<I, O>(), propConclusionVisitor);
	}
	
	@Override
	public O visit(InitializationSubsumer<?> conclusion, I parameter) {
		return null;
	}

	@Override
	public O visit(SubClassOfSubsumer<?> conclusion, I cxt) {
		conclusion.getPremise().accept(classConclusionVisitor_, cxt);
		return null;
	}

	@Override
	public O visit(ComposedConjunction conclusion, I parameter) {
		conclusion.getFirstConjunct().accept(classConclusionVisitor_, parameter);
		conclusion.getSecondConjunct().accept(classConclusionVisitor_, parameter);
		return null;
	}

	@Override
	public O visit(DecomposedConjunction conclusion, I parameter) {
		conclusion.getConjunction().accept(classConclusionVisitor_, parameter);
		return null;
	}

	@Override
	public O visit(PropagatedSubsumer conclusion, I parameter) {
		conclusion.getBackwardLink().accept(classConclusionVisitor_, parameter);
		conclusion.getPropagation().accept(classConclusionVisitor_, parameter);
		return null;
	}

	@Override
	public O visit(ReflexiveSubsumer<?> conclusion, I parameter) {
		conclusion.getReflexivityPremise().accept(propertyConclusionVisitor_, parameter);
		return null;
	}

	@Override
	public O visit(ComposedBackwardLink conclusion, I parameter) {
		conclusion.getBackwardLink().accept(classConclusionVisitor_, parameter);
		conclusion.getForwardLink().accept(classConclusionVisitor_, parameter);
		conclusion.getLeftSubObjectProperty().accept(propertyConclusionVisitor_, parameter);
		conclusion.getRightSubObjectPropertyChain().accept(propertyConclusionVisitor_, parameter);
		return null;
	}

	@Override
	public O visit(ComposedForwardLink conclusion, I parameter) {
		conclusion.getBackwardLink().accept(classConclusionVisitor_, parameter);
		conclusion.getForwardLink().accept(classConclusionVisitor_, parameter);
		conclusion.getLeftSubObjectProperty().accept(propertyConclusionVisitor_, parameter);
		conclusion.getRightSubObjectPropertyChain().accept(propertyConclusionVisitor_, parameter);
		return null;
	}

	@Override
	public O visit(ReversedForwardLink conclusion, I parameter) {
		conclusion.getSourceLink().accept(classConclusionVisitor_, parameter);
		return null;
	}

	@Override
	public O visit(DecomposedExistentialBackwardLink conclusion, I parameter) {
		conclusion.getExistential().accept(classConclusionVisitor_, parameter);
		return null;
	}

	@Override
	public O visit(DecomposedExistentialForwardLink conclusion, I parameter) {
		conclusion.getExistential().accept(classConclusionVisitor_, parameter);
		return null;
	}

	@Override
	public O visit(TracedPropagation conclusion, I parameter) {
		conclusion.getSubsumer().accept(classConclusionVisitor_, parameter);
		conclusion.getSubPropertyPremise().accept(propertyConclusionVisitor_, parameter);
		return null;
	}

	@Override
	public O visit(ContradictionFromInconsistentDisjointnessAxiom conclusion,
			I input) {
		conclusion.getPremise().accept(classConclusionVisitor_, input);
		return null;
	}

	@Override
	public O visit(ContradictionFromDisjointSubsumers conclusion, I input) {
		for (DisjointSubsumer subsumer : conclusion.getPremises()) {
			subsumer.accept(classConclusionVisitor_, input);
		}

		return null;
	}

	@Override
	public O visit(ContradictionFromNegation conclusion, I input) {
		conclusion.getPremise().accept(classConclusionVisitor_, input); 
		conclusion.getPositivePremise().accept(classConclusionVisitor_, input);
		return null;
	}

	@Override
	public O visit(ContradictionFromOwlNothing conclusion, I input) {
		conclusion.getPremise().accept(classConclusionVisitor_, input);
		return null;
	}

	@Override
	public O visit(PropagatedContradiction conclusion, I input) {
		conclusion.getLinkPremise().accept(classConclusionVisitor_, input);
		conclusion.getContradictionPremise().accept(classConclusionVisitor_, input);
		return null;
	}

	@Override
	public O visit(DisjointSubsumerFromSubsumer conclusion, I input) {
		conclusion.getPremise().accept(classConclusionVisitor_, input);
		return null;
	}
	
	@Override
	public O visit(DisjunctionComposition conclusion, I input) {
		conclusion.getPremise().accept(classConclusionVisitor_, input);
		return null;
	}
	
	@Override
	public O visit(PropertyChainInitialization conclusion, I input) {
		// no premises
		return null;
	}

	@Override
	public O visit(ToldReflexiveProperty inference, I input) {
		inference.getPropertyInitialization().accept(propertyConclusionVisitor_, input);
		return null;
	}

	@Override
	public O visit(ReflexiveToldSubObjectProperty inference, I input) {
		inference.getSubProperty().accept(propertyConclusionVisitor_, input);
		return null;
	}

	@Override
	public O visit(ReflexivePropertyChainInference inference, I input) {
		inference.getLeftReflexiveProperty().accept(propertyConclusionVisitor_, input);
		inference.getRightReflexivePropertyChain().accept(propertyConclusionVisitor_, input);
		return null;
	}

	@Override
	public O visit(LeftReflexiveSubPropertyChainInference inference, I input) {
		inference.getReflexivePremise().accept(propertyConclusionVisitor_, input);
		return null;
	}

	@Override
	public O visit(RightReflexiveSubPropertyChainInference inference, I input) {
		inference.getReflexivePremise().accept(propertyConclusionVisitor_, input);
		return null;
	}

	@Override
	public O visit(ToldSubPropertyInference inference, I input) {
		inference.getPremise().accept(propertyConclusionVisitor_, input);
		return null;
	}
	
	@Override
	public O visit(GeneralSubPropertyInference inference, I input) {
		inference.getFirstPremise().accept(propertyConclusionVisitor_, input);
		inference.getSecondPremise().accept(propertyConclusionVisitor_, input);
		return null;
	}

}
