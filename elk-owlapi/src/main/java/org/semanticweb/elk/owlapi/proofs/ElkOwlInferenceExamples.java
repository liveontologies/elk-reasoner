package org.semanticweb.elk.owlapi.proofs;

/*-
 * #%L
 * ELK OWL API Binding
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

import org.liveontologies.proof.util.Inference;
import org.liveontologies.proof.util.InferenceExampleProvider;
import org.semanticweb.elk.owl.inferences.ElkInferenceExamples;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;

public class ElkOwlInferenceExamples
		implements InferenceExampleProvider<OWLAxiom> {

	private final InferenceExampleProvider<ElkAxiom> elkInferenceExamples_ = new ElkInferenceExamples();

	@Override
	public Inference<OWLAxiom> getExample(Inference<OWLAxiom> inference) {
		if (inference instanceof ElkOwlInference) {
			return new ElkOwlInference(elkInferenceExamples_.getExample(
					((ElkOwlInference) inference).getElkInference()));

		}
		// else
		return null;
	}

}
