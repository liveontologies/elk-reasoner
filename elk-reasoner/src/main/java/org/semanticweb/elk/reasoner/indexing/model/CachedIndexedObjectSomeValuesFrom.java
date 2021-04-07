package org.semanticweb.elk.reasoner.indexing.model;

import org.semanticweb.elk.util.collections.entryset.GenericStructuralObject;
import org.semanticweb.elk.util.hashing.HashGenerator;

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
 * A {@link ModifiableIndexedObjectSomeValuesFrom} that can be used for
 * memoization (caching).
 * 
 * @author "Yevgeny Kazakov"
 */
public interface CachedIndexedObjectSomeValuesFrom
		extends ModifiableIndexedObjectSomeValuesFrom,
		CachedIndexedComplexClassExpression,
		GenericStructuralObject<CachedIndexedObjectSomeValuesFrom> {

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		CachedIndexedObjectSomeValuesFrom getIndexedObjectSomeValuesFrom(
				ModifiableIndexedObjectProperty property,
				ModifiableIndexedClassExpression filler);

	}
	
	/**
	 * A filter for mapping objects
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Filter {

		CachedIndexedObjectSomeValuesFrom filter(
				CachedIndexedObjectSomeValuesFrom element);

	}
		
	static int structuralHashCode(ModifiableIndexedObjectProperty property,
			ModifiableIndexedClassExpression filler) {
		return HashGenerator.combinedHashCode(
				CachedIndexedObjectSomeValuesFrom.class, property, filler);
	}

	@Override
	default CachedIndexedObjectSomeValuesFrom structuralEquals(Object other) {
		if (this == other) {
			return this;
		}
		if (other instanceof CachedIndexedObjectSomeValuesFrom) {
			CachedIndexedObjectSomeValuesFrom secondEntry = (CachedIndexedObjectSomeValuesFrom) other;
			if (getProperty().equals(secondEntry.getProperty())
					&& getFiller().equals(secondEntry.getFiller()))
				return secondEntry;
		}
		// else
		return null;
	}

}
