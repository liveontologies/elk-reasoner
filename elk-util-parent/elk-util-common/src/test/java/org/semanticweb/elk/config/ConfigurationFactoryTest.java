/*
 * #%L
 * elk-util-common
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
package org.semanticweb.elk.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.testing.TestUtils;

/**
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ConfigurationFactoryTest {

	@SuppressWarnings("static-method")
	@Before
	public void setUp() {
		TestUtils.createTestEnvironment(new File(""));
	}

	@SuppressWarnings("static-method")
	@After
	public void cleanUp() {
		TestUtils.cleanUp(new File(""));
	}

	@SuppressWarnings("static-method")
	@Test
	public void getDefaultConfiguration() {
		BaseConfiguration defaultConfig = new ConfigurationFactory()
				.getConfiguration("", BaseConfiguration.class);

		assertEquals(3, defaultConfig.getParameterNames().size());
	}

	@SuppressWarnings("static-method")
	@Test
	public void getDefaultConfigurationWithPrefix() {
		BaseConfiguration defaultConfig = new ConfigurationFactory()
				.getConfiguration("elk.reasoner", BaseConfiguration.class);

		assertEquals(2, defaultConfig.getParameterNames().size());

		defaultConfig = new ConfigurationFactory().getConfiguration(
				"elk.parser", BaseConfiguration.class);

		assertEquals(1, defaultConfig.getParameterNames().size());
	}

	@Test
	public void getConfigurationFromStream() throws ConfigurationException,
			IOException {
		InputStream stream = null;

		try {
			stream = this.getClass().getClassLoader()
					.getResourceAsStream("elk.properties");

			BaseConfiguration config = new ConfigurationFactory()
					.getConfiguration(stream, "", BaseConfiguration.class);

			assertEquals(3, config.getParameterNames().size());
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	@SuppressWarnings("static-method")
	@Test
	public void roundtrip() throws ConfigurationException, IOException {
		ConfigurationFactory factory = new ConfigurationFactory();
		BaseConfiguration defaultConfig = factory.getConfiguration("",
				BaseConfiguration.class);
		InputStream stream = null;
		File testFile = new File(TestUtils.TEST_ROOT + "/test.properties");

		factory.saveConfiguration(testFile, defaultConfig);

		try {
			stream = new FileInputStream(testFile);

			BaseConfiguration loaded = factory.getConfiguration(stream, "",
					BaseConfiguration.class);

			assertNotNull(loaded);
			assertEquals(defaultConfig.getParameterNames().size(), loaded
					.getParameterNames().size());

			for (String key : defaultConfig.getParameterNames()) {
				assertEquals(defaultConfig.getParameter(key),
						loaded.getParameter(key));
			}
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
}