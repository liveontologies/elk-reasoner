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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.semanticweb.elk.owl.implementation.ElkDataPropertyListRestrictionQualifiedImpl;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAsymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeDefinitionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDifferentIndividualsAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentDataPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkFunctionalDataPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkHasKeyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkInverseObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIrreflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNegativeDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSWRLRule;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.predefined.PredefinedElkPrefix;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;

/**
 * Abstract tests for Elk functional syntax parsers
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 */
public abstract class AbstractOwl2FunctionalSyntaxParseTest {

	/*-----------------
	 * Utility methods
	 ------------------*/

	protected InputStream getInputOntology(String fileName) {
		return getClass().getClassLoader().getResourceAsStream(fileName);
	}

	protected ElkTestAxiomProcessor parseOntology(InputStream input)
			throws Owl2ParseException {
		Owl2Parser parser = instantiateParser(input);
		ElkTestAxiomProcessor axiomCounter = new ElkTestAxiomProcessor();

		parser.accept(axiomCounter);

		return axiomCounter;
	}

	protected ElkTestAxiomProcessor parseOntology(String input)
			throws Owl2ParseException {
		Owl2Parser parser = instantiateParser(new StringReader(input));
		ElkTestAxiomProcessor axiomCounter = new ElkTestAxiomProcessor();

		parser.accept(axiomCounter);

		return axiomCounter;
	}

	protected static void setDefaultPrefixes(Owl2Parser parser) {
		for (PredefinedElkPrefix prefix : PredefinedElkPrefix.values())
			parser.declarePrefix(prefix);
	}

	/**
	 * This method checks that the parser created the correct number of axioms
	 * of each type (identified by the corresponding Java class name)
	 * 
	 * @param axiomTypeCounts
	 * @param checkAll
	 *            If set to true, the check will fail if the parser created
	 *            axioms of other types, in addition to those specified in the
	 *            count map
	 * @throws IOException
	 */
	protected static void checkAxiomTypeCounts(ElkTestAxiomProcessor processor,
			Map<Class<?>, Integer> axiomTypeCounts, boolean checkAll)
			throws IOException {
		boolean error = false;
		// parsed without errors, check the output
		for (Iterator<Entry<Class<?>, List<ElkAxiom>>> actualEntryIter = processor
				.getAxiomMapEntries().iterator(); actualEntryIter.hasNext();) {
			Map.Entry<Class<?>, List<ElkAxiom>> actualEntry = actualEntryIter
					.next();
			// check that the parser created the right number of axioms of each
			// type
			Integer expectedCount = getExpectedCount(axiomTypeCounts,
					actualEntry.getKey());

			if (expectedCount == null) {
				if (checkAll) {
					error = true;
					dumpAxioms(actualEntry.getValue());
					System.err.println("Unexpectedly parsed axioms");
				}
			} else if (expectedCount.intValue() != actualEntry.getValue()
					.size()) {
				error = true;
				dumpAxioms(actualEntry.getValue());
				System.err.println("Wrong number of axioms parsed. Expected "
						+ expectedCount + ", actual "
						+ actualEntry.getValue().size());
			}

			axiomTypeCounts.remove(actualEntry.getKey());
		}

		/*
		 * for (Map.Entry<Class<?>, Integer> expectedEntry :
		 * axiomTypeCounts.entrySet()) {
		 * System.err.println(expectedEntry.getValue() +
		 * " axiom(s) of the type " + expectedEntry.getKey() +
		 * " were not parsed"); error = true; }
		 */

		assertFalse("Parsing errors detected (see the output above)", error);
	}

	private static int getExpectedCount(Map<Class<?>, Integer> axiomTypeCounts,
			Class<?> actualType) {
		int count = 0;
		// TODO A bit slow, something like Trie would help here but who cares,
		// it's just a test
		for (Map.Entry<Class<?>, Integer> entry : axiomTypeCounts.entrySet()) {
			if (entry.getKey().isAssignableFrom(actualType)) {
				count += entry.getValue();
			}
		}

		return count;
	}

	private static void dumpAxioms(Iterable<? extends ElkAxiom> axioms) throws IOException {
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
	@Test
	public void testOWL2Primer() throws Exception {
		InputStream input = getInputOntology("owl2primer.owl");

		assertNotNull(input);

		ElkTestAxiomProcessor counter = parseOntology(input);
		Map<Class<?>, Integer> expectedCountMap = new HashMap<Class<?>, Integer>();

		expectedCountMap.put(ElkSubClassOfAxiom.class, 8);
		expectedCountMap.put(ElkEquivalentClassesAxiom.class, 11);
		expectedCountMap.put(ElkDisjointClassesAxiom.class, 2);
		expectedCountMap.put(ElkSubObjectPropertyOfAxiom.class, 4);
		expectedCountMap.put(ElkEquivalentObjectPropertiesAxiom.class, 1);
		expectedCountMap.put(ElkEquivalentDataPropertiesAxiom.class, 1);
		expectedCountMap.put(ElkDisjointObjectPropertiesAxiom.class, 2);
		expectedCountMap.put(ElkInverseObjectPropertiesAxiom.class, 1);
		expectedCountMap.put(ElkObjectPropertyDomainAxiom.class, 1);
		expectedCountMap.put(ElkObjectPropertyRangeAxiom.class, 1);
		expectedCountMap.put(ElkDataPropertyDomainAxiom.class, 1);
		expectedCountMap.put(ElkDataPropertyRangeAxiom.class, 1);

		expectedCountMap.put(ElkAnnotationAssertionAxiom.class, 1);

		expectedCountMap.put(ElkSymmetricObjectPropertyAxiom.class, 1);
		expectedCountMap.put(ElkAsymmetricObjectPropertyAxiom.class, 1);
		expectedCountMap.put(ElkReflexiveObjectPropertyAxiom.class, 1);
		expectedCountMap.put(ElkIrreflexiveObjectPropertyAxiom.class, 1);

		expectedCountMap.put(ElkFunctionalObjectPropertyAxiom.class, 1);
		expectedCountMap.put(ElkIrreflexiveObjectPropertyAxiom.class, 1);
		expectedCountMap.put(ElkInverseFunctionalObjectPropertyAxiom.class, 1);
		expectedCountMap.put(ElkTransitiveObjectPropertyAxiom.class, 1);
		expectedCountMap.put(ElkFunctionalDataPropertyAxiom.class, 1);

		expectedCountMap.put(ElkHasKeyAxiom.class, 1);
		expectedCountMap.put(ElkDatatypeDefinitionAxiom.class, 3);

		expectedCountMap.put(ElkClassAssertionAxiom.class, 9);
		expectedCountMap.put(ElkObjectPropertyAssertionAxiom.class, 1);
		expectedCountMap.put(ElkNegativeObjectPropertyAssertionAxiom.class, 2);
		expectedCountMap.put(ElkDataPropertyAssertionAxiom.class, 1);
		expectedCountMap.put(ElkNegativeDataPropertyAssertionAxiom.class, 1);

		expectedCountMap.put(ElkSameIndividualAxiom.class, 3);
		expectedCountMap.put(ElkDifferentIndividualsAxiom.class, 1);

		expectedCountMap.put(ElkDeclarationAxiom.class, 43);

		expectedCountMap.put(ElkSWRLRule.class, 3);

		checkAxiomTypeCounts(counter, expectedCountMap, false);
		assertEquals(111L, counter.getTotalAxiomCount());
	}

	@Test
	public void testComments() throws Owl2ParseException {
		String testString = "#comment at the beginning\r"
				+ "Prefix #comments are allowed here\n"
				+ "(#and here\r :#and here\n = #and here\r"
				+ "<http://www.example.org#>#the last # in <> is not a comment\n"
				+ " )#comment after axiom\n"
				+ "Prefix(rdfs:#comment\n#commbent\r=#comment#\n"
				+ "<http://www.w3.org/2000/01/rdf-schema#>#comment\n)"
				+ "Ontology(#comment\n <http://www.my.example.com/example># comment \n"
				+ "Declaration(#comment\n Class#comment\n(##\n#\n :Person ) ) \n"
				+ "AnnotationAssertion( rdfs:comment :Person\n"
				+ "\"Represents the set of#not a comment\n"
				+ "all \\\"people#not a comment\\\".\"#comment\r)\n"
				+ "# This is a comment \n"
				+ "SubClassOf( :Person # another comment: #this doesn't count: SubClassOf( :Person\n"
				+ ":Human) #This is another comment\n"
				+ ")# comment at the end";

		parseOntology(testString);
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
	public void testLiteralParsing() throws Owl2ParseException, IOException {
		String input = "Prefix ( rdfs: = <http://www.w3.org/2000/01/rdf-schema#> )\n"
				+ "Prefix ( a: = <http://www.example.org#> )\n"
				+ "Prefix ( xsd: = <http://www.w3.org/2001/XMLSchema#> )\n"
				+ "Ontology(<http://www.example.org/>\n"
				// Testing if literal parsing is ambiguous
				+ "Annotation(rdfs:comment \"String literal with language\"@en)\n"
				+ "Annotation(rdfs:comment \"String literal no language\")\n"
				+ "Annotation(rdfs:label \"Typed literal\"^^xsd:string)\n"
				+ ")";

		parseOntology(input);
	}

	/*
	 * Testing if DataSomeValuesFrom parsing could be ambiguous
	 */
	@Test
	public void testNaryDataSomeValuesFrom() throws Owl2ParseException {
		String input = "Prefix ( rdfs: = <http://www.w3.org/2000/01/rdf-schema#> )\n"
				+ "Prefix ( a: = <http://www.example.org#> )\n"
				+ "Prefix ( xsd: = <http://www.w3.org/2001/XMLSchema#> )\n"
				+ "Ontology(<http://www.example.org/>\n"
				+ "SubClassOf(a:2DFigure \n"
				+ "   DataSomeValuesFrom(a:hasWidth a:hasLength xsd:integer)\n"
				+ ")\n"
				+ "SubClassOf(a:2DFigure \n"
				+ "   DataAllValuesFrom(a:hasWidth a:hasLength xsd:integer)\n"
				+ ")\n" + ")";

		ElkTestAxiomProcessor counter = parseOntology(input);
		List<ElkAxiom> axioms = counter
				.getAxiomsForType(ElkSubClassOfAxiom.class);

		assertEquals(2, axioms.size());

		for (ElkAxiom axiom : axioms) {
			ElkSubClassOfAxiom sbAxiom = (ElkSubClassOfAxiom) axiom;

			assertTrue(sbAxiom.getSuperClassExpression() instanceof ElkDataPropertyListRestrictionQualifiedImpl);

			ElkDataPropertyListRestrictionQualifiedImpl superCE = (ElkDataPropertyListRestrictionQualifiedImpl) sbAxiom
					.getSuperClassExpression();

			assertEquals(2, superCE.getDataPropertyExpressions().size());
		}
	}

	@Test
	public void testEmptyPrefix() throws Owl2ParseException {
		String testString = "Prefix(:=<>)" + "Ontology("
				+ "Declaration(Class(:A))" + ")";

		parseOntology(testString);
	}

	@Test
	public void testQualifiedNamesInIris() throws Owl2ParseException {
		String testString = "Prefix(p: = <>)" + "Ontology("
				+ "SubClassOf(p:Class p:Ontology)" + ")";

		parseOntology(testString);
	}

	@Test
	public void testSWRL() throws Owl2ParseException {
		String testString = "Prefix(:=<www.example.org>) "
				+ "Ontology(<http://www.example.org#swrl-rule-test> " + "DLSafeRule( "
				+ "Body( " + "ClassAtom(:A Variable(:x)) " + ") " + "Head( "
				+ "ClassAtom(:B Variable(:x) ) ) ) )";

		parseOntology(testString);
	}
}