package org.semanticweb.elk.alc.saturation.reduction;

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

import java.util.Map;

import org.junit.Test;
import org.semanticweb.elk.alc.indexing.hierarchy.ChangeIndexingProcessor;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedSubClassOfAxiom;
import org.semanticweb.elk.alc.indexing.hierarchy.OntologyIndex;
import org.semanticweb.elk.alc.indexing.hierarchy.TestAxiomIndexerVisitor;
import org.semanticweb.elk.alc.indexing.visitors.IndexedAxiomVisitor;
import org.semanticweb.elk.alc.loading.AxiomLoader;
import org.semanticweb.elk.alc.loading.ElkLoadingException;
import org.semanticweb.elk.alc.loading.Owl2StreamLoader;
import org.semanticweb.elk.alc.reasoner.Reasoner;
import org.semanticweb.elk.alc.saturation.SaturationTest;
import org.semanticweb.elk.alc.saturation.SubsumptionCheckingAxiomVisitor;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.parsing.Owl2ParserFactory;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.util.collections.Operations.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubsumptionReductionTest {

	private static final Logger LOGGER_ = LoggerFactory.getLogger(SaturationTest.class);

	private final Owl2ParserFactory parserFactory_ = new Owl2FunctionalStyleParserFactory();
	
	/**
	 * FIXME reuse some parts the code in the conditions
	 */
	void testTransitiveReduction(String ontology, String expectedDirectSubsumers,
			String expectedNonSubsumptions, String expectedEquivalences) throws ElkLoadingException {
		final Reasoner reasoner = new Reasoner(new Owl2StreamLoader(parserFactory_,
				ontology));
		
		Condition<IndexedSubClassOfAxiom> subsumptionCondition = new Condition<IndexedSubClassOfAxiom>() {

			Map<IndexedClass, SubsumptionReduct> taxonomy_;
			
			@Override
			public boolean holds(IndexedSubClassOfAxiom axiom) {
				try {
					if (taxonomy_ == null) {
						taxonomy_ = reasoner.classifyAndReduce();
					}
					
					SubsumptionReduct reduct = taxonomy_.get(axiom.getSubClass());

					return reduct.directSubsumers.contains(axiom.getSuperClass());
				} catch (ElkLoadingException e) {
					LOGGER_.error("Exception during reasoning", e);
					return false;
				}
			}
			
			@Override
			public String toString() {
				return "direct subsumption expected";
			}
			
		};
		Condition<IndexedSubClassOfAxiom> nonSubsumptionCondition = new Condition<IndexedSubClassOfAxiom>() {

			Map<IndexedClass, SubsumptionReduct> taxonomy_;
			
			@Override
			public boolean holds(IndexedSubClassOfAxiom axiom) {
				try {
					if (taxonomy_ == null) {
						taxonomy_ = reasoner.classifyAndReduce();
					}
					
					SubsumptionReduct reduct = taxonomy_.get(axiom.getSubClass());

					return !reduct.directSubsumers.contains(axiom.getSuperClass());
				} catch (ElkLoadingException e) {
					LOGGER_.error("Exception during reasoning", e);
					return false;
				}
			}
			
			@Override
			public String toString() {
				return "direct subsumption NOT expected";
			}
			
		};
		
		Condition<IndexedSubClassOfAxiom> equivalenceCondition = new Condition<IndexedSubClassOfAxiom>() {

			Map<IndexedClass, SubsumptionReduct> taxonomy_;
			
			@Override
			public boolean holds(IndexedSubClassOfAxiom axiom) {
				try {
					if (taxonomy_ == null) {
						taxonomy_ = reasoner.classifyAndReduce();
					}
					
					IndexedClassExpression first = axiom.getSubClass();
					IndexedClassExpression second = axiom.getSuperClass();

					return taxonomy_.get(first).equivalent.contains(second) && taxonomy_.get(second).equivalent.contains(first);
				} catch (ElkLoadingException e) {
					LOGGER_.error("Exception during reasoning", e);
					return false;
				}
			}
			
			@Override
			public String toString() {
				return "symmetric equivalence expected";
			}
			
		};
		
		reasoner.forceLoading();
		
		OntologyIndex index = reasoner.getOntologyIndex();
		// check expected direct subsumers
		loadAndCheck(expectedDirectSubsumers, subsumptionCondition, index);
		// check that these are not direct subsumers (or maybe not subsumers at all)
		loadAndCheck(expectedNonSubsumptions, nonSubsumptionCondition, index);
		// check expected equivalences
		loadAndCheck(expectedEquivalences, equivalenceCondition, index);
	}

	void loadAndCheck(String axioms, Condition<IndexedSubClassOfAxiom> conditionToCheck, OntologyIndex index)  throws ElkLoadingException {
		IndexedAxiomVisitor<Void> subsumptionChecker = new SubsumptionCheckingAxiomVisitor(
				conditionToCheck);
		TestAxiomIndexerVisitor indexer = new TestAxiomIndexerVisitor(
				index, subsumptionChecker);
		AxiomLoader loader = new Owl2StreamLoader(
				parserFactory_, axioms);
		ElkAxiomProcessor deleter = new ElkAxiomProcessor() {
			@Override
			public void visit(ElkAxiom elkAxiom) {
				// does nothing
			}
		};
		ElkAxiomProcessor inserter = new ChangeIndexingProcessor(indexer);
		
		try {
			loader.load(inserter, deleter);
		} finally {
			loader.dispose();
			// clear interrupt status
			Thread.interrupted();
		}
	}
	
	@Test
	public void testSimpleAncestors() throws ElkLoadingException {
		testTransitiveReduction(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:B :D)"//
						+ "SubClassOf(:C :D)"//
						+ ")",
				// Expected direct subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:B :D)"//
						+ "SubClassOf(:C :D)"//
						+ ")",//
				// Expected non-direct subsumptions or non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :D)"//
						+ ")",//
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ ")"//						
		);
	}

	@Test
	public void testSimpleEquivalences() throws ElkLoadingException {
		testTransitiveReduction(// Ontology:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:B :D)"//
						+ "SubClassOf(:D :A)"//
						+ "SubClassOf(:C :F)"//
						+ ")",
				// Expected direct subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :C)"//
						+ "SubClassOf(:B :C)"//
						+ "SubClassOf(:D :C)"//
						+ ")",//
				// Expected non-direct subsumptions or non-subsumptions:
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :F)"//
						+ ")",//
				// Expected equivalences (here parsed as subsumptions though)		
				"Prefix(:=<>)"//
						+ "Ontology("//
						+ "SubClassOf(:A :B)"//
						+ "SubClassOf(:B :D)"//
						+ "SubClassOf(:A :D)"//
						+ ")"//						
		);
	}
}
