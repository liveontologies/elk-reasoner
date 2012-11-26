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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
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
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.parsing.Owl2ParserAxiomProcessor;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.stages.LoggingStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyPrinter;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class LowLevelIncrementalReasoningTest {

	final ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

	@Test
	public void testBasicDeletion() throws ElkException {
		Reasoner reasoner = TestReasonerUtils.createTestReasoner(
				new LoggingStageExecutor(), 1);
		TestChangesLoader loader = new TestChangesLoader();

		reasoner.registerOntologyLoader(loader);

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
		reasoner.registerOntologyChangesLoader(loader);

		loader.remove(objectFactory.getSubClassOfAxiom(b, d));

		taxonomy = reasoner.getTaxonomy();

		assertFalse(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));
	}

	@Test
	public void testDeletePositiveExistential() throws ElkException {
		Reasoner reasoner = TestReasonerUtils.createTestReasoner(
				new LoggingStageExecutor(), 1);
		TestChangesLoader loader = new TestChangesLoader();

		reasoner.registerOntologyLoader(loader);

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
		reasoner.registerOntologyChangesLoader(loader);

		loader.remove(posExistential);

		taxonomy = reasoner.getTaxonomy();

		assertFalse(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));
	}

	@Test
	public void testNewClassUnsatisfiable() throws ElkException {
		Reasoner reasoner = TestReasonerUtils.createTestReasoner(
				new LoggingStageExecutor(), 1);
		TestChangesLoader loader = new TestChangesLoader();

		reasoner.registerOntologyLoader(loader);

		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));

		loader.add(objectFactory.getSubClassOfAxiom(a, b));

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(b)));
		loader.clear();

		reasoner.setIncrementalMode(true);
		reasoner.registerOntologyChangesLoader(loader);

		ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));
		ElkClass d = objectFactory.getClass(new ElkFullIri(":D"));
		loader.add(objectFactory.getDisjointClassesAxiom(Arrays.asList(c, d, c)));

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
			TestChangesLoader loader = new TestChangesLoader();
			Reasoner reasoner = TestReasonerUtils.createTestReasoner(
					new LoggingStageExecutor(), 1);

			reasoner.registerOntologyLoader(initialLoader);

			for (ElkAxiom axiom : ontology) {
				initialLoader.add(axiom);
			}

			Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

			try {
				Writer writer = new OutputStreamWriter(System.out);
				TaxonomyPrinter.dumpClassTaxomomy(taxonomy, writer, false);
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

			assertTrue(taxonomy.getNode(tree).getDirectSuperNodes()
					.contains(taxonomy.getNode(greenThing)));

			System.out.println("===========================================");

			reasoner.setIncrementalMode(true);

			for (ElkAxiom del : deletions) {
				loader.remove(del);
			}

			reasoner.registerOntologyChangesLoader(loader);
			taxonomy = reasoner.getTaxonomy();

			try {
				Writer writer = new OutputStreamWriter(System.out);
				TaxonomyPrinter.dumpClassTaxomomy(taxonomy, writer, false);
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

			assertTrue(taxonomy.getNode(tree).getDirectSuperNodes()
					.contains(taxonomy.getNode(greenThing)));

		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	@Test
	public void testDuplicateSubclassAxioms() throws ElkException {
		Reasoner reasoner = TestReasonerUtils.createTestReasoner(
				new LoggingStageExecutor(), 1);
		TestChangesLoader loader = new TestChangesLoader();

		reasoner.registerOntologyLoader(loader);

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
		reasoner.registerOntologyChangesLoader(loader);

		loader.add(objectFactory.getSubClassOfAxiom(a, b));

		System.out.println("===========================================");

		taxonomy = reasoner.getTaxonomy();

		/*
		 * try { Writer writer = new OutputStreamWriter(System.out);
		 * TaxonomyPrinter.dumpClassTaxomomy(taxonomy, writer, false);
		 * writer.flush(); } catch (IOException e) { e.printStackTrace(); }
		 */

		// B should still be a superclass of A
		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(b)));

		// now delete it the second time

		loader.clear();
		reasoner.setIncrementalMode(true);
		reasoner.registerOntologyChangesLoader(loader);

		loader.add(objectFactory.getSubClassOfAxiom(a, b));

		System.out.println("===========================================");

		taxonomy = reasoner.getTaxonomy();

		/*
		 * try { Writer writer = new OutputStreamWriter(System.out);
		 * TaxonomyPrinter.dumpClassTaxomomy(taxonomy, writer, false);
		 * writer.flush(); } catch (IOException e) { e.printStackTrace(); }
		 */

		// B should still be no longer a superclass of A
		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(b)));
	}

	@Test
	public void testPropositionalAdditions() throws ElkException {
		Reasoner reasoner = TestReasonerUtils.createTestReasoner(
				new LoggingStageExecutor(), 1);
		TestChangesLoader loader = new TestChangesLoader();

		reasoner.registerOntologyLoader(loader);

		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));
		ElkClass d = objectFactory.getClass(new ElkFullIri(":D"));

		loader.add(objectFactory.getSubClassOfAxiom(a, c)).add(
				objectFactory.getSubClassOfAxiom(c, d));

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		loader.clear();
		reasoner.setIncrementalMode(true);
		reasoner.registerOntologyChangesLoader(loader);

		loader.add(objectFactory.getSubClassOfAxiom(a, b)).add(
				objectFactory.getSubClassOfAxiom(b, d));

		System.out.println("===========================================");

		taxonomy = reasoner.getTaxonomy();

		/*
		 * try { Writer writer = new OutputStreamWriter(System.out);
		 * TaxonomyPrinter.dumpClassTaxomomy(taxonomy, writer, false);
		 * writer.flush(); } catch (IOException e) { e.printStackTrace(); }
		 */

		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));
		assertTrue(taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(b)));
	}

	@Test
	public void testDeleteBinaryDisjointness() throws ElkException {
		Reasoner reasoner = TestReasonerUtils.createTestReasoner(
				new LoggingStageExecutor(), 1);
		TestChangesLoader loader = new TestChangesLoader();

		reasoner.registerOntologyLoader(loader);

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
		loader.clear();

		reasoner.setIncrementalMode(true);
		reasoner.registerOntologyChangesLoader(loader);

		loader.remove(disjCB);

		System.out.println("===========================================");

		taxonomy = reasoner.getTaxonomy();

		/*
		 * try { Writer writer = new OutputStreamWriter(System.out);
		 * TaxonomyPrinter.dumpClassTaxomomy(taxonomy, writer, false);
		 * writer.flush(); } catch (IOException e) { e.printStackTrace(); }
		 */

		assertTrue(taxonomy.getNode(a) == taxonomy.getBottomNode());

		// delete another disjointness, A should become satisfiable again
		loader.clear();
		reasoner.setIncrementalMode(true);
		reasoner.registerOntologyChangesLoader(loader);

		loader.remove(disjBC);

		System.out.println("===========================================");

		taxonomy = reasoner.getTaxonomy();

		assertFalse(taxonomy.getNode(a) == taxonomy.getBottomNode());
	}

	@Test
	public void testDeleteNaryDisjointness() throws ElkException {
		Reasoner reasoner = TestReasonerUtils.createTestReasoner(
				new LoggingStageExecutor(), 1);
		TestChangesLoader loader = new TestChangesLoader();

		reasoner.registerOntologyLoader(loader);

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
		loader.clear();

		reasoner.setIncrementalMode(true);
		reasoner.registerOntologyChangesLoader(loader);

		loader.remove(disjABCD);

		System.out.println("===========================================");

		taxonomy = reasoner.getTaxonomy();

		/*
		 * try { Writer writer = new OutputStreamWriter(System.out);
		 * TaxonomyPrinter.dumpClassTaxomomy(taxonomy, writer, false);
		 * writer.flush(); } catch (IOException e) { e.printStackTrace(); }
		 */

		assertTrue(taxonomy.getNode(a) == taxonomy.getBottomNode());

		// now delete the other disjointness, A should is become satisfiable
		loader.clear();

		reasoner.setIncrementalMode(true);
		reasoner.registerOntologyChangesLoader(loader);

		loader.remove(disjACBD);

		System.out.println("===========================================");

		taxonomy = reasoner.getTaxonomy();

		/*
		 * try { Writer writer = new OutputStreamWriter(System.out);
		 * TaxonomyPrinter.dumpClassTaxomomy(taxonomy, writer, false);
		 * writer.flush(); } catch (IOException e) { e.printStackTrace(); }
		 */

		assertFalse(taxonomy.getNode(a) == taxonomy.getBottomNode());

	}

	@Test
	public void testAddClassRemoveClass() throws ElkException {
		Reasoner reasoner = TestReasonerUtils.createTestReasoner(
				new LoggingStageExecutor(), 1);
		TestChangesLoader loader = new TestChangesLoader();

		reasoner.registerOntologyLoader(loader);

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
		loader.clear();

		reasoner.setIncrementalMode(true);
		reasoner.registerOntologyChangesLoader(loader);

		loader.remove(objectFactory.getSubClassOfAxiom(d, a))
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

	/*
	 * This tests that removing a backward link unsaturates its source context,
	 * not the context where the link is stored
	 */
	@Test
	public void testDeleteBackwardLinkAndModifySourceContext()
			throws ElkException {
		Reasoner reasoner = TestReasonerUtils.createTestReasoner(
				new LoggingStageExecutor(), 1);
		TestChangesLoader loader = new TestChangesLoader();

		reasoner.registerOntologyLoader(loader);

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

		loader.clear();

		reasoner.setIncrementalMode(true);
		reasoner.registerOntologyChangesLoader(loader);

		loader.remove(toDelete).add(toAdd1).add(toAdd2);

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