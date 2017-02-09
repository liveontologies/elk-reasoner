package org.semanticweb.elk.protege.proof;

/*-
 * #%L
 * ELK Reasoner Protege Plug-in
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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

import org.liveontologies.puli.ConvertedProofNode;
import org.liveontologies.puli.ConvertedProofStep;
import org.liveontologies.puli.ProofNode;
import org.liveontologies.puli.ProofStep;
import org.semanticweb.owlapi.model.OWLAxiom;

public class InlinedOwlProofStep extends ConvertedProofStep<OWLAxiom> {

	protected InlinedOwlProofStep(ProofStep<OWLAxiom> step) {
		super(getFlattenedStep(step));
	}

	@Override
	protected ConvertedProofNode<OWLAxiom> convert(ProofNode<OWLAxiom> node) {
		return new InlinedOwlProofNode(node);
	}

	private static ProofStep<OWLAxiom> getFlattenedStep(
			ProofStep<OWLAxiom> step) {
		ProofStep<OWLAxiom> result = InlinedClassInclusionHierarchyStep
				.convert(step);
		if (result == null) {
			result = InlinedPropertyInclusionHierarchyStep.convert(step);
		}
		if (result == null) {
			result = InlinedClassInclusionExistentialPropertyExpansionStep
					.convert(step);
		}
		if (result == null) {
			result = step;
		}
		return result;
	}

}
