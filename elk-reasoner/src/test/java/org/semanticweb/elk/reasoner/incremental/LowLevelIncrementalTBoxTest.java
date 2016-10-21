/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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
package org.semanticweb.elk.reasoner.incremental;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.loading.TestAxiomLoaderFactory;
import org.semanticweb.elk.loading.TestChangesLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.iris.ElkAbbreviatedIri;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.iris.ElkPrefixImpl;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class LowLevelIncrementalTBoxTest {
	
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(LowLevelIncrementalTBoxTest.class);

	final ElkObject.Factory objectFactory = new ElkObjectEntityRecyclingFactory();
	
	final ElkPrefixImpl p = new ElkPrefixImpl(":", new ElkFullIri(LowLevelIncrementalTBoxTest.class.getName()));

	ElkClass createElkClass(String name) {
		return objectFactory.getClass(new ElkAbbreviatedIri(p, name));
	}
	
	ElkObjectProperty createElkObjectProperty(String name) {
		return objectFactory.getObjectProperty(new ElkAbbreviatedIri(p, name));
	}
	
	@Test
	public void testBasicDeletion() throws ElkException {

		TestChangesLoader loader = new TestChangesLoader();

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(loader,
				new LoggingStageExecutor());
		reasoner.setAllowIncrementalMode(false);

		ElkClass a = createElkClass("A");
		ElkClass b = createElkClass("B");
		ElkClass c = createElkClass("C");
		ElkClass d = createElkClass("D");
		ElkObjectProperty r = objectFactory.getObjectProperty(new ElkFullIri("R"));

		loader.add(objectFactory.getSubClassOfAxiom(b, d))
				.add(objectFactory.getSubClassOfAxiom(a,
						objectFactory.getObjectSomeValuesFrom(r, b)))
				.add(objectFactory.getSubClassOfAxiom(
						objectFactory.getObjectSomeValuesFrom(r, d), c));

		LOGGER_.trace("********************* Initial taxonomy computation *********************");
		
		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));
		// now delete B [= D, should retract A [= C
		//

		reasoner.setAllowIncrementalMode(true);
		TestChangesLoader changeLoader = new TestChangesLoader();
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		changeLoader.remove(objectFactory.getSubClassOfAxiom(b, d));

		LOGGER_.trace("********** Taxonomy re-computation after incremental change ************");
		
		taxonomy = reasoner.getTaxonomy();

		assertFalse(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));
	}

	@Test
	public void testDeletePositiveExistential() throws ElkException {
		TestChangesLoader loader = new TestChangesLoader();

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(loader,
				new LoggingStageExecutor());

		reasoner.setAllowIncrementalMode(false);

		ElkClass a = createElkClass("A");
		ElkClass b = createElkClass("B");
		ElkClass c = createElkClass("C");
		ElkClass d = createElkClass("D");
		ElkClass e = createElkClass("E");
		ElkObjectProperty r = objectFactory
				.getObjectProperty(new ElkFullIri("R"));
		ElkAxiom posExistential = objectFactory.getSubClassOfAxiom(a,
				objectFactory.getObjectSomeValuesFrom(r, b));

		loader.add(objectFactory.getSubClassOfAxiom(b, d)).add(posExistential)
				.add(objectFactory.getSubClassOfAxiom(a, e))
				.add(objectFactory.getSubClassOfAxiom(
						objectFactory.getObjectSomeValuesFrom(r, d), c));

		LOGGER_.trace("********************* Initial taxonomy computation *********************");
		
		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));
		// now delete A subclass R some B, should retract A subclass C
		//

		reasoner.setAllowIncrementalMode(true);
		TestChangesLoader changeLoader = new TestChangesLoader();
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));
		changeLoader.remove(posExistential);

		LOGGER_.trace("********** Taxonomy re-computation after incremental change ************");
		
		taxonomy = reasoner.getTaxonomy();

		assertFalse(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));
	}

	@Test
	public void testDeleteAddConjunction() throws ElkException {
		TestChangesLoader loader = new TestChangesLoader();

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(loader,
				new LoggingStageExecutor(), 1);

		reasoner.setAllowIncrementalMode(false);

		ElkClass a = createElkClass("A");
		ElkClass b = createElkClass("B");
		ElkClass c = createElkClass("C");

		ElkAxiom axAandBsubC = objectFactory.getSubClassOfAxiom(
				objectFactory.getObjectIntersectionOf(a, b), c);

		loader.add(objectFactory.getSubClassOfAxiom(a, b)).add(axAandBsubC);

		LOGGER_.trace("********************* Initial taxonomy computation *********************");
		
		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));

		//

		reasoner.setAllowIncrementalMode(true);

		TestChangesLoader changeLoader = new TestChangesLoader();
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		// this should not change anything
		changeLoader.remove(axAandBsubC).add(axAandBsubC);

		LOGGER_.trace("********** Taxonomy re-computation after incremental change ************");
		
		taxonomy = reasoner.getTaxonomy();

		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));

		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));
		assertEquals(2, taxonomy.getNode(a).getDirectSuperNodes().size());

		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		// this again should not change anything
		changeLoader.remove(axAandBsubC).add(axAandBsubC);

		LOGGER_.trace("********** Taxonomy re-computation after incremental change ************");
		
		taxonomy = reasoner.getTaxonomy();

		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));

		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));
		assertEquals(2, taxonomy.getNode(a).getDirectSuperNodes().size());

		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		// when this axiom removed, we loose node C
		changeLoader.remove(axAandBsubC);

		LOGGER_.trace("********** Taxonomy re-computation after incremental change ************");
		
		taxonomy = reasoner.getTaxonomy();

		assertEquals(1, taxonomy.getNode(a).getDirectSuperNodes().size());

		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		// this should not change anything

		changeLoader.add(axAandBsubC).remove(axAandBsubC);

		LOGGER_.trace("********** Taxonomy re-computation after incremental change ************");
		
		taxonomy = reasoner.getTaxonomy();

		assertEquals(1, taxonomy.getNode(a).getDirectSuperNodes().size());

		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		// this again should not change anything

		changeLoader.add(axAandBsubC).remove(axAandBsubC);

		LOGGER_.trace("********** Taxonomy re-computation after incremental change ************");
		
		taxonomy = reasoner.getTaxonomy();

		assertEquals(1, taxonomy.getNode(a).getDirectSuperNodes().size());

	}

	@Test
	public void testEquivalence() throws ElkException {
		TestChangesLoader loader = new TestChangesLoader();

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(loader,
				new LoggingStageExecutor(), 1);

		reasoner.setAllowIncrementalMode(false);

		ElkClass a = createElkClass("A");
		ElkClass b = createElkClass("B");
		ElkClass c = createElkClass("C");

		ElkAxiom axAeqB = objectFactory.getEquivalentClassesAxiom(a, b);

		loader.add(objectFactory.getSubClassOfAxiom(a, c)).add(axAeqB);

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		assertEquals(2, taxonomy.getNode(a).size());

		reasoner.setAllowIncrementalMode(true);

		TestChangesLoader changeLoader = new TestChangesLoader();
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		// this should not change anything
		changeLoader.remove(axAeqB).add(axAeqB);

		LOGGER_.trace("********************* Initial taxonomy computation *********************");
		
		taxonomy = reasoner.getTaxonomy();

		assertEquals(2, taxonomy.getNode(a).size());

		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		// when this axiom removed, we loose A=B
		changeLoader.remove(axAeqB);

		LOGGER_.trace("********** Taxonomy re-computation after incremental change ************");
		
		taxonomy = reasoner.getTaxonomy();

		assertEquals(1, taxonomy.getNode(a).size());

		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		// this should not change anything

		changeLoader.add(axAeqB).remove(axAeqB);

		LOGGER_.trace("********** Taxonomy re-computation after incremental change ************");
		
		taxonomy = reasoner.getTaxonomy();

		assertEquals(1, taxonomy.getNode(a).size());

		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		// this should change it back

		changeLoader.add(axAeqB);

		LOGGER_.trace("********** Taxonomy re-computation after incremental change ************");
		
		taxonomy = reasoner.getTaxonomy();

		assertEquals(2, taxonomy.getNode(a).size());

	}

	@Test
	public void testNewClassUnsatisfiable() throws ElkException {
		TestChangesLoader loader = new TestChangesLoader();

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(loader,
				new LoggingStageExecutor());

		reasoner.setAllowIncrementalMode(false);

		ElkClass a = createElkClass("A");
		ElkClass b = createElkClass("B");

		loader.add(objectFactory.getSubClassOfAxiom(a, b));

		LOGGER_.trace("********************* Initial taxonomy computation *********************");
		
		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(b)));

		reasoner.setAllowIncrementalMode(true);

		TestChangesLoader changeLoader = new TestChangesLoader();
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		ElkClass c = createElkClass("C");
		ElkClass d = createElkClass("D");

		changeLoader.add(
				objectFactory.getDisjointClassesAxiom(Arrays.asList(c, d, c)));
		
		LOGGER_.trace("********** Taxonomy re-computation after incremental change ************");
		
		taxonomy = reasoner.getTaxonomy();

		assertSame(taxonomy.getBottomNode(), taxonomy.getNode(c));
		assertNotSame(taxonomy.getBottomNode(), taxonomy.getNode(d));
	}

	@Test
	public void testDeleteFromForest() throws ElkException, IOException {
		InputStream stream = null;
		String toDelete = "Prefix(:=<http://www.test.com/schema#>) Ontology(\n"
				+ "SubClassOf(ObjectSomeValuesFrom(:has-color :brown) :brown-thing) \n"
				+ "SubClassOf(:green :color) \n" + ")";
		ElkClass tree = objectFactory
				.getClass(
						new ElkAbbreviatedIri(
								new ElkPrefixImpl(":",
										new ElkFullIri(
												"http://www.test.com/schema#")),
								"tree"));
		ElkClass greenThing = objectFactory
				.getClass(
						new ElkAbbreviatedIri(
								new ElkPrefixImpl(":",
										new ElkFullIri(
												"http://www.test.com/schema#")),
								"green-thing"));

		try {
			stream = getClass().getClassLoader()
					.getResourceAsStream("incremental/forest.owl");

			List<ElkAxiom> ontology = loadAxioms(stream);
			List<ElkAxiom> deletions = loadAxioms(new StringReader(toDelete));
			TestChangesLoader initialLoader = new TestChangesLoader();

			Reasoner reasoner = TestReasonerUtils.createTestReasoner(
					initialLoader, new LoggingStageExecutor());

			reasoner.setAllowIncrementalMode(false);

			for (ElkAxiom axiom : ontology) {
				initialLoader.add(axiom);
			}

			LOGGER_.trace("********************* Initial taxonomy computation *********************");
			
			Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

			assertTrue(taxonomy.getNode(tree).getDirectSuperNodes()
					.contains(taxonomy.getNode(greenThing)));

			reasoner.setAllowIncrementalMode(true);
			TestChangesLoader changeLoader = new TestChangesLoader();
			reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

			for (ElkAxiom del : deletions) {
				changeLoader.remove(del);
			}

			LOGGER_.trace("********** Taxonomy re-computation after incremental change ************");
			
			taxonomy = reasoner.getTaxonomy();

			assertTrue(taxonomy.getNode(tree).getDirectSuperNodes()
					.contains(taxonomy.getNode(greenThing)));

		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	@Test
	public void testDeleteFromKangaroo() throws ElkException, IOException {
		InputStream stream = null;
		String toDelete = "Prefix(:=<http://www.test.com/schema#>) Ontology(\n"
				// +
				// "SubClassOf(:KangarooInfant
				// ObjectIntersectionOf(ObjectSomeValuesFrom(:lives-in :Pouch)
				// :Kangaroo)) \n"
				+ "DisjointClasses(:Irrational :Rational) \n "
				+ "SubClassOf(:Kangaroo :Beast) \n" + ")";
		ElkClass maternityKangaroo = objectFactory
				.getClass(
						new ElkAbbreviatedIri(
								new ElkPrefixImpl(":",
										new ElkFullIri(
												"http://www.test.com/schema#")),
								"MaternityKangaroo"));

		try {
			stream = getClass().getClassLoader()
					.getResourceAsStream("incremental/kangaroo.owl");

			List<ElkAxiom> ontology = loadAxioms(stream);
			List<ElkAxiom> deletions = loadAxioms(new StringReader(toDelete));
			TestChangesLoader initialLoader = new TestChangesLoader();

			Reasoner reasoner = TestReasonerUtils.createTestReasoner(
					initialLoader, new LoggingStageExecutor());
			reasoner.setAllowIncrementalMode(false);

			for (ElkAxiom axiom : ontology) {
				initialLoader.add(axiom);
			}

			LOGGER_.trace("********************* Initial taxonomy computation *********************");
			
			Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

			assertSame(taxonomy.getBottomNode(),
					taxonomy.getNode(maternityKangaroo));

			reasoner.setAllowIncrementalMode(true);
			TestChangesLoader changeLoader = new TestChangesLoader();
			reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

			for (ElkAxiom del : deletions) {
				changeLoader.remove(del);
			}

			LOGGER_.trace("********* Taxonomy re-computation after incremental deletions **********");
			
			taxonomy = reasoner.getTaxonomy();

			assertNotSame(taxonomy.getBottomNode(),
					taxonomy.getNode(maternityKangaroo));

			reasoner.setAllowIncrementalMode(true);
			reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

			for (ElkAxiom del : deletions) {
				changeLoader.add(del);
			}

			LOGGER_.trace("********* Taxonomy re-computation after incremental additions **********");
			
			taxonomy = reasoner.getTaxonomy();

			assertSame(taxonomy.getBottomNode(),
					taxonomy.getNode(maternityKangaroo));

		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	@Test
	public void testDuplicateSubclassAxioms() throws ElkException {
		TestChangesLoader loader = new TestChangesLoader();

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(loader,
				new LoggingStageExecutor());
		reasoner.setAllowIncrementalMode(false);

		ElkClass a = createElkClass("A");
		ElkClass b = createElkClass("B");

		// add axiom two times
		loader.add(objectFactory.getSubClassOfAxiom(a, b))
				.add(objectFactory.getSubClassOfAxiom(a, b));

		LOGGER_.trace("********************* Initial taxonomy computation *********************");
		
		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(b)));

		// now delete it one time

		reasoner.setAllowIncrementalMode(true);
		TestChangesLoader changeLoader = new TestChangesLoader();
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		changeLoader.add(objectFactory.getSubClassOfAxiom(a, b));

		LOGGER_.trace("********* Taxonomy re-computation after incremental deletions **********");

		taxonomy = reasoner.getTaxonomy();

		// B should still be a superclass of A
		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(b)));

		// now delete it the second time

		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));
		changeLoader.add(objectFactory.getSubClassOfAxiom(a, b));

		LOGGER_.trace("********* Taxonomy re-computation after incremental deletions **********");

		taxonomy = reasoner.getTaxonomy();

		// B should still be no longer a superclass of A
		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(b)));
	}

	@Test
	public void testTaxonomyIncrementalConsistencyTaxonomy() throws ElkException {
		TestChangesLoader loader = new TestChangesLoader();

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(loader,
				new LoggingStageExecutor());
		reasoner.setAllowIncrementalMode(true);
		
		ElkClass a = objectFactory.getClass(new ElkAbbreviatedIri(p, "A"));
		ElkClass b = objectFactory.getClass(new ElkAbbreviatedIri(p, "B"));
		ElkClass c = objectFactory.getClass(new ElkAbbreviatedIri(p, "C"));
		ElkObjectProperty r = objectFactory
				.getObjectProperty(new ElkAbbreviatedIri(p, "r"));

		// add axiom
		loader.add(objectFactory.getSubClassOfAxiom(a, b));

		LOGGER_.trace("********************* Initial taxonomy computation *********************");
		
		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		// B should be a superclass of A
		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(b)));

		// make a non-incremental change

		loader = new TestChangesLoader();
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(loader));

		loader.remove(objectFactory.getSubClassOfAxiom(a, b))
				// use owl:Nothing to make consistency computation non-trivial
				.add(objectFactory.getSubClassOfAxiom(a,
						objectFactory.getOwlNothing()))
				// new subsumption
				.add(objectFactory.getSubClassOfAxiom(b, c))
				// to make sure that context b is saturated
				.add(objectFactory.getSubClassOfAxiom(
						objectFactory.getOwlThing(),
						objectFactory.getObjectSomeValuesFrom(r, b)))
				// non-incremental change
				.add(objectFactory.getTransitiveObjectPropertyAxiom(r));

		LOGGER_.trace("********** Consistency checking after non-incremental change ***********");
		
		assertFalse(reasoner.isInconsistent());
		
		// make an incremental change
		
		loader = new TestChangesLoader();
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(loader));

		// just to trigger loading
		loader.add(objectFactory.getSubClassOfAxiom(a, a));		
		
		LOGGER_.trace("************ Taxonomy computation after incremental change *************");
		
		taxonomy = reasoner.getTaxonomy();
		
		// C should be a superclass of B
		assertTrue(taxonomy.getNode(b).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));
		// A should be inconsistent
		assertTrue(taxonomy.getBottomNode().contains(a));

	}

	@Test
	public void testPropositionalAdditions() throws ElkException {
		TestChangesLoader loader = new TestChangesLoader();

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(loader,
				new LoggingStageExecutor());

		reasoner.setAllowIncrementalMode(false);

		ElkClass a = createElkClass("A");
		ElkClass b = createElkClass("B");
		ElkClass c = createElkClass("C");
		ElkClass d = createElkClass("D");

		loader.add(objectFactory.getSubClassOfAxiom(a, c))
				.add(objectFactory.getSubClassOfAxiom(c, d));

		LOGGER_.trace("********************* Initial taxonomy computation *********************");
		
		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		reasoner.setAllowIncrementalMode(true);

		TestChangesLoader changeLoader = new TestChangesLoader();
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		changeLoader.add(objectFactory.getSubClassOfAxiom(a, b))
				.add(objectFactory.getSubClassOfAxiom(b, d));

		LOGGER_.trace("********* Taxonomy re-computation after incremental additions **********");

		taxonomy = reasoner.getTaxonomy();

		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));
		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(b)));
	}

	/*
	 * Tests to check whether we can leave a context for a removed class. Here R
	 * some B is removed from the index after the first delete but B still
	 * references it via a backward link. Thus, during the 2nd delete, C is
	 * removed from (R some B)'s context but D is left there because its changes
	 * didn't get initialized (since it's not in the index). We should check
	 * that all contexts to be clean actually are cleaned and throw an error in
	 * this case.
	 */
	@Test
	public void testCleanObsoleteContexts() throws ElkException {
		TestChangesLoader loader = new TestChangesLoader();
		TestChangesLoader changeLoader = new TestChangesLoader();

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(loader,
				new PostProcessingStageExecutor(), 1);

		reasoner.setAllowIncrementalMode(false);
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		ElkClass a = createElkClass("A");
		ElkClass b = createElkClass("B");
		ElkClass c = createElkClass("C");
		ElkClass d = createElkClass("D");
		ElkObjectProperty s = createElkObjectProperty("S");
		ElkObjectProperty r = createElkObjectProperty("R");
		ElkSubClassOfAxiom axiom1 = objectFactory.getSubClassOfAxiom(a,
				objectFactory.getObjectSomeValuesFrom(s,
						objectFactory.getObjectSomeValuesFrom(r, b)));
		ElkSubClassOfAxiom axiom2 = objectFactory.getSubClassOfAxiom(c, d);
		ElkSubClassOfAxiom axiom3 = objectFactory.getSubClassOfAxiom(b, c);

		loader.add(axiom1)
				.add(objectFactory.getSubClassOfAxiom(
						objectFactory.getObjectSomeValuesFrom(r, c), c))
				.add(axiom2).add(axiom3);

		LOGGER_.trace("********************* Initial taxonomy computation *********************");
		
		reasoner.getTaxonomy();

		reasoner.setAllowIncrementalMode(true);

		changeLoader.remove(axiom1);

		LOGGER_.trace("********* Taxonomy re-computation after incremental deletions **********");

		reasoner.getTaxonomy();

		changeLoader.remove(axiom2).remove(axiom3);

		LOGGER_.trace("********* Taxonomy re-computation after incremental deletions **********");

		reasoner.getTaxonomy();
	}

	@Test
	public void testDeleteBinaryDisjointness() throws ElkException {
		TestChangesLoader loader = new TestChangesLoader();

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(loader,
				new LoggingStageExecutor());

		reasoner.setAllowIncrementalMode(false);

		ElkClass a = createElkClass("A");
		ElkClass b = createElkClass("B");
		ElkClass c = createElkClass("C");
		// add two structurally equivalent but syntactically different
		// disjointness
		ElkAxiom disjBC = objectFactory
				.getDisjointClassesAxiom(Arrays.asList(b, c));
		ElkAxiom disjCB = objectFactory
				.getDisjointClassesAxiom(Arrays.asList(c, b));

		loader.add(objectFactory.getSubClassOfAxiom(a, b))
				.add(objectFactory.getSubClassOfAxiom(a, c)).add(disjBC)
				.add(disjCB);

		LOGGER_.trace("********************* Initial taxonomy computation *********************");
		
		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		// clearly, A is unsatisfiable
		assertTrue(taxonomy.getNode(a) == taxonomy.getBottomNode());

		// now delete one disjointness, A should remain unsatisfiable
		reasoner.setAllowIncrementalMode(true);

		TestChangesLoader changeLoader = new TestChangesLoader();
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		changeLoader.remove(disjCB);

		LOGGER_.trace("********* Taxonomy re-computation after incremental deletions **********");

		taxonomy = reasoner.getTaxonomy();

		assertTrue(taxonomy.getNode(a) == taxonomy.getBottomNode());

		// delete another disjointness, A should become satisfiable again
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));
		changeLoader.remove(disjBC);

		LOGGER_.trace("********* Taxonomy re-computation after incremental deletions **********");

		taxonomy = reasoner.getTaxonomy();

		assertFalse(taxonomy.getNode(a) == taxonomy.getBottomNode());
	}

	@Test
	public void testDeleteNaryDisjointness() throws ElkException {
		try {
			TestChangesLoader loader = new TestChangesLoader();

			Reasoner reasoner = TestReasonerUtils.createTestReasoner(loader,
					new LoggingStageExecutor());

			reasoner.setAllowIncrementalMode(false);

			ElkClass a = createElkClass("A");
			ElkClass b = createElkClass("B");
			ElkClass c = createElkClass("C");
			ElkClass d = createElkClass("D");
			ElkAxiom disjABCD = objectFactory
					.getDisjointClassesAxiom(Arrays.asList(a, b, c, d));
			ElkAxiom disjACBD = objectFactory
					.getDisjointClassesAxiom(Arrays.asList(a, c, b, d));

			loader.add(objectFactory.getSubClassOfAxiom(a, b))
					.add(objectFactory.getSubClassOfAxiom(a, c))
					.add(objectFactory.getSubClassOfAxiom(c, d)).add(disjABCD)
					.add(disjACBD);

			LOGGER_.trace("********************* Initial taxonomy computation *********************");
			
			Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

			// clearly, A is unsatisfiable
			assertTrue(taxonomy.getNode(a) == taxonomy.getBottomNode());

			// now delete one disjointness, A should is still unsatisfiable
			reasoner.setAllowIncrementalMode(true);
			TestChangesLoader changeLoader = new TestChangesLoader();
			reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

			changeLoader.remove(disjABCD);
			
			LOGGER_.trace("********* Taxonomy re-computation after incremental deletions **********");
			
			taxonomy = reasoner.getTaxonomy();

			assertTrue(taxonomy.getNode(a) == taxonomy.getBottomNode());

			// now delete the other disjointness, A should is become satisfiable
			changeLoader.remove(disjACBD);
			reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));
			
			LOGGER_.trace("********* Taxonomy re-computation after incremental deletions **********");
			
			taxonomy = reasoner.getTaxonomy();

			assertFalse(taxonomy.getNode(a) == taxonomy.getBottomNode());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAddClassRemoveClass() throws ElkException {
		TestChangesLoader loader = new TestChangesLoader();

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(loader,
				new LoggingStageExecutor());

		reasoner.setAllowIncrementalMode(false);

		ElkClass a = createElkClass("A");
		ElkClass b = createElkClass("B");
		ElkClass c = createElkClass("C");
		ElkClass d = createElkClass("D");
		ElkClass e = createElkClass("E");
		ElkObjectProperty r = objectFactory
				.getObjectProperty(new ElkFullIri("R"));

		loader.add(objectFactory.getSubClassOfAxiom(b, c))
				.add(objectFactory.getSubClassOfAxiom(d, a))
				.add(objectFactory.getSubClassOfAxiom(a,
						objectFactory.getObjectSomeValuesFrom(r, b)));

		LOGGER_.trace("********************* Initial taxonomy computation *********************");
		
		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		assertTrue(taxonomy.getNode(d).getDirectSuperNodes()
				.contains(taxonomy.getNode(a)));
		// A should be deleted, E should appear, D should be a subclass of C now
		reasoner.setAllowIncrementalMode(true);
		TestChangesLoader changeLoader = new TestChangesLoader();
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		changeLoader.remove(objectFactory.getSubClassOfAxiom(d, a))
				.remove(objectFactory.getSubClassOfAxiom(a,
						objectFactory.getObjectSomeValuesFrom(r, b)))
				.add(objectFactory.getSubClassOfAxiom(d, c))
				.add(objectFactory.getSubClassOfAxiom(e, b));

		LOGGER_.trace("************ Taxonomy computation after incremental change *************");
		
		taxonomy = reasoner.getTaxonomy();

		assertNull(taxonomy.getNode(a));
		assertNotNull(taxonomy.getNode(d));
		assertNotNull(taxonomy.getNode(e));
		assertTrue(taxonomy.getNode(d).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));
	}

	@Test
	public void testPropagations() throws ElkException {

		TestChangesLoader loader = new TestChangesLoader();

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(loader,
				new LoggingStageExecutor(), 1);

		reasoner.setAllowIncrementalMode(false);

		ElkClass a = createElkClass("A");
		ElkClass b = createElkClass("B");
		ElkClass c = createElkClass("C");
		ElkClass d = createElkClass("D");
		ElkObjectProperty r = createElkObjectProperty("r");

		ElkAxiom axAsubRsomeB = objectFactory.getSubClassOfAxiom(a,
				objectFactory.getObjectSomeValuesFrom(r, b));
		ElkAxiom axBsubC = objectFactory.getSubClassOfAxiom(b, c);
		ElkAxiom axRsomeCsubD = objectFactory.getSubClassOfAxiom(
				objectFactory.getObjectSomeValuesFrom(r, c), d);

		loader.add(axBsubC).add(axRsomeCsubD);

		LOGGER_.trace("********************* Initial taxonomy computation *********************");
		
		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		// bottom node is the only sub-node of D
		assertEquals(1, taxonomy.getNode(d).getDirectSubNodes().size());
		assertTrue(taxonomy.getNode(d).getDirectSubNodes()
				.contains(taxonomy.getNode(objectFactory.getOwlNothing())));

		reasoner.setAllowIncrementalMode(true);
		TestChangesLoader changeLoader = new TestChangesLoader();
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		changeLoader.add(axAsubRsomeB);

		LOGGER_.trace("********* Taxonomy re-computation after incremental additions **********");		
		
		taxonomy = reasoner.getTaxonomy();

		// now A is now a sub-node of D
		assertEquals(1, taxonomy.getNode(d).getDirectSubNodes().size());
		assertTrue(taxonomy.getNode(d).getDirectSubNodes()
				.contains(taxonomy.getNode(a)));

	}

	/*
	 * This test was to reveal incorrect incremental construction of the
	 * taxonomy: although the saturation for the members of the node for X = [X]
	 * does not change during the incremental update, the members of the node
	 * for X change, so the node should be re-created.
	 */
	@Test
	public void testEquivalences() throws ElkException {

		TestChangesLoader loader = new TestChangesLoader();

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(loader,
				new LoggingStageExecutor(), 1);

		reasoner.setAllowIncrementalMode(false);

		ElkClass x = createElkClass("X");
		ElkClass y = createElkClass("Y");

		ElkAxiom axXsubY = objectFactory.getSubClassOfAxiom(x, y);
		ElkAxiom axYsubX = objectFactory.getSubClassOfAxiom(y, x);

		loader.add(axXsubY);

		LOGGER_.trace("********************* Initial taxonomy computation *********************");
		
		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		// node for X = [X]
		assertEquals(1, taxonomy.getNode(x).size());

		reasoner.setAllowIncrementalMode(true);
		TestChangesLoader changeLoader = new TestChangesLoader();
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		LOGGER_.trace("********* Taxonomy re-computation after incremental additions **********");
		
		changeLoader.add(axYsubX);

		taxonomy = reasoner.getTaxonomy();

		// node for X = [X,Y]
		assertEquals(2, taxonomy.getNode(y).size());

	}

	@Test
	public void testEquivalencesPropagations() throws ElkException {

		TestChangesLoader loader = new TestChangesLoader();

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(loader,
				new LoggingStageExecutor(), 1);

		reasoner.setAllowIncrementalMode(false);

		ElkClass x = createElkClass("X");
		ElkClass y = createElkClass("Y");
		ElkClass a = createElkClass("A");
		ElkClass b = createElkClass("B");
		ElkObjectProperty r = createElkObjectProperty("r");

		ElkAxiom axXeqAandRsomeB = objectFactory.getEquivalentClassesAxiom(x,
				objectFactory.getObjectIntersectionOf(a,
						objectFactory.getObjectSomeValuesFrom(r, b)));
		ElkAxiom axYeqA = objectFactory.getEquivalentClassesAxiom(y, a);
		ElkAxiom axYsubRsomeB = objectFactory.getSubClassOfAxiom(y,
				objectFactory.getObjectSomeValuesFrom(r, b));

		loader.add(axXeqAandRsomeB).add(axYeqA);

		LOGGER_.trace("********************* Initial taxonomy computation *********************");
		
		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		// Y = A
		assertEquals(2, taxonomy.getNode(y).size());

		reasoner.setAllowIncrementalMode(true);
		TestChangesLoader changeLoader = new TestChangesLoader();
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		changeLoader.add(axYsubRsomeB);

		LOGGER_.trace("********* Taxonomy re-computation after incremental additions **********");		
		
		taxonomy = reasoner.getTaxonomy();

		// Y = X = A
		assertEquals(3, taxonomy.getNode(y).size());

	}

	/*
	 * This tests that removing a backward link unsaturates its source context,
	 * not the context where the link is stored
	 */
	@Test
	public void testDeleteBackwardLinkAndModifySourceContext()
			throws ElkException {
		TestChangesLoader loader = new TestChangesLoader();

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(loader,
				new LoggingStageExecutor());

		reasoner.setAllowIncrementalMode(false);

		ElkClass a = createElkClass("A");
		ElkClass b = createElkClass("B");
		ElkClass c = createElkClass("C");
		ElkClass d = createElkClass("D");
		ElkClass e = createElkClass("E");
		ElkObjectProperty r = objectFactory
				.getObjectProperty(new ElkFullIri("R"));
		ElkAxiom toDelete = objectFactory.getSubClassOfAxiom(b,
				objectFactory.getObjectSomeValuesFrom(r, c));
		ElkAxiom toAdd1 = objectFactory.getSubClassOfAxiom(
				objectFactory.getObjectSomeValuesFrom(r, d), e);
		ElkAxiom toAdd2 = objectFactory.getSubClassOfAxiom(c, d);

		loader.add(objectFactory.getSubClassOfAxiom(a,
				objectFactory.getObjectSomeValuesFrom(r, b)))
				.add(objectFactory.getSubClassOfAxiom(a,
						objectFactory.getObjectSomeValuesFrom(r, c)))
				.add(toDelete)
				.add(objectFactory.getSubObjectPropertyOfAxiom(objectFactory
						.getObjectPropertyChain(Arrays.asList(r, r)), r));

		LOGGER_.trace("********************* Initial taxonomy computation *********************");
		
		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		reasoner.setAllowIncrementalMode(true);
		TestChangesLoader changeLoader = new TestChangesLoader();
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		changeLoader.remove(toDelete).add(toAdd1).add(toAdd2);

		LOGGER_.trace("************ Taxonomy computation after incremental change *************");

		taxonomy = reasoner.getTaxonomy();

		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(e)));
	}

	/**
	 * Tests that redundant existential decomposition rules are dealt with
	 * properly
	 * 
	 * @throws ElkException
	 * @throws IOException
	 */
	@Test
	public void testRedundantExistentialDecomposition()
			throws ElkException, IOException {
		String ontology = "Prefix(:=<http://www.test.com/schema#>) Ontology(\n"
				+ "SubClassOf(:A ObjectSomeValuesFrom(:R :B)) "
				// use filler for which the context should not be created by
				// classification
				+ "SubClassOf(:B ObjectIntersectionOf(:C :C)) "
				// decomposition optimization should prevent creating a context
				// for B & C
				+ "SubClassOf(ObjectSomeValuesFrom(:R ObjectIntersectionOf(:C :C)) :D) "
				+ ")";

		ElkClass A = objectFactory
				.getClass(new ElkFullIri("http://www.test.com/schema#A"));
		ElkClass B = objectFactory
				.getClass(new ElkFullIri("http://www.test.com/schema#B"));
		ElkClass C = objectFactory
				.getClass(new ElkFullIri("http://www.test.com/schema#C"));
		ElkClass D = objectFactory
				.getClass(new ElkFullIri("http://www.test.com/schema#D"));

		List<ElkAxiom> axioms = loadAxioms(new StringReader(ontology));
		TestChangesLoader initialLoader = new TestChangesLoader();

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(initialLoader,
				new LoggingStageExecutor());

		reasoner.setAllowIncrementalMode(false);

		for (ElkAxiom axiom : axioms) {
			initialLoader.add(axiom);
		}

		LOGGER_.trace("********************* Initial taxonomy computation *********************");
		
		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		assertTrue(taxonomy.getNode(B).getDirectSuperNodes()
				.contains(taxonomy.getNode(C)));
		assertTrue(taxonomy.getNode(A).getDirectSuperNodes()
				.contains(taxonomy.getNode(D)));

		reasoner.setAllowIncrementalMode(true);
		TestChangesLoader changeLoader = new TestChangesLoader();

		// adding a redundant axiom
		String toChange = "Prefix(:=<http://www.test.com/schema#>) Ontology(\n"
				// without the previous axioms the context for B & C would have
				// been created
				+ "SubClassOf(:A ObjectSomeValuesFrom(:R ObjectIntersectionOf(:C :C))) "
				+ ")";
		List<ElkAxiom> changes = loadAxioms(new StringReader(toChange));

		for (ElkAxiom axiom : changes) {
			changeLoader.add(axiom);
		}
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));
		
		LOGGER_.trace("********* Taxonomy re-computation after incremental additions **********");
		
		taxonomy = reasoner.getTaxonomy();
		// nothing should change
		assertTrue(taxonomy.getNode(B).getDirectSuperNodes()
				.contains(taxonomy.getNode(C)));
		assertTrue(taxonomy.getNode(A).getDirectSuperNodes()
				.contains(taxonomy.getNode(D)));

		// removing the redundant axiom
		for (ElkAxiom axiom : changes) {
			changeLoader.remove(axiom);
		}
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		LOGGER_.trace("********* Taxonomy re-computation after incremental deletions **********");
		
		taxonomy = reasoner.getTaxonomy();
		// nothing should change either
		assertTrue(taxonomy.getNode(B).getDirectSuperNodes()
				.contains(taxonomy.getNode(C)));
		assertTrue(taxonomy.getNode(A).getDirectSuperNodes()
				.contains(taxonomy.getNode(D)));
	}

	@Test
	public void testDeterministicLinks() throws ElkException, IOException {
		String ontology = "Prefix(:=<http://www.test.com/schema#>) Ontology(\n"
				+ "SubClassOf(:A :B) "
				+ "SubClassOf(:B ObjectSomeValuesFrom(:R :B1)) "
				+ "SubClassOf(:B1 :C1) "
				+ "SubClassOf(ObjectSomeValuesFrom(:R :C1) :F) "
				+ "SubClassOf(:A :C) " + "SubClassOf(:C :E) "
				+ "TransitiveObjectProperty(:R) "
				+ "SubClassOf(:D ObjectSomeValuesFrom(:R :A)) " + ")";

		ElkClass A = objectFactory
				.getClass(new ElkFullIri("http://www.test.com/schema#A"));
		// ElkClass C = objectFactory.getClass(new
		// ElkFullIri("http://www.test.com/schema#C"));
		ElkClass F = objectFactory
				.getClass(new ElkFullIri("http://www.test.com/schema#F"));

		List<ElkAxiom> axioms = loadAxioms(new StringReader(ontology));
		TestChangesLoader initialLoader = new TestChangesLoader();

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(initialLoader,
				new LoggingStageExecutor());

		reasoner.setAllowIncrementalMode(false);

		for (ElkAxiom axiom : axioms) {
			initialLoader.add(axiom);
		}
		
		LOGGER_.trace("********************* Initial taxonomy computation *********************");

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		assertTrue(taxonomy.getNode(A).getAllSuperNodes()
				.contains(taxonomy.getNode(F)));
		// at this point there is a backward link from B1 to A, and a (negative)
		// subsumer R some C1 for A
		// The subsumer R some C1 already exists, so it won't be processed
		// (decomposed) for A again
		// as a result, there won't be a backward link from C1 to A
		String toAdd = "Prefix(:=<http://www.test.com/schema#>) Ontology(\n"
				+ "SubClassOf(:C1 :B1) "
				+ "SubClassOf(:C ObjectSomeValuesFrom(:R :C1)) " + ")";
		List<ElkAxiom> additions = loadAxioms(new StringReader(toAdd));

		reasoner.setAllowIncrementalMode(true);

		TestChangesLoader changeLoader = new TestChangesLoader();
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		for (ElkAxiom axiom : additions) {
			changeLoader.add(axiom);
		}

		LOGGER_.trace("********* Taxonomy re-computation after incremental additions **********");		
		
		taxonomy = reasoner.getTaxonomy();

		// System.out
		// .println("\n\n\n\n===========================================");
		// this leads to cleaning of A so the backward link A <-R<- B1 will be
		// deleted
		String toDelete = "Prefix(:=<http://www.test.com/schema#>) Ontology(\n"
				+ "SubClassOf(:C :E) " + ")";
		List<ElkAxiom> deletions = loadAxioms(new StringReader(toDelete));

		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		for (ElkAxiom axiom : deletions) {
			changeLoader.remove(axiom);
		}
		
		LOGGER_.trace("********* Taxonomy re-computation after incremental deletions **********");

		taxonomy = reasoner.getTaxonomy();

		// System.out
		// .println("\n\n\n\n===========================================");

		toDelete = "Prefix(:=<http://www.test.com/schema#>) Ontology(\n"
				+ "SubClassOf(:D ObjectSomeValuesFrom(:R :A)) " + ")";

		deletions = loadAxioms(new StringReader(toDelete));

		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		for (ElkAxiom axiom : deletions) {
			changeLoader.remove(axiom);
		}

		LOGGER_.trace("********* Taxonomy re-computation after incremental deletions **********");
		
		taxonomy = reasoner.getTaxonomy();
	}

	private List<ElkAxiom> loadAxioms(InputStream stream)
			throws IOException, Owl2ParseException {
		return loadAxioms(new InputStreamReader(stream));
	}

	private List<ElkAxiom> loadAxioms(Reader reader)
			throws IOException, Owl2ParseException {
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
