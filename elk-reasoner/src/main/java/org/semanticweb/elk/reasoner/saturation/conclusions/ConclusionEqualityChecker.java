/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;

/**
 * Checks if they conclusions should be considered logically equal.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ConclusionEqualityChecker implements ConclusionVisitor<Boolean, Conclusion> {

	@Override
	public Boolean visit(ComposedSubsumer negSCE, Conclusion other) {
		return other.accept(new BaseBooleanConclusionVisitor<IndexedClassExpression>(){

			@Override
			public Boolean visit(ComposedSubsumer subsumer, IndexedClassExpression ice) {
				return subsumer.getExpression() == ice;
			}

			@Override
			public Boolean visit(DecomposedSubsumer subsumer, IndexedClassExpression ice) {
				return subsumer.getExpression() == ice;
			}
			
			
		}, negSCE.getExpression());
	}

	@Override
	public Boolean visit(DecomposedSubsumer posSCE, Conclusion other) {
		return other.accept(new BaseBooleanConclusionVisitor<IndexedClassExpression>(){

			@Override
			public Boolean visit(ComposedSubsumer subsumer, IndexedClassExpression ice) {
				return subsumer.getExpression() == ice;
			}

			@Override
			public Boolean visit(DecomposedSubsumer subsumer, IndexedClassExpression ice) {
				return subsumer.getExpression() == ice;
			}
			
		}, posSCE.getExpression());
	}

	@Override
	public Boolean visit(final BackwardLink link, Conclusion other) {
		return other.accept(new BaseBooleanConclusionVisitor<Void>(){

			@Override
			public Boolean visit(BackwardLink otherLink, Void ignored) {
				return otherLink.getRelation() == link.getRelation() && otherLink.getSource().getRoot() == link.getSource().getRoot();
			}
			
		}, null);
	}

	@Override
	public Boolean visit(final ForwardLink link, Conclusion other) {
		return other.accept(new BaseBooleanConclusionVisitor<Void>(){

			@Override
			public Boolean visit(ForwardLink otherLink, Void ignored) {
				return otherLink.getRelation() == link.getRelation() && otherLink.getTarget().getRoot() == link.getTarget().getRoot();
			}
			
		}, null);
	}

	@Override
	public Boolean visit(Contradiction bot, Conclusion other) {
		//TODO
		return false;
	}

	@Override
	public Boolean visit(final Propagation propagation, Conclusion other) {
		return other.accept(new BaseBooleanConclusionVisitor<Void>(){

			@Override
			public Boolean visit(Propagation otherPropagation, Void ignored) {
				return otherPropagation.getRelation() == propagation.getRelation() && otherPropagation.getCarry() == propagation.getCarry();
			}
			
		}, null);
	}

	@Override
	public Boolean visit(DisjointnessAxiom disjointnessAxiom, Conclusion other) {
		// TODO 
		return false;
	}

}
