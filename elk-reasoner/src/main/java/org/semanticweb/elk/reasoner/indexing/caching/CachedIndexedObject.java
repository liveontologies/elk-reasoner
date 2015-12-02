package org.semanticweb.elk.reasoner.indexing.caching;

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

import java.util.List;

import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObject;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * A {@link ModifiableIndexedObject} that can be used for memoization (caching),
 * that is, reusing previously constructed structurally equal objects instead of
 * creating new ones. To implement memoization, the objects require structural
 * comparison (equality and hash functions). These methods are provided for
 * every type of {@link ModifiableIndexedObject} based on its fields using
 * helper methods.
 * 
 * @see IndexedObjectCache
 * 
 * @author "Yevgeny Kazakov"
 *
 * @param <T>
 *            the type of the {@link CachedIndexedObject}
 */
public interface CachedIndexedObject<T extends CachedIndexedObject<T>> extends
		ModifiableIndexedObject {

	/**
	 * @return {@code true} if this object is memoized (occurs in the cache)
	 */
	boolean occurs();

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory
			extends				
				CachedIndexedSubObject.Factory {

		// combined interface

	}
	
	/**
	 * A filter for mapping objects
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Filter extends CachedIndexedSubObject.Filter {

		// combined interface

	}
	
	/**
	 * @param filter
	 * @return the result of applying the filter to this object
	 */
	T accept(Filter filter);

	static class Helper {
		static int combinedHashCode(Object... objects) {
			return HashGenerator.combinedHashCode(objects);
		}

		static int combinedHashCode(List<?> objects) {
			return HashGenerator.combinedHashCode(objects);
		}
	}

}
