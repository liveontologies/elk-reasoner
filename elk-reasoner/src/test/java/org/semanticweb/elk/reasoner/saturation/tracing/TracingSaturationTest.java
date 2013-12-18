/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

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
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.BaseInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;
import org.semanticweb.elk.reasoner.stages.LoggingStageExecutor;
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

			/*ReasonerStageExecutor executor = new PostProcessingStageExecutor(
					PostProcessingStageExecutor.CLASS_TAXONOMY_COMPUTATION,
					CheckTracingStage.class);*/
			ReasonerStageExecutor executor = new LoggingStageExecutor();
			Reasoner reasoner = TestReasonerUtils.createTestReasoner(initialLoader, executor);

			for (ElkAxiom axiom : ontology) {
				initialLoader.add(axiom);
			}

			reasoner.getTaxonomy();
			
			ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
			ElkClass d = factory.getClass(new ElkFullIri("http://example.org/D"));
			
			reasoner.explainSubsumption(a, d, new BaseInferenceVisitor<Void>() {

				@Override
				protected Void defaultVisit(Inference inf) {
					System.out.println(inf);
					
					return super.defaultVisit(inf);
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
