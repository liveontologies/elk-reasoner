/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.lemmas;


/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ElkLemma {

	public <I, O> O accept(ElkLemmaVisitor<I, O> visitor, I input);
}
