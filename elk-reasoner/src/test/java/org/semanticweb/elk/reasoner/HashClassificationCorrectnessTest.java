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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.runner.RunWith;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.TestManifestCreator;
import org.semanticweb.elk.testing.HashTestOutput;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.io.IOUtils;
import org.semanticweb.elk.testing.io.URLTestIO;

/**
 * Runs classification tests for all test input in the test directory
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
@RunWith(PolySuite.class)
public abstract class HashClassificationCorrectnessTest extends BaseClassificationCorrectnessTest<HashTestOutput> {
	
	public HashClassificationCorrectnessTest(ReasoningTestManifest<HashTestOutput, ClassTaxonomyTestOutput> testManifest) {
		super(testManifest);
	}
	
	/*
	 * ---------------------------------------------
	 * Configuration: loading all test input data
	 * ---------------------------------------------
	 */
	
	@Config
	public static Configuration getConfig() throws URISyntaxException, IOException {
		final URI inputURI = HashClassificationCorrectnessTest.class.getClassLoader().getResource(INPUT_DATA_LOCATION).toURI();
		
		return ConfigurationUtils.loadFileBasedTestConfiguration(	inputURI,
																	HashClassificationCorrectnessTest.class,
																	"owl",
																	"expected.hash",
																	new TestManifestCreator<URLTestIO, HashTestOutput, ClassTaxonomyTestOutput>() {
			@Override
			public TestManifest<URLTestIO, HashTestOutput, ClassTaxonomyTestOutput> create(URL input, URL output) {
				//input is an OWL ontology, expected output is a hash code
				try {
					int hash = IOUtils.readInteger(output, 10);
					
					return new ClassTaxonomyHashManifest(input, hash);
				} catch (IOException e) {
					// TODO Log it
					return null;
				} 
			}
		});
	}
}