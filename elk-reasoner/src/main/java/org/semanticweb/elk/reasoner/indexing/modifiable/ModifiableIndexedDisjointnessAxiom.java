package org.semanticweb.elk.reasoner.indexing.modifiable;

import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;

public interface ModifiableIndexedDisjointnessAxiom extends
		ModifiableIndexedCachedAxiom, IndexedDisjointnessAxiom {

	@Override
	public Set<? extends ModifiableIndexedClassExpression> getInconsistentMembers();

	@Override
	public Set<? extends ModifiableIndexedClassExpression> getDisjointMembers();

}
