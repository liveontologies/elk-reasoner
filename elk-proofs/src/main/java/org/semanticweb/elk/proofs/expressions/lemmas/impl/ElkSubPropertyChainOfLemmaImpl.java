/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.lemmas.impl;

import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemmaVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubPropertyChainOfLemma;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ElkSubPropertyChainOfLemmaImpl implements
		ElkSubPropertyChainOfLemma {

	private final ElkSubObjectPropertyExpression sub_; 
	
	private final ElkSubObjectPropertyExpression sup_;
	
	public ElkSubPropertyChainOfLemmaImpl(ElkSubObjectPropertyExpression sub, ElkSubObjectPropertyExpression sup) {
		sub_ = sub;
		sup_ = sup;
	}
	
	@Override
	public ElkSubObjectPropertyExpression getSubPropertyChain() {
		return sub_;
	}

	@Override
	public ElkSubObjectPropertyExpression getSuperPropertyChain() {
		return sup_;
	}
	
	@Override
	public <I, O> O accept(ElkLemmaVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

}
