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
public abstract class AbstractClassInferenceVisitor<O>
		implements
			ClassInference.Visitor<O> {

	protected abstract O defaultTracedVisit(ClassInference inference);

	@Override
	public O visit(BackwardLinkComposition inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionComposedObjectIntersectionOf inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionComposedEntity inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionComposedDefinedClass inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionComposedObjectUnionOf inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionComposedObjectSomeValuesFrom inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(ForwardLinkComposition inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(ContradictionOfDisjointSubsumers inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(ContradictionOfObjectComplementOf inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(ContradictionOfOwlNothing inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionExpandedDefinition inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(BackwardLinkOfObjectSomeValuesFrom inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(ForwardLinkOfObjectSomeValuesFrom inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionDecomposedFirstConjunct inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(BackwardLinkOfObjectHasSelf inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(ForwardLinkOfObjectHasSelf inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionDecomposedSecondConjunct inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(DisjointSubsumerFromSubsumer inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(PropagationGenerated inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionTautology inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionObjectHasSelfPropertyRange inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(ContradictionPropagated inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(BackwardLinkReversed inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionExpandedSubClassOf inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(BackwardLinkReversedExpanded inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionOwlThing inference) {
		return defaultTracedVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionRange inference) {
		return defaultTracedVisit(inference);
	}

}
