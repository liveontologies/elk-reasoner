/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class MultiAxiomExpression<E extends ElkAxiom> implements Expression<E> {

	private final List<E> axioms_;
	
	public MultiAxiomExpression(E... axioms) {
		axioms_ = Arrays.asList(axioms);
	}

	@Override
	public Collection<E> getAxioms() {
		return axioms_;
	}

}
