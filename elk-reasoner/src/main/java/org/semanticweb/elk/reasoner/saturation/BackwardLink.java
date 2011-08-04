package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.indexing.IndexedPropertyExpression;
import org.semanticweb.elk.util.Pair;

/**
 * @author Frantisek Simancik
 *
 */
public class BackwardLink extends Pair<IndexedPropertyExpression, Linkable> implements Queueable {
	
	public BackwardLink(IndexedPropertyExpression relation, Linkable target) {
		super(relation, target);
	}
	
	public IndexedPropertyExpression getRelation() {
		return first;
	}
	
	public Linkable getTarget() {
		return second;
	}

	public <O> O accept(QueueableVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
