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
	 * directory is traversed recursively. The specified creator is used to
	 * create test manifests for each tuple of files, such that:
	 * <ul>
	 * <li/>these files have the same name without the file extension,
	 * <li/>file at some position in the tuple has file extension that is at
	 * this position in the specified tuple file extensions,
	 * <li/>if file with some file extension is missing, {@code null} is at the
	 * position of this extension.
	 * </ul>
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
	public static Configuration loadFileBasedTestConfiguration(
			final String inputDir, final Class<?> srcClass,
			final ManifestCreator<?> creator, final String... fileExts)
			throws IOException, URISyntaxException {

		final List<TestManifest<?>> manifests = new ArrayList<TestManifest<?>>();

		if (fileExts == null || fileExts.length <= 0) {
			return new SimpleConfiguration(manifests);
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

		final List<Integer> minIndices = new ArrayList<Integer>(
				fileExts.length);
		boolean everyHasNext = true;
		final List<String> files = everyNext(fileNameIters);
		if (files == null) {
			everyHasNext = false;
		}

		while (everyHasNext) {

			// Get indices of files with minimal name.
			collectMinIndices(files, new Comparator<String>() {

				@Override
				public int compare(final String o1, final String o2) {
					return FileUtils.dropExtension(o1)
							.compareTo(FileUtils.dropExtension(o2));
				}

			}, minIndices);

			// Create manifests from these files.
			final List<URL> urls = new ArrayList<URL>(fileExts.length);
			for (int i = 0; i < fileExts.length; i++) {
				urls.add(null);
			}
			for (final Integer index : minIndices) {
				urls.set(index, srcClass.getClassLoader()
						.getResource(files.get(index)));
			}
			final Collection<? extends TestManifest<?>> manifs = creator
					.createManifests(urls);
			if (manifs != null) {
				manifests.addAll(manifs);
			}

			// Advance iterators on the minimal indices.
			for (final Integer index : minIndices) {
				final Iterator<String> iter = fileNameIters.get(index);
				if (!iter.hasNext()) {
					everyHasNext = false;
					break;
				}
				// else
				files.set(index, iter.next());
			}

		}

		return new SimpleConfiguration(manifests);
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
	 * After this method successfully returns, {@code result} contains indices
	 * of minimal elements from {@code list} according to {@code comparator}.
	 * 
	 * @param list
	 * @param comparator
	 * @param result
	 */
	private static <T> void collectMinIndices(final List<? extends T> list,
			final Comparator<? super T> comparator,
			final List<Integer> result) {

		result.clear();

		final Iterator<? extends T> iter = list.iterator();

		if (!iter.hasNext()) {
			return;
		}

		T min = iter.next();
		int index = 0;
		result.add(index);
		while (iter.hasNext()) {
			final T current = iter.next();
			index++;
			final int cmp = comparator.compare(min, current);
			if (cmp > 0) {
				// min > current; min is not minimal, reset result
				min = current;
				result.clear();
				result.add(index);
			} else if (cmp == 0) {
				// min == current; current is minimal, add to result
				result.add(index);
			}
			// else min < current; do nothing
		}

	}

	/**
	 * Creates possibly multiple manifests from provided arguments.
	 * 
	 * @author Peter Skocovsky
	 *
	 * @param <M>
	 *            The type of created manifests.
	 */
	public interface ManifestCreator<M extends TestManifest<?>> {

		public Collection<? extends M> createManifests(List<URL> urls)
				throws IOException;

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