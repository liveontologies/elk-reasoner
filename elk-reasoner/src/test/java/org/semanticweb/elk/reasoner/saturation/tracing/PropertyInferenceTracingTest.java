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
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedBackwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedForwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.PropertyChainInitialization;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.SubObjectProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.SubObjectPropertyInference;
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
	public void testSimplePropertyHierarchy() throws Exception {
		Reasoner reasoner = TestReasonerUtils.loadAndClassify("tracing/DeepPropertyHierarchy.owl");
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass d = factory.getClass(new ElkFullIri("http://example.org/D"));
		final IndexedObjectProperty s = ReasonerStateAccessor.transform(reasoner, factory.getObjectProperty(new ElkFullIri("http://example.org/S")));
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
					public Boolean visit(SubObjectPropertyInference inference,
							Void input) {
						// checking that S -> HH is in the trace (i.e. is used)
						return inference.getSubProperty().equals(s) && inference.getSuperProperty().equals(hh); 
					}
			
				});
	}
	
	@Test
	public void testSimpleCompositionInferences() throws Exception {
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
						ObjectPropertyConclusion rightPropertyPremise = conclusion.getRightSubObjectPropertyChain();
						
						if (rightPropertyPremise instanceof SubObjectProperty) {
							SubObjectProperty right = (SubObjectProperty) rightPropertyPremise;
							
							return conclusion.getTarget().equals(dIndexed) &&
									left.getSubProperty().equals(sIndexed) &&
									left.getSuperProperty().equals(ssIndexed) &&
									right.getSubProperty().equals(hIndexed) &&
									right.getSuperProperty().equals(hhIndexed) &&
									conclusion.getRelation().equals(sshhIndexed);
						}
						
						return false;
					}
			
				}, 
				TracingTestUtils.DUMMY_PROPERTY_INFERENCE_CHECKER
				);
		
		// checking that SS o HH initialization inference is there
		TracingTestUtils.checkConditionOverUsedInferences(a, e, reasoner,
				TracingTestUtils.DUMMY_CLASS_INFERENCE_CHECKER,
				new AbstractObjectPropertyInferenceVisitor<Void, Boolean>() {

					@Override
					protected Boolean defaultTracedVisit(
							ObjectPropertyInference inference, Void input) {
						return false;
					}

					@Override
					public Boolean visit(PropertyChainInitialization inference,
							Void input) {
						return inference.getPropertyChain().equals(sshhIndexed);
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
						// check that we use the inference that A <-R- B and B -SS o HH-> D imply A <-T-D  
						return backwardLink.getRelation().equals(tIndexed) &&
								backwardLink.getSource().equals(aIndexed) &&
								backwardLink.getBackwardLink().getSource().equals(aIndexed) &&
								backwardLink.getBackwardLink().getRelation().equals(rIndexed) &&
								backwardLink.getForwardLink().getTarget().equals(dIndexed) &&
								backwardLink.getForwardLink().getRelation().equals(sshhIndexed) &&
								backwardLink.getRelation().equals(tIndexed) &&
								backwardLink.getCompositionInitialization().getPropertyChain().equals(rrsshhIndexed);
					}

				}, 
				new AbstractObjectPropertyInferenceVisitor<Void, Boolean>() {

					@Override
					protected Boolean defaultTracedVisit(
							ObjectPropertyInference inference, Void input) {
						return false;
					}

					@Override
					public Boolean visit(SubObjectPropertyInference inference,
							Void input) {
						// looking for R -> RR
						return inference.getSubProperty().equals(rIndexed) &&
								inference.getSuperProperty().equals(rrIndexed);
					}

				});	
		// checking that RR o SS o HH initialization inference is there
		TracingTestUtils.checkConditionOverUsedInferences(a, e, reasoner,
				TracingTestUtils.DUMMY_CLASS_INFERENCE_CHECKER,
				new AbstractObjectPropertyInferenceVisitor<Void, Boolean>() {

					@Override
					protected Boolean defaultTracedVisit(
							ObjectPropertyInference inference, Void input) {
						return false;
					}

					@Override
					public Boolean visit(PropertyChainInitialization inference,
							Void input) {
						return inference.getPropertyChain().equals(rrsshhIndexed);
					}
				});		
	}	

}
