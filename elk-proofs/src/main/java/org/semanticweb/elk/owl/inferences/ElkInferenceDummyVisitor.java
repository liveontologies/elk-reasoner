package org.semanticweb.elk.owl.inferences;

/**
 * A {@link ElkInference.Visitor} that always returns {@code null}. Can be used
 * as prototype of other visitors by overriding the default visit method.
 * 
 * @author Yevgeny Kazakov
 *
 * @param <O>
 *            the type of the output
 */
public class ElkInferenceDummyVisitor<O> implements ElkInference.Visitor<O> {

	protected O defaultVisit(ElkInference inference) {
		return null;
	}

	@Override
	public O visit(ElkClassInclusionExistentialFillerUnfolding inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkClassInclusionExistentialOfObjectHasSelf inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkClassInclusionExistentialPropertyUnfolding inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkClassInclusionHierarchy inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkClassInclusionObjectIntersectionOfComposition inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(
			ElkClassInclusionObjectIntersectionOfDecomposition inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkClassInclusionObjectUnionOfComposition inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkClassInclusionOfEquivalence inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkClassInclusionOfObjectPropertyDomain inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkClassInclusionOfReflexiveObjectProperty inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkClassInclusionOwlThing inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkClassInclusionReflexivePropertyRange inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkClassInclusionTautology inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkPropertyInclusionHierarchy inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkPropertyInclusionOfTransitiveObjectProperty inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkPropertyInclusionTautology inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkPropertyRangePropertyUnfolding inference) {
		return defaultVisit(inference);
	}

}
