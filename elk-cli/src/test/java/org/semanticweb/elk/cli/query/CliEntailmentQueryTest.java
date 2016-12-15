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
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.runner.RunWith;
import org.semanticweb.elk.cli.CliReasoningTestDelegate;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.query.BaseQueryTest;
import org.semanticweb.elk.reasoner.query.ElkQueryException;
import org.semanticweb.elk.reasoner.query.EntailmentQueryResult;
import org.semanticweb.elk.reasoner.query.EntailmentQueryTestOutput;
import org.semanticweb.elk.reasoner.query.ProperEntailmentQueryResult;
import org.semanticweb.elk.reasoner.query.QueryTestInput;
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
public class CliEntailmentQueryTest extends
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

	public CliEntailmentQueryTest(
			final TestManifestWithOutput<QueryTestInput<Collection<ElkAxiom>>, EntailmentQueryTestOutput<ElkAxiom>, EntailmentQueryTestOutput<ElkAxiom>> manifest) {
		super(manifest,
				new CliReasoningTestDelegate<EntailmentQueryTestOutput<ElkAxiom>>(
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

	@Config
	public static Configuration getConfig()
			throws IOException, URISyntaxException {

		return ConfigurationUtils.loadFileBasedTestConfiguration(
				INPUT_DATA_LOCATION, BaseQueryTest.class, "owl", "expected",
				new MultiManifestCreator<QueryTestInput<Collection<ElkAxiom>>, EntailmentQueryTestOutput<ElkAxiom>, EntailmentQueryTestOutput<ElkAxiom>>() {

					@Override
					public Collection<? extends TestManifestWithOutput<QueryTestInput<Collection<ElkAxiom>>, EntailmentQueryTestOutput<ElkAxiom>, EntailmentQueryTestOutput<ElkAxiom>>> createManifests(
							final URL input, final URL output)
							throws IOException {

						InputStream outputIS = null;
						try {
							outputIS = output.openStream();

							return CliExpectedTestOutputLoader.load(outputIS)
									.getEntailmentManifests(input);

						} finally {
							IOUtils.closeQuietly(outputIS);
						}

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

	private static final EntailmentQueryResult.Visitor<Boolean> RESULT_VISITOR = new EntailmentQueryResult.Visitor<Boolean>() {

		@Override
		public Boolean visit(
				final ProperEntailmentQueryResult properEntailmentQueryResult)
				throws ElkQueryException {
			return properEntailmentQueryResult.isEntailed();
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

}
