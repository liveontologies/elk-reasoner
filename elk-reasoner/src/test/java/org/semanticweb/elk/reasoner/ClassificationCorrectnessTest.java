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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomy;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestInput;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.TestResultComparisonException;
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
public abstract class ClassificationCorrectnessTest {
	
	final static String INPUT_DATA_LOCATION = "classification_test_input";
	
	/*
	 * ---------------------------------------------
	 * Configuration: loading all test input data
	 * ---------------------------------------------
	 */
	
	@Config
	public static Configuration getConfig() throws URISyntaxException, IOException {
		final URI inputURI = ClassificationCorrectnessTest.class.getClassLoader().getResource(INPUT_DATA_LOCATION).toURI();
		final boolean isInJar = inputURI.isOpaque();
		final String OWL_EXT = "owl";
		
		System.out.println(inputURI);
		
		final List<String> testResources = isInJar 
				? IOUtils.getTestResourceNamesFromJAR(inputURI, OWL_EXT, ClassificationCorrectnessTest.class)
				: IOUtils.getTestResourceNamesFromDir(new File(inputURI), OWL_EXT);

		System.out.println(testResources);
		
		return new Configuration() {

			@Override
			public int size() {
				return testResources.size();
			}

			@Override
			public TestManifest getTestValue(int index) {
				String testResource = testResources.get(index);
				TestInput input = new URLTestIO(ClassificationCorrectnessTest.class.getClassLoader().getResource(testResource)); 
				
				return new ClassificationTestManifest(input, null/*TODO file with the expected result or expected hash code*/);
			}

			@Override
			public String getTestName(int index) {
				return "test" + IOUtils.getFileName(IOUtils.dropExtension(testResources.get(index))).toUpperCase();
			}
		};
	}
	
	private final TestManifest manifest;
	private InputStream inputStream;
	private Reasoner reasoner;
	
	public ClassificationCorrectnessTest(TestManifest testManifest) {
		manifest = testManifest;
	}
	
	@Before
	public void before() throws IOException, Owl2ParseException {
		inputStream = ((URLTestIO)manifest.getInput()).getInputStream();
		reasoner = createReasoner(inputStream);
	}
	
	@After
	public void after() {
		IOUtils.closeQuietly(inputStream);
	}
	
	protected abstract Reasoner createReasoner(final InputStream input) throws IOException, Owl2ParseException;
	
	/*
	 * ---------------------------------------------
	 * Tests
	 * ---------------------------------------------
	 */
	

	/**
	 * Checks that the computed taxonomy is correct and complete
	 * 
	 * @throws TestResultComparisonException  in case the comparison fails
	 */
	@Test
	public void classify() throws TestResultComparisonException {
		System.err.println(manifest.toString());
		
		reasoner.classify();
		
		ClassTaxonomy taxonomy = reasoner.getTaxonomy();
		
		System.err.println(taxonomy.getNodes().size());
		
		manifest.compare(new ClassTaxonomyTestOutput(taxonomy));
	}

	@Test
	public void classifyReshuffle() throws TestResultComparisonException {
		System.err.println("Reshuffle");
	}
}
