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
package org.semanticweb.elk.cli.query;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.junit.runner.RunWith;
import org.semanticweb.elk.cli.CliReasoningTestDelegate;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.reasoner.query.BaseQueryTest;
import org.semanticweb.elk.reasoner.query.QueryTestInput;
import org.semanticweb.elk.reasoner.query.RelatedEntitiesTestOutput;
import org.semanticweb.elk.reasoner.taxonomy.ElkClassKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.MultiManifestCreator;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifestWithOutput;

@RunWith(PolySuite.class)
public class CliClassExpressionSuperClassesQueryTest extends
		BaseQueryTest<ElkClassExpression, RelatedEntitiesTestOutput<ElkClass>> {

	// @formatter:off
	static final String[] IGNORE_LIST = {
			"Disjunctions.owl",// Disjuctions not supported
			"OneOf.owl",// Disjuctions not supported
		};
	// @formatter:on

	static {
		Arrays.sort(IGNORE_LIST);
	}

	@Override
	protected boolean ignoreInputFile(final String fileName) {
		return Arrays.binarySearch(IGNORE_LIST, fileName) >= 0;
	}

	public CliClassExpressionSuperClassesQueryTest(
			final TestManifestWithOutput<QueryTestInput<ElkClassExpression>, RelatedEntitiesTestOutput<ElkClass>, RelatedEntitiesTestOutput<ElkClass>> manifest) {
		super(manifest,
				new CliReasoningTestDelegate<RelatedEntitiesTestOutput<ElkClass>>(
						manifest) {

					@Override
					public RelatedEntitiesTestOutput<ElkClass> getActualOutput()
							throws Exception {
						final Set<? extends Node<ElkClass>> subNodes = getReasoner()
								.getSuperClassesQuietly(
										manifest.getInput().getQuery(),
										true);
						return new CliRelatedEntitiesTestOutput<ElkClass>(
								subNodes, ElkClassKeyProvider.INSTANCE);
					}

				});
	}

	@Config
	public static Configuration getConfig()
			throws IOException, URISyntaxException {

		return ConfigurationUtils.loadFileBasedTestConfiguration(
				INPUT_DATA_LOCATION, BaseQueryTest.class, "owl",
				"expected",
				new MultiManifestCreator<QueryTestInput<ElkClassExpression>, RelatedEntitiesTestOutput<ElkClass>, RelatedEntitiesTestOutput<ElkClass>>() {

					@Override
					public Collection<? extends TestManifestWithOutput<QueryTestInput<ElkClassExpression>, RelatedEntitiesTestOutput<ElkClass>, RelatedEntitiesTestOutput<ElkClass>>> createManifests(
							final URL input, final URL output)
							throws IOException {

						InputStream outputIS = null;
						try {
							outputIS = output.openStream();

							return CliExpectedTestOutputLoader.load(outputIS)
									.getSuperEntitiesManifests(input);

						} finally {
							IOUtils.closeQuietly(outputIS);
						}

					}

				});

	}

}
