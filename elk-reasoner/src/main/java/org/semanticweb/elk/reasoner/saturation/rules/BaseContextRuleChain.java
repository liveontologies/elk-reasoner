/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.util.collections.chains.AbstractChain;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class BaseContextRuleChain extends AbstractChain<ContextRules> {

	private ContextRules contextRules_;
	
	@Override
	public ContextRules next() {
		return contextRules_;
	}

	@Override
	public void setNext(ContextRules tail) {
		contextRules_ = tail;
	}
}
