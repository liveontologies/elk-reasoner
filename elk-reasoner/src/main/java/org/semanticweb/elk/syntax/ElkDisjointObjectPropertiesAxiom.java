package org.semanticweb.elk.syntax;

import java.util.ArrayList;
import java.util.List;

/**
 * Corresponds to an <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Disjoint_Object_Properties">Disjoint
 * Object Properties Axiom<a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 */
public class ElkDisjointObjectPropertiesAxiom extends ElkObjectPropertyAxiom {

	private static final int constructorHash_ = "ElkDisjointObjectPropertiesAxiom"
			.hashCode();

	protected final List<? extends ElkObjectPropertyExpression> disjointObjectPropertyExpressions;

	private ElkDisjointObjectPropertiesAxiom(
			List<? extends ElkObjectPropertyExpression> disjointObjectPropertyExpressions) {
		this.disjointObjectPropertyExpressions = disjointObjectPropertyExpressions;
		this.structuralHashCode = ElkObject.computeCompositeHash(
				constructorHash_, disjointObjectPropertyExpressions);
	}

	public static ElkDisjointObjectPropertiesAxiom create(
			List<? extends ElkObjectPropertyExpression> disjointObjectPropertyExpressions) {
		return (ElkDisjointObjectPropertiesAxiom) factory
				.put(new ElkDisjointObjectPropertiesAxiom(
						disjointObjectPropertyExpressions));
	}

	public static ElkDisjointObjectPropertiesAxiom create(
			ElkObjectPropertyExpression firstObjectPropertyExpression,
			ElkObjectPropertyExpression secondObjectPropertyExpression,
			ElkObjectPropertyExpression... otherObjectPropertyExpressions) {
		List<ElkObjectPropertyExpression> objectPropertyExpressions = new ArrayList<ElkObjectPropertyExpression>(
				2 + otherObjectPropertyExpressions.length);
		objectPropertyExpressions.add(firstObjectPropertyExpression);
		objectPropertyExpressions.add(secondObjectPropertyExpression);
		for (int i = 0; i < otherObjectPropertyExpressions.length; i++)
			objectPropertyExpressions.add(otherObjectPropertyExpressions[i]);
		return (ElkDisjointObjectPropertiesAxiom) factory
				.put(new ElkDisjointObjectPropertiesAxiom(
						objectPropertyExpressions));
	}

	public List<? extends ElkObjectPropertyExpression> getDisjointObjectPropertyExpressions() {
		return disjointObjectPropertyExpressions;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("DisjointObjectProperties(");
		for (ElkObjectPropertyExpression ope : disjointObjectPropertyExpressions) {
			result.append(ope.toString());
			result.append(" ");
		}
		result.setCharAt(result.length() - 1, ')');
		return result.toString();
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
		} else if (object instanceof ElkDisjointObjectPropertiesAxiom) {
			return disjointObjectPropertyExpressions
					.equals(((ElkDisjointObjectPropertiesAxiom) object).disjointObjectPropertyExpressions);
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.reasoner.ELKObjectPropertyAxiom#accept(org.semanticweb
	 * .elk .reasoner.ELKObjectPropertyAxiomVisitor)
	 */
	@Override
	public <O> O accept(ElkObjectPropertyAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
