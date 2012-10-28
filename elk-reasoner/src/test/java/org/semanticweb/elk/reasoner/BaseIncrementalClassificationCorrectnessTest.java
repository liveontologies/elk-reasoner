/**
 * 
 */
package org.semanticweb.elk.reasoner;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.runner.RunWith;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
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
 * pavel.klinov@uni-ulm.de
 */
@RunWith(PolySuite.class)
public class BaseIncrementalClassificationCorrectnessTest
		extends BaseIncrementalReasoningCorrectnessTest<ClassTaxonomyTestOutput, ClassTaxonomyTestOutput> {

	final static String INPUT_DATA_LOCATION = "classification_test_input";	
	
	public BaseIncrementalClassificationCorrectnessTest(
			ReasoningTestManifest<ClassTaxonomyTestOutput, ClassTaxonomyTestOutput> testManifest) {
		super(testManifest);
	}

	@Override
	protected void correctnessCheck(Reasoner standardReasoner, Reasoner incrementalReasoner, long seed) throws ElkException {
		Taxonomy<ElkClass> expected = standardReasoner.getTaxonomy();
		Taxonomy<ElkClass> incremental = incrementalReasoner.getTaxonomy();
		
		assertEquals("Seed " + seed, TaxonomyHasher.hash(expected), TaxonomyHasher.hash(incremental));
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
								return new TaxonomyDiffManifest<ClassTaxonomyTestOutput, ClassTaxonomyTestOutput>(input, null);
							}
						});
	}	
}
