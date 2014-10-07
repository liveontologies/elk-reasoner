/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.derived;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.inferences.Inference;

/**
 * Extension of {@link Expression} which returns {@link Inference}s which derive
 * it. This class by itself does not specify whether some or all inferences will
 * be returned. Instead, the code which returns derived expressions should
 * specify completeness guarantees.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface DerivedExpression extends Expression {

	public Iterable<Inference> getInferences() throws ElkException;
}
