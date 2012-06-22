/*
 * #%L
 * ELK Distribution
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
package org.semanticweb.elk.owlapi.examples;

import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

/**
 * Illustrates querying the ontology for the list of subclasses of a named
 * class.
 * 
 * @author Samuel Croset
 * 
 *         samuel.croset@gmail.com
 * 
 */
public class QueryingWithNamedClasses {

	final static String EL_ONTOLOGY = "http://www.samuelcroset.com/debug.owl";
	OWLOntologyManager manager;
	OWLOntology ontology;
	OWLReasonerFactory reasonerFactory;
	OWLReasoner reasoner;
	ShortFormProvider shortFormProvider;
	BidirectionalShortFormProvider mapper;

	public QueryingWithNamedClasses() throws OWLOntologyCreationException {
		// Traditional setup with the OWL-API
		manager = OWLManager.createOWLOntologyManager();
		IRI ontologyIri = IRI.create(EL_ONTOLOGY);
		ontology = manager.loadOntologyFromOntologyDocument(ontologyIri);

		System.out.println("Loaded ontology: " + ontology.getOntologyID());
		// But we use the Elk reasoner (add it to the classpath)
		reasonerFactory = new ElkReasonerFactory();
		reasoner = reasonerFactory.createReasoner(ontology);
		// IMPORTANT: Precompute the inferences beforehand, otherwise no results
		// will be returned
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		// Ontologies are not easy to query with the full name of concept, so we
		// keep only the interesting bit ( = shortform)
		shortFormProvider = new SimpleShortFormProvider();

		Set<OWLOntology> importsClosure = ontology.getImportsClosure();
		mapper = new BidirectionalShortFormProviderAdapter(manager,
				importsClosure, shortFormProvider);
	}

	public static void main(String[] args) throws OWLOntologyCreationException,
			ParserException {
		// Creation of a new Querier = Motor running the query
		System.out.println("Initialization of the querier...");

		QueryingWithNamedClasses querier = new QueryingWithNamedClasses();
		// Actual query:
		// "In our ontology, what are the subclasses of the named class MeatEater?"
		// It will work only if you use a reference to a class already present
		// in your ontology (named class).
		Set<OWLClass> results = querier.getSubClasses("MeatEater");
		// The result is the set of classes satisfying the query.
		for (OWLClass owlClass : results) {
			// Just iterates over it and print the name of the class
			System.out.println("Subclass: "
					+ querier.shortFormProvider.getShortForm(owlClass));
		}
	}

	public Set<OWLClass> getSubClasses(String expression)
			throws ParserException {
		// Convert the class expression (string) into an OWL class expression,
		// which is used to retrieved the named class.
		// In principle, this allows for parsing arbitrary class expressions in
		// OWL, not just named classes (for which a simple
		// OWLDataFactory.getOWLClass(..) would do. However, Elk currently
		// doesn't yet implement getSubClasses for class expressions.
		// It will be supported in a future release.
		OWLClassExpression classExpression = parseClassExpression(expression
				.trim());
		// The flag "true" means that we want to retrieve only the direct
		// subclasses. The flag set in "false" should retrieve the descendant
		// classes.
		NodeSet<OWLClass> subClasses = reasoner.getSubClasses(classExpression,
				true);
		// IMPORTANT: This method will stop the reasoning process and free the
		// Elk threads/workers.
		reasoner.dispose();

		return subClasses.getFlattened();
	}

	public OWLClassExpression parseClassExpression(String expression)
			throws ParserException {
		// Inspired from:
		// http://owlapi.svn.sourceforge.net/viewvc/owlapi/v3/trunk/examples/src/main/java/org/coode/owlapi/examples/dlquery/DLQueryParser.java?revision=1991&view=markup
		// Convert the class expression (string) into an OWL class expression
		OWLDataFactory dataFactory = ontology.getOWLOntologyManager()
				.getOWLDataFactory();
		ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(
				dataFactory, expression);
		OWLEntityChecker entityChecker = new ShortFormEntityChecker(mapper);

		parser.setDefaultOntology(ontology);
		parser.setOWLEntityChecker(entityChecker);

		return parser.parseClassExpression();
	}
}