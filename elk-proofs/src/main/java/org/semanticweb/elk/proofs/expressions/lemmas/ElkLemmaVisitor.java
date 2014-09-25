/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.lemmas;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ElkLemmaVisitor<I, O> {

	public O visit(ElkReflexivePropertyChainLemma lemma, I input);
	
	public O visit(ElkSubClassOfLemma lemma, I input);
	
	public O visit(ElkSubPropertyChainOfLemma lemma, I input);
}
