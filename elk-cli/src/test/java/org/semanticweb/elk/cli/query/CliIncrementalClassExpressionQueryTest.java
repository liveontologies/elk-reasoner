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
import java.util.Collection;

import org.junit.runner.RunWith;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.reasoner.incremental.CliIncrementalReasoningTestDelegate;
import org.semanticweb.elk.reasoner.query.BaseIncrementalQueryTest;
import org.semanticweb.elk.reasoner.query.QueryTestInput;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.MultiManifestCreator;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.TestManifestWithOutput;
import org.semanticweb.elk.testing.TestOutput;

@RunWith(PolySuite.class)
public abstract class CliIncrementalClassExpressionQueryTest<O extends TestOutput>
		extends
		BaseIncrementalQueryTest<ElkClassExpression, ElkAxiom, O> {

	public CliIncrementalClassExpressionQueryTest(
			final TestManifest<QueryTestInput<ElkClassExpression>> manifest,
			final CliIncrementalReasoningTestDelegate<O, O> testDelegate) {
		super(manifest, testDelegate);
	}

	@Config
	public static Configuration getConfig()
			throws IOException, URISyntaxException {

		return ConfigurationUtils.loadFileBasedTestConfiguration(
				INPUT_DATA_LOCATION,
				BaseIncrementalQueryTest.class, "owl",
				"expected",
				new MultiManifestCreator<QueryTestInput<ElkClassExpression>, TestOutput, TestOutput>() {

					@Override
					public Collection<? extends TestManifestWithOutput<QueryTestInput<ElkClassExpression>, TestOutput, TestOutput>> createManifests(
							final URL input, final URL output)
							throws IOException {

						InputStream outputIS = null;
						try {
							outputIS = output.openStream();

							// don't need an expected output for these tests
							return CliExpectedTestOutputLoader.load(outputIS)
									.getNoOutputManifests(input);

						} finally {
							IOUtils.closeQuietly(outputIS);
						}

					}

				});

	}

}
