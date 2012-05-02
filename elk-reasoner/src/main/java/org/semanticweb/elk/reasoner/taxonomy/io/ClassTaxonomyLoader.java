/**
 * 
 */
package org.semanticweb.elk.reasoner.taxonomy.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomy;

/**
 * A simple class to load class taxonomy from an input stream or a reader
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public class ClassTaxonomyLoader {


	public static ClassTaxonomy load(Owl2Parser parser, InputStream input) throws IOException, Owl2ParseException {
		
		return null;
	}
	
	public static ClassTaxonomy load(Owl2Parser parser, File file) throws IOException, Owl2ParseException {
		
		return null;
	}
}
