package org.semanticweb.elk.owlapi;

import org.liveontologies.owlapi.proof.OWLProofNode;
import org.liveontologies.owlapi.proof.OWLProver;
import org.semanticweb.elk.owl.inferences.ElkInferenceSet;
import org.semanticweb.elk.owl.inferences.FlattenedElkInferenceSet;

/*
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

import org.semanticweb.elk.owl.interfaces.ElkClassAxiom;
import org.semanticweb.elk.owlapi.proofs.ElkOWLProofNode;
import org.semanticweb.elk.owlapi.wrapper.OwlClassAxiomConverterVisitor;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;

public class ElkProver extends DelegatingOWLReasoner<ElkReasoner>
		implements OWLProver {

	public ElkProver(ElkReasoner elkReasoner) {
		super(elkReasoner);
	}

	@Override
	public OWLProofNode getProof(OWLAxiom entailment)
			throws UnsupportedEntailmentTypeException {
		ReasonerConfiguration config = getDelegate().getConfigurationOptions();
		boolean flattenInferences = config.getParameterAsBoolean(
				ReasonerConfiguration.FLATTEN_INFERENCES);
		ElkClassAxiom elkAxiom = entailment
				.accept(OwlClassAxiomConverterVisitor.getInstance());
		ElkReasoner elkReasoner = getDelegate();
		ElkInferenceSet elkInferences = elkReasoner.getElkProofProvider()
				.getInferences(elkAxiom);
		if (flattenInferences) {
			elkInferences = new FlattenedElkInferenceSet(elkInferences);
		}
		return new ElkOWLProofNode(elkAxiom, elkInferences,
				elkReasoner.getElkObjectFactory());
	}

}
