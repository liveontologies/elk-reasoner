/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.SubContextInitialization;

/**
 * Checks if they conclusions should be considered logically equal.
 * 
 * TODO complete
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ConclusionEqualityChecker implements ConclusionVisitor<Conclusion, Boolean> {

	public static boolean equal(Conclusion first, Conclusion second, IndexedClassExpression contextRoot) {
		if (first.getSourceRoot(contextRoot) != second.getSourceRoot(contextRoot)) {
			return false;
		}
		
		return first.accept(new ConclusionEqualityChecker(), second);
	}
	
	@Override
	public Boolean visit(ComposedSubsumer negSCE, Conclusion other) {
		return other.accept(new AbstractBooleanConclusionVisitor<IndexedClassExpression>(){

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
		return other.accept(new AbstractBooleanConclusionVisitor<IndexedClassExpression>(){

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
		return other.accept(new AbstractBooleanConclusionVisitor<Void>(){

			@Override
			public Boolean visit(BackwardLink otherLink, Void ignored) {
				return otherLink.getRelation() == link.getRelation() && otherLink.getSource() == link.getSource();
			}
			
		}, null);
	}

	@Override
	public Boolean visit(final ForwardLink link, Conclusion other) {
		return other.accept(new AbstractBooleanConclusionVisitor<Void>(){

			@Override
			public Boolean visit(ForwardLink otherLink, Void ignored) {
				return otherLink.getRelation() == link.getRelation() && otherLink.getTarget() == link.getTarget();
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
		return other.accept(new AbstractBooleanConclusionVisitor<Void>(){

			@Override
			public Boolean visit(Propagation otherPropagation, Void ignored) {
				return otherPropagation.getRelation() == propagation.getRelation() && otherPropagation.getCarry() == propagation.getCarry();
			}
			
		}, null);
	}

	@Override
	public Boolean visit(SubContextInitialization subConclusion, Conclusion input) {
		// TODO 
		return false;
	}

	@Override
	public Boolean visit(ContextInitialization conclusion, Conclusion input) {
		// TODO 
		return false;
	}

	@Override
	public Boolean visit(DisjointSubsumer conclusion, Conclusion input) {
		// TODO 
		return false;
	}

}
