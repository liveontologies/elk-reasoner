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
package org.semanticweb.elk.reasoner.incremental;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;

import org.junit.runner.RunWith;
import org.semanticweb.elk.ElkTestUtils;
import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.reasoner.InstanceTaxonomyTestOutput;
import org.semanticweb.elk.reasoner.SimpleManifestCreator;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyPrinter;
import org.semanticweb.elk.reasoner.taxonomy.hashing.TaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.UrlTestInput;

/**
 * Implements the correctness check based on comparing expected and obtained
 * class taxonomies. Subclasses still need to provide methods to load changes
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 */
@RunWith(PolySuite.class)
public abstract class BaseIncrementalRealizationCorrectnessTest<A> extends
		IncrementalReasoningCorrectnessTestWithInterrupts<UrlTestInput, A, InstanceTaxonomyTestOutput, IncrementalReasoningTestWithInterruptsDelegate<A, InstanceTaxonomyTestOutput>> {

	public BaseIncrementalRealizationCorrectnessTest(
			final TestManifest<UrlTestInput> testManifest,
			final IncrementalReasoningTestWithInterruptsDelegate<A, InstanceTaxonomyTestOutput> testDelegate) {
		super(testManifest, testDelegate);
	}

	@Override
	protected void correctnessCheck(
			final InstanceTaxonomyTestOutput actualOutput,
			final InstanceTaxonomyTestOutput expectedOutput)
			throws ElkException {

		final InstanceTaxonomy<?, ?> expected = expectedOutput.getTaxonomy();

		final InstanceTaxonomy<?, ?> incremental = actualOutput.getTaxonomy();

		if (TaxonomyHasher.hash(expected) != TaxonomyHasher.hash(incremental)
				|| !expected.equals(incremental)) {
			StringWriter writer = new StringWriter();

			try {
				writer.write("EXPECTED TAXONOMY:\n");
				TaxonomyPrinter.dumpInstanceTaxomomy(expected, writer, false);
				writer.write("\nINCREMENTAL TAXONOMY:\n");
				TaxonomyPrinter.dumpInstanceTaxomomy(incremental, writer,
						false);
				writer.flush();
			} catch (IOException ioe) {
				// TODO
			}

			fail(writer.getBuffer().toString());
		}
	}

	@Config
	public static Configuration getConfig()
			throws URISyntaxException, IOException {
		return ConfigurationUtils.loadFileBasedTestConfiguration(
				ElkTestUtils.TEST_INPUT_LOCATION,
				IncrementalClassificationCorrectnessTest.class,
				SimpleManifestCreator.INSTANCE, "owl");
	}

}
