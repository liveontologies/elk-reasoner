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
import java.util.List;

import org.liveontologies.owlapi.proof.util.Inference;
import org.semanticweb.elk.owl.implementation.ElkObjectBaseFactory;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;

public class ElkInferenceAdapter implements Inference<ElkAxiom> {

	private static final ElkObject.Factory CONCLUSION_FACTORY_ = new ElkObjectBaseFactory();

	private final ElkInference elkInference_;

	public ElkInferenceAdapter(ElkInference elkInference) {
		this.elkInference_ = elkInference;
	}

	public ElkInference getElkInference() {
		return elkInference_;
	}

	@Override
	public String getName() {
		return elkInference_.getName();
	}

	@Override
	public ElkAxiom getConclusion() {
		return elkInference_.getConclusion(CONCLUSION_FACTORY_);
	}

	@Override
	public List<? extends ElkAxiom> getPremises() {
		int premiseCount = elkInference_.getPremiseCount();
		List<ElkAxiom> result = new ArrayList<ElkAxiom>(premiseCount);
		for (int i = 0; i < premiseCount; i++) {
			result.add(elkInference_.getPremise(i, CONCLUSION_FACTORY_));
		}
		return result;
	}

}
