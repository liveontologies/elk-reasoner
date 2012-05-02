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
package org.semanticweb.elk.testing.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public class IOUtils {

	public static void closeQuietly(InputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {}
		}
	}
	
	public static FilenameFilter getExtBasedFilenameFilter(final String extension) {
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
		}
		else {
			return filename.substring(0, index);
		}
	}

	public static String getFileName(String path) {
		return new File(path).getName();
	}
	
	public static List<String> getTestResourceNamesFromDir(File dir, String owlExt) {
		List<String> testResources = new ArrayList<String>();
		
		for (String fileName : dir.list(IOUtils.getExtBasedFilenameFilter(owlExt))) {
			testResources.add(dir.getName() + "/" + fileName);
		}
		
		return testResources;
	}

	public static List<String> getTestResourceNamesFromJAR(URI inputURI, String owlExt, Class<?> srcClass) throws IOException {
		CodeSource src = srcClass.getProtectionDomain().getCodeSource();
		List<String> testResources = new ArrayList<String>();
		ZipInputStream zip = null;

		if( src != null ) {
		    URL jar = src.getLocation();
		    ZipEntry ze = null;
		    
		    try {
				zip = new ZipInputStream( jar.openStream());
				
				while( ( ze = zip.getNextEntry() ) != null ) {
				    String entryName = ze.getName();
				    if( entryName.endsWith("." + owlExt) ) {
				    	testResources.add( entryName );
				    }
				}
			} finally {
				closeQuietly(zip);
			}
		 }
		else {
			throw new IOException("Unable to get code source for " + srcClass.getSimpleName());
		}
		
		return testResources;
	}	
	
	public static int readInteger(URL src, int radix) throws IOException {
		String line = null;
		InputStream stream = null; 
				
		try {
			stream = src.openStream();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			
			line = reader.readLine();
			
		} finally {
			closeQuietly(stream);
		}
		
		return Integer.parseInt(line, radix);
	}
}