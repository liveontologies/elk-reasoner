/**
 * 
 */
package org.semanticweb.elk.reasoner.tracing;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.Collections;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.ObjectPropertyInference;

/**
 * An object containing {@link ObjectPropertyInference}s, which can be used to
 * retrieve inferences producing a given {@link ObjectPropertyConclusion}.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public interface ObjectPropertyInferenceSet {

	/**
	 * @param conclusion
	 *            the {@link ObjectPropertyConclusion} for which return
	 *            {@link ObjectPropertyInference}s
	 * @return the {@link ObjectPropertyInference}s producing the given
	 *         {@link ObjectPropertyConclusion}
	 */
	public Iterable<? extends ObjectPropertyInference> getObjectPropertyInferences(
			ObjectPropertyConclusion conclusion);

	public final static ObjectPropertyInferenceSet EMPTY = new ObjectPropertyInferenceSet() {

		@Override
		public Iterable<? extends ObjectPropertyInference> getObjectPropertyInferences(
				ObjectPropertyConclusion conclusion) {
			return Collections.emptyList();
		}

	};

}
