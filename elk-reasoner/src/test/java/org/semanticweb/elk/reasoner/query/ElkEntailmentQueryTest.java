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
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.runner.RunWith;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.reasoner.ElkReasoningTestDelegate;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.query.ElkQueryException;
import org.semanticweb.elk.reasoner.query.EntailmentQueryResult;
import org.semanticweb.elk.reasoner.query.ProperEntailmentQueryResult;
import org.semanticweb.elk.reasoner.query.UnsupportedIndexingEntailmentQueryResult;
import org.semanticweb.elk.reasoner.query.UnsupportedQueryTypeEntailmentQueryResult;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.MultiManifestCreator;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifestWithOutput;
import org.semanticweb.elk.util.collections.Operations;

@RunWith(PolySuite.class)
public class ElkEntailmentQueryTest extends
		BaseQueryTest<Collection<ElkAxiom>, EntailmentQueryTestOutput<ElkAxiom>> {

	// @formatter:off
	static final String[] IGNORE_LIST = {
			"Disjunctions.owl",// Disjuctions not fully supported
			"OneOf.owl",// Disjuctions not fully supported
			"UnsupportedQueryIndexing.owl",// Unsupported class expression
		};
	// @formatter:on

	static {
		Arrays.sort(IGNORE_LIST);
	}

	@Override
	protected boolean ignoreInputFile(final String fileName) {
		return Arrays.binarySearch(IGNORE_LIST, fileName) >= 0;
	}

	public ElkEntailmentQueryTest(
			final TestManifestWithOutput<QueryTestInput<Collection<ElkAxiom>>, EntailmentQueryTestOutput<ElkAxiom>, EntailmentQueryTestOutput<ElkAxiom>> manifest) {
		super(manifest,
				new ElkReasoningTestDelegate<EntailmentQueryTestOutput<ElkAxiom>>(
						manifest) {

					@Override
					public EntailmentQueryTestOutput<ElkAxiom> getActualOutput()
							throws Exception {
						final Map<ElkAxiom, EntailmentQueryResult> result = getReasoner()
								.isEntailed(manifest.getInput().getQuery());
						return new EntailmentQueryTestOutput<ElkAxiom>(
								resultToOutput(result));
					}

				});
	}

	static Map<ElkAxiom, Boolean> resultToOutput(
			final Map<ElkAxiom, EntailmentQueryResult> result) {
		return new AbstractMap<ElkAxiom, Boolean>() {
			@Override
			public Set<Map.Entry<ElkAxiom, Boolean>> entrySet() {
				return new AbstractSet<Map.Entry<ElkAxiom, Boolean>>() {

					@Override
					public Iterator<Map.Entry<ElkAxiom, Boolean>> iterator() {
						return Operations.map(result.entrySet().iterator(),
								RESULT_TO_OUTPUT);
					}

					@Override
					public int size() {
						return result.size();
					}

				};
			}
		};
	}

	private static final Operations.Transformation<Map.Entry<ElkAxiom, EntailmentQueryResult>, Map.Entry<ElkAxiom, Boolean>> RESULT_TO_OUTPUT = new Operations.Transformation<Map.Entry<ElkAxiom, EntailmentQueryResult>, Map.Entry<ElkAxiom, Boolean>>() {

		@Override
		public Map.Entry<ElkAxiom, Boolean> transform(
				final Map.Entry<ElkAxiom, EntailmentQueryResult> element) {

			return new Map.Entry<ElkAxiom, Boolean>() {

				@Override
				public ElkAxiom getKey() {
					return element.getKey();
				}

				@Override
				public Boolean getValue() {
					try {
						return element.getValue().accept(RESULT_VISITOR);
					} catch (final ElkQueryException e) {
						throw new RuntimeException(e);
					}
				}

				@Override
				public Boolean setValue(final Boolean value) {
					throw new UnsupportedOperationException();
				}

			};
		}

	};

	private static final EntailmentQueryResult.Visitor<Boolean, ElkQueryException> RESULT_VISITOR = new EntailmentQueryResult.Visitor<Boolean, ElkQueryException>() {

		@Override
		public Boolean visit(
				final ProperEntailmentQueryResult properEntailmentQueryResult)
				throws ElkQueryException {
			try {
				return properEntailmentQueryResult.isEntailed();
			} finally {
				properEntailmentQueryResult.unlock();
			}
		}

		@Override
		public Boolean visit(
				final UnsupportedIndexingEntailmentQueryResult unsupportedIndexingEntailmentQueryResult) {
			// TODO: this may be an important information for the test
			return false;
		}

		@Override
		public Boolean visit(
				final UnsupportedQueryTypeEntailmentQueryResult unsupportedQueryTypeEntailmentQueryResult) {
			// TODO: this may be an important information for the test
			return false;
		}

	};

	public static final String ENTAILMENT_QUERY_INPUT_DIR = "entailment_query_test_input";

	private static final ConfigurationUtils.ManifestCreator<QueryTestInput<Collection<ElkAxiom>>, EntailmentQueryTestOutput<ElkAxiom>, EntailmentQueryTestOutput<ElkAxiom>> ENTAILMENT_QUERY_TEST_MANIFEST_CREATOR_ = new ConfigurationUtils.ManifestCreator<QueryTestInput<Collection<ElkAxiom>>, EntailmentQueryTestOutput<ElkAxiom>, EntailmentQueryTestOutput<ElkAxiom>>() {

		@Override
		public Collection<? extends TestManifestWithOutput<QueryTestInput<Collection<ElkAxiom>>, EntailmentQueryTestOutput<ElkAxiom>, EntailmentQueryTestOutput<ElkAxiom>>> createManifests(
				final List<URL> urls) throws IOException {

			if (urls.size() < 3) {
				throw new IllegalArgumentException("Need at least 3 URLs!");
			}

			final URL input = urls.get(0);
			InputStream entailedIS = null;
			InputStream notEntailedIS = null;
			try {
				entailedIS = urls.get(1).openStream();
				notEntailedIS = urls.get(2).openStream();

				final List<ElkAxiom> query = new ArrayList<ElkAxiom>();
				final Map<ElkAxiom, Boolean> output = new HashMap<ElkAxiom, Boolean>();

				final Set<ElkAxiom> entailed = TestReasonerUtils
						.loadAxioms(entailedIS);
				final Set<ElkAxiom> notEntailed = TestReasonerUtils
						.loadAxioms(notEntailedIS);
				query.addAll(entailed);
				query.addAll(notEntailed);
				for (final ElkAxiom elkAxiom : entailed) {
					output.put(elkAxiom, true);
				}
				for (final ElkAxiom elkAxiom : notEntailed) {
					output.put(elkAxiom, false);
				}

				return Collections.singleton(
						new EntailmentQueryTestManifest<ElkAxiom>(input, query,
								new EntailmentQueryTestOutput<ElkAxiom>(
										output)));

			} catch (final Owl2ParseException e) {
				throw new IOException(e);
			} finally {
				IOUtils.closeQuietly(entailedIS);
				IOUtils.closeQuietly(notEntailedIS);
			}

		}

	};

	private static final MultiManifestCreator<QueryTestInput<Collection<ElkAxiom>>, EntailmentQueryTestOutput<ElkAxiom>, EntailmentQueryTestOutput<ElkAxiom>> CLASS_QUERY_TEST_MANIFEST_CREATOR_ = new MultiManifestCreator<QueryTestInput<Collection<ElkAxiom>>, EntailmentQueryTestOutput<ElkAxiom>, EntailmentQueryTestOutput<ElkAxiom>>() {

		@Override
		public Collection<? extends TestManifestWithOutput<QueryTestInput<Collection<ElkAxiom>>, EntailmentQueryTestOutput<ElkAxiom>, EntailmentQueryTestOutput<ElkAxiom>>> createManifests(
				final URL input, final URL output) throws IOException {

			InputStream outputIS = null;
			try {
				outputIS = output.openStream();

				return ElkExpectedTestOutputLoader.load(outputIS)
						.getEntailmentManifests(input);

			} finally {
				IOUtils.closeQuietly(outputIS);
			}

		}

	};

	@Config
	public static Configuration getConfig()
			throws IOException, URISyntaxException {

		final Configuration classConfiguration = ConfigurationUtils
				.loadFileBasedTestConfiguration(INPUT_DATA_LOCATION,
						BaseQueryTest.class, "owl", "expected",
						CLASS_QUERY_TEST_MANIFEST_CREATOR_);

		final Configuration entailmentConfiguration = ConfigurationUtils
				.loadFileBasedTestConfiguration(ENTAILMENT_QUERY_INPUT_DIR,
						BaseQueryTest.class,
						ENTAILMENT_QUERY_TEST_MANIFEST_CREATOR_, "owl",
						"entailed", "notentailed");

		return ConfigurationUtils.combine(classConfiguration,
				entailmentConfiguration);

	}

}
