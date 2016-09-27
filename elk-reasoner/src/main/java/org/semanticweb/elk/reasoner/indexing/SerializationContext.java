/*-
 * #%L
 * ELK Reasoner Core
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
package org.semanticweb.elk.reasoner.indexing;

import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObject;

public class SerializationContext {

	private final ElkObject.Factory elkFactory_;
	private final CachedIndexedObject.Factory indexedObjectFactory_;

	public SerializationContext(final ElkObject.Factory elkFactory,
			final CachedIndexedObject.Factory indexedObjectFactory) {
		this.elkFactory_ = elkFactory;
		this.indexedObjectFactory_ = indexedObjectFactory;
	}

	public ElkObject.Factory getElkFactory() {
		return elkFactory_;
	}

	public CachedIndexedObject.Factory getIndexedObjectFactory() {
		return indexedObjectFactory_;
	}

}
