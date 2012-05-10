/*
 * #%L
 * ELK Command Line Interface
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
package org.semanticweb.elk.cli.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.Executors;

import org.semanticweb.elk.cli.IOReasoner;
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomyHasher;
import org.semanticweb.elk.testing.io.IOUtils;

/**
 * Computes correct class taxonomy hash codes for a set of ontologies 
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public class ComputeTaxonomyHashCodes {

	static final String OWL_PATH = "../elk-reasoner/src/test/resources/classification_test_input"; 
	
	/**
	 * args[0]: path to the dir with source ontologies
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		File srcDir = new File(OWL_PATH);
		IOReasoner reasoner = new IOReasoner(Executors.newCachedThreadPool(), 1);//use just one worker to minimize the risk of errors
		
		for (File ontFile : srcDir.listFiles(IOUtils.getExtBasedFilenameFilter("owl"))) {
			
			System.err.println(ontFile.getName());
			
			reasoner.loadOntologyFromFile(ontFile);
			
			ClassTaxonomy taxonomy = reasoner.getTaxonomy();
			int hash = ClassTaxonomyHasher.hash(taxonomy);
			//create the expected result file
			File out = new File(srcDir.getAbsolutePath() + "/" + IOUtils.dropExtension(ontFile.getName()) + ".expected.hash");
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(out));
			
			writer.write(Integer.valueOf(hash).toString());
			writer.flush();
			writer.close();
		}
		
		reasoner.shutdown();
	}

}
