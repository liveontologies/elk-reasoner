package org.semanticweb.elk.owl.inferences;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.liveontologies.owlapi.proof.util.Inference;
import org.liveontologies.owlapi.proof.util.InferenceSet;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;

public class ElkInferenceSetAdapter implements InferenceSet<ElkAxiom> {

	private final ElkInferenceSet elkInferenceSet_;

	public ElkInferenceSetAdapter(ElkInferenceSet elkInferenceSet) {
		this.elkInferenceSet_ = elkInferenceSet;
	}

	@Override
	public Collection<? extends Inference<ElkAxiom>> getInferences(
			ElkAxiom conclusion) {
		Collection<? extends ElkInference> elkInferences = elkInferenceSet_
				.get(conclusion);
		int inferenceCount = elkInferences.size();
		List<Inference<ElkAxiom>> result = new ArrayList<Inference<ElkAxiom>>(
				inferenceCount);
		for (ElkInference elkInference : elkInferences) {
			result.add(new ElkInferenceAdapter(elkInference));
		}
		return result;
	}

}
