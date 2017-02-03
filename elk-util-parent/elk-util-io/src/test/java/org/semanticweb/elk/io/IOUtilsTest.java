/*-
 * #%L
 * ELK Utilities for Input-Output
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
package org.semanticweb.elk.io;

import java.io.IOException;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Assert;
import org.junit.Test;

public class IOUtilsTest {

	@Test
	public void testTraverseJarContentTreeShort() throws Exception {
		testTraverseJarContentTree("META-INF/");
	}

	@Test
	public void testTraverseJarContentTreeLong() throws Exception {
		testTraverseJarContentTree("org");
	}

	public void testTraverseJarContentTree(final String rootPath)
			throws IOException {

		final Class<?> srcClass = Test.class;
		final String root = rootPath.endsWith("/") ? rootPath : rootPath + "/";

		final List<String> jarContent = collectJarContent(root, srcClass);
		if (!jarContent.contains(root)) {
			jarContent.add(root);
		}
		Collections.sort(jarContent);

		final Stack<String> pathStack = new Stack<String>();

		IOUtils.traverseJarContentTree(rootPath, srcClass,
				new IOUtils.PathVisitor() {

					@Override
					public void visitBefore(final String path) {
						/*
						 * This must visit the content in alphabetical order,
						 * each element exactly once!
						 */
						final String first = jarContent.get(0);
						Assert.assertEquals("Visited by Before in wrong order!",
								first, path);
						jarContent.remove(0);
						pathStack.push(path);
					}

					@Override
					public void visitAfter(final String path) {
						/*
						 * This must visit everything that was visited by
						 * Before.
						 */
						Assert.assertFalse("Visited by After before by Before!",
								pathStack.isEmpty());
						final String top = pathStack.pop();
						Assert.assertEquals("Visited by After in wrong order!",
								top, path);
					}

				});

		Assert.assertTrue("Not all elements visited by Before!",
				jarContent.isEmpty());
		Assert.assertTrue("Not all elements visited by After!",
				pathStack.isEmpty());

	}

	public static List<String> collectJarContent(final String rootPath,
			final Class<?> srcClass) throws IOException {

		final CodeSource src = srcClass.getProtectionDomain().getCodeSource();
		if (src == null) {
			throw new IOException("Unable to get code source for "
					+ srcClass.getSimpleName());
		}

		final List<String> paths = new ArrayList<String>();
		ZipInputStream zip = null;
		try {
			zip = new ZipInputStream(src.getLocation().openStream());

			ZipEntry ze = null;
			while ((ze = zip.getNextEntry()) != null) {
				final String entryName = ze.getName();
				if (entryName.startsWith(rootPath)) {
					paths.add(entryName);
				}
			}
		} finally {
			IOUtils.closeQuietly(zip);
		}

		return paths;
	}

}
