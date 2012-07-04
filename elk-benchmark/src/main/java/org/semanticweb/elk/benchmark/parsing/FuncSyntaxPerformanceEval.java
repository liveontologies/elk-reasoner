/*
 * #%L
 * ELK Bencharking Package
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
package org.semanticweb.elk.benchmark.parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.ElkAxiomProcessor;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParser;
import org.semanticweb.elk.util.logging.Statistics;


/**
 * A simple utility to eval loading performance
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public class FuncSyntaxPerformanceEval {
	
	final static Logger LOGGER_ = Logger.getLogger(FuncSyntaxPerformanceEval.class);

	public static void main(String...args) throws Exception {
		if (args.length == 0) {
			System.err.println("Usage java FuncSyntaxPerformanceEval <path to test ontology>");
			
			System.exit(1);
		}
		
		File file = new File(args[0]);
		InputStream stream = null; 
				
		
		if (!file.exists() || !file.isFile()) {
			System.err.println("Wrong file");
			
			System.exit(1);
		}
		
		try {
			stream = new FileInputStream(file);
			Owl2Parser parser = createParser(stream);
			
			Statistics.logOperationStart("loading", LOGGER_);
			
			parser.parseOntology(new ElkAxiomProcessor() {
				@Override
				public void process(ElkAxiom elkAxiom) {}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			IOUtils.closeQuietly(stream);
		}
		
		Statistics.logOperationFinish("loading", LOGGER_);
	}

	private static Owl2Parser createParser(InputStream stream) {
		return new Owl2FunctionalStyleParser(stream);
	}
}
