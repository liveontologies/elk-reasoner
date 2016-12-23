package org.semanticweb.elk.protege.proof;

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

import java.util.Collection;
import java.util.List;

import org.liveontologies.proof.util.ProofNode;
import org.liveontologies.proof.util.ProofStep;
import org.semanticweb.owlapi.model.OWLAxiom;

abstract class AbstractFlattenedInclusionHierarchyStep
		extends AbstractInlinedStep implements ProofStep<OWLAxiom> {

	AbstractFlattenedInclusionHierarchyStep(ProofStep<OWLAxiom> step) {
		super(step);
	}

	abstract boolean canConvert(ProofStep<OWLAxiom> step);

	@Override
	void process(ProofStep<OWLAxiom> step) {
		List<? extends ProofNode<OWLAxiom>> premises = step.getPremises();
		for (int i = 0; i < premises.size(); i++) {
			ProofNode<OWLAxiom> premise = premises.get(i);
			if (!process(premise)) {
				// inferences for the premise cannot be flattened
				addPremise(premise);
			}
		}
	}

	private boolean process(ProofNode<OWLAxiom> node) {
		Collection<? extends ProofStep<OWLAxiom>> steps = node.getInferences();
		if (steps.size() > 1) {
			// don't expand multiple inferences
			return false;
		}
		for (ProofStep<OWLAxiom> step : steps) {
			// just one step
			if (canConvert(step)) {
				process(step);
				return true;
			}
		}
		// else
		return false;
	}

}
