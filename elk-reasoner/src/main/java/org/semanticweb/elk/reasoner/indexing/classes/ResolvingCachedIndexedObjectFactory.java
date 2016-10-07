package org.semanticweb.elk.reasoner.indexing.classes;

import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedSubObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectCache;

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

/**
 * A {@link CachedIndexedObject.Factory} which can only create objects present
 * in the provided {@link ModifiableIndexedObjectCache}. If there is no
 * structurally equivalent object to the one that should be constructed,
 * {@code null} is returned.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
class ResolvingCachedIndexedObjectFactory
		extends DelegatingCachedIndexedObjectFactory {

	private final ModifiableIndexedObjectCache cache_;

	public ResolvingCachedIndexedObjectFactory(
			CachedIndexedObject.Factory baseFactory,
			ModifiableIndexedObjectCache cache) {
		super(baseFactory);
		this.cache_ = cache;
	}

	@Override
	<T extends CachedIndexedSubObject<T>> T filter(T input) {
		if (input == null) {
			return null;
		}
		// else
		return cache_.resolve(input);
	}

}
