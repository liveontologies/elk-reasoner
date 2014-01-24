/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
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
import org.semanticweb.elk.reasoner.incremental.TestChangesLoader;
import org.semanticweb.elk.reasoner.stages.LoggingStageExecutor;
import org.semanticweb.elk.reasoner.stages.ReasonerStageExecutor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TracingSaturationTest {
	
	
	@Test
	public void testBasicTracing() throws Exception {
		Reasoner reasoner = load("tracing/DuplicateExistential.owl");
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass d = factory.getClass(new ElkFullIri("http://example.org/D"));
			
		reasoner.explainSubsumption(a, d, TRACE_MODE.RECURSIVE);
		TracingTestUtils.checkTracingCompleteness(a, d, reasoner);
		TracingTestUtils.checkTracingMinimality(a, d, reasoner);
	}
	
	/*@Test
	public void testGalen() throws Exception {
		Reasoner reasoner = load("tracing/EL-GALEN.owl");
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		IndexedClassExpression clazz = ReasonerStateAccessor.transform(reasoner, factory.getClass(new ElkFullIri("http://www.co-ode.org/ontologies/galen#IndexFingerNail")));
		TraceState state = ReasonerStateAccessor.getTraceState(reasoner);
		
		InputProcessor<ContextTracingJob> engine = state.getContextTracingFactory().getEngine();
		engine.submit(new ContextTracingJob(clazz));
		engine.process();
	}*/
	
	
	@Test
	public void testDuplicateInferenceOfConjunction() throws Exception {
		Reasoner reasoner = load("tracing/DuplicateConjunction.owl");
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
		ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
		ElkClassExpression bAndC = factory.getObjectIntersectionOf(b,  c);
			
		reasoner.explainSubsumption(a, bAndC, TRACE_MODE.NON_RECURSIVE);
		TracingTestUtils.checkNumberOfInferences(a, bAndC, reasoner, 1);
	}
	
	
	@Test
	public void testDuplicateInferenceOfExistential() throws Exception {
		Reasoner reasoner = load("tracing/DuplicateExistential.owl");
		ElkObjectFactory factory = new ElkObjectFactoryImpl();

		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
		ElkObjectProperty r = factory.getObjectProperty(new ElkFullIri("http://example.org/R"));
		ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
		ElkClassExpression rSomeC = factory.getObjectSomeValuesFrom(r, c);
		
		reasoner.explainSubsumption(a, rSomeC, TRACE_MODE.NON_RECURSIVE);
		TracingTestUtils.checkNumberOfInferences(a, rSomeC, reasoner, 1);
		reasoner.explainSubsumption(b, c, TRACE_MODE.NON_RECURSIVE);
		// now check that we didn't get a duplicate inference in A due to tracing B
		TracingTestUtils.checkNumberOfInferences(a, rSomeC, reasoner, 1);
	}
	
	@Ignore
	@Test
	public void testDontTraceOtherContexts() throws Exception {
		Reasoner reasoner = load("tracing/DuplicateExistential.owl");
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
		ElkObjectProperty r = factory.getObjectProperty(new ElkFullIri("http://example.org/R"));
		ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
		ElkClassExpression rSomeC = factory.getObjectSomeValuesFrom(r, c);			
		
		reasoner.explainSubsumption(a, rSomeC, TRACE_MODE.NON_RECURSIVE);
		TracingTestUtils.checkNumberOfInferences(a, rSomeC, reasoner, 1);
		TracingTestUtils.checkNumberOfInferences(b, b, reasoner, 0);
	}
	
	
	@Test
	public void testDuplicateInferenceViaComposition() throws Exception {
		Reasoner reasoner = load("tracing/DuplicateComposition.owl");
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		
		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
		ElkObjectProperty r = factory.getObjectProperty(new ElkFullIri("http://example.org/R"));
		ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
		ElkClassExpression rSomeC = factory.getObjectSomeValuesFrom(r, c);
			
		reasoner.explainSubsumption(a, rSomeC, TRACE_MODE.RECURSIVE);
		TracingTestUtils.checkNumberOfInferences(a, rSomeC, reasoner, 1);
		//B must be traced recursively
		TracingTestUtils.checkNumberOfInferences(b, b, reasoner, 1);
	}
	
	
	@Test
	public void testDuplicateInferenceOfReflexiveExistential() throws Exception {
		Reasoner reasoner = load("tracing/DuplicateReflexiveExistential.owl");
		ElkObjectFactory factory = new ElkObjectFactoryImpl();

		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkObjectProperty r = factory.getObjectProperty(new ElkFullIri("http://example.org/R"));
		ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
		ElkClassExpression rSomeC = factory.getObjectSomeValuesFrom(r, c);

		reasoner.explainSubsumption(a, rSomeC, TRACE_MODE.NON_RECURSIVE);
		TracingTestUtils.checkNumberOfInferences(a, rSomeC, reasoner, 1);
	}
	
	//
	@Test
	public void testRecursiveTracingExistential() throws Exception {
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		Reasoner reasoner = load("tracing/RecursiveExistential.owl");

		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
		ElkObjectProperty r = factory.getObjectProperty(new ElkFullIri(
				"http://example.org/R"));
		ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
		ElkClassExpression rSomeC = factory.getObjectSomeValuesFrom(r, c);

		reasoner.explainSubsumption(a, rSomeC, TRACE_MODE.RECURSIVE);
		TracingTestUtils.checkNumberOfInferences(a, rSomeC, reasoner, 1);
		TracingTestUtils.checkNumberOfInferences(b, c, reasoner, 1);
	}
	
	
	@Test
	public void testRecursiveTracingComposition() throws Exception {
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		Reasoner reasoner = load("tracing/RecursiveComposition.owl");
		
		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
		ElkObjectProperty r = factory.getObjectProperty(new ElkFullIri("http://example.org/R"));
		ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
		ElkClassExpression rSomeC = factory.getObjectSomeValuesFrom(r, c);
		
		reasoner.explainSubsumption(a, rSomeC, TRACE_MODE.RECURSIVE);
		TracingTestUtils.checkNumberOfInferences(a, rSomeC, reasoner, 1);
		TracingTestUtils.checkNumberOfInferences(b, b, reasoner, 1);
	}	
	
	/*
	 */
	@Test
	public void testAvoidTracingDueToCyclicInferences() throws Exception {
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		Reasoner reasoner = load("tracing/TrivialPropagation.owl");
		
		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass a1 = factory.getClass(new ElkFullIri("http://example.org/A1"));
		ElkClass b2 = factory.getClass(new ElkFullIri("http://example.org/B2"));
		
		reasoner.explainSubsumption(a, a1, TRACE_MODE.RECURSIVE);
		TracingTestUtils.checkNumberOfInferences(b2, b2, reasoner, 0);
	}
	
	private Reasoner load(String resource) throws Exception {
		Reasoner reasoner = null;
		InputStream stream = null;
		
		try {
			stream = getClass().getClassLoader().getResourceAsStream(resource);

			List<ElkAxiom> ontology = loadAxioms(stream);
			TestChangesLoader initialLoader = new TestChangesLoader();			
			ReasonerStageExecutor executor = new LoggingStageExecutor();
			
			reasoner = TestReasonerUtils.createTestReasoner(initialLoader, executor);

			for (ElkAxiom axiom : ontology) {
				initialLoader.add(axiom);
			}

			reasoner.getTaxonomy();
		} finally {
			IOUtils.closeQuietly(stream);
		}
		
		return reasoner;
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
