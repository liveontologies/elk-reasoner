/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class MultiAxiomExpression<E extends ElkAxiom> implements Expression<E> {

	private final Iterable<E> axioms_;
	
	public MultiAxiomExpression(Iterable<E> axioms) {
		axioms_ = axioms;
	}

	@Override
	public Iterable<E> getAxioms() {
		return axioms_;
	}

}
