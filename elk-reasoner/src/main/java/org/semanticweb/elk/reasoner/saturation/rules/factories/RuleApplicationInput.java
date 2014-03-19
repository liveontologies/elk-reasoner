/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules.factories;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;

/**
 * Represents the input taken by {@link RuleApplicationFactory}'s processing
 * engines. Always has a context's root but may be extended with other
 * information.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RuleApplicationInput {

	private final IndexedClassExpression root_;
	
	public RuleApplicationInput(IndexedClassExpression root) {
		root_ = root;
	}
	
	public IndexedClassExpression getRoot() {
		return root_;
	}

	@Override
	public String toString() {
		return root_.toString();
	}
	
}
