/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.runner.RunWith;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.ClassTaxonomyTestOutput;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasoningTestManifest;
import org.semanticweb.elk.reasoner.TaxonomyDiffManifest;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyPrinter;
import org.semanticweb.elk.reasoner.taxonomy.hashing.TaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.TestManifestCreator;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.io.URLTestIO;

/**
 * Implements the correctness check based on comparing expected and obtained
 * class taxonomies. Subclasses still need to provide methods to load changes
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
@RunWith(PolySuite.class)
public abstract class BaseIncrementalClassificationCorrectnessTest<T>
		extends
		BaseIncrementalReasoningCorrectnessTest<T, ClassTaxonomyTestOutput, ClassTaxonomyTestOutput> {

	final static String INPUT_DATA_LOCATION = "classification_test_input";

	public BaseIncrementalClassificationCorrectnessTest(
			ReasoningTestManifest<ClassTaxonomyTestOutput, ClassTaxonomyTestOutput> testManifest) {
		super(testManifest);
	}

	@Override
	protected void correctnessCheck(Reasoner standardReasoner,
			Reasoner incrementalReasoner, long seed) throws ElkException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("======= Computing Expected Taxonomy =======");

		Taxonomy<ElkClass> expected = standardReasoner.getTaxonomyQuietly();

		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("======= Computing Incremental Taxonomy =======");

		Taxonomy<ElkClass> incremental = incrementalReasoner.getTaxonomyQuietly();

		try {
			assertEquals("Seed " + seed, TaxonomyHasher.hash(expected),
					TaxonomyHasher.hash(incremental));
		} catch (AssertionError e) {
			try {
				Writer writer = new OutputStreamWriter(System.out);
				TaxonomyPrinter.dumpClassTaxomomy(expected, writer, false);
				TaxonomyPrinter.dumpClassTaxomomy(incremental, writer, false);
				writer.flush();
				
				throw e;
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	@Config
	public static Configuration getConfig() throws URISyntaxException,
			IOException {
		return ConfigurationUtils
				.loadFileBasedTestConfiguration(
						INPUT_DATA_LOCATION,
						IncrementalClassificationCorrectnessTest.class,
						"owl",
						"expected",
						new TestManifestCreator<URLTestIO, ClassTaxonomyTestOutput, ClassTaxonomyTestOutput>() {
							@Override
							public TestManifest<URLTestIO, ClassTaxonomyTestOutput, ClassTaxonomyTestOutput> create(
									URL input, URL output) throws IOException {
								// don't need an expected output for these tests
								return new TaxonomyDiffManifest<ClassTaxonomyTestOutput, ClassTaxonomyTestOutput>(
										input, null);
							}
						});
	}

}
