/*
 * #%L
 * ELK Reasoner
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

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticweb.elk.testing.BasicTestManifest;

/**
 * Test manifest for query tests.
 * 
 * @author Peter Skocovsky
 *
 * @param <Q>
 *            Type of the query.
 * @param <O>
 *            Type of the test output.
 */
public class QueryTestManifest<Q, O>
		extends BasicTestManifest<QueryTestInput<Q>, O> {

	private static final Pattern IRI_REGEXP_ = Pattern
			.compile("<[^>]*/([^/>]+)>");
	private static final int MAX_NAME_LENGTH_ = 100;
	private static final String LONG_NAME_SUFFIX_ = " ...";

	public QueryTestManifest(final String name, final URL input, final Q query,
			final O expectedOutput) {
		super(new QueryTestInput<Q>() {

			@Override
			public String getName() {
				String queryName = query.toString();
				/*
				 * This is a workaround so that Eclipse displays results for all
				 * tests. Eclipse seems to identify the tests by their names,
				 * but if the names are truncated, it may happen that two tests
				 * have the same names.
				 */
				final Matcher matcher = IRI_REGEXP_.matcher(queryName);
				queryName = matcher.replaceAll("<*$1>");
				final String testName = name + " " + queryName;
				final int testNameHash = testName.hashCode();
				return testName.length() <= MAX_NAME_LENGTH_ ? testName
						: testName.substring(0,
								MAX_NAME_LENGTH_ - LONG_NAME_SUFFIX_.length())
								+ LONG_NAME_SUFFIX_ + testNameHash;
			}

			@Override
			public URL getUrl() {
				return input;
			}

			@Override
			public Q getQuery() {
				return query;
			}

		}, expectedOutput);
	}

}
