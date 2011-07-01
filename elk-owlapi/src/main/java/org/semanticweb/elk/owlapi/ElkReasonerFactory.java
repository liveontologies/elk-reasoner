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
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

/**
 * @author Yevgeny Kazakov
 * 
 */
public class ElkReasonerFactory implements OWLReasonerFactory {

	public String getReasonerName() {
		return getClass().getPackage().getImplementationTitle();
	}

	public OWLReasoner createNonBufferingReasoner(OWLOntology ontology) {
		return new ElkReasoner(ontology, false, null);
	}

	public OWLReasoner createReasoner(OWLOntology ontology) {
		return new ElkReasoner(ontology, true, null);
	}

	public OWLReasoner createNonBufferingReasoner(OWLOntology ontology,
			OWLReasonerConfiguration config)
			throws IllegalConfigurationException {
		ReasonerProgressMonitor progressMonitor = null;
		if (config != null)
			progressMonitor = config.getProgressMonitor();
		return new ElkReasoner(ontology, false, progressMonitor);
	}

	public OWLReasoner createReasoner(OWLOntology ontology,
			OWLReasonerConfiguration config)
			throws IllegalConfigurationException {
		return new ElkReasoner(ontology, true, config.getProgressMonitor());
	}

}
