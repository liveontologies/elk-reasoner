/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

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
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Generates a hash code for the given {@link Conclusion}.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ConclusionHashGenerator implements ConclusionVisitor<Void, Integer> {

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
		return HashGenerator.combineListHash(link.getRelation().hashCode(), link.getSource().hashCode());
	}

	@Override
	public Integer visit(ForwardLink link, Void context) {
		return HashGenerator.combineListHash(link.getRelation().hashCode(), link.getTarget().hashCode());
	}

	@Override
	public Integer visit(Contradiction bot, Void context) {
		return bot.hashCode();
	}

	@Override
	public Integer visit(Propagation propagation, Void context) {
		return HashGenerator.combineListHash(propagation.getRelation().hashCode(), propagation.getCarry().hashCode());	}

	@Override
	public Integer visit(SubContextInitialization subConclusion, Void input) {
		return subConclusion.getSubRoot().hashCode();
	}

	@Override
	public Integer visit(ContextInitialization conclusion, Void input) {
		return conclusion.hashCode();
	}

	@Override
	public Integer visit(DisjointSubsumer conclusion, Void input) {
		return HashGenerator.combineListHash(conclusion.getMember().hashCode(), conclusion.getAxiom().hashCode());
	}

	

}
