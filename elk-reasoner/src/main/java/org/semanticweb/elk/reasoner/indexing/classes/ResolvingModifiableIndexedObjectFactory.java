package org.semanticweb.elk.reasoner.indexing.classes;

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

import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectCache;
import org.semanticweb.elk.reasoner.indexing.model.StructuralIndexedSubObject;

/**
 * A {@link ModifiableIndexedObject.Factory} which can only create objects
 * present in the provided {@link ModifiableIndexedObjectCache} or (new) not
 * cacheable objects. If a created object is cacheable and there is no
 * structurally equivalent object in the provided
 * {@link ModifiableIndexedObjectCache}, {@code null} is returned.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public class ResolvingModifiableIndexedObjectFactory
		extends NullableModifiableIndexedObjectFactory
		implements ModifiableIndexedObject.Factory {

	public ResolvingModifiableIndexedObjectFactory(
			final ModifiableIndexedObjectCache cache) {
		super(new ModifiableIndexedObjectBaseFactory() {
			@Override
			protected <T extends StructuralIndexedSubObject<T>> T filter(T input) {
				return cache.resolve(input);
			}
		});

	}

}
