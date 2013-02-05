/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Ignore;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.InstanceTaxonomyTestOutput;
import org.semanticweb.elk.reasoner.ReasoningTestManifest;
import org.semanticweb.elk.reasoner.TaxonomyDiffManifest;
import org.semanticweb.elk.reasoner.incremental.RandomWalkRunnerIO.ElkAPIBasedIO;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.TestManifestCreator;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.io.URLTestIO;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
@Ignore
public class RandomWalkIncrementalRealizationCorrectnessTest extends
		BaseRandomWalkIncrementalCorrectnessTest {

	final static String INPUT_DATA_LOCATION = "realization_test_input";
	
	public RandomWalkIncrementalRealizationCorrectnessTest(
			ReasoningTestManifest<InstanceTaxonomyTestOutput, InstanceTaxonomyTestOutput> testManifest) {
		super(testManifest);
	}

	@Override
	protected RandomWalkIncrementalClassificationRunner<ElkAxiom> getRandomWalkRunner(
			int rounds, int iterations) {
		return new RandomWalkIncrementalRealizationRunner<ElkAxiom>(rounds, iterations, new ElkAPIBasedIO());
	}
	
	
	@Config
	public static Configuration getConfig() throws URISyntaxException,
			IOException {
		return ConfigurationUtils
				.loadFileBasedTestConfiguration(
						INPUT_DATA_LOCATION,
						RandomWalkIncrementalClassificationCorrectnessTest.class,
						"owl",
						"expected",
						new TestManifestCreator<URLTestIO, InstanceTaxonomyTestOutput, InstanceTaxonomyTestOutput>() {
							@Override
							public TestManifest<URLTestIO, InstanceTaxonomyTestOutput, InstanceTaxonomyTestOutput> create(
									URL input, URL output) throws IOException {
								// don't need an expected output for these tests
								return new TaxonomyDiffManifest<InstanceTaxonomyTestOutput, InstanceTaxonomyTestOutput>(
										input, null);
							}
						});
	}

}
