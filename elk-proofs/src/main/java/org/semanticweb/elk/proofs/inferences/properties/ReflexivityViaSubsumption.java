/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.properties;

import java.util.Collection;
import java.util.Collections;

import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.proofs.sideconditions.AxiomPresenceCondition;
import org.semanticweb.elk.proofs.sideconditions.SideCondition;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ReflexivityViaSubsumption extends AbstractPropertyInference {

	private final ElkSubObjectPropertyOfAxiom sideCondition_;
	
	private final Expression<ElkObjectPropertyAxiom> premise_;
	
	public ReflexivityViaSubsumption(ElkSubObjectPropertyOfAxiom axiom, Expression<ElkObjectPropertyAxiom> premise, ElkObjectFactory factory) {
		super(factory.getReflexiveObjectPropertyAxiom(axiom.getSuperObjectPropertyExpression()));
		
		sideCondition_ = axiom;
		premise_ = premise;
	}
	
	@Override
	public Collection<? extends Expression<? extends ElkObjectPropertyAxiom>> getPremises() {
		return Collections.singletonList(premise_);
	}
	

	@Override
	public SideCondition getSideCondition() {
		return new AxiomPresenceCondition<ElkSubObjectPropertyOfAxiom>(sideCondition_);
	}

	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

}
