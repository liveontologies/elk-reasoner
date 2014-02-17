/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Generates a hash code for the given {@link Conclusion}.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ConclusionHashGenerator implements ConclusionVisitor<Integer, Void> {

	@Override
	public Integer visit(ComposedSubsumer negSCE, Void context) {
		return negSCE.getExpression().hashCode();
	}

	@Override
	public Integer visit(DecomposedSubsumer posSCE, Void context) {
		return posSCE.getExpression().hashCode();
	}

	@Override
	public Integer visit(BackwardLink link, Void context) {
		return HashGenerator.combineListHash(link.getRelation().hashCode(), link.getSource().getRoot().hashCode());
	}

	@Override
	public Integer visit(ForwardLink link, Void context) {
		return HashGenerator.combineListHash(link.getRelation().hashCode(), link.getTarget().getRoot().hashCode());
	}

	@Override
	public Integer visit(Contradiction bot, Void context) {
		return bot.hashCode();
	}

	@Override
	public Integer visit(Propagation propagation, Void context) {
		return HashGenerator.combineListHash(propagation.getRelation().hashCode(), propagation.getCarry().hashCode());	}

	@Override
	public Integer visit(DisjointnessAxiom disjointnessAxiom, Void context) {
		return disjointnessAxiom.getAxiom().hashCode();
	}

}
