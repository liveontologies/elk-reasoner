package org.semanticweb.elk.matching.subsumers;

/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkObject;

abstract class AbstractSubsumerElkObjectMatch<V extends ElkObject>
		extends AbstractSubsumerMatch implements SubsumerElkObjectMatch {

	private final V value_;

	AbstractSubsumerElkObjectMatch(V value) {
		this.value_ = value;
	}

	@Override
	public V getValue() {
		return value_;
	}

	@Override
	public final <O> O accept(SubsumerMatch.Visitor<O> visitor) {
		return accept((SubsumerElkObjectMatch.Visitor<O>) visitor);
	}

}
