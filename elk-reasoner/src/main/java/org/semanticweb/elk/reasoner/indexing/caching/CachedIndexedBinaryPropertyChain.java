package org.semanticweb.elk.reasoner.indexing.caching;

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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedBinaryPropertyChain;

public interface CachedIndexedBinaryPropertyChain extends
		ModifiableIndexedBinaryPropertyChain,
		CachedIndexedComplexPropertyChain<CachedIndexedBinaryPropertyChain> {

	static class Helper extends CachedIndexedObject.Helper {

		public static int structuralHashCode(IndexedObjectProperty leftProperty,
				IndexedPropertyChain rightProperty) {
			return combinedHashCode(CachedIndexedBinaryPropertyChain.class,
					leftProperty, rightProperty);
		}

		public static CachedIndexedBinaryPropertyChain structuralEquals(
				CachedIndexedBinaryPropertyChain first, Object second) {
			if (first == second) {
				return first;
			}
			if (second instanceof CachedIndexedBinaryPropertyChain) {
				CachedIndexedBinaryPropertyChain secondEntry = (CachedIndexedBinaryPropertyChain) second;
				if (first.getLeftProperty().equals(
						secondEntry.getLeftProperty())
						&& first.getRightProperty().equals(
								secondEntry.getRightProperty()))
					return secondEntry;
			}
			// else
			return null;
		}

	}

}
