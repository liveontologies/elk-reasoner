/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ReflexiveInference extends AbstractInference {

	private final IndexedPropertyChain reflexiveChain_;
	
	/**
	 * 
	 */
	public ReflexiveInference(IndexedPropertyChain chain) {
		reflexiveChain_ = chain;
	}

	public IndexedPropertyChain getChain() {
		return reflexiveChain_;
	}
}
