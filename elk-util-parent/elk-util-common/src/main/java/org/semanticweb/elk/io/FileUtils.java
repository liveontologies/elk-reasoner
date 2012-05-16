/**
 * 
 */
package org.semanticweb.elk.io;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class FileUtils {

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

}
