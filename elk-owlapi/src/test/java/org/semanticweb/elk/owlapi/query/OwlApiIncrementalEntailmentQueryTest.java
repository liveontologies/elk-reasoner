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
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.runner.RunWith;
import org.semanticweb.elk.owlapi.EntailmentTestManifestCreator;
import org.semanticweb.elk.owlapi.OwlApiIncrementalReasoningTestDelegate;
import org.semanticweb.elk.reasoner.query.BaseIncrementalQueryTest;
import org.semanticweb.elk.reasoner.query.BaseQueryTest;
import org.semanticweb.elk.reasoner.query.EntailmentQueryTestOutput;
import org.semanticweb.elk.reasoner.query.QueryTestInput;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.TestOutput;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;

@RunWith(PolySuite.class)
public class OwlApiIncrementalEntailmentQueryTest<O extends TestOutput> extends
		BaseIncrementalQueryTest<OWLAxiom, OWLAxiom, EntailmentQueryTestOutput<OWLAxiom>> {

	public OwlApiIncrementalEntailmentQueryTest(
			final TestManifest<QueryTestInput<OWLAxiom>> manifest) {
		super(manifest,
				new OwlApiIncrementalReasoningTestDelegate<EntailmentQueryTestOutput<OWLAxiom>, EntailmentQueryTestOutput<OWLAxiom>>(
						manifest) {

					@Override
					public EntailmentQueryTestOutput<OWLAxiom> getExpectedOutput()
							throws Exception {
						final Map<OWLAxiom, Boolean> output = new HashMap<OWLAxiom, Boolean>();
						final OWLAxiom owlAxiom = manifest.getInput()
								.getQuery();
						output.put(owlAxiom,
								getStandardReasoner().isEntailed(owlAxiom));
						return new EntailmentQueryTestOutput<OWLAxiom>(output);
					}

					@Override
					public EntailmentQueryTestOutput<OWLAxiom> getActualOutput()
							throws Exception {
						final Map<OWLAxiom, Boolean> output = new HashMap<OWLAxiom, Boolean>();
						final OWLAxiom owlAxiom = manifest.getInput()
								.getQuery();
						output.put(owlAxiom,
								getIncrementalReasoner().isEntailed(owlAxiom));
						return new EntailmentQueryTestOutput<OWLAxiom>(output);
					}

					@Override
					public Class<? extends Exception> getInterruptionExceptionClass() {
						return ReasonerInterruptedException.class;
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
						OwlApiEntailmentQueryTest.CLASS_QUERY_TEST_MANIFEST_CREATOR,
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
