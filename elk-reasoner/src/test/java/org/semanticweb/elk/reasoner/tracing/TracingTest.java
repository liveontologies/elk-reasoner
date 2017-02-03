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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.elk.ElkTestUtils;
import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.managers.ElkObjectEntityRecyclingFactory;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.SimpleManifestCreator;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestInput;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.UrlTestInput;
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

	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(TracingTest.class);

	protected final TestManifest<UrlTestInput> manifest;

	public TracingTest(final TestManifest<UrlTestInput> testManifest) {
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
				manifest.getInput().getUrl().openStream());

		try {
			TracingTests tests = getTracingTests(reasoner);
			TracingTestVisitor testVisitor = getTracingTestVisitor(reasoner);

			// visit all subsumptions twice: the second time to ensure
			// that the proofs are still the same
			tests.accept(testVisitor);
			tests.accept(testVisitor);
		} finally {
			assertTrue(reasoner.shutdown());
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

					for (final Conclusion conclusion : TracingTestUtils
							.getDerivedConclusionsForSubsumption(subsumee,
									subsumer, reasoner)) {
						TracingInferenceSet inferences = reasoner
								.explainConclusion(conclusion);
						TracingInferenceSetMetrics proofStats = TracingInferenceSetMetrics
								.getStatistics(inferences, conclusion);
						boolean provable = proofStats.isProvable();
						if (!provable) {
							System.out.println(TracingTestUtils
									.print(inferences, conclusion));
						}
						assertTrue("Conclusion is not provable " + conclusion,
								provable);
						TracingInferenceSetMetrics previous = proofsStats_
								.put(conclusion, proofStats);
						if (previous != null) {
							assertEquals("Previous proof does not match!",
									previous, proofStats);
						}
					}

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
				ElkTestUtils.TEST_INPUT_LOCATION, TracingTest.class,
				SimpleManifestCreator.INSTANCE, "owl");
	}

}
