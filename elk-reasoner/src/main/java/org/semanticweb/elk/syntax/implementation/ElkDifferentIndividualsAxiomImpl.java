package org.semanticweb.elk.syntax.implementation;

import java.util.List;

import org.semanticweb.elk.syntax.ElkAssertionAxiomVisitor;
import org.semanticweb.elk.syntax.ElkAxiomVisitor;
import org.semanticweb.elk.syntax.ElkObjectVisitor;
import org.semanticweb.elk.syntax.interfaces.ElkDifferentIndividualsAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkIndividual;

/**
 * ELK implementation of ElkDifferentIndividualsAxiom.
 * 
 * @author Markus Kroetzsch
 */
public class ElkDifferentIndividualsAxiomImpl extends ElkIndividualListObject
		implements ElkDifferentIndividualsAxiom {

	private static final int constructorHash_ = "ElkDifferentIndividualsAxiom"
			.hashCode();

	/* package-private */ElkDifferentIndividualsAxiomImpl(
			List<? extends ElkIndividual> individuals) {
		super(individuals);
		this.structuralHashCode = ElkObjectImpl.computeCompositeHash(
				constructorHash_, individuals);
	}

	@Override
	public String toString() {
		return buildFssString("DifferentIndividuals");
	}

	public boolean structuralEquals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkDifferentIndividualsAxiom) {
			return elkObjects.equals(((ElkDifferentIndividualsAxiom) object)
					.getIndividuals());
		} else {
			return false;
		}
	}

	public <O> O accept(ElkAssertionAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
