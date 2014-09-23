/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;

/**
 * An expression is either an {@link ElkAxiom}, if it can be represented as a
 * single axiom, or a collection of explanations each of which is an
 * {@link ElkAxiom}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface Expression {

	public Iterable<Explanation> getExplanations();
}
