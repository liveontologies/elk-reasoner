/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.indexing.implementation;

import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.modifiable.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedAxiomVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectVisitor;

/**
 * Implements {@link ModifiableIndexedAxiom}
 * 
 * @author "Yevgeny Kazakov"
 *
 */
abstract class ModifiableIndexedAxiomImpl extends ModifiableIndexedObjectImpl
		implements ModifiableIndexedAxiom {

	@Override
	public final <O> O accept(IndexedObjectVisitor<O> visitor) {
		return accept((IndexedAxiomVisitor<O>) visitor);
	}

	@Override
	public final boolean updateOccurrenceNumbers(ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {
		// positive and negative occurrences do not make sense for axioms
		return updateOccurrenceNumbers(index, increment.totalIncrement);
	}

	abstract boolean updateOccurrenceNumbers(ModifiableOntologyIndex index,
			int increment);

}
