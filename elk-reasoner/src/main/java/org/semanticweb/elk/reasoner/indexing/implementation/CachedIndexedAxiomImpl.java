package org.semanticweb.elk.reasoner.indexing.implementation;

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

import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedAxiomVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectVisitor;

/**
 * Implements {@link CachedIndexedAxiom}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of objects this object can be structurally equal to
 */
abstract class CachedIndexedAxiomImpl<T extends CachedIndexedAxiom<T>> extends
		CachedIndexedObjectImpl<T, CachedIndexedAxiom<?>> implements
		CachedIndexedAxiom<T> {

	CachedIndexedAxiomImpl(int structuralHash) {
		super(structuralHash);
	}

	@Override
	public final <O> O accept(IndexedObjectVisitor<O> visitor) {
		return accept((IndexedAxiomVisitor<O>) visitor);
	}

}
