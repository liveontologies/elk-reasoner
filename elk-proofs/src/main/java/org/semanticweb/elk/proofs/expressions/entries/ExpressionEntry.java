/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.entries;
/*
 * #%L
 * ELK Proofs Package
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

import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.util.collections.entryset.StrongKeyEntry;


/**
 * A wrapper around an {@link Expression} object which is convenient to store
 * and find in collections.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ExpressionEntry<K extends Expression> extends StrongKeyEntry<K, K> {

	public ExpressionEntry(K key) {
		super(key);
	}
	
	@Override
	public int computeHashCode() {
		return new StructuralEquivalenceHasher().hashCode(key);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		
		if (object == null || !(object instanceof ExpressionEntry<?>)) {
			return false;
		}
		
		return new StructuralEquivalenceChecker().equal(key, ((ExpressionEntry<?>) object).key);
	}
	
}
