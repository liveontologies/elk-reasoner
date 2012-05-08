/*
 * #%L
 * ELK Reasoner
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
