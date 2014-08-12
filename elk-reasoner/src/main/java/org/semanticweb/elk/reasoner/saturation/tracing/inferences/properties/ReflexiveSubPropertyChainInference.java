/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;

/**
 * S is a sub-chain of R o S if R is reflexive (same goes for R if S is reflexive)
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class ReflexiveSubPropertyChainInference extends SubPropertyChain<IndexedPropertyChain, IndexedBinaryPropertyChain> implements ObjectPropertyInference {

	public ReflexiveSubPropertyChainInference(IndexedPropertyChain subChain, IndexedBinaryPropertyChain chain) {
		super(subChain, chain);
	}
	
	public abstract ReflexivePropertyChain<?> getReflexivePremise();

}
