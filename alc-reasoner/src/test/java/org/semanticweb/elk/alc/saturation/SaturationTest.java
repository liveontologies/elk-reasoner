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
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedSubClassOfAxiom;
import org.semanticweb.elk.alc.indexing.hierarchy.OntologyIndex;
import org.semanticweb.elk.alc.indexing.hierarchy.TestAxiomIndexerVisitor;
import org.semanticweb.elk.alc.indexing.visitors.IndexedAxiomVisitor;
import org.semanticweb.elk.alc.loading.AxiomLoader;
import org.semanticweb.elk.alc.loading.ElkLoadingException;
import org.semanticweb.elk.alc.loading.Owl2StreamLoader;
import org.semanticweb.elk.alc.reasoner.Reasoner;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.parsing.Owl2ParserFactory;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.util.collections.Operations.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaturationTest {

	private static final Logger LOGGER_ = LoggerFactory.getLogger(SaturationTest.class);

	private final Owl2ParserFactory parserFactory_ = new Owl2FunctionalStyleParserFactory();

	void testSaturation(String ontology, String expectedSubsumptions,
			String expectedNonSubsumptions) throws ElkLoadingException {
		final Reasoner reasoner = new Reasoner(new Owl2StreamLoader(parserFactory_,
				ontology));
		Condition<IndexedSubClassOfAxiom> subsumptionCondition = new Condition<IndexedSubClassOfAxiom>() {

			@Override
			public boolean holds(IndexedSubClassOfAxiom axiom) {
				try {
					return reasoner.subsumes(axiom.getSubClass(), axiom.getSuperClass());
				} catch (ElkLoadingException e) {
					LOGGER_.error("Exception during subsumption check", e);
					return false;
				}
			}
			
		};
		Condition<IndexedSubClassOfAxiom> nonSubsumptionCondition = new Condition<IndexedSubClassOfAxiom>() {

			@Override
			public boolean holds(IndexedSubClassOfAxiom axiom) {
				try {
					return !reasoner.subsumes(axiom.getSubClass(), axiom.getSuperClass());
				} catch (ElkLoadingException e) {
					LOGGER_.error("Exception during subsumption check", e);
					return false;
				}
			}
			
		};
		
		
		IndexedAxiomVisitor<Void> subsumptionChecker = new SubsumptionCheckingAxiomVisitor(
				subsumptionCondition);
		IndexedAxiomVisitor<Void> nonSubsumptionChecker = new SubsumptionCheckingAxiomVisitor(
				nonSubsumptionCondition);
		reasoner.forceLoading();
		OntologyIndex index = reasoner.getOntologyIndex();
		TestAxiomIndexerVisitor subsumptionIndexer = new TestAxiomIndexerVisitor(
				index, subsumptionChecker);
		TestAxiomIndexerVisitor nonSubsumptionIndexer = new TestAxiomIndexerVisitor(
				index, nonSubsumptionChecker);
		AxiomLoader expectedSubsumptionsLoader = new Owl2StreamLoader(
				parserFactory_, expectedSubsumptions);
		AxiomLoader expectedNonSubsumptionsLoader = new Owl2StreamLoader(
				parserFactory_, expectedNonSubsumptions);
		ElkAxiomProcessor expectedSubsumptionsInserter = new ChangeIndexingProcessor(
				subsumptionIndexer);
		ElkAxiomProcessor expectedNonSubsumptionsInserter = new ChangeIndexingProcessor(
				nonSubsumptionIndexer);
		ElkAxiomProcessor dummyAxiomDeletor = new ElkAxiomProcessor() {
			@Override
			public void visit(ElkAxiom elkAxiom) {
				// does nothing
			}
		};
		try {
			expectedNonSubsumptionsLoader.load(expectedNonSubsumptionsInserter,
					dummyAxiomDeletor);
		} finally {
			expectedNonSubsumptionsLoader.dispose();
			// clear interrupt status
			Thread.interrupted();
		}
		try {
			expectedSubsumptionsLoader.load(expectedSubsumptionsInserter,
					dummyAxiomDeletor);
		} finally {
			expectedSubsumptionsLoader.dispose();
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
				// Expected subsumptions:
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
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:B :A)"//
						+ "SubClassOf(:B :C)"//
						+ "SubClassOf(:C :A)"//
						+ "SubClassOf(:C :B)"//
						+ "SubClassOf(:D :A)"//
						+ "SubClassOf(:D :B)"//
						+ "SubClassOf(:D :C)"//
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
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:A :D)"//
						+ "SubClassOf(:A :BCD)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:B :A)"//
						+ "SubClassOf(:B :C)"//
						+ "SubClassOf(:B :D)"//
						+ "SubClassOf(:B :BCD)"//
						+ "SubClassOf(:C :A)"//
						+ "SubClassOf(:C :B)"//
						+ "SubClassOf(:C :D)"//
						+ "SubClassOf(:C :BCD)"//
						+ "SubClassOf(:D :A)"//
						+ "SubClassOf(:D :B)"//
						+ "SubClassOf(:D :C)"//
						+ "SubClassOf(:D :BCD)"//
						+ ")"//
		);
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
				// Expected subsumptions:
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
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ ")"//
		);
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
				// Expected subsumptions:
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
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ ")"//
		);
	}

	@Test
	public void testExistentials() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubObjectPropertyOf(:R :S)"//
						+ "SubObjectPropertyOf(:H :R)"//
						+ "SubClassOf(:A ObjectSomeValuesFrom(:R :B))"//
						+ "EquivalentClasses(:B :C)"//
						+ "SubClassOf(ObjectSomeValuesFrom(:S :C) :D)"//
						+ "SubClassOf(ObjectSomeValuesFrom(:H :C) :F)"//
						+ ")",
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :A)"//
						+ "SubClassOf(:A :D)"//
						+ "SubClassOf(:B :B)"//
						+ "SubClassOf(:B :C)"//
						+ "SubClassOf(:C :C)"//
						+ "SubClassOf(:C :B)"//
						+ "SubClassOf(:D :D)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :F)"//
						+ ")"//
		);
	}
	
	@Test
	public void testTransitivityNoHierarchies() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "TransitiveObjectProperty(:R)"//						
						+ "SubClassOf(:A ObjectSomeValuesFrom(:R :B))"//
						+ "SubClassOf(:B ObjectSomeValuesFrom(:R :C))"//						
						+ "EquivalentClasses(:C :D)"//
						+ "SubClassOf(ObjectSomeValuesFrom(:R :D) :E)"//
						+ ")",
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :A)"//
						+ "SubClassOf(:A :E)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:A :D)"//
						+ "SubClassOf(:A :B)"//
						+ ")"//
		);
	}
	
	@Test
	public void testTransitivityWithHierarchy() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "TransitiveObjectProperty(:R)"//
						+ "SubObjectPropertyOf(:R :S)"//	
						+ "SubClassOf(:A ObjectSomeValuesFrom(:R :B))"//
						+ "SubClassOf(:B ObjectSomeValuesFrom(:R :C))"//						
						+ "EquivalentClasses(:C :D)"//
						+ "SubClassOf(ObjectSomeValuesFrom(:S :D) :E)"//
						+ ")",
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :A)"//
						+ "SubClassOf(:A :E)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:A :D)"//
						+ "SubClassOf(:A :B)"//
						+ ")"//
		);
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
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//
						+ "SubClassOf(:A :C)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ ")"//
		);
	}

	@Test
	public void testCyclicExistentialsComplex() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubObjectPropertyOf(:R :S)"//
						+ "SubClassOf(:A ObjectSomeValuesFrom(:R :B))"//
						+ "SubClassOf(:B :C)"//
						+ "SubClassOf(:C ObjectSomeValuesFrom(:R :A))"//
						+ "SubClassOf(:C ObjectSomeValuesFrom(:R :B))"//
						+ "SubClassOf(ObjectSomeValuesFrom(:S :C) :D)"//
						+ "SubClassOf(ObjectSomeValuesFrom(:S :D) :E)"//
						+ ")",
				// Expected subsumptions:
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
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ ")"//
		);
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
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :A)"//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:B :B)"//
						+ "SubClassOf(:B :C)"//
						+ "SubClassOf(:C :C)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ ")"//
		);
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
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A owl:Nothing)"//
						+ "SubClassOf(:B :B)"//
						+ "SubClassOf(:C :C)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ ")"//
		);
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
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :A)"//
						+ "SubClassOf(:A :D)"//
						+ "SubClassOf(:B :B)"//
						+ "SubClassOf(:B :D)"//
						+ "SubClassOf(:C :C)"//
						+ "SubClassOf(:C :D)"//
						+ "SubClassOf(:D :D)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:B :C)"//
						+ "SubClassOf(:C :B)"//
						+ ")"//
		);
	}

	@Test
	public void testNegativeDisjunction() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(ObjectUnionOf(:B :C) :A)"//
						+ ")",
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:B :A)"//
						+ "SubClassOf(:C :A)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:B :C)"//
						+ ")"//
		);
	}

	@Test
	public void testDisjunctionAssociativity() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "EquivalentClasses(:X ObjectUnionOf(:A ObjectUnionOf(:B :C)))"//
						+ "EquivalentClasses(:Y ObjectUnionOf(ObjectUnionOf(:A :B) :C))"//
						+ ")",
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:X :Y)"//
						+ "SubClassOf(:Y :X)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:X :A)"//
						+ "SubClassOf(:X :B)"//
						+ "SubClassOf(:X :C)"//
						+ "SubClassOf(:Y :A)"//
						+ "SubClassOf(:Y :B)"//
						+ "SubClassOf(:Y :C)"//
						+ ")"//
		);
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
				// Expected subsumptions:
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
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:C :A)"//
						+ "SubClassOf(:A :B)"//
						+ "SubClassOf(:C :E)"//
						+ ")"//
		);
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
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:B owl:Nothing)"//
						+ "SubClassOf(:A owl:Nothing)"//
						+ "SubClassOf(:A :B)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ ")"//
		);
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
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:B owl:Nothing)"//
						+ "SubClassOf(:C owl:Nothing)"//
						+ "SubClassOf(:A owl:Nothing)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ ")"//
		);
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
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:E owl:Nothing)"//
						+ "SubClassOf(:C owl:Nothing)"//
						+ "SubClassOf(:D owl:Nothing)"//
						+ "SubClassOf(:B owl:Nothing)"//
						+ "SubClassOf(:A owl:Nothing)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ ")"//
		);
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
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:C owl:Nothing)"//
						+ "SubClassOf(:B :D)"//
						+ "SubClassOf(:A :E)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//
						+ ")"//
		);
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

						// +
						// "SubClassOf(ObjectSomeValuesFrom(:R :E) owl:Nothing)"//

						+ "SubClassOf(ObjectSomeValuesFrom(:R :D) :AD)"//
						+ "SubClassOf(ObjectIntersectionOf(:A :AD) owl:Nothing)"//
						+ "SubClassOf(ObjectSomeValuesFrom(:R :E) :F)"//
						+ ")",
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :F)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:B :C)"//
						+ "SubClassOf(:B :D)"//
						+ "SubClassOf(:B :E)"//
						+ "SubClassOf(:C :B)"//
						+ "SubClassOf(:C :D)"//
						+ "SubClassOf(:C :E)"//
						+ "SubClassOf(:D :B)"//
						+ "SubClassOf(:D :C)"//
						+ "SubClassOf(:D :E)"//
						+ "SubClassOf(:E :B)"//
						+ "SubClassOf(:E :C)"//
						+ "SubClassOf(:E :d)"//
						+ ")"//
		);
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
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :AD)"//
						+ "SubClassOf(:A :B)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :C)"//
						+ ")"//
		);
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
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:A :D)"//
						+ "SubClassOf(:A :BB)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//
						+ "SubClassOf(:B :D)"//
						+ ")"//
		);
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
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :ACD)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :AC)"//
						+ "SubClassOf(:A :AD)"//
						+ ")"//
		);
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
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:C owl:Nothing)"//
						+ "SubClassOf(:A :B)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :AE)"//
						+ "SubClassOf(:A :AF)"//
						+ "SubClassOf(:A :CA)"//
						+ "SubClassOf(:A :CB)"//
						+ ")"//
		);
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
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:D owl:Nothing)"//
						+ "SubClassOf(:B owl:Nothing)"//
						+ "SubClassOf(:C owl:Nothing)"//
						+ "SubClassOf(:A owl:Nothing)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ ")"//
		);
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
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:D owl:Nothing)"//
						+ "SubClassOf(:E owl:Nothing)"//
						+ "SubClassOf(:C owl:Nothing)"//
						+ "SubClassOf(:A owl:Nothing)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:B owl:Nothing)"//
						+ ")"//
		);
	}

	@Test
	public void testPropagationBottomCyclic() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectSomeValuesFrom(:R :B))"//
						+ "SubClassOf(:B ObjectSomeValuesFrom(:R :C))"//
						+ "SubClassOf(:C ObjectSomeValuesFrom(:R :A))"//
						+ "SubClassOf(:B owl:Nothing)"//
						+ ")",
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:B owl:Nothing)"//
						+ "SubClassOf(:A owl:Nothing)"//
						+ "SubClassOf(:C owl:Nothing)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ ")"//
		);
	}

	@Test
	public void testUnitResolution() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectUnionOf(:B :C))"//
						+ "SubClassOf(:B owl:Nothing)"//
						+ ")",
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :C)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//
						+ ")"//
		);
	}

	@Test
	public void testPropagationDecompositionRedundancy()
			throws ElkLoadingException {
		/*
		 * this example illustrate a problem with decomposition optimization for
		 * possible propagated subsumers: it may be that the link through which
		 * the subsumer was propagated does not exist anymore, but subsumer can
		 * block decomposition rules since it was composed
		 */
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectUnionOf(:B ObjectSomeValuesFrom(:R ObjectUnionOf(:D :D)) :C))"
						+ "SubClassOf(:D owl:Nothing)"//
						+ "SubClassOf(:B ObjectUnionOf(:BB :BB))"
						+ "EquivalentClasses(:BB ObjectSomeValuesFrom(:R :D))"//
						+ ")",
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:B :C)"//
						+ "SubClassOf(:B owl:Nothing)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//
						+ ")"//
		);
	}

	@Test
	public void testDeterministicFIFOStrategy() throws ElkLoadingException {
		/*
		 * if used with stack (LIFO) clash can be processed before elements that
		 * were produced earlier, thus earlier elements can be deleted when the
		 * queue is cleared without properly reverting inferences
		 */
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectUnionOf(:B :C))"//
						+ "SubClassOf(:A ObjectUnionOf(:E :E))"//
						+ "SubClassOf(:A ObjectUnionOf(:B :C))"//
						+ "SubClassOf(:C ObjectSomeValuesFrom(:R :D))"//
						+ "SubClassOf(:D owl:Nothing)"//
						+ ")",
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :E)"//
						+ "SubClassOf(:A :B)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :C)"//
						+ ")"//
		);
	}

	@Test
	public void testSplittingDisjunctionsNontEnough()
			throws ElkLoadingException {
		/*
		 * if we non-deterministically split only disjunctions, the result might
		 * be unsound
		 */
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectSomeValuesFrom(:R :A))"//
						+ "SubClassOf(:A ObjectUnionOf(:B :C))"//
						+ "SubClassOf(ObjectIntersectionOf(:B ObjectSomeValuesFrom(:R :B)) :D)"//
						+ "SubClassOf(ObjectIntersectionOf(:C ObjectSomeValuesFrom(:R :C)) :D)"//
						+ ")",
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :D)"//
						+ ")"//
		);
	}

	@Test
	public void testCommonSubsumersSimple() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectUnionOf(:B :C))"//
						+ "SubClassOf(:B :D)"//
						+ "SubClassOf(:B :E)"//
						+ "SubClassOf(:C :E)"//
						+ "SubClassOf(:C :F)"//
						+ ")",
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :E)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:A :D)"//
						+ "SubClassOf(:A :F)"//
						+ ")"//
		);
	}

	@Test
	public void testCommonSubsumersComplex() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectUnionOf(:B :C))"//
						+ "SubClassOf(:B ObjectUnionOf(:D :E))"//
						+ "SubClassOf(:C ObjectUnionOf(:E :D))"//
						+ "SubClassOf(:D :F)"//
						+ "SubClassOf(:E :F)"//
						+ ")",
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :F)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:A :D)"//
						+ "SubClassOf(:A :E)"//
						+ ")"//
		);
	}
	
	@Test
	public void testDisjunctionsComplex() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectUnionOf(:B :C))"//
						+ "SubClassOf(:B ObjectUnionOf(:F :G))"//
						+ "SubClassOf(:C ObjectUnionOf(:D :E))"//
						+ "SubClassOf(:G :K)"//
						+ "SubClassOf(:E :K)"//
						+ "SubClassOf(:D <http://www.w3.org/2002/07/owl#Nothing>)"//
						+ "SubClassOf(:F <http://www.w3.org/2002/07/owl#Nothing>)"//
						+ ")",
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :K)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:A :D)"//
						+ "SubClassOf(:A :E)"//
						+ "SubClassOf(:A :G)"//
						+ ")"//
		);
	}
	
	@Test
	public void testDisjunctionsComplex2() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectUnionOf(:B :C))"//
						+ "SubClassOf(:B ObjectUnionOf(:D :E))"//
						+ "SubClassOf(:B :C)"//
						+ "SubClassOf(:C ObjectUnionOf(:E :F))"//
						+ "SubClassOf(:E :F)"//
						+ "SubClassOf(ObjectIntersectionOf(:D :C) <http://www.w3.org/2002/07/owl#Nothing>)"//
						+ ")",
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:A :F)"//						
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//
						+ "SubClassOf(:A :D)"//
						+ "SubClassOf(:A :E)"//
						+ ")"//
		);
	}

	@Test
	public void testDisjointnessSimple() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectUnionOf(:B :C))"//
						+ "SubClassOf(:B ObjectIntersectionOf(:BB :BC))"//
						+ "DisjointClasses(:A :BC)"//
						+ ")",
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :C)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//						
						+ ")"//
		);
	}	
	
	@Test
	public void testDisjointnessSelfInconsistency() throws ElkLoadingException {
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//
						+ "DisjointClasses(:B :C :B)"//
						+ ")",
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A <http://www.w3.org/2002/07/owl#Nothing>)"//
						+ "SubClassOf(:B <http://www.w3.org/2002/07/owl#Nothing>)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
					
						+ ")"//
		);
	}
	
	@Test
	public void testTransitivityWithHierarchyNonDeterministic() throws ElkLoadingException {
		
		//LOGGER_.info("TRANSITIVITY TEST STARTED");
		/*
		 * testing that transitive propagations get propagated via possible propagated existentials
		 */
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "TransitiveObjectProperty(:R)"//
						+ "SubObjectPropertyOf(:R :S)"//	
						+ "SubClassOf(:A ObjectSomeValuesFrom(:R :B))"//
						+ "SubClassOf(:B ObjectSomeValuesFrom(:R :C))"//						
						+ "SubClassOf(:C ObjectUnionOf(:D1 :D2))"//
						+ "SubClassOf(ObjectSomeValuesFrom(:S :D1) :E)"//
						+ "SubClassOf(ObjectSomeValuesFrom(:S :D2) :E)"//
						+ ")",
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :A)"//
						+ "SubClassOf(:A :E)"//
						+ "SubClassOf(:B :E)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:A :B)"//
						+ ")"//
		);
		
		//LOGGER_.info("TRANSITIVITY TEST ENDED");
	}	
	
	@Test
	public void testRoleHierarchyWithBacktracking() throws ElkLoadingException {
		// testing that negative propagations work OK with role hierarchies so
		// that "S some D_1" is properly propagated after "S some D1" is negated
		// (or vice versa).
		testSaturation(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubObjectPropertyOf(:R :S)"//	
						+ "SubClassOf(:B ObjectSomeValuesFrom(:R :C))"//						
						+ "SubClassOf(:C ObjectUnionOf(:D1 :D2))"//
						+ "SubClassOf(ObjectSomeValuesFrom(:S :D1) :E)"//
						+ "SubClassOf(ObjectSomeValuesFrom(:S :D2) :E)"//
						+ ")",
				// Expected subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:B :E)"//
						+ ")",//
				// Expected non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ ")"//
		);
	}
}
