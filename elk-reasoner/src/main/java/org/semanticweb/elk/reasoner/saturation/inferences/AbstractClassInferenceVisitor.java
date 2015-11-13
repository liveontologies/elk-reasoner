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

/**
 * A skeleton implementation of {@link ClassInference.Visitor}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public abstract class AbstractClassInferenceVisitor<I, O> implements
		ClassInference.Visitor<I, O> {

	protected abstract O defaultTracedVisit(ClassInference conclusion, I input);

	@Override
	public O visit(BackwardLinkComposition conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(SubClassInclusionComposedObjectIntersectionOf conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(SubClassInclusionComposedEntity conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(SubClassInclusionComposedDefinedClass conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(SubClassInclusionComposedObjectUnionOf conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(SubClassInclusionComposedObjectSomeValuesFrom conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(ForwardLinkComposition conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(ContradictionOfDisjointSubsumers conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(ContradictionOfObjectComplementOf conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(ContradictionOfOwlNothing conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(SubClassInclusionExpandedDefinition conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(BackwardLinkOfObjectSomeValuesFrom conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(ForwardLinkOfObjectSomeValuesFrom conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(SubClassInclusionDecomposedFirstConjunct conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(BackwardLinkOfObjectHasSelf conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(ForwardLinkOfObjectHasSelf conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(SubClassInclusionDecomposedSecondConjunct conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(DisjointSubsumerFromSubsumer conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(PropagationGenerated conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(SubClassInclusionTautology conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(SubClassInclusionObjectHasSelfPropertyRange conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(ContradictionPropagated conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(BackwardLinkReversed conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(SubClassInclusionExpandedSubClassOf conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(BackwardLinkReversedExpanded conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

}
