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

import org.semanticweb.elk.io.FileUtils;
import org.semanticweb.elk.testing.BasicTestManifest;
import org.semanticweb.elk.testing.TestOutput;

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
public class QueryTestManifest<Q, O extends TestOutput>
		extends BasicTestManifest<QueryTestInput<Q>, O> {

	public QueryTestManifest(final String testName, final URL input,
			final Q query, final O expectedOutput) {
		super(testName, new QueryTestInput<Q>() {

			@Override
			public String getName() {
				return FileUtils.getFileName(input.getPath()) + " " + query;
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

	/**
	 * Constructor that extract test name from the input URL and the query.
	 * 
	 * @param input
	 * @param query
	 * @param expectedOutput
	 */
	public QueryTestManifest(final URL input, final Q query,
			final O expectedOutput) {
		this(getTestName(input, query), input, query, expectedOutput);
	}

	private static <Q> String getTestName(final URL input, final Q query) {
		final String testName = FileUtils.getFileName(
				FileUtils.dropExtension(input.getPath())) + " " + query;
		return testName.length() <= 80 ? testName
				: testName.substring(0, 76) + " ...";
	}

}
