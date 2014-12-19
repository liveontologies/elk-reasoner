package org.semanticweb.elk.reasoner.indexing.caching;

import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedDisjointnessAxiom;

public interface CachedIndexedDisjointnessAxiom extends
		ModifiableIndexedDisjointnessAxiom,
		CachedIndexedAxiom<CachedIndexedDisjointnessAxiom> {

	static class Helper extends CachedIndexedObject.Helper {

		public static int structuralHashCode(
				Set<? extends ModifiableIndexedClassExpression> inconsistentMembers,
				Set<? extends ModifiableIndexedClassExpression> disjointMembers) {
			return combinedHashCode(CachedIndexedDisjointnessAxiom.class,
					combinedHashCode(inconsistentMembers),
					combinedHashCode(disjointMembers));
		}

		public static CachedIndexedDisjointnessAxiom structuralEquals(
				CachedIndexedDisjointnessAxiom first, Object second) {
			if (first == second) {
				return first;
			}
			if (second instanceof CachedIndexedDisjointnessAxiom) {
				CachedIndexedDisjointnessAxiom secondEntry = (CachedIndexedDisjointnessAxiom) second;
				if (first.getDisjointMembers().equals(
						secondEntry.getDisjointMembers())
						&& first.getInconsistentMembers().equals(
								secondEntry.getInconsistentMembers()))
					return secondEntry;
			}
			// else
			return null;
		}

	}

}
