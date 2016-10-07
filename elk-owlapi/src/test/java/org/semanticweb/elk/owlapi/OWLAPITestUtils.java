/*
 * #%L
 * ELK OWL API Binding
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
 * 
 */
package org.semanticweb.elk.owlapi;

import java.io.IOException;
import java.io.InputStream;

import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.stages.ReasonerStageExecutor;
import org.semanticweb.elk.reasoner.stages.RestartingStageExecutor;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyCreationIOException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * A collection of utility methods to be used in OWL API related tests
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class OWLAPITestUtils {

	public static ElkReasoner createReasoner(InputStream stream)
			throws IOException, Owl2ParseException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = null;

		try {
			ontology = manager.loadOntologyFromOntologyDocument(stream);
		} catch (OWLOntologyCreationIOException e) {
			throw new IOException(e);
		} catch (OWLOntologyCreationException e) {
			throw new Owl2ParseException(e);
		}

		return new ElkReasoner(ontology, false, new RestartingStageExecutor());
	}

	public static ElkReasoner createReasoner(OWLOntology ontology) {
		return new ElkReasoner(ontology, false, new RestartingStageExecutor());
	}
	
	public static ElkProver createProver(OWLOntology ontology) {
		return new ElkProver(createReasoner(ontology));
	}

	public static ElkReasoner createReasoner(OWLOntology ontology,
			ReasonerConfiguration config) {
		return new ElkReasoner(ontology, false,
				new ElkReasonerConfiguration(ElkReasonerConfiguration
						.getDefaultOwlReasonerConfiguration(), config));

	}

	public static ElkReasoner createReasoner(final OWLOntology ontology,
			final boolean isBufferingMode,
			final ReasonerStageExecutor stageExecutor) {
		return new ElkReasoner(ontology, isBufferingMode, stageExecutor);
	}

}
