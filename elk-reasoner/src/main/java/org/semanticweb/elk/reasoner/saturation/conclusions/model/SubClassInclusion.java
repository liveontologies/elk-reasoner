package org.semanticweb.elk.reasoner.saturation.conclusions.model;

import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;

/**
 * A {@link ClassConclusion} representing a derived subclass axiom between the
 * class expression represented by {@link #getDestination()} and the class
 * expression represented by {@link #getSubsumer()} For example, a
 * {@link SubClassInclusion} with {@link #getDestination()} = {@code :A} and
 * {@link #getSubsumer()} = {@code :B} represents {@code SubClassOf(:A :B)}.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 */
public interface SubClassInclusion extends ClassConclusion {

	/**
	 * @return the {@code IndexedClassExpression} corresponding to the
	 *         super-expression of the {@link ElkSubClassOfAxiom} represented by
	 *         this {@link SubClassInclusion}
	 * 
	 * @see ElkSubClassOfAxiom#getSuperClassExpression()
	 */
	public IndexedClassExpression getSubsumer();

	public <O> O accept(Visitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory
			extends
				SubClassInclusionComposed.Factory,
				SubClassInclusionDecomposed.Factory {

		// combined interface

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O>
			extends
				SubClassInclusionComposed.Visitor<O>,
				SubClassInclusionDecomposed.Visitor<O> {

		// combined interface

	}

}
