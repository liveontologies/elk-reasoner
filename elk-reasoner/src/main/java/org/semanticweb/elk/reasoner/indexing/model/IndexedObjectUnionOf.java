package org.semanticweb.elk.reasoner.indexing.model;

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;

/**
 * Represents occurrences of an {@link ElkObjectUnionOf} in an ontology.
 * 
 * @author "Yevgeny Kazakov"
 */
public interface IndexedObjectUnionOf extends IndexedClassExpression {

	/**
	 * @return the {@link IndexedClassExpression}s representing the disjuncts of
	 *         the {@link ElkObjectUnionOf} represented by this
	 *         {@link IndexedObjectUnionOf}.
	 * 
	 * @see IndexedObjectUnionOf#getDisjuncts()
	 */
	List<? extends IndexedClassExpression> getDisjuncts();

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {
		
		O visit(IndexedObjectUnionOf element);
		
	}

}
