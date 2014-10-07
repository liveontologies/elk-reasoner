/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;

import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemma;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface LemmaExpression extends Expression {

	public ElkLemma getLemma();
}
