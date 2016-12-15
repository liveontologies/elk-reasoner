/*-
 * #%L
 * ELK Reasoner Core
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
package org.semanticweb.elk.reasoner.entailments.impl;

import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;

abstract class AbstractEntailmentInference implements EntailmentInference {

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public int hashCode() {
		return EntailmentInferenceHasher.hashCode(this);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		// else
		if (o instanceof EntailmentInference) {
			return hashCode() == o.hashCode() && EntailmentInferenceEquality
					.equals(this, (EntailmentInference) o);
		}
		// else
		return false;
	}

}
