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
package org.semanticweb.elk.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 */
public class IOUtils {

	private static final int BUFFER_SIZE = 1024 * 2;

	public static void closeQuietly(InputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
	}
	
	public static void closeQuietly(Writer writer) {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
			}
		}
	}

	public static void closeQuietly(OutputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Copies bytes from the input stream to the output stream
	 * 
	 * @param input
	 * @param output
	 * 
	 * @return The number of bytes copied
	 * @throws IOException
	 */
	public static int copy(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];

		BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
		BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
		int count = 0, n = 0;

		try {
			while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
				out.write(buffer, 0, n);
				count += n;
			}
			out.flush();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}

		return count;
	}

	public static List<String> getResourceNamesFromDir(File dir,
			String extension) {
		String[] dirContent = dir
				.list(FileUtils.getExtBasedFilenameFilter(extension));
		if (dirContent == null) {
			throw new RuntimeException("Not a directory: " + dir);
		}
		List<String> testResources = new ArrayList<String>();

		for (String fileName : dirContent) {
			testResources.add(dir.getName() + "/" + fileName);
		}

		return testResources;
	}

	/**
	 * Retrieves a set of resource names from a JAR file (the code source for
	 * the given Java class)
	 * 
	 * @param path
	 * 
	 * @param extension
	 * @param clazz
	 * @return the list of resource names from a JAR file
	 * @throws IOException
	 */
	public static List<String> getResourceNamesFromJAR(String path,
			String extension, Class<?> clazz) throws IOException {
		CodeSource src = clazz.getProtectionDomain().getCodeSource();
		List<String> testResources = new ArrayList<String>();
		ZipInputStream zip = null;

		if (src != null) {
			URL jar = src.getLocation();
			ZipEntry ze = null;

			try {
				zip = new ZipInputStream(jar.openStream());

				while ((ze = zip.getNextEntry()) != null) {
					String entryName = ze.getName();

					if (entryName.startsWith(path)
							&& entryName.endsWith("." + extension)) {
						testResources.add(entryName);
					}
				}
			} finally {
				closeQuietly(zip);
			}
		} else {
			throw new IOException("Unable to get code source for "
					+ clazz.getSimpleName());
		}

		return testResources;
	}

	public static int readInteger(URL src, int radix) throws IOException {
		String line = null;
		InputStream stream = null;

		try {
			stream = src.openStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					stream));

			line = reader.readLine();

		} finally {
			closeQuietly(stream);
		}
		
		if (line == null) {
			throw new IOException("Cannot read from " + src);
		}

		return Integer.parseInt(line, radix);
	}
}