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

import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.reasoner.taxonomy.Taxonomy;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.TestInput;
import org.semanticweb.elk.testing.TestOutput;
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
public abstract class BaseClassificationCorrectnessTest<EO extends TestOutput> {
	
	final static String INPUT_DATA_LOCATION = "classification_test_input";
	
	private final ReasoningTestManifest<EO, ClassTaxonomyTestOutput> manifest;
	private InputStream inputStream;
	private Reasoner reasoner;
	
	public BaseClassificationCorrectnessTest(ReasoningTestManifest<EO, ClassTaxonomyTestOutput> testManifest) {
		manifest = testManifest;
	}
	
	@Before
	public void before() throws IOException, Owl2ParseException {
		assumeTrue(!ignore(manifest.getInput()));
		
		inputStream = ((URLTestIO)manifest.getInput()).getInputStream();
		reasoner = createReasoner(inputStream);
	}
	
	@After
	public void after() {
		IOUtils.closeQuietly(inputStream);
	}
	
	protected boolean ignore(TestInput input) {
		return false;
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
		
		Taxonomy<ElkClass> taxonomy;
		try {
			taxonomy = reasoner.getTaxonomy();
			manifest.compare(new ClassTaxonomyTestOutput(taxonomy));
		} catch (InconsistentOntologyException e) {
			manifest.compare(new ClassTaxonomyTestOutput());
		}
		
		/*try {
			Writer writer = new OutputStreamWriter(System.out);
			ClassTaxonomyPrinter.dumpClassTaxomomy(taxonomy, writer, true);
			writer.flush();
		} catch (IOException e) {}*/
		

	}
}