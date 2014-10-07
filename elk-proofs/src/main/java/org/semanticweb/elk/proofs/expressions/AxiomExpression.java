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
public interface AxiomExpression extends Expression {

	public ElkAxiom getAxiom();
}
