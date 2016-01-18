package org.semanticweb.elk.reasoner.indexing.model;

import java.util.List;

/**
 * An {@link IndexedObjectUnionOf} that can be modified as a result of updating
 * the {@link ModifiableOntologyIndex} where this object is stored.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public interface ModifiableIndexedObjectUnionOf extends
		ModifiableIndexedClassExpression, IndexedObjectUnionOf {

	@Override
	List<? extends ModifiableIndexedClassExpression> getDisjuncts();
	
	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		ModifiableIndexedObjectUnionOf getIndexedObjectUnionOf(
				List<? extends ModifiableIndexedClassExpression> disjuncts);

	}
}
