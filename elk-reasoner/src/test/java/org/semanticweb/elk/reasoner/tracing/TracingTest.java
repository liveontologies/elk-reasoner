/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.tracing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.managers.ElkObjectEntityRecyclingFactory;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DerivedClassConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.stages.PostProcessingStageExecutor;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.TestManifestCreator;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestInput;
import org.semanticweb.elk.testing.TestManifestWithOutput;
import org.semanticweb.elk.testing.UrlTestInput;
import org.semanticweb.elk.testing.VoidTestOutput;
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
	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(TracingTest.class);

	protected final TracingTestManifest manifest;

	public TracingTest(TracingTestManifest testManifest) {
		manifest = testManifest;
	}

	@Before
	public void before() throws IOException, Owl2ParseException {
		assumeTrue(!ignore(manifest.getInput()));
	}

	@SuppressWarnings("static-method")
	protected boolean ignore(TestInput input) {
		return false;
	}

	@Test
	public void tracingTest() throws Exception {
		Reasoner reasoner = TestReasonerUtils.createTestReasoner(
				manifest.getInput().getUrl().openStream(),
				new PostProcessingStageExecutor());

		try {
			TracingTests tests = getTracingTests(reasoner);
			TracingTestVisitor testVisitor = getTracingTestVisitor(reasoner);

			// visit all subsumptions twice: the second time to ensure
			// that the proofs are still the same
			tests.accept(testVisitor);
			tests.accept(testVisitor);
		} finally {
			reasoner.shutdown();
		}
	}

	private TracingTestVisitor getTracingTestVisitor(final Reasoner reasoner) {
		return new TracingTestVisitor() {

			private final ElkObject.Factory factory_ = new ElkObjectEntityRecyclingFactory();

			/**
			 * collect statistics about the proofs for conclusions to match
			 * results
			 */
			private final Map<Conclusion, TracingInferenceSetMetrics> proofsStats_ = new HashMap<Conclusion, TracingInferenceSetMetrics>();

			@Override
			public void testSubsumption(ElkClass subsumee, ElkClass subsumer) {

				try {
					LOGGER_.trace("Tracing test: {} ⊑ {}", subsumee, subsumer);

					if (subsumer.equals(factory_.getOwlThing())) {
						// trivial
						return;
					}
					// else
					DerivedClassConclusionVisitor conclusionVisitor = new DerivedClassConclusionVisitor() {

						@Override
						public boolean inconsistentOwlThing(
								ClassInconsistency conclusion)
								throws ElkException {
							return traceConclsuion(conclusion);
						}

						@Override
						public boolean inconsistentIndividual(
								ClassInconsistency conclusion,
								ElkIndividual inconsistent)
								throws ElkException {
							return traceConclsuion(conclusion);
						}

						@Override
						public boolean inconsistentSubClass(
								ClassInconsistency conclusion)
								throws ElkException {
							return traceConclsuion(conclusion);
						}

						@Override
						public boolean derivedClassInclusion(
								SubClassInclusionComposed conclusion)
								throws ElkException {
							return traceConclsuion(conclusion);
						}

						boolean traceConclsuion(ClassConclusion conclusion)
								throws ElkException {
							TracingInferenceSet inferences = reasoner
									.explainConclusion(conclusion);
							TracingInferenceSetMetrics proofStats = TracingInferenceSetMetrics
									.getStatistics(inferences, conclusion);
							boolean provable = proofStats.isProvable();
							if (!provable) {
								System.out.println(TracingTestUtils.print(inferences, conclusion));
							}
							assertTrue(
									"Conclusion is not provable " + conclusion,
									provable);
							TracingInferenceSetMetrics previous = proofsStats_
									.put(conclusion, proofStats);
							if (previous != null) {
								assertEquals("Previous proof does not match!",
										previous, proofStats);
							}
							return true;
						}

					};
					reasoner.visitDerivedConclusionsForSubsumption(subsumee, subsumer,
							conclusionVisitor);

				} catch (ElkException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void testEquivalence(List<? extends ElkClass> equivalent)
					throws Exception {
				int size = equivalent.size();
				if (size <= 1) {
					return;
				}
				ElkClass first = equivalent.get(size - 1);
				for (ElkClass second : equivalent) {
					testSubsumption(first, second);
					first = second;
				}
			}

		};
	}

	@SuppressWarnings("static-method")
	protected TracingTests getTracingTests(Reasoner reasoner)
			throws ElkException {
		return new ComprehensiveSubsumptionTracingTests(reasoner);
	}

	@Config
	public static Configuration getConfig()
			throws URISyntaxException, IOException {
		return ConfigurationUtils.loadFileBasedTestConfiguration(
				INPUT_DATA_LOCATION, TracingTest.class, "owl",
				new TestManifestCreator<UrlTestInput, VoidTestOutput, VoidTestOutput>() {
					@Override
					public TestManifestWithOutput<UrlTestInput, VoidTestOutput, VoidTestOutput> create(
							URL input, URL output) throws IOException {
						// don't need an expected output for these tests
						return new TracingTestManifest(input);
					}
				});
	}
}
