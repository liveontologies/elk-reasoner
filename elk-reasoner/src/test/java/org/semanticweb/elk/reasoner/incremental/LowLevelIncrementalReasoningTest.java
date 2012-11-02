/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;

import static org.junit.Assert.assertFalse;
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
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyPrinter;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class LowLevelIncrementalReasoningTest {

	final ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();
	
	@Test
	public void testBasicDeletion() throws ElkException {
		Reasoner reasoner = TestReasonerUtils.createTestReasoner(new LoggingStageExecutor(), 1);
		TestChangesLoader loader = new TestChangesLoader();
		
		reasoner.registerOntologyLoader(loader);
		
		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));
		ElkClass d = objectFactory.getClass(new ElkFullIri(":D"));
		ElkObjectProperty r = objectFactory.getObjectProperty(new ElkFullIri("R"));
		
		loader.add(objectFactory.getSubClassOfAxiom(b, d))
			.add(objectFactory.getSubClassOfAxiom(a, objectFactory.getObjectSomeValuesFrom(r, b)))
			.add(objectFactory.getSubClassOfAxiom(objectFactory.getObjectSomeValuesFrom(r, d), c));

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();
		
		assertTrue(taxonomy.getNode(a).getDirectSuperNodes().contains(taxonomy.getNode(c)));
		// now delete B [= D, should retract A [= C
		loader.clear();
		
		reasoner.setIncrementalMode(true);
		reasoner.registerOntologyChangesLoader(loader);
		
		loader.remove(objectFactory.getSubClassOfAxiom(b, d));
		
		taxonomy = reasoner.getTaxonomy();
		
		assertFalse(taxonomy.getNode(a).getDirectSuperNodes().contains(taxonomy.getNode(c)));
	}
	
	@Test
	public void testDeleteFromForest() throws ElkException, IOException {
		InputStream stream = null;
		String toDelete = "Prefix(test:=<http://www.test.com/schema#>) Ontology(\n" 
		+ "SubClassOf(ObjectSomeValuesFrom(<test:has-color> <test:brown>) <test:brown-thing>) \n" 
		+ "SubClassOf(<test:green> <test:color>) \n" 
		+ ")";
		ElkClass tree = objectFactory.getClass(new ElkFullIri("test:tree"));
		ElkClass greenThing = objectFactory.getClass(new ElkFullIri("test:green-thing"));
		
		try {
			stream = getClass().getClassLoader().getResourceAsStream("incremental/forest.owl");
			
			List<ElkAxiom> ontology = loadAxioms(stream);
			List<ElkAxiom> deletions = loadAxioms(new StringReader(toDelete));
			TestChangesLoader initialLoader = new TestChangesLoader();		
			TestChangesLoader loader = new TestChangesLoader();
			Reasoner reasoner = TestReasonerUtils.createTestReasoner(new SimpleStageExecutor(), 1);
			
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
			
			assertTrue(taxonomy.getNode(tree).getDirectSuperNodes().contains(taxonomy.getNode(greenThing)));			
			
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
			
			assertTrue(taxonomy.getNode(tree).getDirectSuperNodes().contains(taxonomy.getNode(greenThing)));
			
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
	
	@Test
	public void testPropositionalAdditions() throws ElkException {
		Reasoner reasoner = TestReasonerUtils.createTestReasoner(new LoggingStageExecutor(), 1);
		TestChangesLoader loader = new TestChangesLoader();
		
		reasoner.registerOntologyLoader(loader);
		
		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));
		ElkClass d = objectFactory.getClass(new ElkFullIri(":D"));
		
		loader.add(objectFactory.getSubClassOfAxiom(a, c))
			.add(objectFactory.getSubClassOfAxiom(c, d));
		
		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();

		loader.clear();
		reasoner.setIncrementalMode(true);
		reasoner.registerOntologyChangesLoader(loader);
		
		loader.add(objectFactory.getSubClassOfAxiom(a, b))
			.add(objectFactory.getSubClassOfAxiom(b, d));
		
		System.out.println("===========================================");
		
		taxonomy = reasoner.getTaxonomy();
		
		/*try {
			Writer writer = new OutputStreamWriter(System.out);
			TaxonomyPrinter.dumpClassTaxomomy(taxonomy, writer, false);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		assertTrue(taxonomy.getNode(a).getDirectSuperNodes().contains(taxonomy.getNode(c)));
		assertTrue(taxonomy.getNode(a).getDirectSuperNodes().contains(taxonomy.getNode(b)));
	}
	
	@Test
	public void testDeleteBinaryDisjointness() throws ElkException {
		Reasoner reasoner = TestReasonerUtils.createTestReasoner(new LoggingStageExecutor(), 1);
		TestChangesLoader loader = new TestChangesLoader();
		
		reasoner.registerOntologyLoader(loader);
		
		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));
		ElkAxiom disjAxiom = objectFactory.getDisjointClassesAxiom(Arrays.asList(b, c)); 
		
		loader.add(objectFactory.getSubClassOfAxiom(a, b))
			.add(objectFactory.getSubClassOfAxiom(a, c))
			.add(disjAxiom);

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();
		
		assertTrue(taxonomy.getNode(a) == taxonomy.getBottomNode());
		// now delete disjointness, A should become satisfiable
		loader.clear();
		
		reasoner.setIncrementalMode(true);
		reasoner.registerOntologyChangesLoader(loader);
		
		loader.remove(disjAxiom);
		
		System.out.println("===========================================");
		
		taxonomy = reasoner.getTaxonomy();
		
		/*try {
			Writer writer = new OutputStreamWriter(System.out);
			TaxonomyPrinter.dumpClassTaxomomy(taxonomy, writer, false);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		assertFalse(taxonomy.getNode(a) == taxonomy.getBottomNode());
	}	
	
	@Test
	public void testDeleteNaryDisjointness() throws ElkException {
		Reasoner reasoner = TestReasonerUtils.createTestReasoner(new LoggingStageExecutor(), 1);
		TestChangesLoader loader = new TestChangesLoader();
		
		reasoner.registerOntologyLoader(loader);
		
		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));
		ElkClass d = objectFactory.getClass(new ElkFullIri(":D"));
		ElkAxiom disjAxiom = objectFactory.getDisjointClassesAxiom(Arrays.asList(a, b, c, d)); 
		
		loader.add(objectFactory.getSubClassOfAxiom(a, b))
			.add(objectFactory.getSubClassOfAxiom(a, c))
			.add(objectFactory.getSubClassOfAxiom(c, d))
			.add(disjAxiom);

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();
		
		assertTrue(taxonomy.getNode(a) == taxonomy.getBottomNode());
		// now delete disjointness, A should become satisfiable
		loader.clear();
		
		reasoner.setIncrementalMode(true);
		reasoner.registerOntologyChangesLoader(loader);
		
		loader.remove(disjAxiom);
		
		System.out.println("===========================================");
		
		taxonomy = reasoner.getTaxonomy();
		
		/*try {
			Writer writer = new OutputStreamWriter(System.out);
			TaxonomyPrinter.dumpClassTaxomomy(taxonomy, writer, false);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		assertFalse(taxonomy.getNode(a) == taxonomy.getBottomNode());
	}	
	
	
	private List<ElkAxiom> loadAxioms(InputStream stream) throws IOException, Owl2ParseException {
		return loadAxioms(new InputStreamReader(stream));
	}
	
	private List<ElkAxiom> loadAxioms(Reader reader) throws IOException, Owl2ParseException {
		Owl2Parser parser = new Owl2FunctionalStyleParserFactory().getParser(reader);
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