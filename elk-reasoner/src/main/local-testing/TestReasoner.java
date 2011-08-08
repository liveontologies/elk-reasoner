/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.classification.ClassTaxonomy;
import org.semanticweb.elk.reasoner.classification.ClassNode;
import org.semanticweb.elk.reasoner.indexing.IndexedClassExpression;
import org.semanticweb.elk.syntax.implementation.ElkClassImpl;
import org.semanticweb.elk.syntax.implementation.ElkClassExpressionImpl;
import org.semanticweb.elk.syntax.implementation.ElkEquivalentClassesAxiomImpl;
import org.semanticweb.elk.syntax.implementation.ElkSubClassOfAxiomImpl;
import org.semanticweb.elk.syntax.parsing.javacc.Owl2FunctionalStyleParser;
import org.semanticweb.elk.util.Statistics;

/**
 * @author Markus Kroetzsch
 */
public class TestReasoner {
	
	protected final static Logger LOGGER_ = Logger.getLogger(TestReasoner.class);

	/**
	 * @param args
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Statistics.logOperationStart("Testing", LOGGER_);
		
		Statistics.logOperationFinish("Testing", LOGGER_);
		
//		InputStream stream = new FileInputStream(
//				"/Users/ecull/Documents/workspace/snomed.owl");
//		Owl2FunctionalStyleParser.Init(stream);
//		System.out.print("Parsing...");
//		long time = System.currentTimeMillis();
//		Reasoner reasoner = new Reasoner(16);
//		Owl2FunctionalStyleParser.ontologyDocument(reasoner);
//		stream.close();
//		System.out.println("done in " + (System.currentTimeMillis() - time)
//				+ "ms.");
//		Statistics.printMemoryUsage();
//		time = System.currentTimeMillis();
//		System.out.print("Saturating...");
//		reasoner.saturate();
//		System.out.println("done in " + (System.currentTimeMillis() - time)
//				+ "ms.");
//		Statistics.printMemoryUsage();
//		time = System.currentTimeMillis();
//		System.out.print("Transitive reduction...");
//		reasoner.classify();
//		System.out.println("done in " + (System.currentTimeMillis() - time)
//				+ "ms.");
//		Statistics.printMemoryUsage();
//
//		reasoner.shutdown();

		// Create file


	}

}
