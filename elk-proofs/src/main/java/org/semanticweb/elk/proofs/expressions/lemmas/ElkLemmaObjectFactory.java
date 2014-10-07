/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.lemmas;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ElkLemmaObjectFactory {

	public ElkReflexivePropertyChainLemma getReflexivePropertyChainLemma(ElkSubObjectPropertyExpression chain);
	
	public ElkSubPropertyChainOfLemma getSubPropertyChainOfLemma(ElkSubObjectPropertyExpression subchain, ElkSubObjectPropertyExpression superchain);
	
	public ElkSubClassOfLemma getSubClassOfLemma(ElkClassExpression subclass, ElkComplexClassExpression superclass);
	
	public ElkComplexObjectSomeValuesFrom getComplexObjectSomeValuesFrom(ElkSubObjectPropertyExpression chain, ElkClassExpression filler);

	public ElkClassExpressionWrap wrapElkClassExpression(ElkClassExpression ce);
}
