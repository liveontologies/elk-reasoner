/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
/**
 * @author Yevgeny Kazakov, Jun 29, 2011
 */
package org.semanticweb.elk.owlapi;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.IllegalConfigurationException;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for the OWLAPI reasoner implementation of the ELK reasoner.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 */
public class ElkReasonerFactory implements OWLReasonerFactory {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ElkReasonerFactory.class);

	@Override
	public String getReasonerName() {
		LOGGER_.trace("getReasonerName()");
		return ElkReasonerFactory.class.getPackage().getImplementationTitle();
	}

	@Override
	public ElkReasoner createNonBufferingReasoner(OWLOntology ontology) {
		LOGGER_.trace("createNonBufferingReasoner(OWLOntology)");
		
		return createElkReasoner(ontology, false, null);
	}

	@Override
	public ElkReasoner createReasoner(OWLOntology ontology) {
		LOGGER_.trace("createReasoner(OWLOntology)");
		
		return createElkReasoner(ontology, true, null);
	}

	@Override
	public ElkReasoner createNonBufferingReasoner(OWLOntology ontology,
			OWLReasonerConfiguration config)
			throws IllegalConfigurationException {
		LOGGER_.trace("createNonBufferingReasoner(OWLOntology, OWLReasonerConfiguration)");
		
		return createElkReasoner(ontology, false, config);
	}

	@Override
	public ElkReasoner createReasoner(OWLOntology ontology,
			OWLReasonerConfiguration config)
			throws IllegalConfigurationException {
		LOGGER_.trace("createReasoner(OWLOntology, OWLReasonerConfiguration)");
		
		return createElkReasoner(ontology, true, config);
	}

	@SuppressWarnings("static-method")
	ElkReasoner createElkReasoner(OWLOntology ontology,
			boolean isBufferingMode, OWLReasonerConfiguration config)
			throws IllegalConfigurationException {
		LOGGER_.trace("createElkReasoner(OWLOntology, boolean, OWLReasonerConfiguration)");
		// here we check if the passed configuration also has ELK's parameters
		ElkReasonerConfiguration elkReasonerConfig;
		if (config != null) {
			if (config instanceof ElkReasonerConfiguration) {
				elkReasonerConfig = (ElkReasonerConfiguration) config;
			} else {
				elkReasonerConfig = new ElkReasonerConfiguration(config);
			}
		} else {
			elkReasonerConfig = new ElkReasonerConfiguration();
		}

		return new ElkReasoner(ontology, isBufferingMode, elkReasonerConfig);
	}
}
