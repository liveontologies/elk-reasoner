package org.semanticweb.elk.reasoner.indexing.model;

import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;

/**
 * A {@link ModifiableIndexedDataHasValue} that can be used for memoization
 * (caching).
 * 
 * @author "Yevgeny Kazakov"
 */
public interface CachedIndexedDataHasValue extends
		ModifiableIndexedDataHasValue,
		CachedIndexedComplexClassExpression<CachedIndexedDataHasValue> {

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		CachedIndexedDataHasValue getIndexedDataHasValue(
				ElkDataHasValue elkDataHasValue);

	}
	
	/**
	 * A filter for mapping objects
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Filter {

		CachedIndexedDataHasValue filter(CachedIndexedDataHasValue element);

	}
	
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
