/*-
 * #%L
 * ELK OWL API Binding
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
package org.semanticweb.elk.owlapi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owlapi.query.OwlEntailmentQueryTestOutput;
import org.semanticweb.elk.reasoner.query.QueryTestManifest;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class EntailmentTestManifestCreator implements
		ConfigurationUtils.ManifestCreator<QueryTestManifest<OWLAxiom, OwlEntailmentQueryTestOutput>> {

	public static final EntailmentTestManifestCreator INSTANCE = new EntailmentTestManifestCreator();

	@Override
	public Collection<? extends QueryTestManifest<OWLAxiom, OwlEntailmentQueryTestOutput>> createManifests(
			final String name, final List<URL> urls) throws IOException {

		final Collection<QueryTestManifest<OWLAxiom, OwlEntailmentQueryTestOutput>> manifests = new ArrayList<QueryTestManifest<OWLAxiom, OwlEntailmentQueryTestOutput>>();

		if (urls == null || urls.isEmpty()) {
			// No input files. Fail, while something was probably forgotten.
			throw new IllegalArgumentException("No test inputs!");
		}

		final OWLOntologyManager manager = TestOWLManager
				.createOWLOntologyManager();

		final URL input = urls.get(0);
		if (input == null) {
			// No inputs, no manifests.
			return Collections.emptySet();
		}
		InputStream entailedIS = null;
		InputStream notEntailedIS = null;
		try {

			final List<OWLAxiom> query = new ArrayList<OWLAxiom>();
			final Map<OWLAxiom, Boolean> output = new HashMap<OWLAxiom, Boolean>();

			if (urls.size() >= 2 && urls.get(1) != null) {
				entailedIS = urls.get(1).openStream();
				final Set<OWLLogicalAxiom> entailed = manager
						.loadOntologyFromOntologyDocument(entailedIS)
						.getLogicalAxioms();
				query.addAll(entailed);
				for (final OWLLogicalAxiom elkAxiom : entailed) {
					output.put(elkAxiom, true);
				}
			}
			if (urls.size() >= 3 && urls.get(2) != null) {
				notEntailedIS = urls.get(2).openStream();
				final Set<OWLLogicalAxiom> notEntailed = manager
						.loadOntologyFromOntologyDocument(notEntailedIS)
						.getLogicalAxioms();
				query.addAll(notEntailed);
				for (final OWLLogicalAxiom elkAxiom : notEntailed) {
					output.put(elkAxiom, false);
				}
			}

			// OWL API interface can query only one axiom at once.
			for (final OWLAxiom axiom : query) {
				boolean isEntailed = output.get(axiom);
				manifests.add(
						new QueryTestManifest<OWLAxiom, OwlEntailmentQueryTestOutput>(
								name, input, axiom,
								new OwlEntailmentQueryTestOutput(axiom,
										isEntailed, !isEntailed)));
			}

			return manifests;

		} catch (final OWLOntologyCreationException e) {
			throw new IOException(e);
		} finally {
			IOUtils.closeQuietly(entailedIS);
			IOUtils.closeQuietly(notEntailedIS);
		}

	}

}
