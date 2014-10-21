package org.semanticweb.elk.reasoner.indexing.entries;
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubClassOfAxiom;

/**
 * Implements an equality view for instances of {@link IndexedSubClassOfAxiom}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            The type of the elements in the set where this entry is used
 * 
 * @param <K>
 *            the type of the wrapped indexed object used as the key of the
 *            entry
 */
public class IndexedReflexiveObjectPropertyAxiomEntry<T, K extends IndexedReflexiveObjectPropertyAxiom>
		extends IndexedAxiomEntry<T, K> {

	public IndexedReflexiveObjectPropertyAxiomEntry(K representative) {
		super(representative);
	}

	@Override
	public int computeHashCode() {
		return combinedHashCode(IndexedReflexiveObjectPropertyAxiomEntry.class,
				this.key.getProperty(), this.key.getProperty());
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof IndexedReflexiveObjectPropertyAxiomEntry<?, ?>) {
			IndexedReflexiveObjectPropertyAxiomEntry<?, ?> otherEntry = (IndexedReflexiveObjectPropertyAxiomEntry<?, ?>) other;
			return this.key.getProperty().equals(otherEntry.key.getProperty());
		}
		return false;
	}

}