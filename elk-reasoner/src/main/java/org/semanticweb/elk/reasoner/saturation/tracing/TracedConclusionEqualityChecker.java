/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionEqualityChecker;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Determines equality between two inferences.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TracedConclusionEqualityChecker implements TracedConclusionVisitor<Boolean, TracedConclusion> {

	public static boolean equal(TracedConclusion first, TracedConclusion second, Context context) {
		if (first.getInferenceContext(context).getRoot() != second.getInferenceContext(context).getRoot()) {
			return false;
		}
		
		return first.acceptTraced(new TracedConclusionEqualityChecker(), second);
	}
	
	@Override
	public Boolean visit(InitializationSubsumer conclusion, TracedConclusion other) {
		return other.acceptTraced(new BaseBooleanTracedConclusionVisitor<InitializationSubsumer>() {

			@Override
			public Boolean visit(InitializationSubsumer first, InitializationSubsumer second) {
				return first.getExpression() == second.getExpression();
			}
			
		}, conclusion);
	}

	@Override
	public Boolean visit(SubClassOfSubsumer conclusion, TracedConclusion other) {
		return other.acceptTraced(new BaseBooleanTracedConclusionVisitor<SubClassOfSubsumer>() {

			@Override
			public Boolean visit(SubClassOfSubsumer first, SubClassOfSubsumer second) {
				ConclusionEqualityChecker checker = new ConclusionEqualityChecker();
				
				return first.getPremise().accept(checker, second.getPremise()) && first.getExpression() == second.getExpression();
			}
			
		}, conclusion);
	}

	@Override
	public Boolean visit(ComposedConjunction conclusion, TracedConclusion other) {
		return other.acceptTraced(new BaseBooleanTracedConclusionVisitor<ComposedConjunction>() {

			@Override
			public Boolean visit(ComposedConjunction first, ComposedConjunction second) {
				return first.getExpression() == second.getExpression();
			}
			
		}, conclusion);
	}

	@Override
	public Boolean visit(DecomposedConjunction conclusion, 	TracedConclusion other) {
		return other.acceptTraced(new BaseBooleanTracedConclusionVisitor<DecomposedConjunction>() {

			@Override
			public Boolean visit(DecomposedConjunction first, DecomposedConjunction second) {
				return first.getConjunction().getExpression() == second.getConjunction().getExpression() 
						&& first.getExpression() == second.getExpression();
			}
			
		}, conclusion);
	}

	@Override
	public Boolean visit(PropagatedSubsumer conclusion, TracedConclusion other) {
		return other.acceptTraced(new BaseBooleanTracedConclusionVisitor<PropagatedSubsumer>() {

			@Override
			public Boolean visit(PropagatedSubsumer first, PropagatedSubsumer second) {
				ConclusionEqualityChecker checker = new ConclusionEqualityChecker();
				
				return first.getBackwardLink().accept(checker, 	second.getBackwardLink())
						&& first.getPropagation().accept(checker, second.getPropagation());
			}
			
		}, conclusion);
	}

	@Override
	public Boolean visit(ReflexiveSubsumer conclusion, 	TracedConclusion other) {
		return other.acceptTraced(new BaseBooleanTracedConclusionVisitor<ReflexiveSubsumer>() {

			@Override
			public Boolean visit(ReflexiveSubsumer first, ReflexiveSubsumer second) {
				
				return first.getExpression() == second.getExpression();
			}
			
		}, conclusion);
	}

	@Override
	public Boolean visit(ComposedBackwardLink conclusion, TracedConclusion other) {
		return other.acceptTraced(new BaseBooleanTracedConclusionVisitor<ComposedBackwardLink>() {

			@Override
			public Boolean visit(ComposedBackwardLink first, ComposedBackwardLink second) {
				ConclusionEqualityChecker checker = new ConclusionEqualityChecker();
				
				return first.getBackwardLink().accept(checker, second.getBackwardLink()) &&
						first.getForwardLink().accept(checker, second.getForwardLink());
			}
			
		}, conclusion);
	}

	@Override
	public Boolean visit(ReversedBackwardLink conclusion, TracedConclusion other) {
		return other.acceptTraced(new BaseBooleanTracedConclusionVisitor<ReversedBackwardLink>() {

			@Override
			public Boolean visit(ReversedBackwardLink first, ReversedBackwardLink second) {
				ConclusionEqualityChecker checker = new ConclusionEqualityChecker();
				
				return first.getSourceLink().accept(checker, second.getSourceLink());
			}
			
		}, conclusion);
	}

	@Override
	public Boolean visit(DecomposedExistential conclusion, 	TracedConclusion other) {
		return other.acceptTraced(new BaseBooleanTracedConclusionVisitor<DecomposedExistential>() {

			@Override
			public Boolean visit(DecomposedExistential first, DecomposedExistential second) {
				return first.getExistential().getExpression() == second.getExistential().getExpression() 
						&& first.getSource().getRoot() == second.getSource().getRoot();
			}
			
		}, conclusion);
	}

	@Override
	public Boolean visit(TracedPropagation conclusion, 	TracedConclusion other) {
		return other.acceptTraced(new BaseBooleanTracedConclusionVisitor<TracedPropagation>() {

			@Override
			public Boolean visit(TracedPropagation first, TracedPropagation second) {
				ConclusionEqualityChecker checker = new ConclusionEqualityChecker();
				
				return first.getPremise().accept(checker, second.getPremise()) 
						&& first.getCarry() == second.getCarry() 
						&& first.getRelation() == second.getRelation();
			}
			
		}, conclusion);
	}

}
