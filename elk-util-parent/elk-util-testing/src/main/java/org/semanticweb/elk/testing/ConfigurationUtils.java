/*
 * #%L
 * ELK Utilities for Testing
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
package org.semanticweb.elk.testing;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.semanticweb.elk.io.FileUtils;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.testing.PolySuite.Configuration;

/**
 * A collection of utility methods to create various common test configurations
 * to be run by {@link PolySuite}
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 */
public class ConfigurationUtils {

	/**
	 * Loads configuration from a set of files by passing both input and
	 * expected output URLs to the test manifest creator
	 * 
	 * @param path
	 * @param srcClass
	 * @param inputFileExt
	 * @param outputFileExt
	 * @param creator
	 * 
	 * @return the loaded configuration
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static <I extends TestInput, EO extends TestOutput, AO extends TestOutput> Configuration loadFileBasedTestConfiguration(
			final String path, final Class<?> srcClass,
			final String inputFileExt, final String outputFileExt,
			final TestManifestCreator<I, EO, AO> creator)
			throws IOException, URISyntaxException {
		final URI srcURI = srcClass.getClassLoader().getResource(path).toURI();
		// Load inputs and expected results
		final List<String> inputs = srcURI.isOpaque() ? IOUtils
				.getResourceNamesFromJAR(path, inputFileExt, srcClass)
				: IOUtils.getResourceNamesFromDir(new File(srcURI),
						inputFileExt);
		final List<String> results = srcURI.isOpaque() ? IOUtils
				.getResourceNamesFromJAR(path, outputFileExt, srcClass)
				: IOUtils.getResourceNamesFromDir(new File(srcURI),
						outputFileExt);

		Collections.sort(inputs);
		Collections.sort(results);

		// do the matching via a kind of merge join
		final List<TestManifestWithOutput<I, EO, AO>> manifests = new ArrayList<TestManifestWithOutput<I, EO, AO>>(
				inputs.size());
		Iterator<String> resultIter = results.iterator();
		Iterator<String> inputIter = inputs.iterator();

		if (!endOfData(inputIter, resultIter)) {
			String nextResult = resultIter.next();
			String nextInput = inputIter.next();

			while (true) {
				int cmp = FileUtils
						.dropExtension(nextResult, outputFileExt)
						.compareTo(
								FileUtils
										.dropExtension(nextInput, inputFileExt));

				if (cmp == 0) {
					// Match
					URL inputURL = srcClass.getClassLoader().getResource(
							nextInput);
					URL resultURL = srcClass.getClassLoader().getResource(
							nextResult);
					TestManifestWithOutput<I, EO, AO> manifest = creator.create(
							inputURL, resultURL);

					if (manifest != null)
						manifests.add(manifest);

					if (endOfData(inputIter, resultIter))
						break;

					nextInput = inputIter.next();
					nextResult = resultIter.next();
				} else if (cmp > 0) {
					if (endOfData(inputIter, resultIter))
						break;

					nextInput = inputIter.next();
				} else if (cmp < 0) {
					if (endOfData(inputIter, resultIter))
						break;

					nextResult = resultIter.next();
				}
			}
		}

		return new SimpleConfiguration<I, EO, AO>(manifests);
	}

	/**
	 * In case there're no expected results
	 * 
	 * @param path
	 * @param srcClass
	 * @param inputFileExt
	 * @param creator
	 * 
	 * @return the loaded test cofiguration
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static <I extends TestInput, EO extends TestOutput, AO extends TestOutput> Configuration loadFileBasedTestConfiguration(
			final String path, final Class<?> srcClass,
			final String inputFileExt,
			final TestManifestCreator<I, EO, AO> creator)
			throws IOException, URISyntaxException {
		final URI srcURI = srcClass.getClassLoader().getResource(path).toURI();
		// Load inputs
		final List<String> inputs = srcURI.isOpaque() ? IOUtils
				.getResourceNamesFromJAR(path, inputFileExt, srcClass)
				: IOUtils.getResourceNamesFromDir(new File(srcURI),
						inputFileExt);

		final List<TestManifestWithOutput<I, EO, AO>> manifests = new ArrayList<TestManifestWithOutput<I, EO, AO>>(
				inputs.size());

		for (String input : inputs) {
			URL inputURL = srcClass.getClassLoader().getResource(input);
			TestManifestWithOutput<I, EO, AO> manifest = creator.create(inputURL,
					null);

			if (manifest != null)
				manifests.add(manifest);
		}

		return new SimpleConfiguration<I, EO, AO>(manifests);
	}

	private static boolean endOfData(Iterator<String> inputIter,
			Iterator<String> resultIter) {
		return !resultIter.hasNext() || !inputIter.hasNext();
	}

	/**
	 * Interface for classes which create test manifest from provided arguments
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 * @param <I> 
	 * @param <EO> 
	 * @param <AO> 
	 * 
	 */
	public interface TestManifestCreator<I extends TestInput, EO extends TestOutput, AO extends TestOutput> {

		public TestManifestWithOutput<I, EO, AO> create(URL input, URL output)
				throws IOException;
	}
}