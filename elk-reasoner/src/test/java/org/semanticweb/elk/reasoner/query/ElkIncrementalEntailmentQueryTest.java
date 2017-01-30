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
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;

import org.junit.runner.RunWith;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.incremental.ElkIncrementalReasoningTestDelegate;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifest;

@RunWith(PolySuite.class)
public class ElkIncrementalEntailmentQueryTest extends
		BaseIncrementalQueryTest<Collection<ElkAxiom>, ElkAxiom, EntailmentQueryTestOutput<ElkAxiom>> {

	public ElkIncrementalEntailmentQueryTest(
			final TestManifest<QueryTestInput<Collection<ElkAxiom>>> manifest) {
		super(manifest,
				new ElkIncrementalReasoningTestDelegate<EntailmentQueryTestOutput<ElkAxiom>>(
						manifest) {

					@Override
					public EntailmentQueryTestOutput<ElkAxiom> getExpectedOutput()
							throws Exception {
						final Map<ElkAxiom, EntailmentQueryResult> result = getStandardReasoner()
								.isEntailed(manifest.getInput().getQuery());
						return new EntailmentQueryTestOutput<ElkAxiom>(
								ElkEntailmentQueryTest.resultToOutput(result));
					}

					@Override
					public EntailmentQueryTestOutput<ElkAxiom> getActualOutput()
							throws Exception {
						final Map<ElkAxiom, EntailmentQueryResult> result = getIncrementalReasoner()
								.isEntailed(manifest.getInput().getQuery());
						return new EntailmentQueryTestOutput<ElkAxiom>(
								ElkEntailmentQueryTest.resultToOutput(result));
					}

				});
	}

	public static final String ENTAILMENT_QUERY_INPUT_DIR = "entailment_query_test_input";

	@Config
	public static Configuration getConfig()
			throws IOException, URISyntaxException {

		final Configuration classConfiguration = ConfigurationUtils
				.loadFileBasedTestConfiguration(INPUT_DATA_LOCATION,
						BaseQueryTest.class,
						ElkEntailmentQueryTest.CLASS_QUERY_TEST_MANIFEST_CREATOR,
						"owl", "expected");

		final Configuration entailmentConfiguration = ConfigurationUtils
				.loadFileBasedTestConfiguration(ENTAILMENT_QUERY_INPUT_DIR,
						BaseQueryTest.class,
						EntailmentTestManifestCreator.INSTANCE, "owl",
						"entailed", "notentailed");

		return ConfigurationUtils.combine(classConfiguration,
				entailmentConfiguration);

	}

}
