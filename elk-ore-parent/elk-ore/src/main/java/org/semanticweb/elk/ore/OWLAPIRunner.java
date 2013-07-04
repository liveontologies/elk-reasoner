package org.semanticweb.elk.ore;

/*
 * #%L
 * ELK ORE build
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;

/**
 * @author Rafael S. Goncalves <br/>
 *         Information Management Group (IMG) <br/>
 *         School of Computer Science <br/>
 *         University of Manchester <br/>
 *         <p>
 *         A simple OWL API based reasoner wrapper that supports classification,
 *         satisfiability and consistency checking.
 *         </p>
 */
public class OWLAPIRunner {
	private OWLOntology ont;
	private ThreadMXBean bean;
	private String errorLog;

	/**
	 * Constructor for a simple reasoner wrapper
	 * 
	 * @param ont
	 *            OWL Ontology
	 */
	public OWLAPIRunner(OWLOntology ont) {
		this.ont = ont;
		bean = ManagementFactory.getThreadMXBean();
		errorLog = "";
	}

	/**
	 * Classify ontology (transitive closure)
	 * 
	 * @return Set of all inferred atomic subsumptions
	 */
	public Set<OWLSubClassOfAxiom> classify() {
		InferredSubClassAxiomGenerator gen = new InferredSubClassAxiomGenerator();
		OWLOntologyManager man = ont.getOWLOntologyManager();

		long start = bean.getCurrentThreadCpuTime();
		long start_wc = System.nanoTime();

		Set<OWLSubClassOfAxiom> result = gen
				.createAxioms(man, createReasoner());

		long end = bean.getCurrentThreadCpuTime();
		long end_wc = System.nanoTime();

		result = prune(result);
		System.out.println("\tOperation time: " + (end_wc - start_wc)
				/ 1000000.0);
		System.out
				.println("\tOperation CPU time: " + (end - start) / 1000000.0);
		return result;
	}

	/**
	 * Check if given ontology is consistent
	 * 
	 * @return true if ontology is consistent, false otherwise
	 */
	public boolean isConsistent() {
		long start = bean.getCurrentThreadCpuTime();
		long start_wc = System.nanoTime();

		boolean result = createReasoner().isConsistent();

		long end = bean.getCurrentThreadCpuTime();
		long end_wc = System.nanoTime();

		System.out.println("\tOperation time: " + (end_wc - start_wc)
				/ 1000000.0);
		System.out
				.println("\tOperation CPU time: " + (end - start) / 1000000.0);
		return result;
	}

	/**
	 * Remove axioms of the type A => T
	 * 
	 * @param axioms
	 *            Set of axioms
	 * @return Updated set of axioms
	 */
	private Set<OWLSubClassOfAxiom> prune(Set<OWLSubClassOfAxiom> axioms) {
		Set<OWLSubClassOfAxiom> toRemove = new HashSet<OWLSubClassOfAxiom>();
		for (OWLSubClassOfAxiom ax : axioms) {
			if (ax.getSuperClass().equals(
					ont.getOWLOntologyManager().getOWLDataFactory()
							.getOWLThing()))
				toRemove.add(ax);
		}
		axioms.removeAll(toRemove);
		return axioms;
	}

	/**
	 * Check if given concept is satisfiable
	 * 
	 * @param c
	 *            Concept
	 * @return true if concept is satisfiable, false otherwise
	 */
	public boolean isSatisfiable(OWLClassExpression c) {
		long start = bean.getCurrentThreadCpuTime();
		long start_wc = System.nanoTime();

		boolean result = createReasoner().isSatisfiable(c);

		long end = bean.getCurrentThreadCpuTime();
		long end_wc = System.nanoTime();

		System.out.println("\tOperation time: " + (end_wc - start_wc)
				/ 1000000.0);
		System.out
				.println("\tOperation CPU time: " + (end - start) / 1000000.0);
		return result;
	}

	/**
	 * Create a reasoner instance. In this case, an instance of JFact.
	 * 
	 * @return Reasoner instance
	 */
	public OWLReasoner createReasoner() {
		return new ElkReasonerFactory().createNonBufferingReasoner(ont);
	}

	/**
	 * Serialize classifiation results as an OWL file
	 * 
	 * @param results
	 *            Set of inferred subsumptions
	 * @param man
	 *            OWL Ontology Manager
	 * @param ontDir
	 *            Ontology directory
	 * @param reasonerName
	 *            Reasoner name
	 * @return The file path to where the OWL file was saved
	 */
	@SuppressWarnings("unchecked")
	public String serializeClassificationResults(
			Set<? extends OWLAxiom> results, OWLOntologyManager man,
			String outFile) {
		File output = new File(outFile);
		output.getParentFile().mkdirs();
		IRI iri = IRI.create("file:" + output.getAbsolutePath());
		try {
			man.saveOntology(man.createOntology((Set<OWLAxiom>) results, iri),
					new OWLFunctionalSyntaxOntologyFormat(), iri);
		} catch (OWLOntologyStorageException e) {
			errorLog += e.getStackTrace();
		} catch (OWLOntologyCreationException e) {
			errorLog += e.getStackTrace();
		}
		return iri.toString();
	}

	/**
	 * Serialize the specified string to the given file
	 * 
	 * @param outFile
	 *            Output file path
	 * @param outputString
	 *            Output string
	 */
	public void serializeString(String outFile, String outputString) {
		File output = new File(outFile);
		output.getParentFile().mkdirs();
		FileWriter out;
		try {
			out = new FileWriter(output.getAbsolutePath(), true);
			out.write(outputString + "\n");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 * @throws OWLOntologyCreationException
	 */
	public static void main(String[] args) throws OWLOntologyCreationException {
		String op = args[0];
		String ontFile = args[1];
		String outFile = args[2];

		System.out.println("\tStarted " + op + " on " + ontFile);
		File f = new File(ontFile);
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		OWLOntology ont = man.loadOntologyFromOntologyDocument(f
				.getAbsoluteFile());
		OWLAPIRunner r = new OWLAPIRunner(ont);

		if (op.equalsIgnoreCase("sat")) {
			OWLClass c = man.getOWLDataFactory().getOWLClass(
					IRI.create(args[3]));
			r.serializeString(outFile,
					c.getIRI().toString() + "," + r.isSatisfiable(c));
		} else if (op.equalsIgnoreCase("consistency"))
			r.serializeString(outFile, "" + r.isConsistent());
		else if (op.equalsIgnoreCase("classification")) {
			Set<? extends OWLAxiom> results = r.classify();
			r.serializeClassificationResults(results, man, outFile);
		}

		if (!r.errorLog.equals("")) {
			String outDir = new File(outFile).getParent();
			if (outDir.endsWith(File.separator))
				outDir += File.separator;
			r.serializeString(outDir + "error.txt", r.errorLog);
		}

		System.out.println("\tCompleted " + op + " on " + ontFile);
	}
}