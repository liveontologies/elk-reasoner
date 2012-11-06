/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.util.collections.chains.AbstractChain;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IncrementalIndexRuleChain  extends AbstractChain<IndexRules<IndexedClassExpression>> {

	//private static final Logger LOGGER_ = Logger.getLogger(IncrementalIndexRuleChain.class);
	
	private IndexRules<IndexedClassExpression> indexRules_;

	public IncrementalIndexRuleChain(IndexRules<IndexedClassExpression> rules) {
		indexRules_ = rules;
	}
	
	@Override
	public IndexRules<IndexedClassExpression> next() {
		return indexRules_;
	}

	@Override
	public void setNext(IndexRules<IndexedClassExpression> tail) {
		indexRules_ = tail;
	}	
	
	public void apply(IndexedClassExpression target) {
		IndexRules<IndexedClassExpression> next = indexRules_;
		
		while(next != null) {
			next.apply(target);
			next = next.next();
		}		
	}
	
	public void deapply(IndexedClassExpression target) {
		IndexRules<IndexedClassExpression> next = indexRules_;
		
		while(next != null) {
			next.deapply(target);
			next = next.next();
		}
	}
}
