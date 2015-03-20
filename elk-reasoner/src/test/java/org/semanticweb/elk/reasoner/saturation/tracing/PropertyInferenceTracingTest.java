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

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedBackwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedForwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.LeftReflexiveSubPropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexivePropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexiveToldSubObjectProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.SubObjectProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.SubPropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ToldSubPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractObjectPropertyInferenceVisitor;
import org.semanticweb.elk.reasoner.stages.ReasonerStateAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class PropertyInferenceTracingTest {
	
	private static final Logger LOGGER_ = LoggerFactory.getLogger(PropertyInferenceTracingTest.class);
	
	@Rule public TestName testName = new TestName();

	@Before
	public void beforeTest() {
		LOGGER_.trace("Starting test {}", testName.getMethodName());
	}
	
	@After
	public void afterTest() {
		LOGGER_.trace("Finishing test {}", testName.getMethodName());
	}
	
	@Test
	public void testPropertyHierarchy() throws Exception {
		Reasoner reasoner = TestReasonerUtils.loadAndClassify("tracing/DeepPropertyHierarchy.owl");
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass d = factory.getClass(new ElkFullIri("http://example.org/D"));
		final IndexedObjectProperty s = ReasonerStateAccessor.transform(reasoner, factory.getObjectProperty(new ElkFullIri("http://example.org/S")));
		final IndexedObjectProperty r = ReasonerStateAccessor.transform(reasoner, factory.getObjectProperty(new ElkFullIri("http://example.org/R")));
		final IndexedObjectProperty hh = ReasonerStateAccessor.transform(reasoner, factory.getObjectProperty(new ElkFullIri("http://example.org/HH")));
		
		reasoner.explainSubsumption(a, d);

		TracingTestUtils.checkTracingCompleteness(a, d, reasoner);
		// check that the inference S -> HH has been traced and used during unwinding
		TracingTestUtils.checkConditionOverUsedInferences(a, d, reasoner, 
				TracingTestUtils.DUMMY_CLASS_INFERENCE_CHECKER, 
				new AbstractObjectPropertyInferenceVisitor<Void, Boolean>() {

					@Override
					protected Boolean defaultTracedVisit(
							ObjectPropertyInference inference, Void input) {
						return false;
					}

					@Override
					public Boolean visit(ToldSubPropertyInference inference,
							Void input) {
						// checking that S -> HH is in the trace (i.e. is used)
						return inference.getSubPropertyChain().equals(s) && 
								inference.getSuperPropertyChain().equals(hh) &&
								inference.getPremise().getSubPropertyChain().equals(r);
					}
			
				});
	}
	
	@Test
	public void testCompositionInferences() throws Exception {
		Reasoner reasoner = TestReasonerUtils.loadAndClassify("tracing/SimpleCompositions.owl");
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
		ElkClass d = factory.getClass(new ElkFullIri("http://example.org/D"));
		ElkClass e = factory.getClass(new ElkFullIri("http://example.org/E"));
		ElkObjectProperty r = factory.getObjectProperty(new ElkFullIri("http://example.org/R"));
		ElkObjectProperty s = factory.getObjectProperty(new ElkFullIri("http://example.org/S"));
		ElkObjectProperty h = factory.getObjectProperty(new ElkFullIri("http://example.org/H"));
		ElkObjectProperty ss = factory.getObjectProperty(new ElkFullIri("http://example.org/SS"));
		ElkObjectProperty hh = factory.getObjectProperty(new ElkFullIri("http://example.org/HH"));
		ElkObjectProperty rr = factory.getObjectProperty(new ElkFullIri("http://example.org/RR"));
		ElkObjectProperty t = factory.getObjectProperty(new ElkFullIri("http://example.org/T"));
		ElkSubObjectPropertyExpression sshh = factory.getObjectPropertyChain(Arrays.asList(ss, hh));
		ElkSubObjectPropertyExpression rrsshh = factory.getObjectPropertyChain(Arrays.asList(rr, ss, hh));
		final IndexedPropertyChain sshhIndexed = ReasonerStateAccessor.transform(reasoner, sshh);
		final IndexedPropertyChain sIndexed = ReasonerStateAccessor.transform(reasoner, s);
		final IndexedPropertyChain hIndexed = ReasonerStateAccessor.transform(reasoner, h);
		final IndexedPropertyChain ssIndexed = ReasonerStateAccessor.transform(reasoner, ss);
		final IndexedPropertyChain hhIndexed = ReasonerStateAccessor.transform(reasoner, hh);
		final IndexedPropertyChain rIndexed = ReasonerStateAccessor.transform(reasoner, r);
		final IndexedPropertyChain rrIndexed = ReasonerStateAccessor.transform(reasoner, rr);
		final IndexedPropertyChain rrsshhIndexed = ReasonerStateAccessor.transform(reasoner, rrsshh);
		final IndexedPropertyChain tIndexed = ReasonerStateAccessor.transform(reasoner, t);
		final IndexedClassExpression aIndexed = ReasonerStateAccessor.transform(reasoner, a);
		final IndexedClassExpression dIndexed = ReasonerStateAccessor.transform(reasoner, d);
		
		reasoner.explainSubsumption(a, e);

		TracingTestUtils.checkTracingCompleteness(b, e, reasoner);
		// checking that S o H -> SS o HH is there
		TracingTestUtils.checkConditionOverUsedInferences(a, e, reasoner, 
				new AbstractClassInferenceVisitor<IndexedClassExpression, Boolean>() {

					@Override
					protected Boolean defaultTracedVisit(
							ClassInference conclusion,
							IndexedClassExpression input) {
						return false;
					}

					@Override
					public Boolean visit(ComposedForwardLink conclusion,
							IndexedClassExpression input) {
						// looking for the composition S o H -> SS o HH
						SubObjectProperty left = conclusion.getLeftSubObjectProperty();
						SubPropertyChain<?,?> right = conclusion.getRightSubObjectPropertyChain();
						
							return conclusion.getTarget().equals(dIndexed) &&
									left.getSubPropertyChain().equals(sIndexed) &&
									left.getSuperPropertyChain().equals(ssIndexed) &&
									right.getSubPropertyChain().equals(hIndexed) &&
									right.getSuperPropertyChain().equals(hhIndexed) &&
									conclusion.getRelation().equals(sshhIndexed);
					}
			
				}, 
				TracingTestUtils.DUMMY_PROPERTY_INFERENCE_CHECKER
				);
		
		// checking that S -> SS inference is there
		TracingTestUtils.checkConditionOverUsedInferences(a, e, reasoner,
				TracingTestUtils.DUMMY_CLASS_INFERENCE_CHECKER,
				new AbstractObjectPropertyInferenceVisitor<Void, Boolean>() {

					@Override
					protected Boolean defaultTracedVisit(
							ObjectPropertyInference inference, Void input) {
						return false;
					}

					@Override
					public Boolean visit(ToldSubPropertyInference inference,
							Void input) {
						return inference.getSubPropertyChain().equals(sIndexed) &&
								inference.getSuperPropertyChain().equals(ssIndexed);
					}
					
				});
		
		// checking that the axiom RR o SS o HH -> T is used 
		TracingTestUtils.checkConditionOverUsedInferences(a, e, reasoner, 
				new AbstractClassInferenceVisitor<IndexedClassExpression, Boolean>() {

					@Override
					protected Boolean defaultTracedVisit(ClassInference conclusion,
							IndexedClassExpression input) {
						return false;
					}

					@Override
					public Boolean visit(ComposedBackwardLink backwardLink,
							IndexedClassExpression input) {
						// check that we use the inference that A <-R- B and B -SS o HH-> D imply A <-T- D  
						return backwardLink.getRelation().equals(tIndexed) &&
								backwardLink.getSource().equals(aIndexed) &&
								backwardLink.getBackwardLink().getSource().equals(aIndexed) &&
								backwardLink.getBackwardLink().getRelation().equals(rIndexed) &&
								backwardLink.getForwardLink().getTarget().equals(dIndexed) &&
								backwardLink.getForwardLink().getRelation().equals(sshhIndexed) &&
								backwardLink.getRelation().equals(tIndexed) &&
								backwardLink.getSubPropertyChain().getSubPropertyChain().equals(rrsshhIndexed);
					}

				}, 
				new AbstractObjectPropertyInferenceVisitor<Void, Boolean>() {

					@Override
					protected Boolean defaultTracedVisit(
							ObjectPropertyInference inference, Void input) {
						return false;
					}

					@Override
					public Boolean visit(ToldSubPropertyInference inference,
							Void input) {
						return inference.getSubPropertyChain().equals(rIndexed) &&
								inference.getSuperPropertyChain().equals(rrIndexed);
					}

				});		
	}	

	@Test
	public void testReflexivePropertyInferences() throws Exception {
		Reasoner reasoner = TestReasonerUtils.loadAndClassify("tracing/ReflexivePropertyChains.owl");
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
		ElkObjectProperty h = factory.getObjectProperty(new ElkFullIri("http://example.org/H"));
		ElkObjectProperty s = factory.getObjectProperty(new ElkFullIri("http://example.org/S"));
		ElkObjectProperty rr = factory.getObjectProperty(new ElkFullIri("http://example.org/RR"));
		ElkSubObjectPropertyExpression srr = factory.getObjectPropertyChain(Arrays.asList(s, rr));
		final IndexedObjectProperty hIndexed = ReasonerStateAccessor.transform(reasoner, h);
		final IndexedObjectProperty sIndexed = ReasonerStateAccessor.transform(reasoner, s);
		final IndexedPropertyChain rrIndexed = ReasonerStateAccessor.transform(reasoner, rr);
		final IndexedPropertyChain srrIndexed = ReasonerStateAccessor.transform(reasoner, srr);
		
		reasoner.explainSubsumption(a, b);
		TracingTestUtils.checkTracingCompleteness(a, b, reasoner);
		// H must be reflexive
		TracingTestUtils.checkConditionOverUsedInferences(a, b, reasoner, 
				TracingTestUtils.DUMMY_CLASS_INFERENCE_CHECKER, 
				new AbstractObjectPropertyInferenceVisitor<Void, Boolean>() {

					@Override
					protected Boolean defaultTracedVisit(ObjectPropertyInference inference, Void input) {
						return false;
					}

					@Override
					public Boolean visit(ReflexiveToldSubObjectProperty inference, Void input) {
						// H must be entailed as reflexive because S is reflexive
						return inference.getPropertyChain().equals(hIndexed) &&
								inference.getSubProperty().getPropertyChain().equals(sIndexed);
					}
				});
		// S o RR must be reflexive
		TracingTestUtils.checkConditionOverUsedInferences(a, b, reasoner, 
				TracingTestUtils.DUMMY_CLASS_INFERENCE_CHECKER, 
				new AbstractObjectPropertyInferenceVisitor<Void, Boolean>() {

					@Override
					protected Boolean defaultTracedVisit(
							ObjectPropertyInference inference, Void input) {
						return false;
					}

					@Override
					public Boolean visit(ReflexivePropertyChainInference inference, Void input) {
						// S o RR must be entailed as reflexive because S and RR are reflexive
						return inference.getPropertyChain().equals(srrIndexed) &&
								inference.getLeftReflexiveProperty().getPropertyChain().equals(sIndexed) &&
								inference.getRightReflexivePropertyChain().getPropertyChain().equals(rrIndexed);
					}
				});		
	}
	
	@Test
	public void testReflexivePropertyChains() throws Exception {
		Reasoner reasoner = TestReasonerUtils.loadAndClassify("tracing/ReflexivityAndPropertyChains.owl");
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass d = factory.getClass(new ElkFullIri("http://example.org/D"));
		ElkObjectProperty h = factory.getObjectProperty(new ElkFullIri("http://example.org/H"));
		ElkObjectProperty s = factory.getObjectProperty(new ElkFullIri("http://example.org/S"));
		ElkObjectProperty sh = factory.getObjectProperty(new ElkFullIri("http://example.org/SH"));
		final IndexedObjectProperty hIndexed = ReasonerStateAccessor.transform(reasoner, h);
		final IndexedObjectProperty sIndexed = ReasonerStateAccessor.transform(reasoner, s);
		final IndexedPropertyChain shIndexed = ReasonerStateAccessor.transform(reasoner, sh);
		
		reasoner.explainSubsumption(a, d);
		TracingTestUtils.checkTracingCompleteness(a, d, reasoner);
		// looking for the inference that H is a sub-property of SH (because S is entailed as reflexive)
		TracingTestUtils.checkConditionOverUsedInferences(a, d, reasoner, 
				TracingTestUtils.DUMMY_CLASS_INFERENCE_CHECKER, 
				new AbstractObjectPropertyInferenceVisitor<Void, Boolean>() {

					@Override
					protected Boolean defaultTracedVisit(ObjectPropertyInference inference, Void input) {
						return false;
					}

					@Override
					public Boolean visit(LeftReflexiveSubPropertyChainInference inference, Void input) {
						return inference.getSubPropertyChain().equals(hIndexed) &&
								inference.getSuperPropertyChain().equals(shIndexed) &&
								inference.getReflexivePremise().getPropertyChain().equals(sIndexed);
					}
				});
	
	}		
	
	@Test
	public void testReflexivePropertyChains2() throws Exception {
		Reasoner reasoner = TestReasonerUtils.loadAndClassify("tracing/ReflexiveRole.owl");
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		ElkClass c1 = factory.getClass(new ElkFullIri("http://example.org/C1"));
		ElkClass f = factory.getClass(new ElkFullIri("http://example.org/F"));

		reasoner.explainSubsumption(c1, f);
		TracingTestUtils.checkTracingCompleteness(c1, f, reasoner);

	}	
	
}
