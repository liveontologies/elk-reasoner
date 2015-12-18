/*
 * #%L
 * Elk Examples Package
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
package org.semanticweb.elk.distribution;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.elk.io.FileUtils;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.testing.BasicTestManifest;
import org.semanticweb.elk.testing.BooleanTestOutput;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.TestManifestCreator;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.TestUtils;
import org.semanticweb.elk.testing.io.URLTestIO;

/**
 * Tests that each OWL API example compiles with the current version of
 * elk-owlapi-standalone
 * 
 * WARNING: This test does not run from IDE, only via Maven, because it expects
 * some parameters, e.g., the location of elk-owlapi-standalone-VERSION.zip to
 * be passed into it.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 */
@RunWith(PolySuite.class)
public class OWLAPIExamplesTest {

	static final File LIB_DIR = new File(TestUtils.TEST_ROOT + "/lib");
	static final File SRC_DIR = new File(TestUtils.TEST_ROOT + "/src");

	private final BasicTestManifest<URLTestIO, BooleanTestOutput, BooleanTestOutput> manifest;
	private File javaSrcFile = null;
	private String owlapiClassPath = null;
	private String owlapiLibClassPath = null;
	private String elkOwlapiClassPath = null;

	public OWLAPIExamplesTest(
			BasicTestManifest<URLTestIO, BooleanTestOutput, BooleanTestOutput> mfst) {
		manifest = mfst;
	}

	@BeforeClass
	public static void init() throws IOException {
		TestUtils.createTestEnvironment(new File("."));

		if (!LIB_DIR.mkdirs() || !SRC_DIR.mkdirs()) {
			throw new IOException("Unable to init the test environment");
		}

		prepareClassPath();
	}

	@AfterClass
	public static void cleanUp() throws IOException {
		TestUtils.cleanUpOnExit(new File("."));
	}

	private static void prepareClassPath() throws IOException {
		// unpack elk-owlapi (which is, in fact, elk-owlapi-standalone)
		// and prepare the classpath
		String elkOwlApiZipPath = System.getProperty("elk-owlapi-path");

		if (elkOwlApiZipPath != null) {
			extract(new File(elkOwlApiZipPath), LIB_DIR);
		}
	}

	/*
	 * Recursively extract a zip archive
	 */
	private static void extract(File file, File destDir) throws ZipException,
			IOException {
		ZipFile zip = new ZipFile(file);
		Enumeration<? extends ZipEntry> entries = zip.entries();

		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			File f = new java.io.File(destDir + java.io.File.separator
					+ entry.getName());

			if (entry.isDirectory()) { // if its a directory, create it
				f.mkdir();
				continue;
			}

			InputStream is = null;
			FileOutputStream fos = null;

			try {
				is = zip.getInputStream(entry);
				fos = new FileOutputStream(f);
				IOUtils.copy(is, fos);
			} finally {
				IOUtils.closeQuietly(is);
				IOUtils.closeQuietly(fos);
			}
		}
	}

	@Before
	public void before() throws IOException {
		owlapiClassPath = System.getProperty("owlapi-path");
		elkOwlapiClassPath = LIB_DIR.getAbsolutePath()
				+ System.getProperty("elk-owlapi-path-in-zip");
		owlapiLibClassPath = System.getProperty("owlapi-lib-path");

		assumeTrue(owlapiClassPath != null);// Here we gracefully exit if key
											// properties aren't set

		createJavaSource();
	}

	public void createJavaSource() throws IOException {
		// input = input stream of a Java file
		// read it and create a Java file in a test dir/src
		InputStream inStream = null;
		OutputStream outStream = null;

		javaSrcFile = new File(SRC_DIR.getAbsolutePath() + "/"
				+ manifest.getInput().getName());

		try {
			inStream = manifest.getInput().getInputStream();
			outStream = new FileOutputStream(javaSrcFile);

			IOUtils.copy(inStream, outStream);
		} finally {
			IOUtils.closeQuietly(inStream);
			IOUtils.closeQuietly(outStream);
		}
	}

	@SuppressWarnings("static-method")
	@After
	public void after() {
		for (File srcLibFile : SRC_DIR.listFiles()) {
			srcLibFile.delete();
		}
	}

	/*
	 * Configuration: loading all test input data
	 */
	@Config
	public static Configuration getConfig() throws URISyntaxException,
			IOException {
		return ConfigurationUtils
				.loadFileBasedTestConfiguration(
						"owlapi-examples-src",
						OWLAPIExamplesTest.class,
						"java",

						new TestManifestCreator<URLTestIO, BooleanTestOutput, BooleanTestOutput>() {
							@Override
							public TestManifest<URLTestIO, BooleanTestOutput, BooleanTestOutput> create(
									URL input, URL output) {
								String name = FileUtils.getFileName(FileUtils
										.dropExtension(input.toString()));

								return new BasicTestManifest<URLTestIO, BooleanTestOutput, BooleanTestOutput>(
										name, new URLTestIO(input),
										new BooleanTestOutput(true));
							}
						});
	}

	@Test
	public void compile() throws IOException {
		JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileMgr = javac.getStandardFileManager(null,
				null, null);
		// setting the classpath
		String classpath = elkOwlapiClassPath + File.pathSeparator
				+ owlapiClassPath;
		for (File file : new File(new File(elkOwlapiClassPath).getParent() + "/lib").listFiles()) {
			if (file.getName().endsWith(".jar")) {
				classpath += File.pathSeparator + file.getAbsolutePath();
			}
		}
//		for (File file : new File(owlapiLibClassPath).listFiles()) {
//			if (file.getName().endsWith(".jar")) {
//				classpath += File.pathSeparator + file.getAbsolutePath();
//			}
//		}
		classpath += File.pathSeparator + owlapiLibClassPath;
		List<String> options = new ArrayList<String>();

		options.addAll(Arrays.asList("-classpath", classpath));

		JavaCompiler.CompilationTask task = javac.getTask(null, null, null,
				options, null, fileMgr.getJavaFileObjects(javaSrcFile));

		boolean result = task.call();

		// System.out.println("CLASSPATH " + classpath);

		assertTrue(result);
	}
}