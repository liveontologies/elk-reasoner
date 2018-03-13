package org.semanticweb.elk.owlapi;

import org.liveontologies.owlapi.proof.OWLProverFactory;

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

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;

/**
 * An {@link OWLProverFactory} for creating {@link ElkProver}s
 * 
 * @author Yevgeny Kazakov
 */
public class ElkProverFactory implements OWLProverFactory {

	private final ElkReasonerFactory reasonerFactory_ = new ElkReasonerFactory();

	@Override
	public String getReasonerName() {
		return reasonerFactory_.getReasonerName();
	}

	@Override
	public ElkProver createNonBufferingReasoner(OWLOntology ontology) {
		return new ElkProver(
				reasonerFactory_.createNonBufferingReasoner(ontology));
	}

	@Override
	public ElkProver createReasoner(OWLOntology ontology) {
		return new ElkProver(reasonerFactory_.createReasoner(ontology));
	}

	@Override
	public ElkProver createNonBufferingReasoner(OWLOntology ontology,
			OWLReasonerConfiguration config) {
		return new ElkProver(
				reasonerFactory_.createNonBufferingReasoner(ontology, config));
	}

	@Override
	public ElkProver createReasoner(OWLOntology ontology,
			OWLReasonerConfiguration config) {
		return new ElkProver(reasonerFactory_.createReasoner(ontology, config));
	}

}
