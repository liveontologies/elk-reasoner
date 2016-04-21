/**
 * 
 */
package org.semanticweb.elk.reasoner.tracing.factories;

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

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkComposition;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkOfObjectHasSelf;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkOfObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkReversed;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkReversedExpanded;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInconsistencyOfDisjointSubsumers;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInconsistencyOfObjectComplementOf;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInconsistencyOfOwlNothing;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInconsistencyPropagated;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ContextInitializationNoPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.DisjointSubsumerFromSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkComposition;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkOfObjectHasSelf;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkOfObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.inferences.PropagationGenerated;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedDefinedClass;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedEntity;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectUnionOf;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionDecomposedFirstConjunct;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionDecomposedSecondConjunct;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionExpandedDefinition;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionExpandedFirstEquivalentClass;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionExpandedSecondEquivalentClass;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionExpandedSubClassOf;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionObjectHasSelfPropertyRange;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionOwlThing;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionRange;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionTautology;
import org.semanticweb.elk.reasoner.saturation.inferences.SubContextInitializationNoPremises;

/**
 * A {@link ClassInference.Visitor} that returns the conclusions of the visited
 * {@link ClassInference}s.
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ClassInferenceConclusionGettingVisitor
		implements ClassInference.Visitor<ClassConclusion> {

	private final ClassConclusion.Factory conclusionFactory_;

	public ClassInferenceConclusionGettingVisitor(
			ClassConclusion.Factory conclusionFactory) {
		this.conclusionFactory_ = conclusionFactory;
	}

	@Override
	public ClassConclusion visit(BackwardLinkComposition inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(BackwardLinkOfObjectHasSelf inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(BackwardLinkOfObjectSomeValuesFrom inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(BackwardLinkReversed inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(BackwardLinkReversedExpanded inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(
			ClassInconsistencyOfDisjointSubsumers inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(
			ClassInconsistencyOfObjectComplementOf inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(ClassInconsistencyOfOwlNothing inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(ClassInconsistencyPropagated inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(ContextInitializationNoPremises inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(DisjointSubsumerFromSubsumer inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(ForwardLinkComposition inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(ForwardLinkOfObjectHasSelf inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(ForwardLinkOfObjectSomeValuesFrom inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(PropagationGenerated inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(
			SubClassInclusionComposedDefinedClass inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(SubClassInclusionComposedEntity inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(
			SubClassInclusionComposedObjectIntersectionOf inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(
			SubClassInclusionComposedObjectSomeValuesFrom inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(
			SubClassInclusionComposedObjectUnionOf inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(
			SubClassInclusionDecomposedFirstConjunct inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(
			SubClassInclusionDecomposedSecondConjunct inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(
			SubClassInclusionExpandedDefinition inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(
			SubClassInclusionExpandedFirstEquivalentClass inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(
			SubClassInclusionExpandedSecondEquivalentClass inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(
			SubClassInclusionExpandedSubClassOf inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(
			SubClassInclusionObjectHasSelfPropertyRange inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(SubClassInclusionOwlThing inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(SubClassInclusionRange inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(SubClassInclusionTautology inference) {
		return inference.getConclusion(conclusionFactory_);
	}

	@Override
	public ClassConclusion visit(SubContextInitializationNoPremises inference) {
		return inference.getConclusion(conclusionFactory_);
	}

}
