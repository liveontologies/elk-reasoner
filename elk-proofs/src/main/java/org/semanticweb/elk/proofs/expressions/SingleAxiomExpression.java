/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;

import java.util.Collection;
import java.util.Collections;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SingleAxiomExpression<E extends ElkAxiom> implements Expression<E> {

	private final E axiom_;
	
	public SingleAxiomExpression(E ax) {
		axiom_ = ax;
	}
	
	public E getAxiom() {
		return axiom_;
	}
	
	private Collection<E> asCollection() {
		return Collections.singleton(axiom_);
	}

	@Override
	public Collection<E> getAxioms() {
		return asCollection();
	}
	
}
