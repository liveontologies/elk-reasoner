/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.parsing.Owl2ParserAxiomProcessor;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.incremental.TestChangesLoader;
import org.semanticweb.elk.reasoner.stages.CheckTracingStage;
import org.semanticweb.elk.reasoner.stages.LoggingStageExecutor;
import org.semanticweb.elk.reasoner.stages.PostProcessingStageExecutor;
import org.semanticweb.elk.reasoner.stages.ReasonerStageExecutor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TracingSaturationTest {

	/*
	 * Runs reasoning with a post-processing stage that checks that traces of all conclusions have been fully computed.
	 * 
	 */
	@Test
	public void testBasicTracing() throws ElkException, IOException {
		InputStream stream = null;
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		
		try {
			stream = getClass().getClassLoader().getResourceAsStream(
					"classification_test_input/Existentials.owl");

			List<ElkAxiom> ontology = loadAxioms(stream);
			TestChangesLoader initialLoader = new TestChangesLoader();			

			ReasonerStageExecutor executor = new PostProcessingStageExecutor(
					PostProcessingStageExecutor.CONTEXT_TRACING,
					CheckTracingStage.class);
			//ReasonerStageExecutor executor = new LoggingStageExecutor();
			Reasoner reasoner = TestReasonerUtils.createTestReasoner(initialLoader, executor);

			for (ElkAxiom axiom : ontology) {
				initialLoader.add(axiom);
			}

			reasoner.getTaxonomy();
			
			ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
			ElkClass d = factory.getClass(new ElkFullIri("http://example.org/D"));
			
			reasoner.explainSubsumption(a, d, new BaseTracedConclusionVisitor<Void, Void>() {

				@Override
				protected Void defaultTracedVisit(TracedConclusion conclusion, Void v) {
					System.out.println(conclusion);
					
					return super.defaultTracedVisit(conclusion, v);
				}
				
			});
			

		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
	
	@Test
	public void testDuplicateInferenceOfConjunction() throws ElkException, IOException {
		InputStream stream = null;
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		
		try {
			stream = getClass().getClassLoader().getResourceAsStream(
					"tracing/DuplicateConjunction.owl");

			List<ElkAxiom> ontology = loadAxioms(stream);
			TestChangesLoader initialLoader = new TestChangesLoader();			

			/*ReasonerStageExecutor executor = new PostProcessingStageExecutor(
					PostProcessingStageExecutor.CONTEXT_TRACING,
					CheckTracingStage.class);*/
			ReasonerStageExecutor executor = new LoggingStageExecutor();
			Reasoner reasoner = TestReasonerUtils.createTestReasoner(initialLoader, executor);

			for (ElkAxiom axiom : ontology) {
				initialLoader.add(axiom);
			}

			reasoner.getTaxonomy();
			
			ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
			ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
			ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
			ElkClassExpression bAndC = factory.getObjectIntersectionOf(b,  c);
			
			reasoner.explainSubsumption(a, bAndC, new BaseTracedConclusionVisitor<Void, Void>() {

				private int counter_ = 0;
				
				@Override
				protected Void defaultTracedVisit(TracedConclusion conclusion, Void v) {
					System.out.println(InferencePrinter.print(conclusion));
					
					assertTrue("Duplicate inferences for " + conclusion, ++counter_ <= 1);
					
					return super.defaultTracedVisit(conclusion, v);
				}
				
			});
			

		} finally {
			IOUtils.closeQuietly(stream);
		}
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

			@Override
			public void finish() throws Owl2ParseException {
				// everything is processed immediately
			}
		});

		return axioms;
	}
}
