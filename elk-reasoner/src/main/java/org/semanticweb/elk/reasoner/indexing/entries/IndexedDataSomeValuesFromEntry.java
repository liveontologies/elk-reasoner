/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
/*
 * Copyright 2012 Department of Computer Science, University of Oxford.
 *
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
 */
package org.semanticweb.elk.reasoner.indexing.entries;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataSomeValuesFrom;

/**
 *
 * @author Pospishyi Olexandr
 */
public class IndexedDataSomeValuesFromEntry<T, K extends IndexedDataSomeValuesFrom>
		extends IndexedClassExpressionEntry<T, K> {

	public IndexedDataSomeValuesFromEntry(K representative) {
		super(representative);
	}

	@Override
	public int computeHashCode() {
		return combinedHashCode(IndexedDataSomeValuesFromEntry.class,
				this.key.getProperty().getIri(),
				this.key.getFiller());
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof IndexedDataSomeValuesFromEntry<?, ?>) {
			IndexedDataSomeValuesFromEntry<?, ?> otherView = (IndexedDataSomeValuesFromEntry<?, ?>) other;
			return this.key.getProperty().equals(otherView.key.getProperty())
					&& this.key.getFiller().equals(otherView.key.getFiller());
		}
		return false;
	}
}
