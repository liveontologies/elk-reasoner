package org.semanticweb.elk.reasoner.saturation.inferences;

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

public class ClassInferenceByTypesVisitor<O>
		implements
			ClassInference.Visitor<O> {

	private final ClassInferenceTypesVisitor<O> typesVisitor_;

	public ClassInferenceByTypesVisitor() {
		this.typesVisitor_ = getDelegatingVisitor();
	}

	public ClassInferenceByTypesVisitor(
			ClassInferenceTypesVisitor<O> typesVisitor) {
		this.typesVisitor_ = typesVisitor;
	}

	/**
	 * @return the {@link ClassInferenceTypesVisitor} to which the calls are
	 *         delegated
	 */
	ClassInferenceTypesVisitor<O> getDelegatingVisitor() {
		return null;
	}

	@Override
	public O visit(BackwardLinkComposition inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(BackwardLinkOfObjectHasSelf inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(BackwardLinkOfObjectSomeValuesFrom inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(BackwardLinkReversed inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(BackwardLinkReversedExpanded inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(ContextInitializationNoPremises inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(ContradictionOfDisjointSubsumers inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(ContradictionOfObjectComplementOf inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(ContradictionOfOwlNothing inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(ContradictionPropagated inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(DisjointSubsumerFromSubsumer inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(ForwardLinkComposition inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(ForwardLinkOfObjectHasSelf inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(ForwardLinkOfObjectSomeValuesFrom inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(PropagationGenerated inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(SubClassInclusionComposedDefinedClass inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(SubClassInclusionComposedEntity inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(SubClassInclusionComposedObjectIntersectionOf inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(SubClassInclusionComposedObjectSomeValuesFrom inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(SubClassInclusionComposedObjectUnionOf inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(SubClassInclusionDecomposedFirstConjunct inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(SubClassInclusionDecomposedSecondConjunct inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(SubClassInclusionExpandedDefinition inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(SubClassInclusionExpandedSubClassOf inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(SubClassInclusionObjectHasSelfPropertyRange inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(SubClassInclusionOwlThing inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(SubClassInclusionRange inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(SubClassInclusionTautology inference) {
		return typesVisitor_.visit(inference);
	}

	@Override
	public O visit(SubContextInitializationNoPremises inference) {
		return typesVisitor_.visit(inference);
	}

}
