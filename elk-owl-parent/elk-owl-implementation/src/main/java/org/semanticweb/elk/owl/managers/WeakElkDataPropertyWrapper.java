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

import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.util.hashing.HashGenerator;

public class WeakElkDataPropertyWrapper extends WeakWrapper<ElkDataProperty> {

	public WeakElkDataPropertyWrapper(ElkDataProperty referent,
			ReferenceQueue<? super ElkDataProperty> q) {
		super(referent, q);
	}

	@Override
	protected int hashCode(ElkDataProperty referent) {
		return HashGenerator.combinedHashCode("ElkDataProperty", referent.getIri());
	}

	@Override
	protected boolean equal(ElkDataProperty referent, Object obj) {
		if (obj instanceof ElkDataProperty)
			return referent.getIri().equals(((ElkDataProperty) obj).getIri());
		return false;
	}

}
