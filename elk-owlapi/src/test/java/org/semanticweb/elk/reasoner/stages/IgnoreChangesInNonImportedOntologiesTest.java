package org.semanticweb.elk.reasoner.stages;

/*
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 * A simple test to check that changing some irrelevant ontology does not
 * invalidate the change loading stage inside the reasoner.
 * 
 * TODO Think how to move this test to the owlapi package and still be able to
 * test that change loading isn't reset
 * 
 * @author Pavel Klinov
 * 
 */
public class IgnoreChangesInNonImportedOntologiesTest {

	/**
	 * Testing correctness of the reasoner with respect to ontology changes
	 */
	@Test
	public void ignoreChangesInNonImportedOntologies() throws Exception {

		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		OWLDataFactory dataFactory = man.getOWLDataFactory();

		// set up resolution of prefixes
		DefaultPrefixManager pm = new DefaultPrefixManager(
				"http://www.example.com/main#");
		pm.setPrefix("A:", "http://www.example.com/A#");
		pm.setPrefix("B:", "http://www.example.com/B#");

		OWLClass extA = dataFactory.getOWLClass("A:A", pm);
		OWLClass extB = dataFactory.getOWLClass("B:B", pm);

		// loading the root ontology
		OWLOntology root = loadOntology(man, "root.owl");

		// Create an ELK reasoner.
		ElkReasoner reasoner = (ElkReasoner) new ElkReasonerFactory()
				.createReasoner(root);

		try {
			OWLOntology nonImported = loadOntology(man, "nonImported.owl");

			OWLAxiom axiom = dataFactory.getOWLSubClassOfAxiom(extA, extB);
			man.removeAxiom(nonImported, axiom);
			reasoner.flush();

			AbstractReasonerState state = (AbstractReasonerState) reasoner
					.getInternalReasoner();

			assertTrue(state.stageManager.changesLoadingStage.isCompleted);
		} finally {
			reasoner.dispose();
		}
	}

	/**
	 * Loading ontologies from the test resources
	 * 
	 * @param man
	 * @param name
	 * @return the loaded ontology
	 * @throws URISyntaxException
	 * @throws OWLOntologyCreationException
	 */
	private OWLOntology loadOntology(OWLOntologyManager man, String name)
			throws URISyntaxException, OWLOntologyCreationException {

		final URI ontologyRoot = getClass().getClassLoader()
				.getResource("ontologies").toURI();

		man.addIRIMapper(new ThisIRIMapper(ontologyRoot.toString()));

		final URI mainOntology = getClass().getClassLoader()
				.getResource("ontologies/" + name).toURI();

		return man.loadOntologyFromOntologyDocument(new File(mainOntology));

	}

	/**
	 * A simple {@link OWLOntologyIRIMapper} that appends the provided root
	 * directory to the ontologyIRI and creates an owl extension
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	static class ThisIRIMapper implements OWLOntologyIRIMapper {

		final String root;

		ThisIRIMapper(String root) {
			this.root = root;
		}

		@Override
		public IRI getDocumentIRI(IRI ontologyIRI) {
			// and the prefix to the ontology
			return IRI.create(root + "/" + ontologyIRI + ".owl");
		}
	}
}
