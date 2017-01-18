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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
 * @author Peter Skocovsky
 */
public class ConfigurationUtils {

	/**
	 * Loads configuration from files in the specified input directory. The
	 * specified creator is used to create test manifests for each tuple of
	 * files that have the same name without the file extension; one tuple
	 * contains one file for each of the specified file name extensions in the
	 * same order.
	 * 
	 * @param inputDir
	 *            The input directory.
	 * @param srcClass
	 * @param creator
	 * @param fileExts
	 *            The file name extensions.
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static <I extends TestInput, EO extends TestOutput, AO extends TestOutput> Configuration loadFileBasedTestConfiguration(
			final String inputDir, final Class<?> srcClass,
			final ManifestCreator<I, EO, AO> creator, final String... fileExts)
			throws IOException, URISyntaxException {

		final List<TestManifestWithOutput<I, EO, AO>> manifests = new ArrayList<TestManifestWithOutput<I, EO, AO>>();

		if (fileExts == null || fileExts.length <= 0) {
			return new SimpleConfiguration<I, EO, AO>(manifests);
		}

		final URI srcURI = srcClass.getClassLoader().getResource(inputDir)
				.toURI();

		// Get names of files in the input directory and sort them.
		final List<Iterator<String>> fileNameIters = new ArrayList<Iterator<String>>(
				fileExts.length);
		for (final String fileExt : fileExts) {
			final List<String> fileNames = srcURI.isOpaque()
					? IOUtils.getResourceNamesFromJAR(inputDir, fileExt,
							srcClass)
					: IOUtils.getResourceNamesFromDir(new File(srcURI),
							fileExt);
			Collections.sort(fileNames);
			fileNameIters.add(fileNames.iterator());
		}

		// Create manifests for tuples of the same file names without extension.

		List<String> files = everyNext(fileNameIters);
		if (files == null) {
			return new SimpleConfiguration<I, EO, AO>(manifests);
		}

		while (true) {

			final int nonMaxIndex = getNonMaxIndex(files,
					new Comparator<String>() {

						@Override
						public int compare(final String o1, final String o2) {
							return FileUtils.dropExtension(o1)
									.compareTo(FileUtils.dropExtension(o2));
						}

					});

			if (nonMaxIndex < 0) {
				// The files have the same name.

				final List<URL> urls = new ArrayList<URL>(files.size());
				for (final String file : files) {
					urls.add(srcClass.getClassLoader().getResource(file));
				}

				final Collection<? extends TestManifestWithOutput<I, EO, AO>> manifs = creator
						.createManifests(urls);
				if (manifs != null) {
					manifests.addAll(manifs);
				}

				files = everyNext(fileNameIters);
				if (files == null) {
					break;
				}

			} else {
				// The file on the index nonMaxIndex has smaller name.

				final Iterator<String> iter = fileNameIters.get(nonMaxIndex);
				if (!iter.hasNext()) {
					break;
				}
				files.set(nonMaxIndex, iter.next());

			}

		}

		return new SimpleConfiguration<I, EO, AO>(manifests);
	}

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
			final ManifestCreator<I, EO, AO> creator)
			throws IOException, URISyntaxException {

		return loadFileBasedTestConfiguration(path, srcClass, creator, inputFileExt, outputFileExt);

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
			final ManifestCreator<I, EO, AO> creator)
			throws IOException, URISyntaxException {

		return loadFileBasedTestConfiguration(path, srcClass, creator,
				inputFileExt);

	}

	/**
	 * @param iterators
	 * @return Next element from every iterator, or {@code null} if some
	 *         iterator does not have a next element.
	 */
	private static <E> List<E> everyNext(
			final Collection<? extends Iterator<E>> iterators) {
		final List<E> result = new ArrayList<E>(iterators.size());
		for (final Iterator<E> iterator : iterators) {
			if (!iterator.hasNext()) {
				return null;
			}
			result.add(iterator.next());
		}
		return result;
	}

	/**
	 * @param list
	 * @param comparator
	 * @return Index of the element that is not maximal, or {@code -1} if it
	 *         does not exist.
	 */
	private static <T> int getNonMaxIndex(final List<? extends T> list,
			final Comparator<? super T> comparator) {

		final Iterator<? extends T> iter = list.iterator();

		if (!iter.hasNext()) {
			return -1;
		}

		final T first = iter.next();
		int index = 0;
		while (iter.hasNext()) {
			final T current = iter.next();
			index++;
			final int cmp = comparator.compare(first, current);
			if (cmp < 0) {
				// first < current; first is not maximal, return its index
				return 0;
			} else if (cmp > 0) {
				// first > current; current is not maximal, return its index
				return index;
			}
			// else first == current; do nothing
		}

		// all elements are the same
		return -1;
	}

	/**
	 * Creates possibly multiple manifests from provided arguments.
	 * 
	 * @author Peter Skocovsky
	 *
	 * @param <I>
	 * @param <EO>
	 * @param <AO>
	 */
	public interface ManifestCreator<I extends TestInput, EO extends TestOutput, AO extends TestOutput> {

		public Collection<? extends TestManifestWithOutput<I, EO, AO>> createManifests(
				List<URL> urls) throws IOException;

	}

	/**
	 * Creates possibly multiple manifests from provided arguments.
	 * 
	 * @author Peter Skocovsky
	 *
	 * @param <I>
	 * @param <EO>
	 * @param <AO>
	 */
	public static abstract class MultiManifestCreator<I extends TestInput, EO extends TestOutput, AO extends TestOutput>
			implements ManifestCreator<I, EO, AO> {

		@Override
		public Collection<? extends TestManifestWithOutput<I, EO, AO>> createManifests(
				final List<URL> urls) throws IOException {
			if (urls.isEmpty()) {
				return Collections.emptyList();
			}
			// else
			if (urls.size() < 2) {
				return createManifests(urls.get(0), null);
			}
			// else
			return createManifests(urls.get(0), urls.get(1));
		}

		public abstract Collection<? extends TestManifestWithOutput<I, EO, AO>> createManifests(
				URL input, URL output) throws IOException;

	}

	/**
	 * Creates single test manifest from provided arguments.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 * @author Peter Skocovsky
	 * @param <I>
	 * @param <EO>
	 * @param <AO>
	 */
	public static abstract class TestManifestCreator<I extends TestInput, EO extends TestOutput, AO extends TestOutput>
			extends MultiManifestCreator<I, EO, AO> {

		@Override
		public Collection<? extends TestManifestWithOutput<I, EO, AO>> createManifests(
				final URL input, final URL output) throws IOException {
			return Collections.singleton(create(input, output));
		}

		public abstract TestManifestWithOutput<I, EO, AO> create(URL input,
				URL output) throws IOException;

	}

	public static Configuration combine(final Configuration first,
			final Configuration second) {
		if (first == null || first.size() <= 0) {
			return second;
		}
		// else
		return new Configuration() {

			@Override
			public int size() {
				return first.size() + second.size();
			}

			@Override
			public Object getTestValue(final int index) {
				final int firstSize = first.size();
				return index < firstSize ? first.getTestValue(index)
						: second.getTestValue(index - firstSize);
			}

			@Override
			public String getTestName(final int index) {
				final int firstSize = first.size();
				return index < firstSize ? first.getTestName(index)
						: second.getTestName(index - firstSize);
			}

		};
	}

}