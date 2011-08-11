package org.semanticweb.elk.syntax.implementation;

import java.util.List;

import org.semanticweb.elk.syntax.ElkAssertionAxiomVisitor;
import org.semanticweb.elk.syntax.ElkAxiomVisitor;
import org.semanticweb.elk.syntax.ElkObjectVisitor;
import org.semanticweb.elk.syntax.interfaces.ElkIndividual;
import org.semanticweb.elk.syntax.interfaces.ElkSameIndividualAxiom;

/**
 * ELK implementation of ElkSameIndividualAxiom.
 * 
 * @author Markus Kroetzsch
 */
public class ElkSameIndividualAxiomImpl extends ElkIndividualListObject
		implements ElkSameIndividualAxiom {

	private static final int constructorHash_ = "ElkSameIndividualAxiom"
			.hashCode();

	/* package-private */ElkSameIndividualAxiomImpl(
			List<? extends ElkIndividual> individuals) {
		super(individuals);
		this.structuralHashCode = ElkObjectImpl.computeCompositeHash(
				constructorHash_, individuals);
	}

	@Override
	public String toString() {
		return buildFssString("SameIndividual");
	}

	public boolean structuralEquals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkSameIndividualAxiom) {
			return elkObjects.equals(((ElkSameIndividualAxiom) object)
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
