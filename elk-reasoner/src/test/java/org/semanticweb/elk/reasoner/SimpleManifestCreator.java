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
package org.semanticweb.elk.reasoner;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.semanticweb.elk.testing.ConfigurationUtils.ManifestCreator;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.UrlTestInput;

public class SimpleManifestCreator
		implements ManifestCreator<TestManifest<UrlTestInput>> {

	public static final SimpleManifestCreator INSTANCE = new SimpleManifestCreator();

	@Override
	public Collection<? extends TestManifest<UrlTestInput>> createManifests(
			final String name, final List<URL> urls) throws IOException {
		if (urls == null || urls.isEmpty()) {
			// Not enough inputs. Something was probably forgotten.
			throw new IllegalArgumentException("No test inputs!");
		}
		if (urls.get(0) == null) {
			// No inputs, no manifests.
			return Collections.emptySet();
		}
		return Collections.singleton(
				new ReasoningTestManifest<ReasoningTestOutput<?>>(name,
						urls.get(0), null));
	}

}
