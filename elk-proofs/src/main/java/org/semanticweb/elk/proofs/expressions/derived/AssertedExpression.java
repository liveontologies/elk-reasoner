/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.derived;

import java.util.Collections;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
import org.semanticweb.elk.proofs.inferences.AssertedInference;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.InferenceReader;
import org.semanticweb.elk.util.collections.Operations;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class AssertedExpression<E extends ElkAxiom> extends DerivedAxiomExpression {

	private final E axiom_;
	
	public AssertedExpression(E ax, InferenceReader r) {
		super(ax, r);
		axiom_ = ax;
	}
	
	@Override
	public E getAxiom() {
		return axiom_;
	}

	@Override
	public Iterable<Inference> getInferences() throws ElkException {
		// in addition it always returns a single premise-free inference
		// saying that it's present in the ontology
		return Operations.concat(Collections.singletonList(new AssertedInference(this)), super.getInferences());
	}

	@Override
	public <I, O> O accept(ExpressionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

}
