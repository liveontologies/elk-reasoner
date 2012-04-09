/*
 * #%L
 * ELK OWL Model Implementation
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
package org.semanticweb.elk.owl.parsing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.elk.owl.implementation.ElkAsymmetricObjectPropertyAxiomImpl;
import org.semanticweb.elk.owl.implementation.ElkClassAssertionAxiomImpl;
import org.semanticweb.elk.owl.implementation.ElkDataPropertyAssertionAxiomImpl;
import org.semanticweb.elk.owl.implementation.ElkDataPropertyDomainAxiomImpl;
import org.semanticweb.elk.owl.implementation.ElkDataPropertyRangeAxiomImpl;
import org.semanticweb.elk.owl.implementation.ElkDifferentIndividualsAxiomImpl;
import org.semanticweb.elk.owl.implementation.ElkDisjointClassesAxiomImpl;
import org.semanticweb.elk.owl.implementation.ElkDisjointObjectPropertiesAxiomImpl;
import org.semanticweb.elk.owl.implementation.ElkEquivalentClassesAxiomImpl;
import org.semanticweb.elk.owl.implementation.ElkEquivalentDataPropertiesAxiomImpl;
import org.semanticweb.elk.owl.implementation.ElkEquivalentObjectPropertiesAxiomImpl;
import org.semanticweb.elk.owl.implementation.ElkFunctionalDataPropertyAxiomImpl;
import org.semanticweb.elk.owl.implementation.ElkFunctionalObjectPropertyAxiomImpl;
import org.semanticweb.elk.owl.implementation.ElkInverseFunctionalObjectPropertyAxiomImpl;
import org.semanticweb.elk.owl.implementation.ElkInverseObjectPropertiesAxiomImpl;
import org.semanticweb.elk.owl.implementation.ElkIrreflexiveObjectPropertyAxiomImpl;
import org.semanticweb.elk.owl.implementation.ElkNegativeDataPropertyAssertionAxiomImpl;
import org.semanticweb.elk.owl.implementation.ElkObjectPropertyAssertionAxiomImpl;
import org.semanticweb.elk.owl.implementation.ElkObjectPropertyDomainAxiomImpl;
import org.semanticweb.elk.owl.implementation.ElkObjectPropertyRangeAxiomImpl;
import org.semanticweb.elk.owl.implementation.ElkSameIndividualAxiomImpl;
import org.semanticweb.elk.owl.implementation.ElkSubClassOfAxiomImpl;
import org.semanticweb.elk.owl.implementation.ElkSubObjectPropertyOfAxiomImpl;
import org.semanticweb.elk.owl.implementation.ElkSymmetricObjectPropertyAxiomImpl;
import org.semanticweb.elk.owl.implementation.ElkTransitiveObjectPropertyAxiomImpl;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.iris.ElkPrefixDeclarations;
import org.semanticweb.elk.owl.iris.ElkPrefixDeclarationsImpl;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;

/**
 * Abstract tests for Elk functional syntax parsers
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public abstract class AbstractOwl2FunctionalSyntaxParseTest {

	/*-----------------
	 * Utility methods
	 ------------------*/
	
	protected InputStream getInputOntology(String fileName) {
		return getClass().getClassLoader().getResourceAsStream(fileName);
	}
	
	protected ElkTestAxiomProcessor parseOntology(InputStream input) throws Owl2ParseException {
		Owl2Parser parser = instantiateParser(input);
		ElkTestAxiomProcessor axiomCounter = new ElkTestAxiomProcessor();
		
		parser.parseOntology(axiomCounter);
		
		return axiomCounter;
	}
	
	protected ElkTestAxiomProcessor parseOntology(String input) throws Owl2ParseException {
		Owl2Parser parser = instantiateParser(new StringReader(input));
		ElkTestAxiomProcessor axiomCounter = new ElkTestAxiomProcessor();
		
		parser.parseOntology(axiomCounter);
		
		return axiomCounter;
	}
	
	protected void setDefaultPrefixes(Owl2Parser parser) {
		ElkPrefixDeclarations prefixDeclarations = new ElkPrefixDeclarationsImpl();
		
		prefixDeclarations.addOwlDefaultPrefixes();
		parser.setPrefixDeclarations(prefixDeclarations);
	}

	/**
	 * This method checks that the parser created the correct number of axioms of each type 
	 * (identified by the corresponding Java class name)
	 * 
	 * @param input
	 * @param axiomTypeCounts
	 * @param checkAll 	If set to true, the check will fail if the parser created axioms of other types, in addition to those
	 * 					specified in the count map
	 * @throws Exception
	 */
	protected void checkAxiomTypeCounts(ElkTestAxiomProcessor processor, Map<Class<?>, Integer> axiomTypeCounts, boolean checkAll) throws Exception {
		boolean error = false;
		//parsed without errors, check the output
		for (Iterator<Entry<Class<?>, Set<ElkAxiom>>> actualEntryIter = processor.getAxiomMapEntries().iterator(); actualEntryIter.hasNext();) {
			Map.Entry<Class<?>, Set<ElkAxiom>> actualEntry = actualEntryIter.next(); 
			//check that the parser created the right number of axioms of each type
			Integer expectedCount = axiomTypeCounts.get(actualEntry.getKey());
			
			if (expectedCount == null) {
				if (checkAll) {
					error = true;
					dumpAxioms(actualEntry.getValue());
					System.err.println("Unexpectedly parsed axioms");
				}
			}
			else if (expectedCount.intValue() != actualEntry.getValue().size()) {
				error = true;
				dumpAxioms(actualEntry.getValue());
				System.err.println("Wrong number of axioms parsed. Expected " + expectedCount + ", actual " + actualEntry.getValue().size());
			}
			
			axiomTypeCounts.remove(actualEntry.getKey());
		}
		
		for (Map.Entry<Class<?>, Integer> expectedEntry : axiomTypeCounts.entrySet()) {			
			System.err.println(expectedEntry.getValue() + " axioms of the type " + expectedEntry.getKey() + " were not parsed");
			error = true;
		}
		
		assertFalse("Parsing errors detected (see the output above)", error);
	}
	
	private void dumpAxioms(Set<ElkAxiom> axioms) throws IOException {
		StringBuilder builder = new StringBuilder();
		
		for (ElkAxiom axiom : axioms) {
			OwlFunctionalStylePrinter.append(builder, axiom);
			builder.append("\n");
		}
	
		System.err.println(builder.toString());
	}

	
	protected abstract Owl2Parser instantiateParser(InputStream stream);
	protected abstract Owl2Parser instantiateParser(Reader reader);
	
	
	/*-------------------------
	 * TESTS
	 -------------------------*/
	
	/*
	 * See if we can parse the OWL2 primer sample ontology
	 */
	@Ignore
	@Test
	public void testOWL2Primer() throws Exception {
		InputStream input = getInputOntology("owl2primer.owl");
		
		assertNotNull(input);
		
		ElkTestAxiomProcessor counter = parseOntology(input);
		Map<Class<?>, Integer> expectedCountMap = new HashMap<Class<?>, Integer>();
		
		expectedCountMap.put(ElkSubClassOfAxiomImpl.class, 8);
		expectedCountMap.put(ElkEquivalentClassesAxiomImpl.class, 12);
		expectedCountMap.put(ElkDisjointClassesAxiomImpl.class, 2);
		expectedCountMap.put(ElkSubObjectPropertyOfAxiomImpl.class, 4);
		expectedCountMap.put(ElkEquivalentObjectPropertiesAxiomImpl.class, 1);
		expectedCountMap.put(ElkEquivalentDataPropertiesAxiomImpl.class, 1);
		expectedCountMap.put(ElkDisjointObjectPropertiesAxiomImpl.class, 2);
		expectedCountMap.put(ElkInverseObjectPropertiesAxiomImpl.class, 1);
		expectedCountMap.put(ElkObjectPropertyDomainAxiomImpl.class, 1);
		expectedCountMap.put(ElkObjectPropertyRangeAxiomImpl.class, 1);
		expectedCountMap.put(ElkDataPropertyDomainAxiomImpl.class, 1);
		expectedCountMap.put(ElkDataPropertyRangeAxiomImpl.class, 1);
		
		expectedCountMap.put(ElkSymmetricObjectPropertyAxiomImpl.class, 1);
		expectedCountMap.put(ElkAsymmetricObjectPropertyAxiomImpl.class, 1);
		//expectedCountMap.put(ElkReflexiveObjectPropertyAxiom.class, 1);
		expectedCountMap.put(ElkIrreflexiveObjectPropertyAxiomImpl.class, 1);
		
		expectedCountMap.put(ElkFunctionalObjectPropertyAxiomImpl.class, 1);
		expectedCountMap.put(ElkIrreflexiveObjectPropertyAxiomImpl.class, 1);
		expectedCountMap.put(ElkInverseFunctionalObjectPropertyAxiomImpl.class, 1);
		expectedCountMap.put(ElkTransitiveObjectPropertyAxiomImpl.class, 1);
		expectedCountMap.put(ElkFunctionalDataPropertyAxiomImpl.class, 1);
		
		//expectedCountMap.put(ElkHasKeyAxiomImpl.class, 1);
		
		//TODO What's the deal with datatype definitions?
		
		expectedCountMap.put(ElkClassAssertionAxiomImpl.class, 9);
		expectedCountMap.put(ElkObjectPropertyAssertionAxiomImpl.class, 1);
		//expectedCountMap.put(ElkNegativeObjectPropertyAssertionAxiom.class, 2);
		expectedCountMap.put(ElkDataPropertyAssertionAxiomImpl.class, 1);
		expectedCountMap.put(ElkNegativeDataPropertyAssertionAxiomImpl.class, 1);
		
		expectedCountMap.put(ElkSameIndividualAxiomImpl.class, 3);
		expectedCountMap.put(ElkDifferentIndividualsAxiomImpl.class, 1);
		
		checkAxiomTypeCounts(counter, expectedCountMap, false);
	}
	
	
	@Test(expected = Owl2ParseException.class)
	public void testPrefixDeclarations() throws Owl2ParseException {
		String testString = "Ontology( <http://www.my.example.com/example>"
				+ "Declaration( Class( :Person ) )"
				+ "SubClassOf( :Person owl:Thing )" + ") ";
		
		parseOntology(testString);
		fail("Should have thrown Owl2ParseException");
	}
	
	@Test
	public void testObjectOneOf() throws Owl2ParseException {
		String testString = "Ontology(SubClassOf( <A> ObjectOneOf(<i>)))";
		
		parseOntology(testString);
	}
	
	@Test
	public void testOntologyDocument() throws Owl2ParseException {
		String testString = "Prefix ( rdfs: = <http://www.w3.org/2000/01/rdf-schema#> )"
				+ "Prefix ( xsd: = <http://www.w3.org/2001/XMLSchema#> )"
				+ "Ontology(<http://www.example.org/>"
				// Testing if literal parsing is ambiguous
				+ "Annotation(rdfs:comment \"String literal with langauge\"@en)"
				+ "Annotation(rdfs:comment \"String literal no language\")"
				+ "Annotation(rdfs:label \"Typed literal\"^^xsd:string)"
				// Testing if DataSomeValuesFrom parsing is ambiguous
				// + "SubClassOf(a:2DFigure \n"
				// + "   DataSomeValuesFrom(a:hasWidth a:hasLength xsd:integer)"
				// + ")\n"
				// + "SubClassOf(a:1DFigure "
				// + "   DataSomeValuesFrom(a:hasLength xsd:integer)"
				// + ")"
				+ ")";

		parseOntology(testString);
	}
	
	@Test
	public void testEmptyPrefix() throws Owl2ParseException {
		String testString = "Prefix(:=<>)" +
		"Ontology("
		+ "Declaration(Class(:A))"
		+ ")";
		
		parseOntology(testString);
	}
	
	@Test
	public void testQualifiedNamesInIris() throws Owl2ParseException {
		String testString = "Prefix(p: = <>)" +
				"Ontology("
				+ "SubClassOf(p:Class p:Ontology)"
				+ ")";

		parseOntology(testString);
	}
}