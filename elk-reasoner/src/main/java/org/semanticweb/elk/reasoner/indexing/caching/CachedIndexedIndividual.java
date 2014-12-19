package org.semanticweb.elk.reasoner.indexing.caching;

import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedIndividual;

public interface CachedIndexedIndividual extends ModifiableIndexedIndividual,
		CachedIndexedClassExpression<CachedIndexedIndividual>,
		CachedIndexedClassEntity<CachedIndexedIndividual> {

	static class Helper extends CachedIndexedObject.Helper {

		public static int structuralHashCode(ElkNamedIndividual entity) {
			return combinedHashCode(CachedIndexedIndividual.class,
					entity.getIri());
		}

		public static CachedIndexedIndividual structuralEquals(
				CachedIndexedIndividual first, Object second) {
			if (first == second) {
				return first;
			}
			if (second instanceof CachedIndexedIndividual) {
				CachedIndexedIndividual secondEntry = (CachedIndexedIndividual) second;
				if (first.getElkEntity().getIri()
						.equals(secondEntry.getElkEntity().getIri()))
					return secondEntry;
			}
			// else
			return null;
		}

	}

}
