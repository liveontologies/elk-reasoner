/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
import java.net.URISyntaxException;
import java.net.URL;

import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.TestManifestCreator;
import org.semanticweb.elk.testing.HashTestOutput;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifestWithOutput;
import org.semanticweb.elk.testing.UrlTestInput;

/**
 * Loads a hash-based configuration
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class HashConfigurationUtils {

	static <AO extends TaxonomyTestOutput<?>> Configuration loadConfiguration(
			String location) throws URISyntaxException, IOException {
		return ConfigurationUtils.loadFileBasedTestConfiguration(location,
				HashConfigurationUtils.class, "owl",
				"expected.hash",
				new TestManifestCreator<UrlTestInput, HashTestOutput, AO>() {
					@Override
					public TestManifestWithOutput<UrlTestInput, HashTestOutput, AO> create(
							URL input, URL output) throws IOException {
						// input is an OWL ontology, expected output is a hash
						// code
						int hash = IOUtils.readInteger(output, 10);

						return new HashTaxonomyTestManifest<AO>(input,
								new HashTestOutput(hash));
					}
				});
	}
}