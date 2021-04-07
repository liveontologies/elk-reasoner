package org.semanticweb.elk.reasoner.indexing.model;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2021 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.util.collections.entryset.Entry;
import org.semanticweb.elk.util.collections.entryset.GenericStructuralObject;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * A {@link ModifiableIndexedComplexPropertyChain} that can be used for
 * memoization (caching).
 * 
 * @author "Yevgeny Kazakov"
 */
public interface CachedIndexedComplexPropertyChain extends
		ModifiableIndexedComplexPropertyChain, CachedIndexedPropertyChain,
		GenericStructuralObject<CachedIndexedComplexPropertyChain>,
		Entry<CachedIndexedComplexPropertyChain> {

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		CachedIndexedComplexPropertyChain getIndexedComplexPropertyChain(
				ModifiableIndexedObjectProperty leftProperty,
				ModifiableIndexedPropertyChain rightProperty);

	}
	
	/**
	 * A filter for mapping objects
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Filter {
		
		CachedIndexedComplexPropertyChain filter(
				CachedIndexedComplexPropertyChain element);
		
	}
	
	static int structuralHashCode(ModifiableIndexedObjectProperty firstProperty,
			ModifiableIndexedPropertyChain suffixChain) {
		return HashGenerator.combinedHashCode(
				CachedIndexedComplexPropertyChain.class, firstProperty,
				suffixChain);
	}

	@Override
	default CachedIndexedComplexPropertyChain structuralEquals(Object second) {
		if (this == second) {
			return this;
		}
		if (second instanceof CachedIndexedComplexPropertyChain) {
			CachedIndexedComplexPropertyChain secondEntry = (CachedIndexedComplexPropertyChain) second;
			if (this.getFirstProperty().equals(secondEntry.getFirstProperty())
					&& this.getSuffixChain()
							.equals(secondEntry.getSuffixChain()))
				return secondEntry;
		}
		// else
		return null;
	}
	
}
