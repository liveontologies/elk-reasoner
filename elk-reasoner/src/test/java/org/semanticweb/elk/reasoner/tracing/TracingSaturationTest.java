/**
 * 
 */
package org.semanticweb.elk.reasoner.tracing;

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
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.managers.ElkObjectEntityRecyclingFactory;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 *         
 * @author Yevgeny Kazakov        
 */
public class TracingSaturationTest {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(TracingSaturationTest.class);

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
	@SuppressWarnings("static-method")
	public void testBasicTracing() throws Exception {
		Reasoner reasoner = TestReasonerUtils
				.loadAndClassify(TestReasonerUtils.loadAxioms("tracing/DuplicateExistential.owl"));
		ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();
		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass d = factory.getClass(new ElkFullIri("http://example.org/D"));

		TracingTestUtils.checkTracingCompleteness(a, d, reasoner);
	}

	@Test
	@SuppressWarnings("static-method")
	public void testInconsistency() throws Exception {
		Reasoner reasoner = TestReasonerUtils
				.loadAndClassify(TestReasonerUtils.loadAxioms("classification_test_input/Inconsistent.owl"));
		ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();

		TracingTestUtils.checkTracingCompleteness(factory.getOwlThing(),
				factory.getOwlNothing(), reasoner);		
	}

	@Test
	@SuppressWarnings("static-method")
	public void testDuplicateInferenceOfConjunction() throws Exception {
		Reasoner reasoner = TestReasonerUtils
				.loadAndClassify(TestReasonerUtils.loadAxioms("tracing/DuplicateConjunction.owl"));
		ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();
		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
	
		TracingTestUtils.checkTracingCompleteness(a, b, reasoner);		
	}

	@Test
	@SuppressWarnings("static-method")
	public void testDuplicateInferenceOfExistential() throws Exception {
		Reasoner reasoner = TestReasonerUtils
				.loadAndClassify(TestReasonerUtils.loadAxioms("tracing/DuplicateExistential.owl"));
		ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();

		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass d = factory.getClass(new ElkFullIri("http://example.org/D"));
		ElkClass e = factory.getClass(new ElkFullIri("http://example.org/E"));
		TracingTestUtils.checkTracingCompleteness(a, d, reasoner);
		TracingTestUtils.checkTracingCompleteness(e, d, reasoner);
	}

	@Test
	@SuppressWarnings("static-method")	
	public void testDuplicateInferenceViaComposition() throws Exception {
		Reasoner reasoner = TestReasonerUtils
				.loadAndClassify(TestReasonerUtils.loadAxioms("tracing/DuplicateComposition.owl"));
		ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();

		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
		ElkObjectProperty r = factory
				.getObjectProperty(new ElkFullIri("http://example.org/R"));
		ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
		ElkClassExpression rSomeC = factory.getObjectSomeValuesFrom(r, c);

		TracingTestUtils.checkTracingCompleteness(b, b, reasoner);
		TracingTestUtils.checkTracingCompleteness(a, rSomeC, reasoner);
	}

	@Test
	@SuppressWarnings("static-method")
	public void testDuplicateInferenceOfReflexiveExistential()
			throws Exception {
		Reasoner reasoner = TestReasonerUtils
				.loadAndClassify(TestReasonerUtils.loadAxioms("tracing/DuplicateReflexiveExistential.owl"));
		ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();

		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkObjectProperty r = factory
				.getObjectProperty(new ElkFullIri("http://example.org/R"));
		ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
		ElkClassExpression rSomeC = factory.getObjectSomeValuesFrom(r, c);
		TracingTestUtils.checkTracingCompleteness(a, rSomeC, reasoner);
	}

	@Test
	@SuppressWarnings("static-method")
	public void testRecursiveTracingExistential() throws Exception {
		ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();
		Reasoner reasoner = TestReasonerUtils
				.loadAndClassify(TestReasonerUtils.loadAxioms("tracing/RecursiveExistential.owl"));

		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));

		ElkObjectProperty r = factory
				.getObjectProperty(new ElkFullIri("http://example.org/R"));
		ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
		ElkClassExpression rSomeC = factory.getObjectSomeValuesFrom(r, c);

		TracingTestUtils.checkTracingCompleteness(a, rSomeC, reasoner);		
	}

	@Test
	@SuppressWarnings("static-method")
	public void testRecursiveTracingComposition() throws Exception {
		ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();
		Reasoner reasoner = TestReasonerUtils
				.loadAndClassify(TestReasonerUtils.loadAxioms("tracing/RecursiveComposition.owl"));

		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkObjectProperty r = factory
				.getObjectProperty(new ElkFullIri("http://example.org/R"));
		ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
		ElkClassExpression rSomeC = factory.getObjectSomeValuesFrom(r, c);

		TracingTestUtils.checkTracingCompleteness(a, rSomeC, reasoner);
	}

	@Test
	@SuppressWarnings("static-method")
	public void testAvoidTracingDueToCyclicInferences() throws Exception {
		ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();
		Reasoner reasoner = TestReasonerUtils
				.loadAndClassify(TestReasonerUtils.loadAxioms("tracing/TrivialPropagation.owl"));

		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass a1 = factory.getClass(new ElkFullIri("http://example.org/A1"));

		TracingTestUtils.checkTracingCompleteness(a, a1, reasoner);		
	}

}
