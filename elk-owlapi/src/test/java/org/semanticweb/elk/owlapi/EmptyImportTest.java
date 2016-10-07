package org.semanticweb.elk.owlapi;
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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class EmptyImportTest {

	/**
	 * Testing loading of ontologies that have no axioms (but possibly import
	 * declarations).
	 * 
	 * @see <a
	 *      href="http://code.google.com/p/elk-reasoner/issues/detail?id=7">Issue 7<a>
	 * @throws OWLOntologyCreationException
	 * @throws URISyntaxException
	 */
	@Test
	public void testImport() throws OWLOntologyCreationException,
			URISyntaxException {

		OWLOntologyManager man = OWLManager.createOWLOntologyManager();

		// loading the root ontology
		OWLOntology root = loadOntology(man, "root.owl");

		// Create an ELK reasoner.
		OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(root);

		try {
			// statistics about the root ontology
			assertEquals(root.getAxiomCount(), 0);
			// all two ontologies should be in the closure
			assertEquals(root.getImportsClosure().size(), 2);
			// all axioms from two ontologies should be in the closure
			assertEquals(getAxioms(root).size(), 0);

			// reasoner queries -- all subclasses are there
			reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
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
				.getResource("empty_import").toURI();

		OWLOntologyIRIMapper iriMapper = new ThisIRIMapper(ontologyRoot.toString());
		
		man.setIRIMappers(Collections.singleton(iriMapper));

		final URI mainOntology = getClass().getClassLoader()
				.getResource("empty_import/" + name).toURI();

		return man.loadOntologyFromOntologyDocument(new File(mainOntology));

	}

	/**
	 * @return the list of the axioms from the import closure of the given
	 *         ontology
	 */
	private static List<OWLAxiom> getAxioms(OWLOntology main) {
		List<OWLAxiom> axioms = new ArrayList<OWLAxiom>();
		for (OWLOntology ont : main.getImportsClosure()) {
			axioms.addAll(ont.getAxioms());
		}
		return axioms;
	}

	/**
	 * A simple {@link OWLOntologyIRIMapper} that appends the provided root
	 * directory to the ontologyIRI and creates an owl extension
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	static class ThisIRIMapper implements OWLOntologyIRIMapper {

		private static final long serialVersionUID = -6313558704830529341L;
		final String root;

		ThisIRIMapper(String root) {
			this.root = root;
		}

		@Override
		public IRI getDocumentIRI(IRI ontologyIRI) {
			// and the prefix to the ontology
			return IRI.create(root + "/" + ontologyIRI.getShortForm() + ".owl");
		}
	}

}
