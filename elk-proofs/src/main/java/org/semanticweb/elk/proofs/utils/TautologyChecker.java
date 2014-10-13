package org.semanticweb.elk.proofs.utils;

import org.semanticweb.elk.owl.AbstractElkAxiomVisitor;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedLemmaExpression;
import org.semanticweb.elk.proofs.expressions.derived.entries.StructuralEquivalenceChecker;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemmaVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkReflexivePropertyChainLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubClassOfLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubPropertyChainOfLemma;

/**
 * Checks if an expression is a tautology.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TautologyChecker extends
		AbstractElkAxiomVisitor<Boolean> implements
		ExpressionVisitor<Void, Boolean>, ElkLemmaVisitor<Void, Boolean> {

	@Override
	protected Boolean defaultLogicalVisit(ElkAxiom axiom) {
		return false;
	}

	@Override
	public Boolean visit(
			ElkReflexivePropertyChainLemma lemma, Void input) {
		return false;
	}

	@Override
	public Boolean visit(ElkSubClassOfLemma lemma, Void input) {
		return false;
	}

	@Override
	public Boolean visit(ElkSubPropertyChainOfLemma lemma,
			Void input) {
		return StructuralEquivalenceChecker.equal(lemma.getSubPropertyChain(), lemma.getSuperPropertyChain());
	}

	@Override
	public Boolean visit(DerivedAxiomExpression<?> expr,
			Void input) {
		return expr.getAxiom().accept(this);
	}

	@Override
	public Boolean visit(DerivedLemmaExpression expr,
			Void input) {
		return expr.getLemma().accept(this, input);
	}

	@Override
	public Boolean visit(ElkSubClassOfAxiom ax) {
		return StructuralEquivalenceChecker.equal(ax.getSubClassExpression(), ax.getSuperClassExpression());
	}

	@Override
	public Boolean visit(
			ElkSubObjectPropertyOfAxiom ax) {
		return StructuralEquivalenceChecker.equal(ax.getSubObjectPropertyExpression(), ax.getSuperObjectPropertyExpression());
	}
	
}