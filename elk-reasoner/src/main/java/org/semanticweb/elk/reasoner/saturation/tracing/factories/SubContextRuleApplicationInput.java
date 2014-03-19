/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.factories;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationInput;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SubContextRuleApplicationInput extends RuleApplicationInput {

	private final IndexedObjectProperty subRoot_;
	
	SubContextRuleApplicationInput(IndexedClassExpression root, IndexedObjectProperty subRoot) {
		super(root);
		subRoot_ = subRoot;
	}
	
	public IndexedObjectProperty getSubRoot() {
		return subRoot_;
	}
}
