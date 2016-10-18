/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.incremental;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.loading.TestAxiomLoaderFactory;
import org.semanticweb.elk.loading.TestChangesLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.managers.ElkObjectEntityRecyclingFactory;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.parsing.Owl2ParserAxiomProcessor;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.stages.LoggingStageExecutor;
import org.semanticweb.elk.reasoner.stages.PostProcessingStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

/**
 * A collections of low-level tests to check dynamic disallowing of the
 * incremental reasoning mode.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IncrementalModeSwitchTest {

	final ElkObject.Factory objectFactory = new ElkObjectEntityRecyclingFactory();

	@Test
	public void testAddedTransitivity() throws ElkException {
		TestChangesLoader loader = new TestChangesLoader();
		Reasoner reasoner = TestReasonerUtils.createTestReasoner(loader,
				new PostProcessingStageExecutor());

		reasoner.setAllowIncrementalMode(false);

		ElkClass A = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass B = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass C = objectFactory.getClass(new ElkFullIri(":C"));
		ElkClass D = objectFactory.getClass(new ElkFullIri(":D"));
		ElkObjectProperty R = objectFactory.getObjectProperty(new ElkFullIri(
				":R"));
		ElkAxiom axASubRB = objectFactory.getSubClassOfAxiom(A,
				objectFactory.getObjectSomeValuesFrom(R, B));
		ElkAxiom axBSubRC = objectFactory.getSubClassOfAxiom(B,
				objectFactory.getObjectSomeValuesFrom(R, C));
		ElkAxiom axRCSubD = objectFactory.getSubClassOfAxiom(
				objectFactory.getObjectSomeValuesFrom(R, C), D);
		ElkAxiom axTransR = objectFactory.getTransitiveObjectPropertyAxiom(R);

		loader.add(axASubRB).add(axBSubRC).add(axRCSubD);

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomyQuietly();

		assertFalse(taxonomy.getNode(A).getAllSuperNodes()
				.contains(taxonomy.getNode(D)));

		reasoner.setAllowIncrementalMode(true);
		TestChangesLoader changeLoader = new TestChangesLoader();
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		changeLoader.add(axTransR);

		taxonomy = reasoner.getTaxonomyQuietly();
		// Now A should be a subclass of D since R is transitive
		assertTrue(taxonomy.getNode(A).getAllSuperNodes()
				.contains(taxonomy.getNode(D)));
	}

	@Test
	public void testSwitchDueToNewRole() throws ElkException, IOException {
		String toAdd = "Prefix( : = <http://example.org/> ) Prefix( owl: = <http://www.w3.org/2002/07/owl#> ) Ontology(\n"
				+ "SubClassOf(:C ObjectSomeValuesFrom(:T :B)) \n"
				+ "ObjectPropertyDomain(:T owl:Nothing) \n" + ")";
		String initial = "Prefix( : = <http://example.org/> )\n"
				+ "Prefix( owl: = <http://www.w3.org/2002/07/owl#> ) \n"
				+ "Ontology( \n" + "EquivalentClasses(:A :C) \n "
				+ "SubClassOf(owl:Thing ObjectSomeValuesFrom(:R :B)) \n "
				+ "SubClassOf(ObjectSomeValuesFrom(:S :B) :A) \n"
				+ "SubObjectPropertyOf(:R :S) )";

		List<ElkAxiom> ontology = loadAxioms(new StringReader(initial));
		List<ElkAxiom> additions = loadAxioms(new StringReader(toAdd));
		TestChangesLoader initialLoader = new TestChangesLoader();

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(initialLoader,
				new LoggingStageExecutor());

		reasoner.setAllowIncrementalMode(false);

		for (ElkAxiom axiom : ontology) {
			initialLoader.add(axiom);
		}

		assertFalse(reasoner.isInconsistent());
		reasoner.getTaxonomy();

//		System.out.println("===========================================");

		reasoner.setAllowIncrementalMode(true);

		TestChangesLoader changeLoader = new TestChangesLoader();
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		for (ElkAxiom add : additions) {
			changeLoader.add(add);
		}

		assertTrue(reasoner.isInconsistent());
	}

	private List<ElkAxiom> loadAxioms(Reader reader) throws IOException,
			Owl2ParseException {
		Owl2Parser parser = new Owl2FunctionalStyleParserFactory()
				.getParser(reader);
		final List<ElkAxiom> axioms = new ArrayList<ElkAxiom>();

		parser.accept(new Owl2ParserAxiomProcessor() {

			@Override
			public void visit(ElkPrefix elkPrefix) throws Owl2ParseException {
				// does nothing
			}

			@Override
			public void visit(ElkAxiom elkAxiom) throws Owl2ParseException {
				axioms.add(elkAxiom);
			}

			@Override
			public void finish() throws Owl2ParseException {
				// everything is processed immediately
			}
		});

		return axioms;
	}

}
