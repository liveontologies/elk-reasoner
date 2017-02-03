/*-
 * #%L
 * ELK Utilities for Testing
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.semanticweb.elk.io.FileUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.ManifestCreator;

public class ConfigurationBuilder {

	private final Class<?> srcClass_;

	private final ManifestCreator<?> creator_;

	private String name_;

	private final List<PolySuite.Configuration> children_;

	/**
	 * Iteration over the entries must be in the same order as they were
	 * inserted!
	 */
	private final Map<String, List<String>> fileNamesPerExtension_;

	public ConfigurationBuilder(final Class<?> srcClass,
			final ManifestCreator<?> creator, final String fileExt,
			final String... fileExts) {
		this.srcClass_ = srcClass;
		this.creator_ = creator;
		this.name_ = null;
		this.children_ = new ArrayList<PolySuite.Configuration>();
		this.fileNamesPerExtension_ = new LinkedHashMap<String, List<String>>();
		this.fileNamesPerExtension_.put(fileExt, new ArrayList<String>());
		if (fileExts != null) {
			for (final String ext : fileExts) {
				this.fileNamesPerExtension_.put(ext, new ArrayList<String>());
			}
		}
	}

	public ConfigurationBuilder setName(final String name) {
		this.name_ = name;
		return this;
	}

	public String getName() {
		return name_;
	}

	public ConfigurationBuilder addFileName(final String fileName) {
		final List<String> fileNames = fileNamesPerExtension_
				.get(FileUtils.getExtension(fileName));
		if (fileNames != null) {
			fileNames.add(fileName);
		}
		return this;
	}

	public ConfigurationBuilder addChild(final PolySuite.Configuration child) {
		children_.add(child);
		return this;
	}

	public PolySuite.Configuration build() throws IOException {

		// Check required fields.
		if (name_ == null) {
			throw new IllegalStateException("name must not be null!");
		}

		// Get names of files in the input directory and sort them.
		final List<Iterator<String>> fileNameIters = new ArrayList<Iterator<String>>(
				fileNamesPerExtension_.size());
		for (final List<String> fileNames : fileNamesPerExtension_.values()) {
			Collections.sort(fileNames);
			fileNameIters.add(fileNames.iterator());
		}

		// Create manifests for tuples of the same file names without extension.
		final List<TestManifest<?>> manifests = new ArrayList<TestManifest<?>>();

		final List<Integer> minIndices = new ArrayList<Integer>(
				fileNamesPerExtension_.size());
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
			final List<URL> urls = new ArrayList<URL>(
					fileNamesPerExtension_.size());
			for (int i = 0; i < fileNamesPerExtension_.size(); i++) {
				urls.add(null);
			}
			String file = null;
			for (final Integer index : minIndices) {
				urls.set(index, srcClass_.getClassLoader()
						.getResource(files.get(index)));
				file = files.get(index);
			}
			final Collection<? extends TestManifest<?>> manifs = creator_
					.createManifests(FileUtils.dropExtension(file), urls);
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

		return new ConfigurationImpl(name_, manifests, children_);
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

	private static class ConfigurationImpl implements PolySuite.Configuration {

		private final String name_;
		private final Collection<? extends TestManifest<?>> manifests_;
		private final Collection<? extends PolySuite.Configuration> children_;

		public ConfigurationImpl(final String name,
				final Collection<? extends TestManifest<?>> manifests,
				final Collection<? extends PolySuite.Configuration> children) {
			this.name_ = name;
			this.manifests_ = Collections.unmodifiableCollection(manifests);
			this.children_ = Collections.unmodifiableCollection(children);
		}

		@Override
		public String getName() {
			return name_;
		}

		@Override
		public Collection<? extends TestManifest<?>> getManifests() {
			return manifests_;
		}

		@Override
		public Collection<? extends PolySuite.Configuration> getChildren() {
			return children_;
		}

		@Override
		public boolean isEmpty() {
			return manifests_.isEmpty() && children_.isEmpty();
		}

	}

}
