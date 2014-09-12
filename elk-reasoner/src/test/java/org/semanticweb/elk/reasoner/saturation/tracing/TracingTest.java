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
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.managers.ElkEntityRecycler;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.ElkInconsistentOntologyException;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DisjointSubsumerFromSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.SubClassOfSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractClassInferenceVisitor;
import org.semanticweb.elk.reasoner.stages.PostProcessingStageExecutor;
import org.semanticweb.elk.reasoner.stages.ReasonerStateAccessor;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.TestManifestCreator;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestInput;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.VoidTestOutput;
import org.semanticweb.elk.testing.io.URLTestIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests tracing and axiom binding for all atomic subsumption inferences in all
 * our standard test ontologies.
 * 
 * @author Pavel Klinov
 * 
 */
@RunWith(PolySuite.class)
public class TracingTest {

	final static String INPUT_DATA_LOCATION = "classification_test_input";
	protected static final Logger LOGGER_ = LoggerFactory	.getLogger(TracingTest.class);

	protected final TracingTestManifest manifest;

	public TracingTest(	TracingTestManifest testManifest) {
		manifest = testManifest;
	}

	@Before
	public void before() throws IOException, Owl2ParseException {
		assumeTrue(!ignore(manifest.getInput()));
	}

	protected boolean ignore(@SuppressWarnings("unused") TestInput input) {
		return false;
	}

	@Test
	@Ignore
	public void tracingTest() throws Exception {
		AxiomLoader fileLoader = new Owl2StreamLoader(
				new Owl2FunctionalStyleParserFactory(new ElkObjectFactoryImpl(
						new ElkEntityRecycler())), manifest.getInput().getInputStream());
		Reasoner reasoner = TestReasonerUtils.createTestReasoner(fileLoader, new PostProcessingStageExecutor());

		try {
			TracingTests tests = getTracingTests(reasoner.getTaxonomy());
			
			tests.accept(getTracingTestVisitor(reasoner));
		} catch (ElkInconsistentOntologyException e) {
			//swallow..
			LOGGER_.trace("The test ontology is inconsistent so tracing tests do not make sense");
		} finally {
			reasoner.shutdown();
		}
	}
	
	// test that we can always look up asserted axioms for certain types of inferences.
	// this test makes sense only if the reasoner stores the asserted axioms which may depend on a configuration option.
	@Test
	public void axiomBindingTest() throws Exception {
		AxiomLoader fileLoader = new Owl2StreamLoader(
				new Owl2FunctionalStyleParserFactory(new ElkObjectFactoryImpl(
						new ElkEntityRecycler())), manifest.getInput().getInputStream());
		Reasoner reasoner = TestReasonerUtils.createTestReasoner(fileLoader, new PostProcessingStageExecutor());

		try {
			TracingTests tests = getTracingTests(reasoner.getTaxonomy());
			
			tests.accept(getAxiomBindingTestVisitor(reasoner));
		} catch (ElkInconsistentOntologyException e) {
			//swallow..
			LOGGER_.trace("The test ontology is inconsistent so tracing tests do not make sense");
		} finally {
			reasoner.shutdown();
		}
	}	

	private TracingTestVisitor getAxiomBindingTestVisitor(final Reasoner reasoner) {
		return new TracingTestVisitor() {
			
			@Override
			public boolean visit(ElkClassExpression subsumee, 	ElkClassExpression subsumer) {
				ReasonerStateAccessor.cleanClassTraces(reasoner);
				
				try {
					LOGGER_.trace("Axiom binding test: {} => {}", subsumee, subsumer);
					
					TracingTestUtils.visitClassInferences(subsumee, subsumer, reasoner, new AbstractClassInferenceVisitor<IndexedClassExpression, Void>() {

						@Override
						protected Void defaultTracedVisit(ClassInference inference, IndexedClassExpression root) {
							return null;
						}
						
						// axioms used as side conditions of this rule, should be able to look them up
						@Override
						public Void visit(SubClassOfSubsumer<?> inference, IndexedClassExpression root) {
							ElkAxiom axiom = new SideConditionLookup().lookup(inference);
							
							assertNotNull("Failed to look up the ontology axiom for the subsumption inference " + inference, axiom);
							return null;
						}
						
						@Override
						public Void visit(DisjointSubsumerFromSubsumer inference, IndexedClassExpression root) {
							ElkAxiom axiom = new SideConditionLookup().lookup(inference);
							
							assertNotNull("Failed to look up the ontology axiom for the disjoint subsumer inference " + inference, axiom);
							return null;
						}						
					});
				} catch (ElkException e) {
					throw new RuntimeException(e);
				}
				
				return true;
			}
		};
	}

	private TracingTestVisitor getTracingTestVisitor(final Reasoner reasoner) {
		return new TracingTestVisitor() {
			
			@Override
			public boolean visit(ElkClassExpression subsumee, 	ElkClassExpression subsumer) {
				ReasonerStateAccessor.cleanClassTraces(reasoner);
				
				try {
					
					LOGGER_.trace("Tracing test: {} => {}", subsumee, subsumer);
					
					reasoner.explainSubsumption(subsumee, subsumer);
						
					TracingTestUtils.checkTracingCompleteness(subsumee, subsumer, reasoner);
					TracingTestUtils.checkTracingMinimality(subsumee, subsumer, reasoner);
				} catch (ElkException e) {
					throw new RuntimeException(e);
				}
				
				return true;
			}
		};
	}

	protected TracingTests getTracingTests(Taxonomy<ElkClass> taxonomy) {
		return new ComprehensiveSubsumptionTracingTests(taxonomy);
	}

	@Config
	public static Configuration getConfig() throws URISyntaxException,
			IOException {
		return ConfigurationUtils
				.loadFileBasedTestConfiguration(
						INPUT_DATA_LOCATION,
						TracingTest.class,
						"owl",
						new TestManifestCreator<URLTestIO, VoidTestOutput, VoidTestOutput>() {
							@Override
							public TestManifest<URLTestIO, VoidTestOutput, VoidTestOutput> create(
									URL input, URL output) throws IOException {
								// don't need an expected output for these tests
								return new TracingTestManifest(input);
							}
						});
	}
}
