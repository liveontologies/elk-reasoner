package org.semanticweb.elk.alc.saturation;

/*
 * #%L
 * ALC Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.junit.Test;
import org.semanticweb.elk.alc.indexing.hierarchy.ChangeIndexingProcessor;
import org.semanticweb.elk.alc.indexing.hierarchy.OntologyIndex;
import org.semanticweb.elk.alc.indexing.hierarchy.TestAxiomIndexerVisitor;
import org.semanticweb.elk.alc.loading.AxiomLoader;
import org.semanticweb.elk.alc.loading.ElkLoadingException;
import org.semanticweb.elk.alc.loading.Owl2StreamLoader;
import org.semanticweb.elk.alc.reasoner.Reasoner;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.parsing.Owl2ParserFactory;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;

public class SaturationTest {

	// logger for events

	private final Owl2ParserFactory parserFactory_ = new Owl2FunctionalStyleParserFactory();

	void testSaturation(String ontology, String expectedSaturation)
			throws ElkLoadingException {
		Reasoner reasoner = new Reasoner(new Owl2StreamLoader(parserFactory_,
				ontology));
		SaturationCheckingAxiomVisitor checker = new SaturationCheckingAxiomVisitor(
				reasoner);
		reasoner.forceLoading();
		OntologyIndex index = reasoner.getOntologyIndex();
		TestAxiomIndexerVisitor testIndexer = new TestAxiomIndexerVisitor(
				index, checker);
		AxiomLoader expectedAxiomLoader = new Owl2StreamLoader(parserFactory_,
				expectedSaturation);
		ElkAxiomProcessor expectedAxiomInserter = new ChangeIndexingProcessor(
				testIndexer);
		ElkAxiomProcessor dummyAxiomDeletor = new ElkAxiomProcessor() {
			@Override
			public void visit(ElkAxiom elkAxiom) {
				// does nothing
			}
		};
		try {
			expectedAxiomLoader.load(expectedAxiomInserter, dummyAxiomDeletor);
		} finally {
			expectedAxiomLoader.dispose();
			// clear interrupt status
			Thread.interrupted();
		}
	}

	@Test
	public void testAncestors() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:B :D)"//
						+ "SubClassOf(:C :D)"//
						+ ")",
				// Expected saturation:
				"Prefix(:=<>)"//
						+ "Ontology("//
						// Told
						+ "SubClassOf(:A :B)"//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:B :D)"//
						+ "SubClassOf(:C :D)"//
						// Inferred
						+ "SubClassOf(:A :A)"//
						+ "SubClassOf(:B :B)"//
						+ "SubClassOf(:C :C)"//
						+ "SubClassOf(:D :D)"//
						+ "SubClassOf(:A :D)"//
						+ ")"//
		);
	}

	@Test
	public void testConjunctions() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:A :D)"//
						+ "SubClassOf(ObjectIntersectionOf(:B :C :D) :BCD)"//
						+ ")",
				// Expected saturation:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:A :D)"//
						+ "SubClassOf(:A :BCD)"//
						+ ")");
	}

	@Test
	public void testConjunctionsComplex() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:A :D)"//
						+ "SubClassOf(ObjectIntersectionOf(:B :B) :BB)"//
						+ "SubClassOf(ObjectIntersectionOf(:C :C) :CC)"//
						+ "SubClassOf(ObjectIntersectionOf(:D :D) :DD)"//
						+ "SubClassOf(ObjectIntersectionOf(:B :C) :BC)"//
						+ "SubClassOf(ObjectIntersectionOf(:B :D) :BD)"//
						+ "SubClassOf(ObjectIntersectionOf(:C :B) :CB)"//
						+ "SubClassOf(ObjectIntersectionOf(:C :D) :CD)"//
						+ "SubClassOf(ObjectIntersectionOf(:D :C) :DC)"//
						+ "SubClassOf(ObjectIntersectionOf(:D :B) :DB)"//
						+ "SubClassOf(ObjectIntersectionOf(:B :C :D) :BCD)"//
						+ "SubClassOf(ObjectIntersectionOf(:B :D :C) :BDC)"//
						+ "SubClassOf(ObjectIntersectionOf(:C :B :D) :CBD)"//
						+ "SubClassOf(ObjectIntersectionOf(:C :D :B) :CDB)"//
						+ "SubClassOf(ObjectIntersectionOf(:D :B :C) :DBC)"//
						+ "SubClassOf(ObjectIntersectionOf(:D :C :B) :DCB)"//
						+ ")",//
				// Expected saturation:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(<A> <B>)"//
						+ "SubClassOf(<A> <C>)"//
						+ "SubClassOf(<A> <D>)"//
						+ "SubClassOf(<B> <BB>)"//
						+ "SubClassOf(<C> <CC>)"//
						+ "SubClassOf(<D> <DD>)"//
						+ "SubClassOf(<A> <BC>)"//
						+ "SubClassOf(<A> <BD>)"//
						+ "SubClassOf(<A> <CB>)"//
						+ "SubClassOf(<A> <CD>)"//
						+ "SubClassOf(<A> <DC>)"//
						+ "SubClassOf(<A> <DB>)"//
						+ "SubClassOf(<A> <BCD>)"//
						+ "SubClassOf(<A> <BDC>)"//
						+ "SubClassOf(<A> <CBD>)"//
						+ "SubClassOf(<A> <CDB>)"//
						+ "SubClassOf(<A> <DBC>)"//
						+ "SubClassOf(<A> <DCB>)"//
						+ ")");
	}

	@Test
	public void testDeclarationEquivalences() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "Declaration(Class(:A))"//
						+ "Declaration(Class(:B))"//
						+ "Declaration(Class(:C))"//
						+ "Declaration(Class(:D))"//
						+ "Declaration(Class(:E))"//
						+ "EquivalentClasses(:B :A)"//
						+ "EquivalentClasses(:C :D)"//
						+ ")",
				// Expected saturation:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :A)"//
						+ "SubClassOf(:A :B)"//
						+ "SubClassOf(:B :B)"//
						+ "SubClassOf(:B :A)"//
						+ "SubClassOf(:C :C)"//
						+ "SubClassOf(:C :D)"//
						+ "SubClassOf(:D :D)"//
						+ "SubClassOf(:D :C)"//
						+ "SubClassOf(:E :E)"//
						+ ")");
	}

	@Test
	public void testExistentials() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectSomeValuesFrom(:R :B))"//
						+ "EquivalentClasses(:B :C)"//
						+ "SubClassOf(ObjectSomeValuesFrom(:R :C) :D)"//
						+ ")",
				// Expected saturation:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :A)"//
						+ "SubClassOf(:A :D)"//
						+ "SubClassOf(:B :B)"//
						+ "SubClassOf(:B :C)"//
						+ "SubClassOf(:C :C)"//
						+ "SubClassOf(:C :B)"//
						+ "SubClassOf(:D :D)"//
						+ ")");
	}

	@Test
	public void testCyclicExistentialsSimple() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectSomeValuesFrom(:R :A))"//
						+ "SubClassOf(:A :B)"//
						+ "SubClassOf(ObjectSomeValuesFrom(:R :B) :C)"//
						+ ")",
				// Expected saturation:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//
						+ "SubClassOf(:A :C)"//
						+ ")");
	}

	@Test
	public void testCyclicExistentialsComplex() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectSomeValuesFrom(:R :B))"//
						+ "SubClassOf(:B :C)"//
						+ "SubClassOf(:C ObjectSomeValuesFrom(:R :A))"//
						+ "SubClassOf(:C ObjectSomeValuesFrom(:R :B))"//
						+ "SubClassOf(ObjectSomeValuesFrom(:R :C) :D)"//
						+ "SubClassOf(ObjectSomeValuesFrom(:R :D) :E)"//
						+ ")",
				// Expected saturation:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :A)"//
						+ "SubClassOf(:A :D)"//
						+ "SubClassOf(:A :E)"//
						+ "SubClassOf(:B :B)"//
						+ "SubClassOf(:B :C)"//
						+ "SubClassOf(:B :D)"//
						+ "SubClassOf(:B :E)"//
						+ "SubClassOf(:C :C)"//
						+ "SubClassOf(:C :D)"//
						+ "SubClassOf(:C :E)"//
						+ "SubClassOf(:D :D)"//
						+ "SubClassOf(:E :E)"//
						+ ")");
	}

	@Test
	public void testReflexiveExistential() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectSomeValuesFrom(:R :A))"//
						+ "SubClassOf(:B ObjectSomeValuesFrom(:R :A))"//
						+ "SubClassOf(ObjectSomeValuesFrom(:R :A) :C)"//
						+ ")",
				// Expected saturation:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :A)"//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:B :B)"//
						+ "SubClassOf(:B :C)"//
						+ "SubClassOf(:C :C)"//
						+ ")");
	}

	@Test
	public void testInconsistentConjunction() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(ObjectIntersectionOf(:B :C) owl:Nothing)"//
						+ ")",
				// Expected saturation:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A owl:Nothing)"//
						+ "SubClassOf(:B :B)"//
						+ "SubClassOf(:C :C)"//
						+ ")");
	}

	@Test
	public void testDisjunction() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectUnionOf(:B :C))"//
						+ "SubClassOf(:B :D)"//
						+ "SubClassOf(:C :D)"//
						+ ")",
				// Expected saturation:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :A)"//
						+ "SubClassOf(:A :D)"//
						+ "SubClassOf(:B :B)"//
						+ "SubClassOf(:B :D)"//
						+ "SubClassOf(:C :C)"//
						+ "SubClassOf(:C :D)"//
						+ "SubClassOf(:D :D)"//
						+ ")");
	}

	@Test
	public void testDisjunctionBacktracking() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectUnionOf(:B :C))"//
						+ "SubClassOf(:B owl:Nothing)"//
						+ "SubClassOf(:C ObjectUnionOf(:D :E))"//
						+ "SubClassOf(:E owl:Nothing)"//
						+ ")",
				// Expected saturation:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :A)"//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:A :D)"//
						+ "SubClassOf(:B owl:Nothing)"//
						+ "SubClassOf(:C :C)"//
						+ "SubClassOf(:C :D)"//
						+ "SubClassOf(:E owl:Nothing)"//
						+ ")");
	}

	@Test
	public void testContradictionExistential() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectSomeValuesFrom(:R :B))"//
						+ "SubClassOf(:B owl:Nothing)"//
						+ ")",
				// Expected saturation:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:B owl:Nothing)"//
						+ "SubClassOf(:A owl:Nothing)"//
						+ ")");
	}

	@Test
	public void testContradictionExistentialClashPropagation()
			throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectUnionOf(:B :C))"//
						+ "SubClassOf(:B ObjectSomeValuesFrom(:R ObjectIntersectionOf(:D :D)))"//
						+ "SubClassOf(:C ObjectSomeValuesFrom(:S ObjectIntersectionOf(:D :D)))"//
						+ "SubClassOf(:D owl:Nothing)"//
						+ ")",
				// Expected saturation:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:B owl:Nothing)"//
						+ "SubClassOf(:C owl:Nothing)"//
						+ "SubClassOf(:A owl:Nothing)"//
						+ ")");
	}

	@Test
	public void testContradictionExistentialChainClashPropagation()
			throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectSomeValuesFrom(:R ObjectIntersectionOf(:B :B)))"//
						+ "SubClassOf(:B ObjectUnionOf(:C :D))"//
						+ "SubClassOf(:C ObjectSomeValuesFrom(:R ObjectIntersectionOf(:E :E)))"//
						+ "SubClassOf(:D ObjectSomeValuesFrom(:S ObjectIntersectionOf(:E :E)))"//
						+ "SubClassOf(:E owl:Nothing)"//
						+ ")",
				// Expected saturation:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:E owl:Nothing)"//
						+ "SubClassOf(:C owl:Nothing)"//
						+ "SubClassOf(:D owl:Nothing)"//
						+ "SubClassOf(:B owl:Nothing)"//
						+ "SubClassOf(:A owl:Nothing)"//
						+ ")");
	}

	@Test
	public void testPropagationDisjunction() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectSomeValuesFrom(:R ObjectUnionOf(:B :C)))"//
						+ "SubClassOf(:C owl:Nothing)"//
						+ "SubClassOf(:B ObjectUnionOf(:C :D))"//
						+ "SubClassOf(ObjectSomeValuesFrom(:R :D) :E)"//
						+ ")",
				// Expected saturation:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:C owl:Nothing)"//
						+ "SubClassOf(:B :D)"//
						+ "SubClassOf(:A :E)"//
						+ ")");
	}

	@Test
	public void testNonDeterministicPropagatedClash()
			throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectSomeValuesFrom(:R ObjectUnionOf(:B :C)))"//
						+ "SubClassOf(ObjectSomeValuesFrom(:R :C) :AC)"//
						+ "SubClassOf(ObjectIntersectionOf(:A :AC) owl:Nothing)"//
						+ "SubClassOf(:B ObjectUnionOf(:D :E))"//
						+ "SubClassOf(ObjectSomeValuesFrom(:R :D) :AD)"//
						+ "SubClassOf(ObjectIntersectionOf(:A :AD) owl:Nothing)"//
						+ "SubClassOf(ObjectSomeValuesFrom(:R :E) :F)"//
						+ ")",
				// Expected saturation:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :F)"//
						+ ")");
	}

	@Test
	public void testDisjunctionAndPropagation() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectUnionOf(:B :C))"//
						+ "SubClassOf(:A ObjectSomeValuesFrom(:R ObjectIntersectionOf(:D :D)))"//
						+ "SubClassOf(ObjectSomeValuesFrom(:R :D) :AD)"//
						+ "SubClassOf(ObjectIntersectionOf(:C :AD) owl:Nothing)"//
						+ ")",
				// Expected saturation:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//
						+ "SubClassOf(:A :AD)"//
						+ ")");
	}

	@Test
	public void testSubsumedDisjunct() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectUnionOf(:BB :B))"//
						+ "SubClassOf(:A :AA)"//
						+ "SubClassOf(:B :C)"//
						+ "SubClassOf(:AA ObjectUnionOf(:C :C))"//
						+ "SubClassOf(:AA ObjectUnionOf(:D :D))"//
						+ "SubClassOf(ObjectIntersectionOf(:B :D) owl:Nothing)"//
						+ ")",
				// Expected saturation:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:A :D)"//
						+ "SubClassOf(:A :BB)"//
						+ ")");
	}

	@Test
	public void testDisjunctionPropagation() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectSomeValuesFrom(:R ObjectIntersectionOf(:B :B)))"//
						+ "SubClassOf(:B ObjectUnionOf(:C :D))"//
						+ "SubClassOf(ObjectSomeValuesFrom(:R :C) :AC)"//
						+ "SubClassOf(ObjectSomeValuesFrom(:R :D) :AD)"//
						+ "SubClassOf(:AC :ACD)"//
						+ "SubClassOf(:AD :ACD)"//
						+ ")",
				// Expected saturation:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :ACD)"//
						+ ")");
	}

	@Test
	public void testBackForthPropagation() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectUnionOf(:B :C))"//
						+ "SubClassOf(:C ObjectSomeValuesFrom(:R ObjectIntersectionOf(:D :D)))"//
						+ "SubClassOf(:D ObjectUnionOf(:E :F))"//
						+ "SubClassOf(ObjectSomeValuesFrom(:R :F) :AF)"//
						+ "SubClassOf(ObjectSomeValuesFrom(:R :E) :AE)"//
						+ "SubClassOf(:AF :AEF)"//
						+ "SubClassOf(:AE :AEF)"//
						+ "SubClassOf(ObjectIntersectionOf(:C :AEF) owl:Nothing)"//
						+ "SubClassOf(:C ObjectUnionOf(:CA :CB))"//
						+ ")",
				// Expected saturation:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:C owl:Nothing)"//
						+ "SubClassOf(:A :B)"//
						+ ")");
	}

	@Test
	public void testPropagationBottom() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectUnionOf(:B :C))"//
						+ "SubClassOf(:A ObjectSomeValuesFrom(:R ObjectIntersectionOf(:D :D)))"//
						+ "SubClassOf(:B ObjectSomeValuesFrom(:R ObjectIntersectionOf(:D :D)))"//
						+ "SubClassOf(:C ObjectSomeValuesFrom(:R ObjectIntersectionOf(:D :D)))"//
						+ "SubClassOf(:D owl:Nothing)"//
						+ ")",
				// Expected saturation:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:D owl:Nothing)"//
						+ "SubClassOf(:B owl:Nothing)"//
						+ "SubClassOf(:C owl:Nothing)"//
						+ "SubClassOf(:A owl:Nothing)"//
						+ ")");
	}

	@Test
	public void testPropagationBottomComplex() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectUnionOf(:B :C))"//
						+ "SubClassOf(:A ObjectUnionOf(:C :B))"//
						+ "SubClassOf(:A ObjectSomeValuesFrom(:R ObjectIntersectionOf(:D :D)))"//
						+ "SubClassOf(:C ObjectSomeValuesFrom(:R ObjectIntersectionOf(:E :E)))"//
						+ "SubClassOf(:D owl:Nothing)"//
						+ "SubClassOf(:E owl:Nothing)"//
						+ ")",
				// Expected saturation:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:D owl:Nothing)"//
						+ "SubClassOf(:E owl:Nothing)"//
						+ "SubClassOf(:C owl:Nothing)"//
						+ "SubClassOf(:C owl:Nothing)"//
						+ "SubClassOf(:A owl:Nothing)"//
						+ ")");
	}

}
