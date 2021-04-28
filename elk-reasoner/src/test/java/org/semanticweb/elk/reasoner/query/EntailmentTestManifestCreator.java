/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.TestManifestWithOutput;

public class EntailmentTestManifestCreator implements
		ConfigurationUtils.ManifestCreator<TestManifestWithOutput<QueryTestInput<Collection<ElkAxiom>>, ElkEntailmentQueryTestOutput>> {

	public static final EntailmentTestManifestCreator INSTANCE = new EntailmentTestManifestCreator();

	@SuppressWarnings("resource")
	@Override
	public Collection<? extends TestManifestWithOutput<QueryTestInput<Collection<ElkAxiom>>, ElkEntailmentQueryTestOutput>> createManifests(
			final String name, final List<URL> urls) throws IOException {

		if (urls == null || urls.isEmpty()) {
			// No input files. Fail, while something was probably forgotten.
			throw new IllegalArgumentException("No test inputs!");
		}

		final URL input = urls.get(0);
		if (input == null) {
			// No input, no manifests.
			return Collections.emptySet();
		}
		InputStream entailedIS = null;
		InputStream notEntailedIS = null;
		try {

			final List<ElkAxiom> query = new ArrayList<ElkAxiom>();
			final Map<ElkAxiom, QueryResult> output = new HashMap<>();

			if (urls.size() >= 2 && urls.get(1) != null) {
				entailedIS = urls.get(1).openStream();
				final List<ElkAxiom> entailed = TestReasonerUtils
						.loadAxioms(entailedIS);
				query.addAll(entailed);
				for (final ElkAxiom elkAxiom : entailed) {
					output.put(elkAxiom,
							new CompleteQueryResult(elkAxiom, true));
				}
			}
			if (urls.size() >= 3 && urls.get(2) != null) {
				notEntailedIS = urls.get(2).openStream();
				final List<ElkAxiom> notEntailed = TestReasonerUtils
						.loadAxioms(notEntailedIS);
				query.addAll(notEntailed);
				for (final ElkAxiom elkAxiom : notEntailed) {
					output.put(elkAxiom,
							new CompleteQueryResult(elkAxiom, false));
				}
			}

			try {
				return Collections.singleton(
						new EntailmentQueryTestManifest<Collection<ElkAxiom>>(
								name, input, query,
								new ElkEntailmentQueryTestOutput(output)));
			} catch (ElkQueryException e) {
				throw new RuntimeException(e);
			}

		} catch (final Owl2ParseException e) {
			throw new IOException(e);
		} finally {
			IOUtils.closeQuietly(entailedIS);
			IOUtils.closeQuietly(notEntailedIS);
		}

	}

}
