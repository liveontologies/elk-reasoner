/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.lemmas.impl;

import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemmaVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkReflexivePropertyChainLemma;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ElkReflexivePropertyChainLemmaImpl implements
		ElkReflexivePropertyChainLemma {

	private final ElkSubObjectPropertyExpression chain_;
	
	ElkReflexivePropertyChainLemmaImpl(ElkSubObjectPropertyExpression chain) {
		chain_ = chain;
	}
	
	@Override
	public ElkSubObjectPropertyExpression getPropertyChain() {
		return chain_;
	}

	@Override
	public <I, O> O accept(ElkLemmaVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

}
