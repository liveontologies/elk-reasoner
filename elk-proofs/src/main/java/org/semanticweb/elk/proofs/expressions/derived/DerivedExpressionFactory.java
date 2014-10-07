/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.derived;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemma;

/**
 * Creates {@link Expression}s.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface DerivedExpressionFactory {

	public DerivedAxiomExpression create(ElkAxiom axiom);
	
	public DerivedLemmaExpression create(ElkLemma lemma);
}
