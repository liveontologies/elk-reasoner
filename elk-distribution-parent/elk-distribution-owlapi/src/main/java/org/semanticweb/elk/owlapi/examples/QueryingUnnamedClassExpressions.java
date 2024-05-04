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

import java.io.File;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 * The current version of ELK does not support querying (retrieving subclasses,
 * superclasses, and instances) of unnamed (complex) class expressions. This
 * example demonstrates how to get around this by introducing a new name for the
 * desired complex class and querying for the new named class instead.
 * 
 * 
 * @author Frantisek Simancik
 * 
 */
public class QueryingUnnamedClassExpressions {

	public static void main(String[] args) throws OWLOntologyCreationException {
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		OWLDataFactory dataFactory = man.getOWLDataFactory();

		// Load your ontology.
		OWLOntology ont = man.loadOntologyFromOntologyDocument(new File(
				"c:/ontologies/ontology.owl"));

		// Create an ELK reasoner.
		OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(ont);

		// Create your desired query class expression. In this example we
		// will query ObjectIntersectionOf(A ObjectSomeValuesFrom(R B)).
		PrefixManager pm = new DefaultPrefixManager();
		pm.setDefaultPrefix("http://example.org/");
		OWLClass A = dataFactory.getOWLClass(":A", pm);
		OWLObjectProperty R = dataFactory.getOWLObjectProperty(":R", pm);
		OWLClass B = dataFactory.getOWLClass(":B", pm);
		OWLClassExpression query = dataFactory.getOWLObjectIntersectionOf(A,
				dataFactory.getOWLObjectSomeValuesFrom(R, B));

		// Create a fresh name for the query.
		OWLClass newName = dataFactory.getOWLClass(IRI.create("temp001"));
		// Make the query equivalent to the fresh class
		OWLAxiom definition = dataFactory.getOWLEquivalentClassesAxiom(newName,
				query);
		man.addAxiom(ont, definition);

		// Remember to either flush the reasoner after the ontology change
		// or create the reasoner in non-buffering mode. Note that querying
		// a reasoner after an ontology change triggers re-classification of
		// the whole ontology which might be costly. Therefore, if you plan
		// to query for multiple complex class expressions, it will be more
		// efficient to add the corresponding definitions to the ontology at
		// once before asking any queries to the reasoner.
		reasoner.flush();

		// You can now retrieve subclasses, superclasses, and instances of
		// the query class by using its new name instead.
		reasoner.getSubClasses(newName, true);
		reasoner.getSuperClasses(newName, true);
		reasoner.getInstances(newName, false);

		// After you are done with the query, you should remove the definition
		man.removeAxiom(ont, definition);

		// You can now add new definitions for new queries in the same way

		// After you are done with all queries, do not forget to free the
		// resources occupied by the reasoner
		reasoner.dispose();
	}
}
