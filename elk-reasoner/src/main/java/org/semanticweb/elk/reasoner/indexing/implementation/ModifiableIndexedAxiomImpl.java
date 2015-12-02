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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObject;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedAxiom;

/**
 * Implements {@link ModifiableIndexedAxiom}
 * 
 * @author "Yevgeny Kazakov"
 *
 * @param <A>
 *            the type of the {@link ElkAxiom} from which this axiom originates
 */
abstract class ModifiableIndexedAxiomImpl<A extends ElkAxiom>
		extends
			ModifiableIndexedObjectImpl
		implements ModifiableIndexedAxiom {

	private final A originalAxiom_;

	ModifiableIndexedAxiomImpl(A originalAxiom) {
		this.originalAxiom_ = originalAxiom;
	}

	@Override
	public A getOriginalAxiom() {
		return originalAxiom_;
	}

	@Override
	public final <O> O accept(IndexedObject.Visitor<O> visitor) {
		return accept((IndexedAxiom.Visitor<O>) visitor);
	}

}
