package org.semanticweb.elk.reasoner.saturation.conclusions.model;

import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;

/**
 * A {@link ClassConclusion} representing a derived subclass axiom between the
 * class expression represented by {@link #getSource()} and the existential
 * restriction on property expression represented by {@link #getRelation()} and
 * filler represented by {@link #getDestination()}. For example, a
 * {@link BackwardLink} with {@link #getSource()} = {@code :A},
 * {@link #getRelation()} = {@code :r} and {@link #getDestination()} =
 * {@code :B} represents {@code SubClassOf(:A ObjectSomeValuesFrom(:r :B))}.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public interface BackwardLink extends SubClassConclusion {

	public static final String NAME = "Backward Link";

	/**
	 * @return the property expression of the existential relation between
	 *         {@link #getSource()} and {@link #getDestination()}
	 */
	public IndexedObjectProperty getRelation();

	/**
	 * @return the representation of the concept from which the existential
	 *         restriction corresponding to this {@link BackwardLink} follows
	 */
	public IndexedContextRoot getSource();

	public <O> O accept(Visitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		BackwardLink getBackwardLink(IndexedContextRoot destination,
				IndexedObjectProperty relation, IndexedContextRoot source);

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		public O visit(BackwardLink conclusion);

	}

}
