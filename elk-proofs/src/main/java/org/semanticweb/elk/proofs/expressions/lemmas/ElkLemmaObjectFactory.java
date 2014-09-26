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

	public ElkReflexivePropertyChainLemma getReflexivePropertyChain(ElkSubObjectPropertyExpression chain);
	
	public ElkSubPropertyChainOfLemma getComplexSubPropertyChainAxiom(ElkSubObjectPropertyExpression subchain, ElkSubObjectPropertyExpression superchain);
	
	public ElkSubClassOfLemma getComplexSubClassOfAxiom(ElkClassExpression subclass, ElkComplexClassExpression superclass);
	
	public ElkComplexObjectSomeValuesFrom getComplexObjectSomeValuesFrom(ElkSubObjectPropertyExpression chain, ElkClassExpression filler);

	public ElkClassExpressionWrap wrapElkClassExpression(ElkClassExpression ce);
}
