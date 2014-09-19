/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.properties;

import java.util.Collection;
import java.util.Collections;

import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.SingleAxiomExpression;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.proofs.sideconditions.AxiomPresenceCondition;
import org.semanticweb.elk.proofs.sideconditions.SideCondition;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ToldReflexivity extends AbstractPropertyInference {

	private final ElkReflexiveObjectPropertyAxiom sideCondition_;
	
	private final Expression<ElkSubObjectPropertyOfAxiom> premise_;
	
	public ToldReflexivity(ElkReflexiveObjectPropertyAxiom axiom, ElkObjectFactory factory) {
		super(axiom);
		
		sideCondition_ = axiom;
		premise_ = new SingleAxiomExpression<ElkSubObjectPropertyOfAxiom>(factory.getSubObjectPropertyOfAxiom(axiom.getProperty(), axiom.getProperty()));
	}
	
	@Override
	public Collection<? extends Expression<? extends ElkObjectPropertyAxiom>> getPremises() {
		return Collections.singletonList(premise_);
	}
	

	@Override
	public SideCondition getSideCondition() {
		return new AxiomPresenceCondition<ElkReflexiveObjectPropertyAxiom>(sideCondition_);
	}

	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		// TODO Auto-generated method stub
		return null;
	}

}
