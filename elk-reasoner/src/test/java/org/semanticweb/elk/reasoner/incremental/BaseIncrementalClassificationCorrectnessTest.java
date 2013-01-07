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
import org.semanticweb.elk.reasoner.ElkInconsistentOntologyException;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasoningTestManifest;
import org.semanticweb.elk.reasoner.TaxonomyDiffManifest;
import org.semanticweb.elk.reasoner.taxonomy.PredefinedTaxonomy;
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
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
@RunWith(PolySuite.class)
public class BaseIncrementalClassificationCorrectnessTest
		extends
		BaseIncrementalReasoningCorrectnessTest<ClassTaxonomyTestOutput, ClassTaxonomyTestOutput> {

	final static String INPUT_DATA_LOCATION = "classification_test_input";

	public BaseIncrementalClassificationCorrectnessTest(
			ReasoningTestManifest<ClassTaxonomyTestOutput, ClassTaxonomyTestOutput> testManifest) {
		super(testManifest);
	}

	@Override
	protected void correctnessCheck(Reasoner standardReasoner,
			Reasoner incrementalReasoner, long seed) throws ElkException {
		if (LOGGER_.isDebugEnabled())
			LOGGER_.debug("======= Computing Expected Taxonomy =======");

		Taxonomy<ElkClass> expected = getTaxonomy(standardReasoner);

		if (LOGGER_.isDebugEnabled())
			LOGGER_.debug("======= Computing Incremental Taxonomy =======");

		Taxonomy<ElkClass> incremental;
		try {
			incremental = getTaxonomy(incrementalReasoner);
		} catch (Exception e) {
			throw new RuntimeException("Seed: " + seed, e);
		}

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

	private Taxonomy<ElkClass> getTaxonomy(Reasoner reasoner)
			throws ElkException {
		Taxonomy<ElkClass> result = null;

		try {
			result = reasoner.getTaxonomy();
		} catch (ElkInconsistentOntologyException e) {
			LOGGER_.info("Ontology is inconsistent");

			result = PredefinedTaxonomy.INCONSISTENT_CLASS_TAXONOMY;
		}

		return result;
	}

	@Config
	public static Configuration getConfig() throws URISyntaxException,
			IOException {
		return ConfigurationUtils
				.loadFileBasedTestConfiguration(
						INPUT_DATA_LOCATION,
						BaseIncrementalClassificationCorrectnessTest.class,
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
