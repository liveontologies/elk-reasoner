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
public abstract class AbstractClassInferenceVisitor<O> implements
		ClassInference.Visitor<O> {

	protected abstract O defaultTracedVisit(ClassInference conclusion);

	@Override
	public O visit(BackwardLinkComposition conclusion) {
		return defaultTracedVisit(conclusion);
	}

	@Override
	public O visit(SubClassInclusionComposedObjectIntersectionOf conclusion) {
		return defaultTracedVisit(conclusion);
	}

	@Override
	public O visit(SubClassInclusionComposedEntity conclusion) {
		return defaultTracedVisit(conclusion);
	}

	@Override
	public O visit(SubClassInclusionComposedDefinedClass conclusion) {
		return defaultTracedVisit(conclusion);
	}

	@Override
	public O visit(SubClassInclusionComposedObjectUnionOf conclusion) {
		return defaultTracedVisit(conclusion);
	}

	@Override
	public O visit(SubClassInclusionComposedObjectSomeValuesFrom conclusion) {
		return defaultTracedVisit(conclusion);
	}

	@Override
	public O visit(ForwardLinkComposition conclusion) {
		return defaultTracedVisit(conclusion);
	}

	@Override
	public O visit(ContradictionOfDisjointSubsumers conclusion) {
		return defaultTracedVisit(conclusion);
	}

	@Override
	public O visit(ContradictionOfObjectComplementOf conclusion) {
		return defaultTracedVisit(conclusion);
	}

	@Override
	public O visit(ContradictionOfOwlNothing conclusion) {
		return defaultTracedVisit(conclusion);
	}

	@Override
	public O visit(SubClassInclusionExpandedDefinition conclusion) {
		return defaultTracedVisit(conclusion);
	}

	@Override
	public O visit(BackwardLinkOfObjectSomeValuesFrom conclusion) {
		return defaultTracedVisit(conclusion);
	}

	@Override
	public O visit(ForwardLinkOfObjectSomeValuesFrom conclusion) {
		return defaultTracedVisit(conclusion);
	}

	@Override
	public O visit(SubClassInclusionDecomposedFirstConjunct conclusion) {
		return defaultTracedVisit(conclusion);
	}

	@Override
	public O visit(BackwardLinkOfObjectHasSelf conclusion) {
		return defaultTracedVisit(conclusion);
	}

	@Override
	public O visit(ForwardLinkOfObjectHasSelf conclusion) {
		return defaultTracedVisit(conclusion);
	}

	@Override
	public O visit(SubClassInclusionDecomposedSecondConjunct conclusion) {
		return defaultTracedVisit(conclusion);
	}

	@Override
	public O visit(DisjointSubsumerFromSubsumer conclusion) {
		return defaultTracedVisit(conclusion);
	}

	@Override
	public O visit(PropagationGenerated conclusion) {
		return defaultTracedVisit(conclusion);
	}

	@Override
	public O visit(SubClassInclusionTautology conclusion) {
		return defaultTracedVisit(conclusion);
	}

	@Override
	public O visit(SubClassInclusionObjectHasSelfPropertyRange conclusion) {
		return defaultTracedVisit(conclusion);
	}

	@Override
	public O visit(ContradictionPropagated conclusion) {
		return defaultTracedVisit(conclusion);
	}

	@Override
	public O visit(BackwardLinkReversed conclusion) {
		return defaultTracedVisit(conclusion);
	}

	@Override
	public O visit(SubClassInclusionExpandedSubClassOf conclusion) {
		return defaultTracedVisit(conclusion);
	}

	@Override
	public O visit(BackwardLinkReversedExpanded conclusion) {
		return defaultTracedVisit(conclusion);
	}

}
