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
/**
 * 
 */
package org.semanticweb.elk.io;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class FileUtils {

	public static FilenameFilter getExtBasedFilenameFilter(
			final String extension) {
		return new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				return name.endsWith("." + extension);
			}
		};
	}

	public static String dropExtension(String filename) {
		int index = -1;

		if ((index = filename.lastIndexOf('.')) < 0) {
			return filename;
		} else {
			return filename.substring(0, index);
		}
	}
	
	public static String dropExtension(String filename, String extension) {
		int index = -1;

		if ((index = filename.lastIndexOf("." + extension)) < 0) {
			return filename;
		} else {
			return filename.substring(0, index);
		}
	}	

	public static String getFileName(String path) {
		return new File(path).getName();
	}

	public static void deleteRecursively(File file) throws IOException {
		deleteRecursively(file, false);
	}

	public static void deleteRecursively(File file, boolean deleteOnExit)
			throws IOException {
		File[] directoryFiles;
		if ((directoryFiles = file.listFiles()) != null) {
			for (File c : directoryFiles)
				deleteRecursively(c);
		}

		if (deleteOnExit) {
			file.deleteOnExit();
		}
		else {
			if (!file.delete()) {
				throw new IOException("Failed to delete file: " + file);
			}	
		}
	}
}
