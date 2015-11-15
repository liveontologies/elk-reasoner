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
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.DummyClassConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.DummyObjectPropertyConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SaturationConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainExpandedSubObjectPropertyOf;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainTautology;

/**
 * Visits all premises for the given {@link ClassInference} or
 * {@link ObjectPropertyInference}. Each premise implements {@link ClassConclusion}
 * or {@link ObjectPropertyConclusion}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ClassInferencePremiseVisitor<I, O> implements
		ClassInference.Visitor<I, O>, ObjectPropertyInference.Visitor<I, O> {

	private final ClassConclusion.Visitor<I, O> classPremiseVisitor_;

	private final ObjectPropertyConclusion.Visitor<I, O> propertyPremiseVisitor_;
	
	private final SaturationConclusion.Factory factory_ = new ConclusionBaseFactory();

	public ClassInferencePremiseVisitor(
			ClassConclusion.Visitor<I, O> classPremiseVisitor,
			ObjectPropertyConclusion.Visitor<I, O> propertyPremiseVisitor) {
		classPremiseVisitor_ = classPremiseVisitor;
		propertyPremiseVisitor_ = propertyPremiseVisitor;
	}

	public ClassInferencePremiseVisitor(
			ClassConclusion.Visitor<I, O> classPremiseVisitor) {
		this(classPremiseVisitor,
				new DummyObjectPropertyConclusionVisitor<I, O>());
	}

	public ClassInferencePremiseVisitor(
			ObjectPropertyConclusion.Visitor<I, O> propertyPremiseVisitor) {
		this(new DummyClassConclusionVisitor<I, O>(), propertyPremiseVisitor);
	}

	@Override
	public O visit(SubClassInclusionTautology conclusion, I input) {
		return null;
	}

	@Override
	public O visit(SubClassInclusionExpandedSubClassOf conclusion, I input) {
		conclusion.getPremise(factory_).accept(classPremiseVisitor_, input);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedObjectIntersectionOf conclusion, I input) {
		conclusion.getFirstPremise(factory_).accept(classPremiseVisitor_, input);
		conclusion.getSecondPremise(factory_).accept(classPremiseVisitor_, input);
		return null;
	}

	@Override
	public O visit(SubClassInclusionDecomposedFirstConjunct conclusion, I input) {
		conclusion.getPremise(factory_).accept(classPremiseVisitor_, input);
		return null;
	}

	@Override
	public O visit(SubClassInclusionDecomposedSecondConjunct conclusion, I input) {
		conclusion.getPremise(factory_).accept(classPremiseVisitor_, input);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedObjectSomeValuesFrom conclusion, I input) {
		conclusion.getFirstPremise(factory_).accept(classPremiseVisitor_, input);
		conclusion.getSecondPremise(factory_).accept(classPremiseVisitor_, input);
		return null;
	}

	@Override
	public O visit(BackwardLinkComposition conclusion, I input) {
		conclusion.getFirstPremise(factory_).accept(classPremiseVisitor_, input);
		conclusion.getSecondPremise(factory_).accept(propertyPremiseVisitor_, input);
		conclusion.getThirdPremise(factory_).accept(classPremiseVisitor_, input);
		conclusion.getFourthPremise(factory_).accept(propertyPremiseVisitor_, input);
		return null;
	}

	@Override
	public O visit(ForwardLinkComposition conclusion, I input) {
		conclusion.getFirstPremise(factory_).accept(classPremiseVisitor_, input);
		conclusion.getSecondPremise(factory_).accept(propertyPremiseVisitor_, input);
		conclusion.getThirdPremise(factory_).accept(classPremiseVisitor_, input);
		conclusion.getFourthPremise(factory_).accept(propertyPremiseVisitor_, input);
		return null;
	}

	@Override
	public O visit(BackwardLinkReversed conclusion, I input) {
		conclusion.getPremise(factory_).accept(classPremiseVisitor_, input);
		return null;
	}

	@Override
	public O visit(BackwardLinkReversedExpanded conclusion, I input) {
		conclusion.getFirstPremise(factory_).accept(classPremiseVisitor_, input);
		conclusion.getSecondPremise(factory_).accept(propertyPremiseVisitor_, input);
		return null;
	}

	@Override
	public O visit(BackwardLinkOfObjectSomeValuesFrom conclusion, I input) {
		conclusion.getPremise(factory_).accept(classPremiseVisitor_, input);
		return null;
	}

	@Override
	public O visit(ForwardLinkOfObjectSomeValuesFrom conclusion, I input) {
		conclusion.getPremise(factory_).accept(classPremiseVisitor_, input);
		return null;
	}

	@Override
	public O visit(BackwardLinkOfObjectHasSelf conclusion, I input) {
		conclusion.getPremise(factory_).accept(classPremiseVisitor_, input);
		return null;
	}

	@Override
	public O visit(ForwardLinkOfObjectHasSelf conclusion, I input) {
		conclusion.getPremise(factory_).accept(classPremiseVisitor_, input);
		return null;
	}

	@Override
	public O visit(PropagationGenerated conclusion, I input) {
		conclusion.getFirstPremise(factory_).accept(classPremiseVisitor_, input);
		conclusion.getSecondPremise(factory_).accept(propertyPremiseVisitor_, input);
		return null;
	}

	@Override
	public O visit(ContradictionOfDisjointSubsumers conclusion, I input) {
		conclusion.getFirstPremise(factory_).accept(classPremiseVisitor_, input);
		conclusion.getSecondPremise(factory_).accept(classPremiseVisitor_, input);		

		return null;
	}

	@Override
	public O visit(ContradictionOfObjectComplementOf conclusion, I input) {
		conclusion.getFirstPremise(factory_).accept(classPremiseVisitor_, input);
		conclusion.getSecondPremise(factory_).accept(classPremiseVisitor_, input);
		return null;
	}

	@Override
	public O visit(ContradictionOfOwlNothing conclusion, I input) {
		conclusion.getPremise(factory_).accept(classPremiseVisitor_, input);
		return null;
	}

	@Override
	public O visit(ContradictionPropagated conclusion, I input) {
		conclusion.getFirstPremise(factory_).accept(classPremiseVisitor_, input);
		conclusion.getSecondPremise(factory_)
				.accept(classPremiseVisitor_, input);
		return null;
	}

	@Override
	public O visit(DisjointSubsumerFromSubsumer conclusion, I input) {
		conclusion.getPremise(factory_).accept(classPremiseVisitor_, input);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedObjectUnionOf conclusion, I input) {
		conclusion.getPremise(factory_).accept(classPremiseVisitor_, input);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedEntity inference, I input) {
		inference.getPremise(factory_).accept(classPremiseVisitor_, input);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedDefinedClass inference, I input) {
		inference.getPremise(factory_).accept(classPremiseVisitor_, input);
		return null;
	}

	@Override
	public O visit(SubClassInclusionExpandedDefinition inference, I input) {
		inference.getPremise(factory_).accept(classPremiseVisitor_, input);
		return null;
	}

	@Override
	public O visit(SubPropertyChainTautology conclusion, I input) {
		// no premises
		return null;
	}

	@Override
	public O visit(SubPropertyChainExpandedSubObjectPropertyOf inference, I input) {
		inference.getPremise(factory_).accept(propertyPremiseVisitor_, input);
		return null;
	}

	@Override
	public O visit(SubClassInclusionObjectHasSelfPropertyRange inference, I input) {
		inference.getPremise(factory_).accept(classPremiseVisitor_, input);
		// TODO: process the property range premise
		return null;
	}

}
