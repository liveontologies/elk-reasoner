package org.semanticweb.elk.reasoner.indexing.model;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

/**
 * A {@link ModifiableIndexedPropertyChain} that can be used for memoization
 * (caching).
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public interface CachedIndexedPropertyChain
		extends ModifiableIndexedPropertyChain, CachedIndexedSubObject {

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory
			extends
				CachedIndexedComplexPropertyChain.Factory,
				CachedIndexedObjectProperty.Factory {

		// combined interface

	}
	
	/**
	 * A filter for mapping objects
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Filter
			extends
				CachedIndexedComplexPropertyChain.Filter,
				CachedIndexedObjectProperty.Filter {

		// combined interface

	}
	
	CachedIndexedPropertyChain accept(Filter filter);		

}
