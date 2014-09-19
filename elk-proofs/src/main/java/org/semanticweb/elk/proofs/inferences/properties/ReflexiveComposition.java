/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.properties;

import java.util.Arrays;
import java.util.Collection;

import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAxiom;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.proofs.sideconditions.SideCondition;
import org.semanticweb.elk.proofs.utils.ProofUtils;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ReflexiveComposition implements Inference {

	private final Expression<ElkObjectPropertyAxiom> firstReflexive_;
	
	private final Expression<ElkObjectPropertyAxiom> secondReflexive_;
	
	private final Expression<ElkObjectPropertyAxiom> conclusion_;
	
	@SuppressWarnings("unchecked")
	public ReflexiveComposition(Expression<ElkObjectPropertyAxiom> firstRefl, Expression<ElkObjectPropertyAxiom> secondRefl) {
		conclusion_ = ProofUtils.mergeExpressions(firstRefl, secondRefl);
		
		firstReflexive_ = firstRefl;
		secondReflexive_ = secondRefl;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<? extends Expression<ElkObjectPropertyAxiom>> getPremises() {
		return Arrays.asList(firstReflexive_, secondReflexive_);
	}
	
	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression<ElkObjectPropertyAxiom> getConclusion() {
		return conclusion_;
	}

	@Override
	public SideCondition getSideCondition() {
		return null;
	}

}
