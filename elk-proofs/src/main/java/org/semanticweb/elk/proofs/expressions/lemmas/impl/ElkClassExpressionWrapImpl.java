/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.lemmas.impl;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkClassExpressionWrap;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexClassExpressionVisitor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ElkClassExpressionWrapImpl implements ElkClassExpressionWrap {

	private final ElkClassExpression class_;
	
	ElkClassExpressionWrapImpl(ElkClassExpression ce) {
		class_ = ce;
	}
	
	@Override
	public ElkClassExpression getClassExpression() {
		return class_;
	}

	@Override
	public <I, O> O accept(ElkComplexClassExpressionVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}

}
