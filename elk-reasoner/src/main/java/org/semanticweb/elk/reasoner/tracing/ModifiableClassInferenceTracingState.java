package org.semanticweb.elk.reasoner.tracing;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;

public interface ModifiableClassInferenceTracingState extends
		ClassInferenceTracingState {

	/**
	 * Assigns the given {@link ClassInference}s to the given
	 * {@link IndexedContextRoot}
	 * 
	 * @param inferenceOriginRoot
	 *            the origin {@link IndexedContextRoot} for which the
	 *            {@link ClassInference}s should be assigned
	 * 
	 * @param classInferences
	 *            the {@link ClassInference}s that should be assigned; the given
	 *            {@link IndexedContextRoot} should be the origin of all of them
	 * 
	 * @return the {@link ClassInferenceSet} that contain all the given
	 *         {@link ClassInference} that can be used in proofs (i.e., not
	 *         cyclic).
	 */
	public ClassInferenceSet setClassInferences(
			IndexedContextRoot inferenceOriginRoot,
			Iterable<? extends ClassInference> classInferences);

}
