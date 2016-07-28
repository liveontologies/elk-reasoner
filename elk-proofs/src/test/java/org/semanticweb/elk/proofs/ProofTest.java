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
package org.semanticweb.elk.proofs;

import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectDelegatingFactory;
import org.semanticweb.elk.owl.managers.ElkObjectEntityRecyclingFactory;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.proofs.utils.TestUtils;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.stages.PostProcessingStageExecutor;
import org.semanticweb.elk.reasoner.tracing.ComprehensiveSubsumptionTracingTests;
import org.semanticweb.elk.reasoner.tracing.TracingTestManifest;
import org.semanticweb.elk.reasoner.tracing.TracingTestVisitor;
import org.semanticweb.elk.reasoner.tracing.TracingTests;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.TestManifestCreator;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestInput;
import org.semanticweb.elk.testing.TestManifestWithOutput;
import org.semanticweb.elk.testing.UrlTestInput;
import org.semanticweb.elk.testing.VoidTestOutput;
import org.semanticweb.elk.util.collections.ArrayHashSet;

/**
 * Tests tracing and axiom binding for all atomic subsumption inferences in all
 * our standard test ontologies.
 * 
 * @author Pavel Klinov
 * 
 */
@RunWith(PolySuite.class)
public class ProofTest {

	final static String INPUT_DATA_LOCATION = "classification_test_input";

	// remove when proofs for ranges are supported
	static final String[] IGNORE_LIST = { "kangaroo.owl",
			"PropertyRangesHierarchy.owl", "ReflexivePropertyRanges.owl" };

	static {
		Arrays.sort(IGNORE_LIST);
	}

	protected final TracingTestManifest manifest;

	public ProofTest(TracingTestManifest testManifest) {
		manifest = testManifest;
	}

	@Before
	public void before() throws IOException, Owl2ParseException {
		assumeTrue(!ignore(manifest.getInput()));
	}

	protected boolean ignore(TestInput input) {
		return Arrays.binarySearch(IGNORE_LIST, input.getName()) >= 0;
	}

	@Test
	public void provabilityTest() throws Exception {
		ElkObject.Factory elkFactory = new ElkObjectEntityRecyclingFactory();
		// to save all loaded axioms
		final Set<ElkAxiom> ontology = new ArrayHashSet<ElkAxiom>();
		ElkObject.Factory axiomSavingFactory = new ElkObjectDelegatingFactory(
				elkFactory) {

			@Override
			protected <C extends ElkObject> C filter(C candidate) {
				if (candidate instanceof ElkAxiom) {
					ontology.add((ElkAxiom) candidate);
				}
				return candidate;
			}
		};

		AxiomLoader fileLoader = new Owl2StreamLoader(
				new Owl2FunctionalStyleParserFactory(axiomSavingFactory),
				manifest.getInput().getUrl().openStream()) {

		};

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(fileLoader,
				new PostProcessingStageExecutor());

		try {
			// reasoner.getTaxonomy();
			// ElkClass sub = elkFactory.getClass(new ElkFullIri("StiffNeck"));
			// ElkClass sup = elkFactory
			// .getClass(new ElkFullIri("PropertyOfPhenomenon"));
			//
			// getTestingVisitor(reasoner, ontology).subsumptionTest(sub, sup);

			TracingTests tests = getProvabilityTests(reasoner);

			tests.accept(getTestingVisitor(reasoner, ontology));
		} finally {
			reasoner.shutdown();
		}
	}

	private TracingTestVisitor getTestingVisitor(final Reasoner reasoner,
			final Set<ElkAxiom> ontology) {
		return new TracingTestVisitor() {

			private final ElkObject.Factory factory_ = new ElkObjectEntityRecyclingFactory();

			@Override
			public void testSubsumption(ElkClass subsumee, ElkClass subsumer) {
				try {
					TestUtils.provabilityTest(reasoner, ontology, factory_,
							factory_.getSubClassOfAxiom(subsumee, subsumer));
				} catch (ElkException e) {
					throw new ElkRuntimeException(e);
				}
			}

			@Override
			public void testEquivalence(List<? extends ElkClass> equivalent)
					throws Exception {
				try {
					TestUtils.provabilityTest(reasoner, ontology, factory_,
							factory_.getEquivalentClassesAxiom(equivalent));
				} catch (ElkException e) {
					throw new ElkRuntimeException(e);
				}
			}

		};
	}

	protected TracingTests getProvabilityTests(Reasoner reasoner)
			throws ElkException {
		return new ComprehensiveSubsumptionTracingTests(reasoner);
	}

	@Config
	public static Configuration getConfig()
			throws URISyntaxException, IOException {
		return ConfigurationUtils.loadFileBasedTestConfiguration(
				INPUT_DATA_LOCATION, TracingTestManifest.class, "owl",
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
