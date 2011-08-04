package org.semanticweb.elk.syntax;

import org.semanticweb.elk.util.HashGenerator;

/**
 * Corresponds to a <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Self-Restriction">Self-Restriction<a> in
 * the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 */
public class ElkObjectHasSelf extends ElkClassExpression {

	protected final ElkObjectPropertyExpression objectPropertyExpression;

	private static final int constructorHash_ = "ElkObjectHasSelf".hashCode();

	private ElkObjectHasSelf(
			ElkObjectPropertyExpression objectPropertyExpression) {
		this.objectPropertyExpression = objectPropertyExpression;
		this.structuralHashCode = HashGenerator
				.combineListHash(constructorHash_,
						objectPropertyExpression.structuralHashCode());
	}

	public static ElkObjectHasSelf create(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return (ElkObjectHasSelf) factory.put(new ElkObjectHasSelf(
				objectPropertyExpression));
	}

	public ElkObjectPropertyExpression getObjectPropertyExpression() {
		return objectPropertyExpression;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticweb.elk.reasoner.ELKClassExpression#accept(org.
	 * semanticweb.elk.reasoner.ELKClassExpressionVisitor)
	 */
	@Override
	public <O> O accept(ElkClassExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.reasoner.ElkObject#structuralEquals(java.lang.Object)
	 */
	@Override
	public boolean structuralEquals(ElkObject object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkObjectHasSelf) {
			return objectPropertyExpression
					.equals(((ElkObjectHasSelf) object).objectPropertyExpression);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("ObjectHasSelf(");
		result.append(objectPropertyExpression.toString());
		result.append(")");
		return result.toString();
	}

}
