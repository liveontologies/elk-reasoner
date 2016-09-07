/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.query;

import java.net.URL;

import org.semanticweb.elk.io.FileUtils;
import org.semanticweb.elk.testing.BasicTestManifest;
import org.semanticweb.elk.testing.TestOutput;

/**
 * Test manifest for complex class query tests.
 * 
 * @author Peter Skocovsky
 *
 * @param <C>
 *            Type of the query class.
 * @param <O>
 *            Type of the test output.
 */
public class ClassExpressionQueryTestManifest<C, O extends TestOutput>
		extends BasicTestManifest<ClassQueryTestInput<C>, O, O> {

	public ClassExpressionQueryTestManifest(final String testName,
			final URL input, final C queryClass, final O expectedOutput) {
		super(testName, new ClassQueryTestInput<C>() {

			@Override
			public String getName() {
				return FileUtils.getFileName(input.getPath());
			}

			@Override
			public URL getUrl() {
				return input;
			}

			@Override
			public C getClassQuery() {
				return queryClass;
			}

		}, expectedOutput);
	}

	/**
	 * Constructor that extract test name from the input URL.
	 * 
	 * @param input
	 * @param queryClass
	 * @param expectedOutput
	 */
	public ClassExpressionQueryTestManifest(final URL input, final C queryClass,
			final O expectedOutput) {
		this(FileUtils.getFileName(FileUtils.dropExtension(input.getPath())),
				input, queryClass, expectedOutput);
	}

}
