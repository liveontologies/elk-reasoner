package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;

/**
 * A dummy class representing changes in the index.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class IndexedClassExpressionChange extends IndexedClassExpression {

	@Override
	protected void updateOccurrenceNumbers(IndexUpdater indexUpdater,
			int increment, int positiveIncrement, int negativeIncrement) {
		// TODO Auto-generated method stub
	}

	@Override
	public <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
