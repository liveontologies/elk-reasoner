package org.semanticweb.elk.reasoner.indexing.caching;

import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedDataHasValue;

public interface CachedIndexedDataHasValue extends
		ModifiableIndexedDataHasValue,
		CachedIndexedComplexClassExpression<CachedIndexedDataHasValue> {

	static class Helper extends CachedIndexedObject.Helper {

		public static int structuralHashCode(ElkDataProperty property,
				ElkLiteral filler) {
			return combinedHashCode(CachedIndexedDataHasValue.class,
					property.getIri(), filler.getLexicalForm());
		}

		public static CachedIndexedDataHasValue structuralEquals(
				CachedIndexedDataHasValue first, Object second) {
			if (first == second) {
				return first;
			}
			if (second instanceof CachedIndexedDataHasValue) {
				CachedIndexedDataHasValue secondEntry = (CachedIndexedDataHasValue) second;
				if (first.getRelation().getIri()
						.equals(secondEntry.getRelation().getIri())
						&& first.getFiller()
								.getLexicalForm()
								.equals(secondEntry.getFiller()
										.getLexicalForm()))
					return secondEntry;
			}
			// else
			return null;
		}

	}

}
