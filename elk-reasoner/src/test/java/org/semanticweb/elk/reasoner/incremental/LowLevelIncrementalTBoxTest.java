/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;

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
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkPrefix;
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
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class LowLevelIncrementalTBoxTest {

	final ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

/*	@Test
	public void testTmp() throws ElkException {
		Reasoner reasoner = TestReasonerUtils
				.createTestReasoner(new LoggingStageExecutor());
		TestChangesLoader loader = new TestChangesLoader();
		TestChangesLoader changeLoader = new TestChangesLoader();

		reasoner.setIncrementalMode(false);
		reasoner.registerOntologyLoader(loader);
		reasoner.registerOntologyChangesLoader(changeLoader);

		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));
		ElkAxiom axAsubB = objectFactory.getSubClassOfAxiom(a, b);
		
		loader.add(axAsubB).add(objectFactory.getSubClassOfAxiom(objectFactory.getOwlThing(), c));

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();
		loader.clear();

		reasoner.setIncrementalMode(true);

		changeLoader.remove(axAsubB);

		taxonomy = reasoner.getTaxonomy();
		
		changeLoader.clear();
		changeLoader.add(axAsubB);
		
		taxonomy = reasoner.getTaxonomy();
	}		
*/	
	
	@Test
	public void testBasicDeletion() throws ElkException {
		Reasoner reasoner = TestReasonerUtils
				.createTestReasoner(new LoggingStageExecutor());
		TestChangesLoader loader = new TestChangesLoader();
		TestChangesLoader changeLoader = new TestChangesLoader();

		reasoner.setIncrementalMode(false);
		reasoner.registerOntologyLoader(loader);
		reasoner.registerOntologyChangesLoader(changeLoader);

		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));
		ElkClass d = objectFactory.getClass(new ElkFullIri(":D"));
		ElkObjectProperty r = objectFactory.getObjectProperty(new ElkFullIri(
				"R"));

		loader.add(objectFactory.getSubClassOfAxiom(b, d))
				.add(objectFactory.getSubClassOfAxiom(a,
						objectFactory.getObjectSomeValuesFrom(r, b)))
				.add(objectFactory.getSubClassOfAxiom(
						objectFactory.getObjectSomeValuesFrom(r, d), c));

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));
		// now delete B [= D, should retract A [= C
		loader.clear();

		reasoner.setIncrementalMode(true);

		changeLoader.remove(objectFactory.getSubClassOfAxiom(b, d));

		taxonomy = reasoner.getTaxonomy();

		assertFalse(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));
	}

	@Test
	public void testDeletePositiveExistential() throws ElkException {
		Reasoner reasoner = TestReasonerUtils
				.createTestReasoner(new LoggingStageExecutor());
		TestChangesLoader loader = new TestChangesLoader();
		TestChangesLoader changeLoader = new TestChangesLoader();

		reasoner.setIncrementalMode(false);
		reasoner.registerOntologyLoader(loader);
		reasoner.registerOntologyChangesLoader(changeLoader);

		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));
		ElkClass d = objectFactory.getClass(new ElkFullIri(":D"));
		ElkClass e = objectFactory.getClass(new ElkFullIri(":E"));
		ElkObjectProperty r = objectFactory.getObjectProperty(new ElkFullIri(
				"R"));
		ElkAxiom posExistential = objectFactory.getSubClassOfAxiom(a,
				objectFactory.getObjectSomeValuesFrom(r, b));

		loader.add(objectFactory.getSubClassOfAxiom(b, d))
				.add(posExistential)
				.add(objectFactory.getSubClassOfAxiom(a, e))
				.add(objectFactory.getSubClassOfAxiom(
						objectFactory.getObjectSomeValuesFrom(r, d), c));

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));
		// now delete A subclass R some B, should retract A subclass C
		loader.clear();

		reasoner.setIncrementalMode(true);
		changeLoader.remove(posExistential);

		taxonomy = reasoner.getTaxonomy();

		assertFalse(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));
	}

	@Test
	public void testDeleteAddConjunction() throws ElkException {
		Reasoner reasoner = TestReasonerUtils.createTestReasoner(
				new LoggingStageExecutor(), 1);
		TestChangesLoader loader = new TestChangesLoader();
		TestChangesLoader changeLoader = new TestChangesLoader();

		reasoner.setIncrementalMode(false);
		reasoner.registerOntologyLoader(loader);
		reasoner.registerOntologyChangesLoader(changeLoader);

		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));

		ElkAxiom axAandBsubC = objectFactory.getSubClassOfAxiom(
				objectFactory.getObjectIntersectionOf(a, b), c);

		loader.add(objectFactory.getSubClassOfAxiom(a, b)).add(axAandBsubC);

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));

		loader.clear();

		reasoner.setIncrementalMode(true);

		changeLoader.clear();
		reasoner.registerOntologyChangesLoader(changeLoader);

		// this should not change anything
		changeLoader.remove(axAandBsubC).add(axAandBsubC);

		taxonomy = reasoner.getTaxonomy();

		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));

		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));
		assertEquals(2, taxonomy.getNode(a).getDirectSuperNodes().size());

		changeLoader.clear();
		reasoner.registerOntologyChangesLoader(changeLoader);

		// this again should not change anything
		changeLoader.remove(axAandBsubC).add(axAandBsubC);

		taxonomy = reasoner.getTaxonomy();

		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));

		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));
		assertEquals(2, taxonomy.getNode(a).getDirectSuperNodes().size());

		changeLoader.clear();
		reasoner.registerOntologyChangesLoader(changeLoader);

		// when this axiom removed, we loose node C
		changeLoader.remove(axAandBsubC);

		taxonomy = reasoner.getTaxonomy();

		assertEquals(1, taxonomy.getNode(a).getDirectSuperNodes().size());

		changeLoader.clear();
		reasoner.registerOntologyChangesLoader(changeLoader);

		// this should not change anything

		changeLoader.add(axAandBsubC).remove(axAandBsubC);

		taxonomy = reasoner.getTaxonomy();

		assertEquals(1, taxonomy.getNode(a).getDirectSuperNodes().size());
		
		changeLoader.clear();
		reasoner.registerOntologyChangesLoader(changeLoader);

		// this again should not change anything

		changeLoader.add(axAandBsubC).remove(axAandBsubC);

		taxonomy = reasoner.getTaxonomy();

		assertEquals(1, taxonomy.getNode(a).getDirectSuperNodes().size());

	}

	@Test
	public void testEquivalence() throws ElkException {
		Reasoner reasoner = TestReasonerUtils.createTestReasoner(
				new LoggingStageExecutor(), 1);
		TestChangesLoader loader = new TestChangesLoader();
		TestChangesLoader changeLoader = new TestChangesLoader();

		reasoner.setIncrementalMode(false);
		reasoner.registerOntologyLoader(loader);
		reasoner.registerOntologyChangesLoader(changeLoader);

		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));

		ElkAxiom axAeqB = objectFactory.getEquivalentClassesAxiom(a, b);

		loader.add(objectFactory.getSubClassOfAxiom(a, c)).add(axAeqB);

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		assertEquals(2, taxonomy.getNode(a).getMembers().size());

		loader.clear();

		reasoner.setIncrementalMode(true);

		changeLoader.clear();
		reasoner.registerOntologyChangesLoader(changeLoader);

		// this should not change anything
		changeLoader.remove(axAeqB).add(axAeqB);

		taxonomy = reasoner.getTaxonomy();

		assertEquals(2, taxonomy.getNode(a).getMembers().size());

		changeLoader.clear();
		reasoner.registerOntologyChangesLoader(changeLoader);

		// when this axiom removed, we loose A=B
		changeLoader.remove(axAeqB);

		taxonomy = reasoner.getTaxonomy();

		assertEquals(1, taxonomy.getNode(a).getMembers().size());

		changeLoader.clear();
		reasoner.registerOntologyChangesLoader(changeLoader);

		// this should not change anything

		changeLoader.add(axAeqB).remove(axAeqB);

		taxonomy = reasoner.getTaxonomy();

		assertEquals(1, taxonomy.getNode(a).getMembers().size());

		changeLoader.clear();
		reasoner.registerOntologyChangesLoader(changeLoader);

		// this should change it back

		changeLoader.add(axAeqB);

		taxonomy = reasoner.getTaxonomy();

		assertEquals(2, taxonomy.getNode(a).getMembers().size());

	}

	@Test
	public void testNewClassUnsatisfiable() throws ElkException {
		Reasoner reasoner = TestReasonerUtils
				.createTestReasoner(new LoggingStageExecutor());
		TestChangesLoader loader = new TestChangesLoader();
		TestChangesLoader changeLoader = new TestChangesLoader();

		reasoner.setIncrementalMode(false);
		reasoner.registerOntologyLoader(loader);
		reasoner.registerOntologyChangesLoader(changeLoader);

		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));

		loader.add(objectFactory.getSubClassOfAxiom(a, b));

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(b)));
		loader.clear();

		reasoner.setIncrementalMode(true);

		ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));
		ElkClass d = objectFactory.getClass(new ElkFullIri(":D"));

		changeLoader.add(objectFactory.getDisjointClassesAxiom(Arrays.asList(c,
				d, c)));
		taxonomy = reasoner.getTaxonomy();

		assertSame(taxonomy.getBottomNode(), taxonomy.getNode(c));
		assertNotSame(taxonomy.getBottomNode(), taxonomy.getNode(d));
	}

	@Test
	public void testDeleteFromForest() throws ElkException, IOException {
		InputStream stream = null;
		String toDelete = "Prefix(test:=<http://www.test.com/schema#>) Ontology(\n"
				+ "SubClassOf(ObjectSomeValuesFrom(<test:has-color> <test:brown>) <test:brown-thing>) \n"
				+ "SubClassOf(<test:green> <test:color>) \n" + ")";
		ElkClass tree = objectFactory.getClass(new ElkFullIri("test:tree"));
		ElkClass greenThing = objectFactory.getClass(new ElkFullIri(
				"test:green-thing"));

		try {
			stream = getClass().getClassLoader().getResourceAsStream(
					"incremental/forest.owl");

			List<ElkAxiom> ontology = loadAxioms(stream);
			List<ElkAxiom> deletions = loadAxioms(new StringReader(toDelete));
			TestChangesLoader initialLoader = new TestChangesLoader();
			TestChangesLoader changeLoader = new TestChangesLoader();

			Reasoner reasoner = TestReasonerUtils
					.createTestReasoner(new LoggingStageExecutor());

			reasoner.setIncrementalMode(false);
			reasoner.registerOntologyLoader(initialLoader);
			reasoner.registerOntologyChangesLoader(changeLoader);

			for (ElkAxiom axiom : ontology) {
				initialLoader.add(axiom);
			}

			Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

			assertTrue(taxonomy.getNode(tree).getDirectSuperNodes()
					.contains(taxonomy.getNode(greenThing)));

			// System.out.println("===========================================");

			reasoner.setIncrementalMode(true);

			for (ElkAxiom del : deletions) {
				changeLoader.remove(del);
			}

			// reasoner.registerOntologyChangesLoader(loader);
			taxonomy = reasoner.getTaxonomy();

			assertTrue(taxonomy.getNode(tree).getDirectSuperNodes()
					.contains(taxonomy.getNode(greenThing)));

		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	
	@Test
	public void testDuplicateSubclassAxioms() throws ElkException {
		Reasoner reasoner = TestReasonerUtils
				.createTestReasoner(new LoggingStageExecutor());
		TestChangesLoader loader = new TestChangesLoader();
		TestChangesLoader changeLoader = new TestChangesLoader();

		reasoner.setIncrementalMode(false);
		reasoner.registerOntologyLoader(loader);
		reasoner.registerOntologyChangesLoader(changeLoader);

		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));

		// add axiom two times
		loader.add(objectFactory.getSubClassOfAxiom(a, b)).add(
				objectFactory.getSubClassOfAxiom(a, b));

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(b)));

		// now delete it one time
		loader.clear();
		reasoner.setIncrementalMode(true);

		changeLoader.add(objectFactory.getSubClassOfAxiom(a, b));

		// System.out.println("===========================================");

		taxonomy = reasoner.getTaxonomy();

		// B should still be a superclass of A
		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(b)));

		// now delete it the second time

		loader.clear();
		// reasoner.setIncrementalMode(true);
		// reasoner.registerOntologyChangesLoader(loader);

		changeLoader.add(objectFactory.getSubClassOfAxiom(a, b));

		// System.out.println("===========================================");

		taxonomy = reasoner.getTaxonomy();

		// B should still be no longer a superclass of A
		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(b)));
	}

	@Test
	public void testPropositionalAdditions() throws ElkException {
		Reasoner reasoner = TestReasonerUtils
				.createTestReasoner(new LoggingStageExecutor());
		TestChangesLoader loader = new TestChangesLoader();
		TestChangesLoader changeLoader = new TestChangesLoader();

		reasoner.setIncrementalMode(false);
		reasoner.registerOntologyLoader(loader);
		reasoner.registerOntologyChangesLoader(changeLoader);

		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));
		ElkClass d = objectFactory.getClass(new ElkFullIri(":D"));

		loader.add(objectFactory.getSubClassOfAxiom(a, c)).add(
				objectFactory.getSubClassOfAxiom(c, d));

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		loader.clear();
		reasoner.setIncrementalMode(true);

		changeLoader.add(objectFactory.getSubClassOfAxiom(a, b)).add(
				objectFactory.getSubClassOfAxiom(b, d));

		// System.out.println("===========================================");

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
		Reasoner reasoner = TestReasonerUtils
				.createTestReasoner(new PostProcessingStageExecutor(), 1);
		TestChangesLoader loader = new TestChangesLoader();
		TestChangesLoader changeLoader = new TestChangesLoader();

		reasoner.setIncrementalMode(false);
		reasoner.registerOntologyLoader(loader);
		reasoner.registerOntologyChangesLoader(changeLoader);

		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));
		ElkClass d = objectFactory.getClass(new ElkFullIri(":D"));
		ElkObjectProperty s = objectFactory.getObjectProperty(new ElkFullIri(":S"));
		ElkObjectProperty r = objectFactory.getObjectProperty(new ElkFullIri(":R"));
		ElkSubClassOfAxiom axiom1 = objectFactory.getSubClassOfAxiom(a, objectFactory.getObjectSomeValuesFrom(s, objectFactory.getObjectSomeValuesFrom(r, b))); 
		ElkSubClassOfAxiom axiom2 = objectFactory.getSubClassOfAxiom(c, d);
		ElkSubClassOfAxiom axiom3 = objectFactory.getSubClassOfAxiom(b, c);

		loader.add(axiom1)
				.add(objectFactory.getSubClassOfAxiom(objectFactory.getObjectSomeValuesFrom(r, c), c))
				.add(axiom2)
				.add(axiom3);		

		reasoner.getTaxonomy();

		loader.clear();
		reasoner.setIncrementalMode(true);

		changeLoader.remove(axiom1);

		System.out.println("===========================================");

		reasoner.getTaxonomy();
		
		changeLoader.clear();
		changeLoader.remove(axiom2).remove(axiom3);
		
		System.out.println("===========================================");
		
		reasoner.getTaxonomy();
	}	

	@Test
	public void testDeleteBinaryDisjointness() throws ElkException {
		Reasoner reasoner = TestReasonerUtils
				.createTestReasoner(new LoggingStageExecutor());
		TestChangesLoader loader = new TestChangesLoader();
		TestChangesLoader changeLoader = new TestChangesLoader();

		reasoner.setIncrementalMode(false);
		reasoner.registerOntologyLoader(loader);
		reasoner.registerOntologyChangesLoader(changeLoader);

		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));
		// add two structurally equivalent but syntactically different
		// disjointness
		ElkAxiom disjBC = objectFactory.getDisjointClassesAxiom(Arrays.asList(
				b, c));
		ElkAxiom disjCB = objectFactory.getDisjointClassesAxiom(Arrays.asList(
				c, b));

		loader.add(objectFactory.getSubClassOfAxiom(a, b))
				.add(objectFactory.getSubClassOfAxiom(a, c)).add(disjBC)
				.add(disjCB);

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		// clearly, A is unsatisfiable
		assertTrue(taxonomy.getNode(a) == taxonomy.getBottomNode());

		// now delete one disjointness, A should remain unsatisfiable
		reasoner.setIncrementalMode(true);

		changeLoader.remove(disjCB);

		// System.out.println("===========================================");

		taxonomy = reasoner.getTaxonomy();

		assertTrue(taxonomy.getNode(a) == taxonomy.getBottomNode());

		// delete another disjointness, A should become satisfiable again
		changeLoader.remove(disjBC);

		// System.out.println("===========================================");

		taxonomy = reasoner.getTaxonomy();

		assertFalse(taxonomy.getNode(a) == taxonomy.getBottomNode());
	}

	@Test
	public void testDeleteNaryDisjointness() throws ElkException {
		try {
			Reasoner reasoner = TestReasonerUtils
					.createTestReasoner(new LoggingStageExecutor());
			TestChangesLoader loader = new TestChangesLoader();
			TestChangesLoader changeLoader = new TestChangesLoader();

			reasoner.setIncrementalMode(false);
			reasoner.registerOntologyLoader(loader);
			reasoner.registerOntologyChangesLoader(changeLoader);

			ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
			ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));
			ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));
			ElkClass d = objectFactory.getClass(new ElkFullIri(":D"));
			ElkAxiom disjABCD = objectFactory.getDisjointClassesAxiom(Arrays
					.asList(a, b, c, d));
			ElkAxiom disjACBD = objectFactory.getDisjointClassesAxiom(Arrays
					.asList(a, c, b, d));

			loader.add(objectFactory.getSubClassOfAxiom(a, b))
					.add(objectFactory.getSubClassOfAxiom(a, c))
					.add(objectFactory.getSubClassOfAxiom(c, d)).add(disjABCD)
					.add(disjACBD);

			Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

			// clearly, A is unsatisfiable
			assertTrue(taxonomy.getNode(a) == taxonomy.getBottomNode());

			// now delete one disjointness, A should is still unsatisfiable
			reasoner.setIncrementalMode(true);
			// reasoner.registerOntologyChangesLoader(loader);

			changeLoader.remove(disjABCD);
			taxonomy = reasoner.getTaxonomy();

			assertTrue(taxonomy.getNode(a) == taxonomy.getBottomNode());

			// now delete the other disjointness, A should is become satisfiable
			changeLoader.remove(disjACBD);
			taxonomy = reasoner.getTaxonomy();

			assertFalse(taxonomy.getNode(a) == taxonomy.getBottomNode());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAddClassRemoveClass() throws ElkException {
		Reasoner reasoner = TestReasonerUtils
				.createTestReasoner(new LoggingStageExecutor());
		TestChangesLoader loader = new TestChangesLoader();
		TestChangesLoader changeLoader = new TestChangesLoader();

		reasoner.setIncrementalMode(false);
		reasoner.registerOntologyLoader(loader);
		reasoner.registerOntologyChangesLoader(changeLoader);

		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));
		ElkClass d = objectFactory.getClass(new ElkFullIri(":D"));
		ElkClass e = objectFactory.getClass(new ElkFullIri(":E"));
		ElkObjectProperty r = objectFactory.getObjectProperty(new ElkFullIri(
				"R"));

		loader.add(objectFactory.getSubClassOfAxiom(b, c))
				.add(objectFactory.getSubClassOfAxiom(d, a))
				.add(objectFactory.getSubClassOfAxiom(a,
						objectFactory.getObjectSomeValuesFrom(r, b)));

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		assertTrue(taxonomy.getNode(d).getDirectSuperNodes()
				.contains(taxonomy.getNode(a)));
		// A should be deleted, E should appear, D should be a subclass of C now
		reasoner.setIncrementalMode(true);

		changeLoader
				.remove(objectFactory.getSubClassOfAxiom(d, a))
				.remove(objectFactory.getSubClassOfAxiom(a,
						objectFactory.getObjectSomeValuesFrom(r, b)))
				.add(objectFactory.getSubClassOfAxiom(d, c))
				.add(objectFactory.getSubClassOfAxiom(e, b));

		taxonomy = reasoner.getTaxonomy();

		assertNull(taxonomy.getNode(a));
		assertNotNull(taxonomy.getNode(d));
		assertNotNull(taxonomy.getNode(e));
		assertTrue(taxonomy.getNode(d).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));
	}

	@Test
	public void testPropagations() throws ElkException {

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(
				new LoggingStageExecutor(), 1);
		TestChangesLoader loader = new TestChangesLoader();
		TestChangesLoader changeLoader = new TestChangesLoader();

		reasoner.setIncrementalMode(false);
		reasoner.registerOntologyLoader(loader);
		reasoner.registerOntologyChangesLoader(changeLoader);

		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));
		ElkClass d = objectFactory.getClass(new ElkFullIri(":D"));
		ElkObjectProperty r = objectFactory.getObjectProperty(new ElkFullIri(
				":r"));

		ElkAxiom axAsubRsomeB = objectFactory.getSubClassOfAxiom(a,
				objectFactory.getObjectSomeValuesFrom(r, b));
		ElkAxiom axBsubC = objectFactory.getSubClassOfAxiom(b, c);
		ElkAxiom axRsomeCsubD = objectFactory.getSubClassOfAxiom(
				objectFactory.getObjectSomeValuesFrom(r, c), d);

		loader.add(axBsubC).add(axRsomeCsubD);

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		// bottom node is the only sub-node of D
		assertEquals(1, taxonomy.getNode(d).getDirectSubNodes().size());
		assertTrue(taxonomy.getNode(d).getDirectSubNodes()
				.contains(taxonomy.getNode(objectFactory.getOwlNothing())));

		loader.clear();

		reasoner.setIncrementalMode(true);
		changeLoader.clear();
		reasoner.registerOntologyChangesLoader(changeLoader);

		changeLoader.add(axAsubRsomeB);

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

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(
				new LoggingStageExecutor(), 1);
		TestChangesLoader loader = new TestChangesLoader();
		TestChangesLoader changeLoader = new TestChangesLoader();

		reasoner.setIncrementalMode(false);
		reasoner.registerOntologyLoader(loader);
		reasoner.registerOntologyChangesLoader(changeLoader);

		ElkClass x = objectFactory.getClass(new ElkFullIri(":X"));
		ElkClass y = objectFactory.getClass(new ElkFullIri(":Y"));

		ElkAxiom axXsubY = objectFactory.getSubClassOfAxiom(x, y);
		ElkAxiom axYsubX = objectFactory.getSubClassOfAxiom(y, x);

		loader.add(axXsubY);

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		// node for X = [X]
		assertEquals(1, taxonomy.getNode(x).getMembers().size());

		loader.clear();

		reasoner.setIncrementalMode(true);
		changeLoader.clear();
		reasoner.registerOntologyChangesLoader(changeLoader);

		changeLoader.add(axYsubX);

		taxonomy = reasoner.getTaxonomy();

		// node for X = [X,Y]
		assertEquals(2, taxonomy.getNode(y).getMembers().size());

	}

	@Test
	public void testEquivalencesPropagations() throws ElkException {

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(
				new LoggingStageExecutor(), 1);
		TestChangesLoader loader = new TestChangesLoader();
		TestChangesLoader changeLoader = new TestChangesLoader();

		reasoner.setIncrementalMode(false);
		reasoner.registerOntologyLoader(loader);
		reasoner.registerOntologyChangesLoader(changeLoader);

		ElkClass x = objectFactory.getClass(new ElkFullIri(":X"));
		ElkClass y = objectFactory.getClass(new ElkFullIri(":Y"));
		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));
		ElkObjectProperty r = objectFactory.getObjectProperty(new ElkFullIri(
				":r"));

		ElkAxiom axXeqAandRsomeB = objectFactory.getEquivalentClassesAxiom(
				x,
				objectFactory.getObjectIntersectionOf(a,
						objectFactory.getObjectSomeValuesFrom(r, b)));
		ElkAxiom axYeqA = objectFactory.getEquivalentClassesAxiom(y, a);
		ElkAxiom axYsubRsomeB = objectFactory.getSubClassOfAxiom(y,
				objectFactory.getObjectSomeValuesFrom(r, b));

		loader.add(axXeqAandRsomeB).add(axYeqA);

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		// Y = A
		assertEquals(2, taxonomy.getNode(y).getMembers().size());

		loader.clear();

		reasoner.setIncrementalMode(true);
		changeLoader.clear();
		reasoner.registerOntologyChangesLoader(changeLoader);

		changeLoader.add(axYsubRsomeB);

		taxonomy = reasoner.getTaxonomy();

		// Y = X = A
		assertEquals(3, taxonomy.getNode(y).getMembers().size());

	}

	/*
	 * This tests that removing a backward link unsaturates its source context,
	 * not the context where the link is stored
	 */
	@Test
	public void testDeleteBackwardLinkAndModifySourceContext()
			throws ElkException {
		Reasoner reasoner = TestReasonerUtils
				.createTestReasoner(new LoggingStageExecutor());
		TestChangesLoader loader = new TestChangesLoader();
		TestChangesLoader changeLoader = new TestChangesLoader();

		reasoner.setIncrementalMode(false);
		reasoner.registerOntologyLoader(loader);
		reasoner.registerOntologyChangesLoader(changeLoader);

		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));
		ElkClass d = objectFactory.getClass(new ElkFullIri(":D"));
		ElkClass e = objectFactory.getClass(new ElkFullIri(":E"));
		ElkObjectProperty r = objectFactory.getObjectProperty(new ElkFullIri(
				"R"));
		ElkAxiom toDelete = objectFactory.getSubClassOfAxiom(b,
				objectFactory.getObjectSomeValuesFrom(r, c));
		ElkAxiom toAdd1 = objectFactory.getSubClassOfAxiom(
				objectFactory.getObjectSomeValuesFrom(r, d), e);
		ElkAxiom toAdd2 = objectFactory.getSubClassOfAxiom(c, d);

		loader.add(
				objectFactory.getSubClassOfAxiom(a,
						objectFactory.getObjectSomeValuesFrom(r, b)))
				.add(objectFactory.getSubClassOfAxiom(a,
						objectFactory.getObjectSomeValuesFrom(r, c)))
				.add(toDelete)
				.add(objectFactory.getSubObjectPropertyOfAxiom(objectFactory
						.getObjectPropertyChain(Arrays.asList(r, r)), r));

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		reasoner.setIncrementalMode(true);
		changeLoader.remove(toDelete).add(toAdd1).add(toAdd2);

		taxonomy = reasoner.getTaxonomy();

		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(e)));
	}

	private List<ElkAxiom> loadAxioms(InputStream stream) throws IOException,
			Owl2ParseException {
		return loadAxioms(new InputStreamReader(stream));
	}

	private List<ElkAxiom> loadAxioms(Reader reader) throws IOException,
			Owl2ParseException {
		Owl2Parser parser = new Owl2FunctionalStyleParserFactory()
				.getParser(reader);
		final List<ElkAxiom> axioms = new ArrayList<ElkAxiom>();

		parser.accept(new Owl2ParserAxiomProcessor() {

			@Override
			public void visit(ElkPrefix elkPrefix) throws Owl2ParseException {
			}

			@Override
			public void visit(ElkAxiom elkAxiom) throws Owl2ParseException {
				axioms.add(elkAxiom);
			}
		});

		return axioms;
	}
}
