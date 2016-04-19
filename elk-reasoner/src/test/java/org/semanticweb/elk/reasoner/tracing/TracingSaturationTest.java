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
import org.junit.Ignore;
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
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 *         TODO: these tests do not make much sense since they depend on the
 *         implementation of the tracing algorithm but not on its specification
 *         / requirement. Should probably be removed.
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
	public void testBasicTracing() throws Exception {
		Reasoner reasoner = TestReasonerUtils
				.loadAndClassify(TestReasonerUtils.loadAxioms("tracing/DuplicateExistential.owl"));
		ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();
		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass d = factory.getClass(new ElkFullIri("http://example.org/D"));

		ClassConclusion conclusion = reasoner.getConclusion(factory.getSubClassOfAxiom(a, d));
		reasoner.explainConclusion(conclusion);
		TracingTestUtils.checkTracingCompleteness(conclusion, reasoner);
	}

	@Test
	public void testInconsistency() throws Exception {
		Reasoner reasoner = TestReasonerUtils
				.loadAndClassify(TestReasonerUtils.loadAxioms("classification_test_input/Inconsistent.owl"));
		ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();

		reasoner.explainInconsistency();
		TracingTestUtils.checkTracingCompleteness(
				reasoner.getConclusion(factory.getSubClassOfAxiom(factory.getOwlThing(),
						factory.getOwlNothing())),
				reasoner);
	}

	@Test
	public void testDuplicateInferenceOfConjunction() throws Exception {
		Reasoner reasoner = TestReasonerUtils
				.loadAndClassify(TestReasonerUtils.loadAxioms("tracing/DuplicateConjunction.owl"));
		ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();
		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
		ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
		ElkClassExpression bAndC = factory.getObjectIntersectionOf(b, c);

		ClassConclusion aSubBAndC = reasoner.getConclusion(factory.getSubClassOfAxiom(a, bAndC));
		ClassConclusion aSubB = reasoner.getConclusion(factory.getSubClassOfAxiom(a, b));
		ClassConclusion aSubC = reasoner.getConclusion(factory.getSubClassOfAxiom(a, c));
		reasoner.explainConclusion(aSubBAndC);
		TracingTestUtils.checkNumberOfInferences(aSubB, reasoner, 1);
		TracingTestUtils.checkNumberOfInferences(aSubB, reasoner, 1);
		TracingTestUtils.checkNumberOfInferences(aSubC, reasoner, 1);
		TracingTestUtils.checkTracingCompleteness(aSubB, reasoner);
	}

	@Test
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

		ClassConclusion aSubRSomeC = reasoner.getConclusion(factory.getSubClassOfAxiom(a, rSomeC));
		reasoner.explainConclusion(aSubRSomeC);
		TracingTestUtils.checkNumberOfInferences(aSubRSomeC, reasoner, 1);
		TracingTestUtils.checkTracingCompleteness(aSubRSomeC, reasoner);
		// B must be traced recursively
		ClassConclusion bSubB = reasoner.getConclusion(factory.getSubClassOfAxiom(b, b));
		TracingTestUtils.checkNumberOfInferences(bSubB, reasoner, 1);
	}

	@Test
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

		ClassConclusion aSubRSomeC = reasoner.getConclusion(factory.getSubClassOfAxiom(a, rSomeC));
		reasoner.explainConclusion(aSubRSomeC);
		TracingTestUtils.checkNumberOfInferences(aSubRSomeC, reasoner, 1);
		TracingTestUtils.checkTracingCompleteness(aSubRSomeC, reasoner);
	}

	//
	@Test
	public void testRecursiveTracingExistential() throws Exception {
		ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();
		Reasoner reasoner = TestReasonerUtils
				.loadAndClassify(TestReasonerUtils.loadAxioms("tracing/RecursiveExistential.owl"));

		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
		ElkObjectProperty r = factory
				.getObjectProperty(new ElkFullIri("http://example.org/R"));
		ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
		ElkClassExpression rSomeC = factory.getObjectSomeValuesFrom(r, c);

		ClassConclusion aSubRSomeC = reasoner.getConclusion(factory.getSubClassOfAxiom(a, rSomeC));
		ClassConclusion bSubC = reasoner.getConclusion(factory.getSubClassOfAxiom(b, c));
		reasoner.explainConclusion(aSubRSomeC);
		TracingTestUtils.checkNumberOfInferences(aSubRSomeC, reasoner, 1);
		// b might be not traced because it is a filler
		TracingTestUtils.checkNumberOfInferences(bSubC, reasoner, 1); 
		TracingTestUtils.checkTracingCompleteness(aSubRSomeC, reasoner);
	}

	@Test
	public void testRecursiveTracingComposition() throws Exception {
		ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();
		Reasoner reasoner = TestReasonerUtils
				.loadAndClassify(TestReasonerUtils.loadAxioms("tracing/RecursiveComposition.owl"));

		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
		ElkObjectProperty r = factory
				.getObjectProperty(new ElkFullIri("http://example.org/R"));
		ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
		ElkClassExpression rSomeC = factory.getObjectSomeValuesFrom(r, c);

		ClassConclusion aSubRSomeC = reasoner.getConclusion(factory.getSubClassOfAxiom(a, rSomeC));
		ClassConclusion aSubB = reasoner.getConclusion(factory.getSubClassOfAxiom(b, b));
		reasoner.explainConclusion(aSubRSomeC);
		TracingTestUtils.checkNumberOfInferences(aSubRSomeC, reasoner, 1);
		TracingTestUtils.checkNumberOfInferences(aSubB, reasoner, 1);
		TracingTestUtils.checkTracingCompleteness(aSubRSomeC, reasoner);
	}

	@Test
	@Ignore
	public void testAvoidTracingDueToCyclicInferences() throws Exception {
		ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();
		Reasoner reasoner = TestReasonerUtils
				.loadAndClassify(TestReasonerUtils.loadAxioms("tracing/TrivialPropagation.owl"));

		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass a1 = factory.getClass(new ElkFullIri("http://example.org/A1"));
		ElkClass b2 = factory.getClass(new ElkFullIri("http://example.org/B2"));

		ClassConclusion aSubA1 = reasoner.getConclusion(factory.getSubClassOfAxiom(a, a1));
		ClassConclusion b2SubB2 = reasoner.getConclusion(factory.getSubClassOfAxiom(b2, b2));
		reasoner.explainConclusion(aSubA1);
		TracingTestUtils.checkNumberOfInferences(b2SubB2, reasoner, 0);
	}

}
