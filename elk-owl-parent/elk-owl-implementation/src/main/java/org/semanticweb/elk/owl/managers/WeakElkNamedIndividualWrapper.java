/*
 * #%L
 * ELK OWL Model Implementation
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
package org.semanticweb.elk.owl.managers;

import java.lang.ref.ReferenceQueue;

import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.util.hashing.HashGenerator;

class WeakElkNamedIndividualWrapper extends WeakWrapper<ElkNamedIndividual> {

	WeakElkNamedIndividualWrapper(ElkNamedIndividual referent,
			ReferenceQueue<? super ElkNamedIndividual> q) {
		super(referent, q);
	}

	@Override
	protected int hashCode(ElkNamedIndividual referent) {
		return HashGenerator.combinedHashCode("ElkNamedIndividual", referent.getIri());
	}

	@Override
	protected boolean equal(ElkNamedIndividual referent, Object obj) {
		if (obj instanceof ElkNamedIndividual)
			return referent.getIri().equals(((ElkNamedIndividual) obj).getIri());
		return false;
	}

}
