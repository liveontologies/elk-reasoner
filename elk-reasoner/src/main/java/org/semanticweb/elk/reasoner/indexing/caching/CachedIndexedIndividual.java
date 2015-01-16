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

import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedIndividual;

/**
 * A {@link ModifiableIndexedIndividual} that can be used for memoization
 * (caching).
 * 
 * @author "Yevgeny Kazakov"
 *
 * @param <T>
 *            the type of the {@link CachedIndexedIndividual}
 */
public interface CachedIndexedIndividual extends ModifiableIndexedIndividual,
		CachedIndexedClassExpression<CachedIndexedIndividual>,
		CachedIndexedClassEntity<CachedIndexedIndividual> {

	static class Helper extends CachedIndexedObject.Helper {

		public static int structuralHashCode(ElkNamedIndividual entity) {
			return combinedHashCode(CachedIndexedIndividual.class,
					entity.getIri());
		}

		public static CachedIndexedIndividual structuralEquals(
				CachedIndexedIndividual first, Object second) {
			if (first == second) {
				return first;
			}
			if (second instanceof CachedIndexedIndividual) {
				CachedIndexedIndividual secondEntry = (CachedIndexedIndividual) second;
				if (first.getElkEntity().getIri()
						.equals(secondEntry.getElkEntity().getIri()))
					return secondEntry;
			}
			// else
			return null;
		}

	}

}
