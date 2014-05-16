package org.semanticweb.elk.ore;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentTarget;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;


/**
 * A OWL API based reasoner wrapper that supports classification, satisfiability, consistency, and realisation.
 */
public class OREv2ReasonerWrapper {
	private OWLOntology mOntology = null;
	private String mLoadReasonerFactoryClass = null;
	private String mErrorOutputFileString = null;
	private boolean mErrorFileAppend = false;
	
	/**
	 * Constructor for the reasoner wrapper
	 * @param ontology	OWL Ontology
	 */
	public OREv2ReasonerWrapper(OWLOntology ontology, String reasonerFactoryClass, String errorOutputFileString) {
		this.mOntology = ontology;
		this.mLoadReasonerFactoryClass = reasonerFactoryClass;
		this.mErrorOutputFileString = errorOutputFileString;
	}
	
	
	protected void writeOutput(String outputString) {		
		System.out.println(outputString);
	}
	
	protected void writeError(String errorString) {		
		FileWriter mErrorFileWriter = null;
		try {
			mErrorFileWriter = new FileWriter(mErrorOutputFileString, mErrorFileAppend);			
			mErrorFileWriter.write(errorString + "\n");
			mErrorFileWriter.close();
			mErrorFileAppend = true;
		} catch (IOException e) {
		}
	}
	
	/**
	 * Classify the given ontology
	 * @return Set of all inferred atomic subsumptions
	 */
	public Set<OWLAxiom> classify() {
		InferredSubClassAxiomGenerator subClassGenerator = new InferredSubClassAxiomGenerator();
		InferredEquivalentClassAxiomGenerator equivClassGenerator = new InferredEquivalentClassAxiomGenerator();
		OWLOntologyManager manager = mOntology.getOWLOntologyManager();
		
		long start = System.currentTimeMillis();
		
		Set<OWLAxiom> resultAxioms = new HashSet<OWLAxiom>(); 
		OWLReasoner reasoner = createReasoner();
		try {
			Set<OWLSubClassOfAxiom> subClassAxioms = subClassGenerator.createAxioms(manager, reasoner);
			Set<OWLEquivalentClassesAxiom> equivClassAxioms = equivClassGenerator.createAxioms(manager, reasoner);
			resultAxioms.addAll(subClassAxioms);
			resultAxioms.addAll(equivClassAxioms);
		} catch (InconsistentOntologyException e) {		
			OWLDataFactory factory = manager.getOWLDataFactory();
			resultAxioms.add(factory.getOWLSubClassOfAxiom(factory.getOWLThing(), factory.getOWLNothing()));
		} catch (Exception e) {
			writeError(e.getStackTrace().toString());
		}
		
		long end = System.currentTimeMillis();
		
		writeOutput("Operation time: " + (end-start));
		return resultAxioms;
	}
	
	
	
	/**
	 * Realise the given ontology
	 * @return Set of all inferred types
	 */
	@SuppressWarnings("unchecked")
	public Set<OWLAxiom> realise() {
		InferredClassAssertionAxiomGenerator classAssertionGenerator = new InferredClassAssertionAxiomGenerator();
		OWLOntologyManager manager = mOntology.getOWLOntologyManager();
		
		long start = System.currentTimeMillis();
		
		OWLReasoner reasoner = createReasoner();
		Set<? extends OWLAxiom> resultAxioms = null;
		try {
			resultAxioms = classAssertionGenerator.createAxioms(manager, reasoner);
		} catch (InconsistentOntologyException e) {		
			OWLDataFactory factory = manager.getOWLDataFactory();
			resultAxioms = Collections.singleton(factory.getOWLSubClassOfAxiom(factory.getOWLThing(), factory.getOWLNothing()));
		} catch (Exception e) {
			writeError(e.getStackTrace().toString());
		}
		
		long end = System.currentTimeMillis();
		
		writeOutput("Operation time: " + (end-start));
		return (Set<OWLAxiom>)resultAxioms;
	}	
	
	
	
	
	
	/**
	 * Check entailment for the given ontology
	 * @param axiom	OWL Axiom for which the entailment is checked
	 * @return true if the axiom is entailed by the ontology, false otherwise
	 */
	public boolean entails(OWLAxiom axiom) {
		
		long start = System.currentTimeMillis();
		
		OWLReasoner reasoner = createReasoner();
		boolean entailed = false;
		try {
			entailed = reasoner.isEntailed(axiom);
		} catch (InconsistentOntologyException e) {		
			entailed = true;
		} catch (Exception e) {
			writeError(e.getStackTrace().toString());
		}
		
		long end = System.currentTimeMillis();
		
		writeOutput("Operation time: " + (end-start));
		return entailed;
	}		
	
	
	
	/**
	 * Check if given ontology is consistent
	 * @return true if ontology is consistent, false otherwise
	 */
	public boolean isConsistent() {
		long start = System.currentTimeMillis();
		
		boolean result = createReasoner().isConsistent();
		
		long end = System.currentTimeMillis();
		
		writeOutput("Operation time: " + (end-start));
		return result;
	}
	
	

	
	
	/**
	 * Check if given concept is satisfiable
	 * @param c	Concept
	 * @return true if concept is satisfiable, false otherwise
	 */
	public boolean isSatisfiable(OWLClassExpression c) {
		long start = System.currentTimeMillis();
		
		boolean result = false;
		OWLReasoner reasoner = createReasoner();
		try {
			result = reasoner.isSatisfiable(c);
		} catch (InconsistentOntologyException e) {		
			result = false;
		} catch (Exception e) {
			writeError(e.getStackTrace().toString());
		}
		
		long end = System.currentTimeMillis();
		
		writeOutput("Operation time: " + (end-start));
		return result;
	}
	
	
	/**
	 * Create a reasoner instance.
	 * @return Reasoner instance
	 */
	public OWLReasoner createReasoner() {
		Class<?> factoryClass = null;
	    OWLReasonerFactory factory = null;
		try {
			factoryClass = Class.forName(mLoadReasonerFactoryClass);
		    factory = (OWLReasonerFactory) factoryClass.newInstance();
		    return factory.createReasoner(mOntology);
		} catch (Exception e) {
			writeError(e.getStackTrace().toString());
		}		
		return null;   
	}
	
	
	/**
	 * Serialize results as an OWL file
	 * @param results	Set of inferred axioms
	 * @param manager	OWL Ontology Manager
	 * @param ontDir	Ontology directory
	 * @return The file path to where the OWL file was saved
	 */
	public String serializeOntologyResults(Set<OWLAxiom> results, OWLOntologyManager manager, String outputFileString) {
		File outputFile = new File(outputFileString);
		if (outputFile.getParentFile() != null) {
			outputFile.getParentFile().mkdirs();
		}
		IRI iri = IRI.create("file:" + outputFile.getAbsolutePath());
		try {
			manager.saveOntology(manager.createOntology(results, iri), new OWLFunctionalSyntaxOntologyFormat(), new FileDocumentTarget(outputFile));
		} catch (Exception e) {
			writeError(e.getStackTrace().toString());
		}
		return iri.toString();
	}
	
	
	/**
	 * Serialize the specified string to the given file
	 * @param outFile	Output file path
	 * @param outputString	Output string
	 */
	public void serializeString(String outFile, String outputString) {
		File output = new File(outFile);
		if (output.getParentFile() != null) {
			output.getParentFile().mkdirs();
		}
		FileWriter outputWriter = null;
		try {
			outputWriter = new FileWriter(output.getAbsolutePath(), true);
			outputWriter.write(outputString + "\n");
			outputWriter.close();
		} catch (IOException e) {
			writeError(e.getStackTrace().toString());
		}
	}
	
	
	/**
	 * @param args
	 * @throws OWLOntologyCreationException 
	 */
	public static void main(String[] args) throws OWLOntologyCreationException {
		String factoryString = args[0];
		String operationString = args[1];
		String ontologyFileString = args[2];
		String outputFileString = args[3];
				
		System.out.println("Started " + operationString + " on " + ontologyFileString);
		File ontologyFile = new File(ontologyFileString);
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(ontologyFile.getAbsoluteFile());
		
		OREv2ReasonerWrapper reasonerWrapper = new OREv2ReasonerWrapper(ontology,factoryString,outputFileString+"_err");
		
		if (operationString.equalsIgnoreCase("satisfiability")) {
			OWLClass satClass = manager.getOWLDataFactory().getOWLClass(IRI.create(args[4]));
			reasonerWrapper.serializeString(outputFileString, satClass.getIRI().toString() + "," + reasonerWrapper.isSatisfiable(satClass));
			
		} else if (operationString.equalsIgnoreCase("consistency")) {
			reasonerWrapper.serializeString(outputFileString, "" + reasonerWrapper.isConsistent());
			
		} else if (operationString.equalsIgnoreCase("classification")) {
			Set<OWLAxiom> results = reasonerWrapper.classify();
			reasonerWrapper.serializeOntologyResults(results, manager, outputFileString);
			
		} else if (operationString.equalsIgnoreCase("realisation") || operationString.equalsIgnoreCase("realization")) {
			Set<OWLAxiom> results = reasonerWrapper.realise();
			reasonerWrapper.serializeOntologyResults(results, manager, outputFileString);
			
		} else if (operationString.equalsIgnoreCase("entailment")) {			
			String entailmentsOntologyFileString = args[4];
			File entailmentsOntologyFile = new File(entailmentsOntologyFileString);
			OWLOntologyManager managerEntailments = OWLManager.createOWLOntologyManager();
			OWLOntology entailmentsOntology = managerEntailments.loadOntologyFromOntologyDocument(entailmentsOntologyFile.getAbsoluteFile());
			boolean entailed = false;
			// test entailment for first axiom
			for (OWLAxiom axiom : entailmentsOntology.getAxioms()) {
				if (!axiom.isAnnotationAxiom() && axiom.getAxiomType() != AxiomType.DECLARATION) {
					entailed = reasonerWrapper.entails(axiom);
					break;
				}
			}
			reasonerWrapper.serializeString(outputFileString, "" +entailed);			
		}
		
		
		System.out.println("Completed " + operationString + " on " + ontologyFileString);
	}
}
