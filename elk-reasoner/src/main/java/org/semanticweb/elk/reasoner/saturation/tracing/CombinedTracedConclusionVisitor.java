/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class CombinedTracedConclusionVisitor<C> implements TracedConclusionVisitor<Boolean, C> {

	private final TracedConclusionVisitor<Boolean, C> first_;
	
	private final TracedConclusionVisitor<Boolean, C> second_;

	public CombinedTracedConclusionVisitor(TracedConclusionVisitor<Boolean, C> f, TracedConclusionVisitor<Boolean, C> s) {
		first_ = f;
		second_ = s;
	}
	
	@Override
	public Boolean visit(InitializationSubsumer conclusion, C parameter) {
		return first_.visit(conclusion, parameter) && second_.visit(conclusion, parameter);
	}

	@Override
	public Boolean visit(SubClassOfSubsumer conclusion, C parameter) {
		return first_.visit(conclusion, parameter) && second_.visit(conclusion, parameter);
	}

	@Override
	public Boolean visit(ComposedConjunction conclusion, C parameter) {
		return first_.visit(conclusion, parameter) && second_.visit(conclusion, parameter);
	}

	@Override
	public Boolean visit(DecomposedConjunction conclusion, C parameter) {
		return first_.visit(conclusion, parameter) && second_.visit(conclusion, parameter);
	}

	@Override
	public Boolean visit(PropagatedSubsumer conclusion, C parameter) {
		return first_.visit(conclusion, parameter) && second_.visit(conclusion, parameter);
	}

	@Override
	public Boolean visit(ReflexiveSubsumer conclusion, C parameter) {
		return first_.visit(conclusion, parameter) && second_.visit(conclusion, parameter);
	}

	@Override
	public Boolean visit(ComposedBackwardLink conclusion, C parameter) {
		return first_.visit(conclusion, parameter) && second_.visit(conclusion, parameter);
	}

	@Override
	public Boolean visit(ReversedBackwardLink conclusion, C parameter) {
		return first_.visit(conclusion, parameter) && second_.visit(conclusion, parameter);
	}

	@Override
	public Boolean visit(DecomposedExistential conclusion, C parameter) {
		return first_.visit(conclusion, parameter) && second_.visit(conclusion, parameter);
	}

	@Override
	public Boolean visit(TracedPropagation conclusion, C parameter) {
		return first_.visit(conclusion, parameter) && second_.visit(conclusion, parameter);
	}

	
}
