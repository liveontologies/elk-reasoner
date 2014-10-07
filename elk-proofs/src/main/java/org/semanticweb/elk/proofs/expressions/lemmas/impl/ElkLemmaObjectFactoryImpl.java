/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.lemmas.impl;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkClassExpressionWrap;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexClassExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexObjectSomeValuesFrom;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubClassOfLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubPropertyChainOfLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemmaObjectFactory;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkReflexivePropertyChainLemma;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ElkLemmaObjectFactoryImpl implements ElkLemmaObjectFactory {

	@Override
	public ElkReflexivePropertyChainLemma getReflexivePropertyChainLemma(
			ElkSubObjectPropertyExpression chain) {
		return new ElkReflexivePropertyChainLemmaImpl(chain);
	}

	@Override
	public ElkSubPropertyChainOfLemma getSubPropertyChainOfLemma(
			ElkSubObjectPropertyExpression subchain,
			ElkSubObjectPropertyExpression superchain) {
		return new ElkSubPropertyChainOfLemmaImpl(subchain, superchain);
	}

	@Override
	public ElkSubClassOfLemma getSubClassOfLemma(
			ElkClassExpression subclass,
			ElkComplexClassExpression superclass) {
		return new ElkSubClassOfLemmaImpl(subclass, superclass);
	}

	@Override
	public ElkComplexObjectSomeValuesFrom getComplexObjectSomeValuesFrom(
			ElkSubObjectPropertyExpression chain, ElkClassExpression filler) {
		return new ElkComplexObjectSomeValuesFromImpl(chain, filler);
	}

	@Override
	public ElkClassExpressionWrap wrapElkClassExpression(
			ElkClassExpression ce) {
		return new ElkClassExpressionWrapImpl(ce);
	}

}
