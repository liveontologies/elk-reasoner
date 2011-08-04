package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.indexing.IndexedClassExpression;

public class DecomposedClassExpression implements Queueable {
	protected final IndexedClassExpression classExpression;

	public DecomposedClassExpression(IndexedClassExpression classExpression) {
		if (classExpression == null)
			throw new NullPointerException(
				"Null compononent for org.semanticweb.elk.reasoner.saturation.DecomposedClassExpression");
		this.classExpression = classExpression;
	}
	
	public IndexedClassExpression getClassExpression() {
		return classExpression;
	}

	@Override
	public int hashCode() {
		return 19 + classExpression.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj != null && obj instanceof DecomposedClassExpression)
			return (classExpression == ((DecomposedClassExpression) obj).classExpression);
		
		return false;
	}

	public <O> O accept(QueueableVisitor<O> visitor) {
		return visitor.visit(this);
	}
}
