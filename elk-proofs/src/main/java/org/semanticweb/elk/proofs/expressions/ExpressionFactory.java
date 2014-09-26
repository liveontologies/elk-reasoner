/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemma;

/**
 * Creates {@link Expression}s.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface ExpressionFactory {

	public AxiomExpression create(ElkAxiom axiom);
	
	public LemmaExpression create(ElkLemma lemma);
}
