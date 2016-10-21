package org.semanticweb.elk.owl.inferences.rewriting;

/*-
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

import org.liveontologies.owlapi.proof.util.ProofStep;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;

/**
 * An abstract interface for transformations that flatten inferences, i.e.,
 * replace nested inferences with one. Each instance should be used for one
 * inference.
 * 
 * @author Yevgeny Kazakov
 *
 */
interface ElkInferenceFlattener {

	/**
	 * Performs rewriting of the inference associated with this object using the
	 * {@link ProofStep} that corresponds to this inference, i.e., the members
	 * of the conclusion and premises of the {@link ProofStep} are respectively
	 * conclusion and premises of this inference. This method should be called
	 * at most ones.
	 * 
	 * @param step
	 */
	void flatten(ProofStep<ElkAxiom> step);

}
