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

/**
 * @author Yevgeny Kazakov
 *
 */
public class ElkReasonerFactory implements OWLReasonerFactory {

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasonerFactory#getReasonerName()
	 */
	public String getReasonerName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasonerFactory#createNonBufferingReasoner(org.semanticweb.owlapi.model.OWLOntology)
	 */
	public OWLReasoner createNonBufferingReasoner(OWLOntology ontology) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasonerFactory#createReasoner(org.semanticweb.owlapi.model.OWLOntology)
	 */
	public OWLReasoner createReasoner(OWLOntology ontology) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasonerFactory#createNonBufferingReasoner(org.semanticweb.owlapi.model.OWLOntology, org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration)
	 */
	public OWLReasoner createNonBufferingReasoner(OWLOntology ontology,
			OWLReasonerConfiguration config)
			throws IllegalConfigurationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasonerFactory#createReasoner(org.semanticweb.owlapi.model.OWLOntology, org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration)
	 */
	public OWLReasoner createReasoner(OWLOntology ontology,
			OWLReasonerConfiguration config)
			throws IllegalConfigurationException {
		// TODO Auto-generated method stub
		return null;
	}

}
