package org.semanticweb.elk.reasoner.indexing.modifiable;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ChainableSubsumerRule;
import org.semanticweb.elk.util.collections.chains.Chain;

public interface ModifiableIndexedClassExpression extends
		ModifiableIndexedObject, IndexedClassExpression,
		Comparable<ModifiableIndexedClassExpression> {

	/**
	 * @return the {@link Chain} view of all composition rules assigned to this
	 *         {@link IndexedClassExpression}; this is always not {@code null}.
	 *         This method can be used for convenient search and modification
	 *         (addition and deletion) of the rules using the methods of the
	 *         {@link Chain} interface without worrying about {@code null}
	 *         values.
	 */
	Chain<ChainableSubsumerRule> getCompositionRuleChain();

}
