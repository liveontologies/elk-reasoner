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
import java.util.Arrays;

import org.junit.Test;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.elk.owlapi.OWLAPITestUtils;
import org.semanticweb.elk.reasoner.query.BaseClassExpressionQueryTest;
import org.semanticweb.elk.reasoner.query.BaseClassExpressionQueryTestManifest;
import org.semanticweb.elk.reasoner.query.ClassQueryTestInput;
import org.semanticweb.elk.reasoner.query.RelatedEntitiesTestOutput;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.TestInput;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.TestResultComparisonException;
import org.semanticweb.elk.testing.ConfigurationUtils.TestManifestCreator;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.NodeSet;

public class OwlApiClassExpressionSubClassesQueryTest extends
		BaseClassExpressionQueryTest<OWLOntology, OWLClassExpression, RelatedEntitiesTestOutput<OWLClass>> {

	public OwlApiClassExpressionSubClassesQueryTest(
			final TestManifest<ClassQueryTestInput<OWLOntology, OWLClassExpression>, RelatedEntitiesTestOutput<OWLClass>, RelatedEntitiesTestOutput<OWLClass>> manifest) {
		super(manifest);
	}

	@Config
	public static Configuration getConfig()
			throws IOException, URISyntaxException {

		return ConfigurationUtils.loadFileBasedTestConfiguration(
				INPUT_DATA_LOCATION,
				OwlApiClassExpressionSubClassesQueryTest.class, "owl",
				"expected",
				new TestManifestCreator<ClassQueryTestInput<OWLOntology, OWLClassExpression>, RelatedEntitiesTestOutput<OWLClass>, RelatedEntitiesTestOutput<OWLClass>>() {

					@Override
					public TestManifest<ClassQueryTestInput<OWLOntology, OWLClassExpression>, RelatedEntitiesTestOutput<OWLClass>, RelatedEntitiesTestOutput<OWLClass>> create(
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

							return new BaseClassExpressionQueryTestManifest<OWLOntology, OWLClassExpression, RelatedEntitiesTestOutput<OWLClass>>(
									input, inputOnt, expected.getQueryClass()) {
								@Override
								public RelatedEntitiesTestOutput<OWLClass> getExpectedOutput() {
									return expected.getSubEntitiesTestOutput();
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

	// @formatter:off
	static final String[] IGNORE_LIST = {
//			"CompositionReflexivity0.owl",
//			"CompositionReflexivityComplex0.owl",
//			"ReflexiveRole0.owl",
//			"ReflexiveRole1.owl",
//			"ReflexiveRole2.owl",
//			"ReflexiveRole3.owl",
//			"SameIndividual0.owl",
//			"SameIndividual1.owl",
//			"TransitiveProperty0.owl",
//			"TransitivePropertyChain0.owl",
//			"kangaroo0.owl"
		};
	// @formatter:on

	static {
		Arrays.sort(IGNORE_LIST);
	}

	@Override
	protected boolean ignore(TestInput input) {
		return Arrays.binarySearch(IGNORE_LIST, input.getName()) >= 0;
	}

	@Test
	public void testQuery() throws TestResultComparisonException {

		final ElkReasoner reasoner = OWLAPITestUtils
				.createReasoner(manifest_.getInput().getOntology());

		final NodeSet<OWLClass> subNodes = reasoner
				.getSubClasses(manifest_.getInput().getClassQuery(), true);

		manifest_.compare(new OwlRelatedEntitiesTestOutput(subNodes));

	}

}
