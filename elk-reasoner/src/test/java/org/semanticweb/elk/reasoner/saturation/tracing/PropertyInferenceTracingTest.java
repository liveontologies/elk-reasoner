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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.ConclusionBaseFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkComposition;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkComposition;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.AbstractObjectPropertyInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.SubPropertyChainExpandedSubObjectPropertyOf;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.AbstractClassInferenceVisitor;
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

	private static final Conclusion.Factory FACTORY_ = new ConclusionBaseFactory();
	
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
					public Boolean visit(SubPropertyChainExpandedSubObjectPropertyOf inference,
							Void input) {
						// checking that S -> HH is in the trace (i.e. is used)
						return inference.getSubChain().equals(s) && 
								inference.getSuperChain().equals(hh) &&
								inference.getPremise(FACTORY_).getSubChain().equals(r);
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

		TracingTestUtils.checkTracingCompleteness(b, e, reasoner); // b might be not traced because it is a filler
		// checking that S o H -> SS o HH is there
		TracingTestUtils.checkConditionOverUsedInferences(a, e, reasoner, 
				new AbstractClassInferenceVisitor<IndexedContextRoot, Boolean>() {

					@Override
					protected Boolean defaultTracedVisit(
							ClassInference conclusion,
							IndexedContextRoot input) {
						return false;
					}

					@Override
					public Boolean visit(ForwardLinkComposition conclusion,
							IndexedContextRoot input) {
						// looking for the composition S o H -> SS o HH
						SubPropertyChain left = conclusion.getSecondPremise(FACTORY_);
						SubPropertyChain right = conclusion.getFourthPremise(FACTORY_);
						
							return conclusion.getTarget().equals(dIndexed) &&
									left.getSubChain().equals(sIndexed) &&
									left.getSuperChain().equals(ssIndexed) &&
									right.getSubChain().equals(hIndexed) &&
									right.getSuperChain().equals(hhIndexed) &&
									conclusion.getForwardChain().equals(sshhIndexed);
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
					public Boolean visit(SubPropertyChainExpandedSubObjectPropertyOf inference,
							Void input) {
						return inference.getSubChain().equals(sIndexed) &&
								inference.getSuperChain().equals(ssIndexed);
					}
					
				});
		
		// checking that the axiom RR o SS o HH -> T is used 
		TracingTestUtils.checkConditionOverUsedInferences(a, e, reasoner, 
				new AbstractClassInferenceVisitor<IndexedContextRoot, Boolean>() {

					@Override
					protected Boolean defaultTracedVisit(ClassInference conclusion,
							IndexedContextRoot input) {
						return false;
					}

					@Override
					public Boolean visit(BackwardLinkComposition backwardLink,
							IndexedContextRoot input) {
						// check that we use the inference that A <-R- B and B -SS o HH-> D imply A <-T- D  
						return backwardLink.getBackwardRelation().equals(tIndexed) &&
								backwardLink.getOriginRoot().equals(aIndexed) &&
								backwardLink.getFirstPremise(FACTORY_).getOriginRoot().equals(aIndexed) &&
								backwardLink.getFirstPremise(FACTORY_).getBackwardRelation().equals(rIndexed) &&
								backwardLink.getThirdPremise(FACTORY_).getTarget().equals(dIndexed) &&
								backwardLink.getThirdPremise(FACTORY_).getForwardChain().equals(sshhIndexed) &&
								backwardLink.getBackwardRelation().equals(tIndexed);
					}

				}, 
				new AbstractObjectPropertyInferenceVisitor<Void, Boolean>() {

					@Override
					protected Boolean defaultTracedVisit(
							ObjectPropertyInference inference, Void input) {
						return false;
					}

					@Override
					public Boolean visit(SubPropertyChainExpandedSubObjectPropertyOf inference,
							Void input) {
						return inference.getSubChain().equals(rIndexed) &&
								inference.getSuperChain().equals(rrIndexed);
					}

				});		
	}	
			
}
