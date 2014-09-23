/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.properties;

import java.util.Arrays;
import java.util.Collection;

import org.semanticweb.elk.proofs.expressions.Explanation;
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

	private final Expression firstReflexive_;
	
	private final Expression secondReflexive_;
	
	private final Expression conclusion_;
	
	@SuppressWarnings("unchecked")
	public ReflexiveComposition(Expression firstRefl, Expression secondRefl) {
		conclusion_ = ProofUtils.fromPremiseExplanations(Arrays.<Iterable<Explanation>>asList(firstRefl.getExplanations(), secondRefl.getExplanations()));
		
		firstReflexive_ = firstRefl;
		secondReflexive_ = secondRefl;
	}
	
	@Override
	public Collection<? extends Expression> getPremises() {
		return Arrays.asList(firstReflexive_, secondReflexive_);
	}
	
	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public Expression getConclusion() {
		return conclusion_;
	}

	@Override
	public SideCondition getSideCondition() {
		return null;
	}

}
