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

import org.liveontologies.proof.util.ConvertedProofNode;
import org.liveontologies.proof.util.ConvertedProofStep;
import org.liveontologies.proof.util.ProofNode;
import org.semanticweb.owlapi.model.OWLAxiom;

public class InlinedOwlProofNode extends ConvertedProofNode<OWLAxiom> {

	public InlinedOwlProofNode(ProofNode<OWLAxiom> delegate) {
		super(delegate);
	}

	@Override
	final protected void convert(ConvertedProofStep<OWLAxiom> step) {
		convert(new InlinedOwlProofStep(step.getDelegate()));
	}

	void convert(InlinedOwlProofStep step) {
		super.convert(step);
	}

}
