package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.indexing.IndexedPropertyExpression;
import org.semanticweb.elk.util.Pair;

/**
 * @author Frantisek Simancik
 *
 */
public class Propagation extends Pair<IndexedPropertyExpression, Queueable> implements Queueable {
	public Propagation(IndexedPropertyExpression relation, Queueable carry) {
		super(relation, carry);
	}
	
	public IndexedPropertyExpression getRelation() {
		return first;
	}
	
	public Queueable getCarry() {
		return second;
	}

	public <O> O accept(QueueableVisitor<O> visitor) {
		return visitor.visit(this);
	}
}
