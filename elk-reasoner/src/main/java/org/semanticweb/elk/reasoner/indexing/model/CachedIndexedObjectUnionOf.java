package org.semanticweb.elk.reasoner.indexing.model;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import java.util.List;

import org.semanticweb.elk.util.collections.entryset.GenericStructuralObject;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * A {@link ModifiableIndexedObjectUnionOf} that can be used for memoization
 * (caching).
 * 
 * @author "Yevgeny Kazakov"
 */
public interface CachedIndexedObjectUnionOf extends
		ModifiableIndexedObjectUnionOf, CachedIndexedComplexClassExpression,
		GenericStructuralObject<CachedIndexedObjectUnionOf> {

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		CachedIndexedObjectUnionOf getIndexedObjectUnionOf(
				List<? extends ModifiableIndexedClassExpression> disjuncts);

	}
	
	/**
	 * A filter for mapping objects
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Filter {

		CachedIndexedObjectUnionOf filter(CachedIndexedObjectUnionOf element);

	}
	
	static int structuralHashCode(
			List<ModifiableIndexedClassExpression> disjuncts) {
		return HashGenerator.combinedHashCode(CachedIndexedObjectUnionOf.class,
				disjuncts);
	}

	@Override
	default CachedIndexedObjectUnionOf structuralEquals(Object other) {
		if (this == other) {
			return this;
		}
		if (other instanceof CachedIndexedObjectUnionOf) {
			CachedIndexedObjectUnionOf secondEntry = (CachedIndexedObjectUnionOf) other;
			if (getDisjuncts().equals(secondEntry.getDisjuncts()))
				return secondEntry;
		}
		// else
		return null;
	}

}
