/*
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.owlapi.query;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.elk.owlapi.OWLAPITestUtils;
import org.semanticweb.elk.reasoner.query.BaseClassExpressionQueryTest;
import org.semanticweb.elk.reasoner.query.BaseClassExpressionQueryTestManifest;
import org.semanticweb.elk.reasoner.query.BaseSatisfiabilityTestOutput;
import org.semanticweb.elk.reasoner.query.ClassQueryTestInput;
import org.semanticweb.elk.reasoner.query.SatisfiabilityTestOutput;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.TestResultComparisonException;
import org.semanticweb.elk.testing.ConfigurationUtils.TestManifestCreator;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OwlApiClassExpressionSatisfiabilityQueryTest extends
		BaseClassExpressionQueryTest<OWLOntology, OWLClassExpression, SatisfiabilityTestOutput> {

	public OwlApiClassExpressionSatisfiabilityQueryTest(
			final TestManifest<ClassQueryTestInput<OWLOntology, OWLClassExpression>, SatisfiabilityTestOutput, SatisfiabilityTestOutput> manifest) {
		super(manifest);
	}

	@Config
	public static Configuration getConfig()
			throws IOException, URISyntaxException {

		return ConfigurationUtils.loadFileBasedTestConfiguration(
				INPUT_DATA_LOCATION, OwlApiClassExpressionSatisfiabilityQueryTest.class,
				"owl", "expected",
				new TestManifestCreator<ClassQueryTestInput<OWLOntology, OWLClassExpression>, SatisfiabilityTestOutput, SatisfiabilityTestOutput>() {

					@Override
					public TestManifest<ClassQueryTestInput<OWLOntology, OWLClassExpression>, SatisfiabilityTestOutput, SatisfiabilityTestOutput> create(
							final URL input, final URL output)
									throws IOException {

						final OWLOntologyManager manager = OWLManager
								.createOWLOntologyManager();

						InputStream inputIS = null;
						InputStream outputIS = null;
						try {
							inputIS = input.openStream();
							final OWLOntology inputOnt = manager
									.loadOntologyFromOntologyDocument(inputIS);
							outputIS = output.openStream();
							final ExpectedTestOutputLoader expected = ExpectedTestOutputLoader
									.load(outputIS);

							return new BaseClassExpressionQueryTestManifest<OWLOntology, OWLClassExpression, SatisfiabilityTestOutput>(
									input, inputOnt, expected.getQueryClass()) {
								@Override
								public SatisfiabilityTestOutput getExpectedOutput() {
									return expected.getSatisfiabilityTestOutput();
								}
							};

						} catch (final OWLOntologyCreationException e) {
							throw new IOException(e);
						} finally {
							IOUtils.closeQuietly(inputIS);
							IOUtils.closeQuietly(outputIS);
						}

					}

				});

	}

	@Test
	public void testQuery() throws TestResultComparisonException {

		final ElkReasoner reasoner = OWLAPITestUtils
				.createReasoner(manifest_.getInput().getOntology());

		final boolean isSatisfiable = reasoner.isSatisfiable(manifest_.getInput().getClassQuery());

		manifest_.compare(new BaseSatisfiabilityTestOutput(isSatisfiable));

	}

}
