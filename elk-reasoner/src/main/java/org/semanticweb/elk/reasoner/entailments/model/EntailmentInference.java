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
package org.semanticweb.elk.reasoner.entailments.model;

import org.liveontologies.proof.util.Inference;

/**
 * Instances of this interface explain how was some {@link Entailment} entailed.
 * This {@link Entailment} can be retrieved from {@link #getConclusion()}. If it
 * was entailed from other entailments, they can be obtained from
 * {@link #getPremises()}.
 * 
 * @author Peter Skocovsky
 */
public interface EntailmentInference extends Inference<Entailment> {

	<O> O accept(Visitor<O> visitor);

	public static interface Visitor<O>
			extends OntologyInconsistencyEntailmentInference.Visitor<O>,
			AxiomEntailmentInference.Visito<O> {
		// combined interface
	}

}
