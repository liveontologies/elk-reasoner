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
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

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
			final ManifestCreator<?> creator, final String fileExt,
			final String... fileExts) throws IOException, URISyntaxException {

		final BuildingVisitor buildingVisitor = new BuildingVisitor(srcClass,
				creator, fileExt, fileExts);

		final URI srcURI = srcClass.getClassLoader().getResource(inputDir)
				.toURI();

		if (srcURI.isOpaque()) {
			IOUtils.traverseJarContentTree(inputDir, srcClass, buildingVisitor);
		} else {
			IOUtils.traverseDirectoryTree(new File(srcURI), buildingVisitor);
		}

		if (buildingVisitor.getException() != null) {
			throw buildingVisitor.getException();
		}
		// else

		return buildingVisitor.getResult();
	}

	private static class BuildingVisitor implements IOUtils.PathVisitor {

		private final Class<?> srcClass;
		private final ManifestCreator<?> creator;
		private final String fileExt;
		private final String[] fileExts;

		private final Stack<ConfigurationBuilder> currentBranch = new Stack<ConfigurationBuilder>();

		private Configuration result_ = null;
		private IOException exception_ = null;

		public BuildingVisitor(final Class<?> srcClass,
				final ManifestCreator<?> creator, final String fileExt,
				final String... fileExts) {
			this.srcClass = srcClass;
			this.creator = creator;
			this.fileExt = fileExt;
			this.fileExts = fileExts;
		}

		public Configuration getResult() {
			return result_;
		}

		public IOException getException() {
			return exception_;
		}

		@Override
		public void visitBefore(final String path) {
			if (exception_ != null) {
				return;
			}
			// else
			currentBranch.push(new ConfigurationBuilder(srcClass, creator,
					fileExt, fileExts).setName(path));
		}

		@Override
		public void visitAfter(final String path) {
			if (exception_ != null) {
				return;
			}
			// else
			final ConfigurationBuilder currentBuilder = currentBranch.pop();

			if (currentBranch.isEmpty()) {
				/*
				 * We've just returned to the root. Even if no relevant content
				 * was collected, build and return.
				 */
				result_ = build(currentBuilder);
				return;
			}

			if (currentBuilder.isEmpty()) {
				/*
				 * Either a file or a directory with no files with specified
				 * extensions. Add it as a file to parent, if it is a directory,
				 * it wouldn't have any of the extensions.
				 */
				currentBranch.peek().addFileName(path);
			} else {
				/*
				 * A directory with some relevant content. Build and add to the
				 * parent.
				 */
				final Configuration child = build(currentBuilder);
				if (exception_ == null) {
					currentBranch.peek().addChild(child);
				}
			}

		}

		private Configuration build(final ConfigurationBuilder builder) {
			try {
				return builder.build();
			} catch (final IOException e) {
				exception_ = e;
				return null;
			}
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

		public Collection<? extends M> createManifests(String name,
				List<URL> urls) throws IOException;

	}

	public static Configuration empty() {
		return new Configuration() {

			@Override
			public String getName() {
				return "∅";
			}

			@Override
			public Collection<? extends TestManifest<?>> getManifests() {
				return Collections.emptySet();
			}

			@Override
			public Collection<? extends Configuration> getChildren() {
				return Collections.emptySet();
			}

			@Override
			public boolean isEmpty() {
				return true;
			}

		};
	}

	public static Configuration combine(final Configuration first,
			final Configuration second) {
		final Configuration sec = second == null ? empty() : second;
		if (first == null || first.isEmpty()) {
			return sec;
		}
		// else
		return new Configuration() {

			@Override
			public String getName() {
				return first.getName() + "+" + sec.getName();
			}

			@Override
			public Collection<? extends TestManifest<?>> getManifests() {
				return new AbstractCollection<TestManifest<?>>() {

					@Override
					public Iterator<TestManifest<?>> iterator() {
						final Iterator<? extends TestManifest<?>> firstIter = first
								.getManifests().iterator();
						final Iterator<? extends TestManifest<?>> secondIter = sec
								.getManifests().iterator();
						return new Iterator<TestManifest<?>>() {

							@Override
							public boolean hasNext() {
								return firstIter.hasNext()
										|| secondIter.hasNext();
							}

							@Override
							public TestManifest<?> next() {
								if (firstIter.hasNext()) {
									return firstIter.next();
								}
								// else
								return secondIter.next();
							}

							@Override
							public void remove() {
								throw new UnsupportedOperationException();
							}

						};
					}

					@Override
					public int size() {
						return first.getManifests().size()
								+ sec.getManifests().size();
					}

				};
			}

			@Override
			public Collection<? extends Configuration> getChildren() {
				return new AbstractCollection<Configuration>() {

					@Override
					public Iterator<Configuration> iterator() {
						final Iterator<? extends Configuration> firstIter = first
								.getChildren().iterator();
						final Iterator<? extends Configuration> secondIter = sec
								.getChildren().iterator();
						return new Iterator<Configuration>() {

							@Override
							public boolean hasNext() {
								return firstIter.hasNext()
										|| secondIter.hasNext();
							}

							@Override
							public Configuration next() {
								if (firstIter.hasNext()) {
									return firstIter.next();
								}
								// else
								return secondIter.next();
							}

							@Override
							public void remove() {
								throw new UnsupportedOperationException();
							}

						};
					}

					@Override
					public int size() {
						return first.getManifests().size()
								+ sec.getManifests().size();
					}

				};
			}

			@Override
			public boolean isEmpty() {
				return first.isEmpty() && sec.isEmpty();
			}

		};
	}

}