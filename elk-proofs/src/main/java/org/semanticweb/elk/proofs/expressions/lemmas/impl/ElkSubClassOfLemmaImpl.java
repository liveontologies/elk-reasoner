/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.lemmas.impl;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexClassExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemmaVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubClassOfLemma;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ElkSubClassOfLemmaImpl implements ElkSubClassOfLemma {

	private final ElkClassExpression sub_;
	
	private final ElkComplexClassExpression sup_;
	
	ElkSubClassOfLemmaImpl(ElkClassExpression sub, ElkComplexClassExpression sup) {
		sub_ = sub;
		sup_ = sup;
	}
	
	@Override
	public ElkClassExpression getSubClass() {
		return sub_;
	}

	@Override
	public ElkComplexClassExpression getSuperClass() {
		return sup_;
	}
	
	@Override
	public <I, O> O accept(ElkLemmaVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

}
