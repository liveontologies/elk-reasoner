package org.semanticweb.elk.reasoner.indexing.model;

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

import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.util.collections.entryset.Entry;
import org.semanticweb.elk.util.collections.entryset.GenericStructuralObject;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * A {@link ModifiableIndexedObjectProperty} that can be used for memoization
 * (caching).
 * 
 * @author "Yevgeny Kazakov"
 */
public interface CachedIndexedObjectProperty
		extends ModifiableIndexedObjectProperty, CachedIndexedPropertyChain,
		CachedIndexedEntity, GenericStructuralObject<CachedIndexedObjectProperty>,
		Entry<CachedIndexedObjectProperty> {

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		CachedIndexedObjectProperty getIndexedObjectProperty(
				ElkObjectProperty elkObjectProperty);

	}
	
	/**
	 * A filter for mapping objects
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Filter {

		CachedIndexedObjectProperty filter(CachedIndexedObjectProperty element);

	}
		
	static int structuralHashCode(ElkObjectProperty elkEntity) {
		return HashGenerator.combinedHashCode(CachedIndexedObjectProperty.class,
				elkEntity.getIri());
	}

	@Override
	default CachedIndexedObjectProperty structuralEquals(Object other) {
		if (this == other) {
			return this;
		}
		if (other instanceof CachedIndexedObjectProperty) {
			CachedIndexedObjectProperty secondEntry = (CachedIndexedObjectProperty) other;
			if (getElkEntity().getIri()
					.equals(secondEntry.getElkEntity().getIri()))
				return secondEntry;
		}
		// else
		return null;
	}

}
