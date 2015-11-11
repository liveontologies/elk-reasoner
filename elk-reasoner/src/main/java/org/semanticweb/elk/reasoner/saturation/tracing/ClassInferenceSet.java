/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

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

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;

/**
 * An object containing {@link ClassInference}s, which can be used to retrieve
 * inferences producing a given {@link ClassConclusion}.
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public interface ClassInferenceSet {

	/**
	 * @param conclusion
	 *            the {@link ClassConclusion} for which return {@link ClassInference}
	 *            s
	 * @return the {@link ClassInference}s producing the given
	 *         {@link ClassConclusion}
	 */
	public Iterable<? extends ClassInference> getClassInferences(
			ClassConclusion conclusion);

	public final static ClassInferenceSet EMPTY = new ClassInferenceSet() {

		@Override
		public Iterable<? extends ClassInference> getClassInferences(
				ClassConclusion conclusion) {
			return Collections.emptyList();
		}

	};

}
