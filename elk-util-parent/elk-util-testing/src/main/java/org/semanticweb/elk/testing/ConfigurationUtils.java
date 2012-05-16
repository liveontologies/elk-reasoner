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
/**
 * 
 */
package org.semanticweb.elk.testing;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.semanticweb.elk.io.FileUtils;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.io.URLTestIO;

/**
 * A collection of utility methods to create various common test configurations to be run by {@link PolySuite}}
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public class ConfigurationUtils {

	//final static Logger LOGGER_ = Logger.getLogger(ConfigurationUtils.class);
	final static String EXPECTED_SUFFIX = ".expected";
	
	/**
	 * Loads configuration from a set of files by passing both input and expected output URLs to the test manifest creator
	 * 
	 * @return
	 */
	public static <I extends TestInput, EO extends TestOutput, AO extends TestOutput> Configuration loadFileBasedTestConfiguration(
																final URI srcURI,
																final Class<?> srcClass,
																final String inputFileExt,
																final String outputFileExt,
																final TestManifestCreator<URLTestIO, EO, AO> creator) throws IOException {
		//Load inputs and expected results
		final List<String> inputs = srcURI.isOpaque() 
				? IOUtils.getResourceNamesFromJAR(srcURI, inputFileExt, srcClass)
				: IOUtils.getResourceNamesFromDir(new File(srcURI), inputFileExt);
		final List<String> results = srcURI.isOpaque() 
				? IOUtils.getResourceNamesFromJAR(srcURI, outputFileExt, srcClass)
				: IOUtils.getResourceNamesFromDir(new File(srcURI), outputFileExt);				
		
		Collections.sort(inputs);
		Collections.sort(results);
		
		//do the matching via a kind of merge join
		final List<TestManifest<URLTestIO, EO, AO>> manifests = new ArrayList<TestManifest<URLTestIO,EO,AO>>(inputs.size());
		Iterator<String> resultIter = results.iterator();
		Iterator<String> inputIter = inputs.iterator();
		String nextResult = resultIter.next();
		String nextInput = inputIter.next();
		
		while (true) {
			int cmp = FileUtils.dropExtension(nextResult).compareTo(FileUtils.dropExtension(nextInput) + EXPECTED_SUFFIX); 
			
			if (cmp == 0) {
				//Match
				URL inputURL = srcClass.getClassLoader().getResource(nextInput);
				URL resultURL = srcClass.getClassLoader().getResource(nextResult);
				TestManifest<URLTestIO, EO, AO> manifest = creator.create(inputURL, resultURL);
				
				if (manifest != null) manifests.add(manifest);
				
				if (endOfData(inputIter, resultIter)) break;
				
				nextInput = inputIter.next();
				nextResult = resultIter.next();
			}
			else if (cmp > 0) {
				if (endOfData(inputIter, resultIter)) break;
				
				nextInput = inputIter.next();
			}
			else if (cmp < 0) {
				if (endOfData(inputIter, resultIter)) break;
				
				nextResult = resultIter.next();
			}
		}
		
		return new SimpleConfiguration<URLTestIO, EO, AO>(manifests);
	}
	
	private static boolean endOfData(Iterator<String> inputIter, Iterator<String> resultIter) {
		return !resultIter.hasNext() || !inputIter.hasNext();
	}
	
	/**
	 * Interface for classes which create test manifest from provided arguments
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 *
	 */
	public interface TestManifestCreator<I extends TestInput, EO extends TestOutput, AO extends TestOutput> {
		
		public TestManifest<I, EO, AO> create(URL input, URL output);
	}
}

/**
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
class SimpleConfiguration<I extends TestInput, EO extends TestOutput, AO extends TestOutput> implements Configuration {

	private final List<TestManifest<I, EO, AO>> manifests;
	
	SimpleConfiguration(List<TestManifest<I, EO, AO>> manifests) {
		this.manifests = manifests;
	}
	
	@Override
	public int size() {
		return manifests.size();
	}

	@Override
	public TestManifest<I, EO, AO> getTestValue(int index) {
		return manifests.get(index);
	}

	@Override
	public String getTestName(int index) {
		return "test" + manifests.get(index).getName();
	}
}