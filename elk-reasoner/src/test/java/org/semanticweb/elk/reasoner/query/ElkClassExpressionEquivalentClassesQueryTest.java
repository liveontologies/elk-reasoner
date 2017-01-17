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
package org.semanticweb.elk.reasoner.query;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.reasoner.ElkReasoningTestDelegate;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.MultiManifestCreator;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifestWithOutput;

@RunWith(PolySuite.class)
public class ElkClassExpressionEquivalentClassesQueryTest extends
		BaseQueryTest<ElkClassExpression, EquivalentEntitiesTestOutput<ElkClass>> {

	public ElkClassExpressionEquivalentClassesQueryTest(
			final TestManifestWithOutput<QueryTestInput<ElkClassExpression>, EquivalentEntitiesTestOutput<ElkClass>, EquivalentEntitiesTestOutput<ElkClass>> manifest) {
		super(manifest,
				new ElkReasoningTestDelegate<EquivalentEntitiesTestOutput<ElkClass>>(
						manifest) {

					@Override
					public EquivalentEntitiesTestOutput<ElkClass> getActualOutput()
							throws Exception {
						final Node<ElkClass> equivalent = getReasoner()
								.getEquivalentClassesQuietly(
										manifest.getInput().getQuery());
						return new ElkEquivalentEntitiesTestOutput(equivalent);
					}

				});
	}

	@Config
	public static Configuration getConfig()
			throws IOException, URISyntaxException {

		return ConfigurationUtils.loadFileBasedTestConfiguration(
				INPUT_DATA_LOCATION, BaseQueryTest.class, "owl",
				"expected",
				new MultiManifestCreator<QueryTestInput<ElkClassExpression>, EquivalentEntitiesTestOutput<ElkClass>, EquivalentEntitiesTestOutput<ElkClass>>() {

					@Override
					public Collection<? extends TestManifestWithOutput<QueryTestInput<ElkClassExpression>, EquivalentEntitiesTestOutput<ElkClass>, EquivalentEntitiesTestOutput<ElkClass>>> createManifests(
							final URL input, final URL output)
							throws IOException {

						InputStream outputIS = null;
						try {
							outputIS = output.openStream();

							return ElkExpectedTestOutputLoader.load(outputIS)
									.getEquivalentEntitiesManifests(input);

						} finally {
							IOUtils.closeQuietly(outputIS);
						}

					}

				});

	}

}
