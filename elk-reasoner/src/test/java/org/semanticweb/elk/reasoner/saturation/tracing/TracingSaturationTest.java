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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TracingSaturationTest {
	
	private static final Logger LOGGER_ = LoggerFactory.getLogger(TracingSaturationTest.class);
	
	@Rule 
	public TestName testName = new TestName();

	@Before
	public void beforeTest() {
		LOGGER_.trace("Starting test {}", testName.getMethodName());
	}
	
	@After
	public void afterTest() {
		LOGGER_.trace("Finishing test {}", testName.getMethodName());
	}
	
	@Test
	public void testBasicTracing() throws Exception {
		Reasoner reasoner = TestReasonerUtils.loadAndClassify("tracing/DuplicateExistential.owl");
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass d = factory.getClass(new ElkFullIri("http://example.org/D"));

		reasoner.explainSubsumption(a, d);
		TracingTestUtils.checkTracingCompleteness(a, d, reasoner);
		TracingTestUtils.checkTracingMinimality(a, d, reasoner);
	}
	
	@Test
	public void testInconsistency() throws Exception {
		Reasoner reasoner = TestReasonerUtils.loadAndClassify("classification_test_input/Inconsistent.owl");

		reasoner.explainInconsistency();
		TracingTestUtils.checkTracingCompleteness(PredefinedElkClass.OWL_THING, PredefinedElkClass.OWL_NOTHING, reasoner);
		TracingTestUtils.checkTracingMinimality(PredefinedElkClass.OWL_THING, PredefinedElkClass.OWL_NOTHING, reasoner);
	}
	
	@Test
	public void testDuplicateInferenceOfConjunction() throws Exception {
		Reasoner reasoner = TestReasonerUtils.loadAndClassify("tracing/DuplicateConjunction.owl");
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
		ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
		ElkClassExpression bAndC = factory.getObjectIntersectionOf(b, c);

		reasoner.explainSubsumption(a, bAndC);
		TracingTestUtils.checkNumberOfInferences(a, bAndC, reasoner, 1);
		TracingTestUtils.checkNumberOfInferences(a, b, reasoner, 1);
		TracingTestUtils.checkNumberOfInferences(a, c, reasoner, 1);
		TracingTestUtils.checkInferenceAcyclicity(reasoner);
		TracingTestUtils.checkTracingCompleteness(a, bAndC, reasoner);
	}

	@Test
	public void testDuplicateInferenceOfExistential() throws Exception {
		Reasoner reasoner = TestReasonerUtils.loadAndClassify("tracing/DuplicateExistential.owl");
		ElkObjectFactory factory = new ElkObjectFactoryImpl();

		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
		ElkObjectProperty r = factory.getObjectProperty(new ElkFullIri(
				"http://example.org/R"));
		ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
		ElkClassExpression rSomeC = factory.getObjectSomeValuesFrom(r, c);
		ElkClassExpression rSomeB = factory.getObjectSomeValuesFrom(r, b);

		reasoner.explainSubsumption(a, rSomeC);
		TracingTestUtils.checkNumberOfInferences(a, rSomeB, reasoner, 1);
		TracingTestUtils.checkNumberOfInferences(a, rSomeC, reasoner, 1);
		reasoner.explainSubsumption(b, c);
		// now check that we didn't get a duplicate inference in A due to
		// tracing B
		TracingTestUtils.checkNumberOfInferences(a, rSomeC, reasoner, 1);
		TracingTestUtils.checkInferenceAcyclicity(reasoner);
		TracingTestUtils.checkTracingCompleteness(a, rSomeB, reasoner);
		TracingTestUtils.checkTracingCompleteness(a, rSomeC, reasoner);
	}

	@Test
	public void testDuplicateInferenceViaComposition() throws Exception {
		Reasoner reasoner = TestReasonerUtils.loadAndClassify("tracing/DuplicateComposition.owl");
		ElkObjectFactory factory = new ElkObjectFactoryImpl();

		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
		ElkObjectProperty r = factory.getObjectProperty(new ElkFullIri(
				"http://example.org/R"));
		ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
		ElkClassExpression rSomeC = factory.getObjectSomeValuesFrom(r, c);

		reasoner.explainSubsumption(a, rSomeC);
		TracingTestUtils.checkNumberOfInferences(a, rSomeC, reasoner, 1);
		TracingTestUtils.checkTracingCompleteness(a, rSomeC, reasoner);
		TracingTestUtils.checkInferenceAcyclicity(reasoner);
		// B must be traced recursively
		TracingTestUtils.checkNumberOfInferences(b, b, reasoner, 1);
	}

	@Test
	public void testDuplicateInferenceOfReflexiveExistential() throws Exception {
		Reasoner reasoner = TestReasonerUtils.loadAndClassify("tracing/DuplicateReflexiveExistential.owl");
		ElkObjectFactory factory = new ElkObjectFactoryImpl();

		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkObjectProperty r = factory.getObjectProperty(new ElkFullIri(
				"http://example.org/R"));
		ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
		ElkClassExpression rSomeC = factory.getObjectSomeValuesFrom(r, c);

		reasoner.explainSubsumption(a, rSomeC);
		TracingTestUtils.checkNumberOfInferences(a, rSomeC, reasoner, 1);
		TracingTestUtils.checkTracingCompleteness(a, rSomeC, reasoner);
	}

	//
	@Test
	public void testRecursiveTracingExistential() throws Exception {
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		Reasoner reasoner = TestReasonerUtils.loadAndClassify("tracing/RecursiveExistential.owl");

		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
		ElkObjectProperty r = factory.getObjectProperty(new ElkFullIri(
				"http://example.org/R"));
		ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
		ElkClassExpression rSomeC = factory.getObjectSomeValuesFrom(r, c);

		reasoner.explainSubsumption(a, rSomeC);
		TracingTestUtils.checkNumberOfInferences(a, rSomeC, reasoner, 1);
		TracingTestUtils.checkNumberOfInferences(b, c, reasoner, 1);
		TracingTestUtils.checkTracingCompleteness(a, rSomeC, reasoner);
	}

	@Test
	public void testRecursiveTracingComposition() throws Exception {
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		Reasoner reasoner = TestReasonerUtils.loadAndClassify("tracing/RecursiveComposition.owl");

		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
		ElkObjectProperty r = factory.getObjectProperty(new ElkFullIri(
				"http://example.org/R"));
		ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
		ElkClassExpression rSomeC = factory.getObjectSomeValuesFrom(r, c);

		reasoner.explainSubsumption(a, rSomeC);
		TracingTestUtils.checkNumberOfInferences(a, rSomeC, reasoner, 1);
		TracingTestUtils.checkNumberOfInferences(b, b, reasoner, 1);
		TracingTestUtils.checkInferenceAcyclicity(reasoner);
		TracingTestUtils.checkTracingCompleteness(a, rSomeC, reasoner);
	}	
	
	/*
	 */
	@Test
	public void testAvoidTracingDueToCyclicInferences() throws Exception {		
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		Reasoner reasoner = TestReasonerUtils.loadAndClassify("tracing/TrivialPropagation.owl");

		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass a1 = factory.getClass(new ElkFullIri("http://example.org/A1"));
		ElkClass b2 = factory.getClass(new ElkFullIri("http://example.org/B2"));

		reasoner.explainSubsumption(a, a1);
		TracingTestUtils.checkNumberOfInferences(b2, b2, reasoner, 0);
		TracingTestUtils.checkInferenceAcyclicity(reasoner);
	}


}
