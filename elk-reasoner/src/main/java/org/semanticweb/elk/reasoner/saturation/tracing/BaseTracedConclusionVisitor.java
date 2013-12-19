/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;



/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class BaseTracedConclusionVisitor<R, C> implements TracedConclusionVisitor<R, C> {

	protected R defaultTracedVisit(TracedConclusion conclusion, C parameter) {
		return null;
	}

	@Override
	public R visit(InitializationSubsumer conclusion, C parameter) {
		return defaultTracedVisit(conclusion, parameter);
	}

	@Override
	public R visit(SubClassOfSubsumer conclusion, C parameter) {
		return defaultTracedVisit(conclusion, parameter);
	}

	@Override
	public R visit(ComposedConjunction conclusion, C parameter) {
		return defaultTracedVisit(conclusion, parameter);
	}

	@Override
	public R visit(DecomposedConjunction conclusion, C parameter) {
		return defaultTracedVisit(conclusion, parameter);
	}

	@Override
	public R visit(PropagatedSubsumer conclusion, C parameter) {
		return defaultTracedVisit(conclusion, parameter);
	}

	@Override
	public R visit(ReflexiveSubsumer conclusion, C parameter) {
		return defaultTracedVisit(conclusion, parameter);
	}

	@Override
	public R visit(ComposedBackwardLink conclusion, C parameter) {
		return defaultTracedVisit(conclusion, parameter);
	}

	@Override
	public R visit(ReversedBackwardLink conclusion, C parameter) {
		return defaultTracedVisit(conclusion, parameter);
	}

	@Override
	public R visit(DecomposedExistential conclusion, C parameter) {
		return defaultTracedVisit(conclusion, parameter);
	}

	@Override
	public R visit(TracedPropagation conclusion, C parameter) {
		return defaultTracedVisit(conclusion, parameter);
	}
}
