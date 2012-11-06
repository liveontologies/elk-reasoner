/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing;

import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.ChainImpl;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class IndexRules<T> extends ChainImpl<IndexRules<T>> implements IndexRule<T, Boolean> {

	public IndexRules(IndexRules<T> tail) {
		super(tail);
	}
	
/*	@Override
	public Boolean apply(T target) {
		IndexRule<T, Boolean> next = null;
		boolean changed = false;
		
		while((next = next()) != null) {
			changed |= next.apply(target).booleanValue();
		}
		
		return changed;
	}
	
	@Override
	public Boolean deapply(T target) {
		IndexRule<T, Boolean> next = null;
		boolean changed = false;
		
		while((next = next()) != null) {
			changed |= next.deapply(target).booleanValue();
		}
		
		return changed;
	}*/	
	
	/**
	 * Adds these rules to the given chain
	 * 
	 * @param ruleChain
	 * @return
	 */
	public abstract boolean addTo(Chain<IndexRules<T>> ruleChain);
	
	/**
	 * Removes these rules from the given chain
	 * 
	 * @param ruleChain
	 * @return
	 */
	public abstract boolean removeFrom(Chain<IndexRules<T>> ruleChain);	
}
