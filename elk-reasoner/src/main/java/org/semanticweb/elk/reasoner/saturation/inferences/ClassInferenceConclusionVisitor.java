package org.semanticweb.elk.reasoner.saturation.inferences;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;

public class ClassInferenceConclusionVisitor<O>
		implements
			ClassInference.Visitor<O> {

	private final ClassConclusion.Visitor<O> conclusionVisitor_;

	public ClassInferenceConclusionVisitor(
			ClassConclusion.Visitor<O> conclusionVisitor) {
		this.conclusionVisitor_ = conclusionVisitor;
	}

	@Override
	public O visit(ContradictionOfOwlNothing inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(ContradictionOfDisjointSubsumers inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(ContradictionOfObjectComplementOf inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(ContradictionPropagated inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(DisjointSubsumerFromSubsumer inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(ForwardLinkComposition inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(ForwardLinkOfObjectHasSelf inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(ForwardLinkOfObjectSomeValuesFrom inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(ContextInitializationNoPremises inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(SubContextInitializationNoPremises inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(BackwardLinkComposition inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(BackwardLinkOfObjectHasSelf inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(BackwardLinkOfObjectSomeValuesFrom inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(BackwardLinkReversed inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(BackwardLinkReversedExpanded inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(PropagationGenerated inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(SubClassInclusionComposedDefinedClass inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(SubClassInclusionComposedEntity inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(SubClassInclusionComposedObjectIntersectionOf inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(SubClassInclusionComposedObjectSomeValuesFrom inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(SubClassInclusionComposedObjectUnionOf inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(SubClassInclusionDecomposedFirstConjunct inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(SubClassInclusionDecomposedSecondConjunct inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(SubClassInclusionExpandedDefinition inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(SubClassInclusionExpandedSubClassOf inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(SubClassInclusionObjectHasSelfPropertyRange inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(SubClassInclusionOwlThing inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(SubClassInclusionRange inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(SubClassInclusionTautology inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

}
