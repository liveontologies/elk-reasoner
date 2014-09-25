/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.lemmas.impl;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexClassExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexObjectSomeValuesFrom;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ElkComplexObjectSomeValuesFromImpl implements
		ElkComplexObjectSomeValuesFrom {

	private final ElkSubObjectPropertyExpression chain_;
	
	private final ElkClassExpression filler_;
	
	ElkComplexObjectSomeValuesFromImpl(ElkSubObjectPropertyExpression chain, ElkClassExpression filler) {
		chain_ = chain;
		filler_ = filler;
	}
	
	@Override
	public ElkClassExpression getFiller() {
		return filler_;
	}

	@Override
	public ElkSubObjectPropertyExpression getPropertyChain() {
		return chain_;
	}

	@Override
	public <I, O> O accept(ElkComplexClassExpressionVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}

}
