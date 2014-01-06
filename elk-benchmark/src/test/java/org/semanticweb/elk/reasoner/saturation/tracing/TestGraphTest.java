package org.semanticweb.elk.reasoner.saturation.tracing;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.semanticweb.elk.io.IOUtils;
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
import org.semanticweb.elk.reasoner.stages.LoggingStageExecutor;
import org.semanticweb.elk.reasoner.stages.ReasonerStageExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestGraphTest {
	
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(TestGraphTest.class);

	@Test
	public void testBuildTraceGraph() throws Exception {
		TraceStore traceStore = traceSubsumption("tracing/PropertyChains.owl", "http://example.org/A", "http://example.org/D");
		TraceGraph graph = new TraceGraph(traceStore.getReader());
		
		//LOGGER_.trace(graph.getInitializationInferences().toString());
		
		assertEquals(3, graph.getInitializationInferences().size());
	}
	
	@Test
	public void testSerializeDeserializeTestGraph() throws Exception {
		TraceStore traceStore = traceSubsumption("tracing/PropertyChains.owl", "http://example.org/A", "http://example.org/D");
		TraceGraph graph = new TraceGraph(traceStore.getReader());
		String file = "/home/pavel/tmp/graph.out";
		
		TraceGraphSerializer.serialize(graph, file);
		
		TraceGraph deserialized = TraceGraphSerializer.deserialize(file);
		
		assertEquals(graph.getInitializationInferences().size(), deserialized.getInitializationInferences().size());
	}
	
	private TraceStore traceSubsumption(String ontologyResource, String subsumee, String subsumer) throws Exception {
		InputStream stream = null;
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		
		try {
			stream = getClass().getClassLoader().getResourceAsStream(ontologyResource);

			List<ElkAxiom> ontology = loadAxioms(stream);
			TestChangesLoader initialLoader = new TestChangesLoader();			

			ReasonerStageExecutor executor = new LoggingStageExecutor();
			Reasoner reasoner = TestReasonerUtils.createTestReasoner(initialLoader, executor);

			for (ElkAxiom axiom : ontology) {
				initialLoader.add(axiom);
			}

			reasoner.getTaxonomy();
			
			ElkClass sub = factory.getClass(new ElkFullIri(subsumee));
			ElkClass sup = factory.getClass(new ElkFullIri(subsumer));
			reasoner.explainSubsumption(sub, sup, new BaseTracedConclusionVisitor<Void, Void>() {

				@Override
				protected Void defaultTracedVisit(TracedConclusion conclusion, Void v) {
					LOGGER_.trace("traced inference: {}", InferencePrinter.print(conclusion));
					
					return super.defaultTracedVisit(conclusion, v);
				}
				
			}, TRACE_MODE.RECURSIVE);
			
			return reasoner.getTraceStore();

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
