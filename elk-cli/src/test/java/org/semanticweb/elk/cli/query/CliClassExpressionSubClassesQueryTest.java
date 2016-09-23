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
import java.util.Set;

import org.junit.runner.RunWith;
import org.semanticweb.elk.cli.CliReasoningTestDelegate;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.reasoner.query.BaseClassExpressionQueryTest;
import org.semanticweb.elk.reasoner.query.ClassExpressionQueryTestManifest;
import org.semanticweb.elk.reasoner.query.ClassQueryTestInput;
import org.semanticweb.elk.reasoner.query.RelatedEntitiesTestOutput;
import org.semanticweb.elk.reasoner.taxonomy.ElkClassKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.TestManifestCreator;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifestWithOutput;

@RunWith(PolySuite.class)
public class CliClassExpressionSubClassesQueryTest extends
		BaseClassExpressionQueryTest<ElkClassExpression, RelatedEntitiesTestOutput<ElkClass>> {

	public CliClassExpressionSubClassesQueryTest(
			final TestManifestWithOutput<ClassQueryTestInput<ElkClassExpression>, RelatedEntitiesTestOutput<ElkClass>, RelatedEntitiesTestOutput<ElkClass>> manifest) {
		super(manifest,
				new CliReasoningTestDelegate<RelatedEntitiesTestOutput<ElkClass>>(
						manifest) {

					@Override
					public RelatedEntitiesTestOutput<ElkClass> getActualOutput()
							throws Exception {
						final Set<? extends Node<ElkClass>> subNodes = reasoner_
								.getSubClassesQuietly(
										manifest.getInput().getClassQuery(),
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
				INPUT_DATA_LOCATION, BaseClassExpressionQueryTest.class, "owl",
				"expected",
				new TestManifestCreator<ClassQueryTestInput<ElkClassExpression>, RelatedEntitiesTestOutput<ElkClass>, RelatedEntitiesTestOutput<ElkClass>>() {

					@Override
					public TestManifestWithOutput<ClassQueryTestInput<ElkClassExpression>, RelatedEntitiesTestOutput<ElkClass>, RelatedEntitiesTestOutput<ElkClass>> create(
							final URL input, final URL output)
							throws IOException {

						InputStream outputIS = null;
						try {
							outputIS = output.openStream();
							final ExpectedTestOutputLoader expected = ExpectedTestOutputLoader
									.load(outputIS);

							return new ClassExpressionQueryTestManifest<ElkClassExpression, RelatedEntitiesTestOutput<ElkClass>>(
									input, expected.getQueryClass(),
									expected.getSubEntitiesTestOutput());

						} finally {
							IOUtils.closeQuietly(outputIS);
						}

					}

				});

	}

}
