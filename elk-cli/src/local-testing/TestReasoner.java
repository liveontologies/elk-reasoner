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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.semanticweb.elk.cli.IOReasoner;
import org.semanticweb.elk.owl.ElkAxiomProcessor;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.iris.ElkPrefixDeclarations;
import org.semanticweb.elk.owl.iris.ElkPrefixDeclarationsImpl;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParser;
import org.semanticweb.elk.owl.parsing.javacc.ParseException;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.util.logging.Statistics;


/**
 * @author Markus Kroetzsch
 */
public class TestReasoner {
	
	protected final static Logger LOGGER_ = Logger.getLogger(TestReasoner.class);

	static class DummyElkAxiomProcessor implements ElkAxiomProcessor {
		public final List<ElkAxiom> axiomList = new ArrayList<ElkAxiom>(); 
		
		public void process(ElkAxiom axiom) {
			axiomList.add(axiom);
			if (axiom == null) {
				System.out.println("Unsupported axiom (null)");
			} else {
				System.out.println(axiom);
			}
		}
	}
	
	/**
	 * @param args
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException,ParseException {
		Statistics.logOperationStart("Testing", LOGGER_);
//		
//		ElkPrefixDeclarations prefixDeclarations =
//                new ElkPrefixDeclarationsImpl();
//		prefixDeclarations.addOwlDefaultPrefixes();
		
//		String testString = "Prefix ( : = <http://www.my.example.com#> )"
//				+ "Ontology( <http://www.my.example.com/example>"
//				+ "Declaration( Class( :Person ) )"
//				+ "SubClassOf( :Person owl:Thing )"
//				+ "SubClassOf(  <http://www.my.example.com#Person> owl:Nothing )"
//				+ ") ";
//		try {
//			InputStream stream = new ByteArrayInputStream(testString.getBytes());
//			Owl2FunctionalStyleParser parser = new Owl2FunctionalStyleParser(stream);
//			DummyElkAxiomProcessor axiomProcessor = new DummyElkAxiomProcessor();
//			parser.ontologyDocument(axiomProcessor);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
		
//		try {
//			String testString = "\"Test\"@en";
//			//String testString = "\"Test@en\"^^rdf:PlainLiteral";
//			InputStream stream = new ByteArrayInputStream(testString.getBytes());
//			Owl2FunctionalStyleParser parser = new Owl2FunctionalStyleParser(stream);
//			parser.setPrefixDeclarations(prefixDeclarations);
//			Statistics.logOperationFinish("Testing", LOGGER_);
//			
//			ElkLiteral literal = parser.literal();
//			System.out.println( "LF: " + literal.getLexicalForm() + " DT: " + literal.getDatatype() + " SH: " + literal.hashCode() );
//			testString = "\"Test@en\"^^rdf:PlainLiteral";
//			stream = new ByteArrayInputStream(testString.getBytes());
//			parser = new Owl2FunctionalStyleParser(stream);
//			parser.setPrefixDeclarations(prefixDeclarations);
//			Statistics.logOperationFinish("Testing", LOGGER_);
//
//			ElkLiteral literal2 = parser.literal();
//			System.out.println( "LF: " + literal2.getLexicalForm() + " DT: " + literal2.getDatatype() + " SH: " + literal2.hashCode() );
//			System.out.println( " Eq? " + literal.equals(literal2));
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
		
//		IOReasoner reasoner = new IOReasoner(
//				Executors.newCachedThreadPool(), 16);
//		Statistics.logOperationStart("Loading ontology", LOGGER_);
//		reasoner.loadOntologyFromFile("/home/markus/Dokumente/Ontologies/ElkTesting/snomed_functional.owl");
//		Statistics.logOperationFinish("Loading ontology", LOGGER_);
//		Statistics.logOperationStart("Classification", LOGGER_);
//		reasoner.classify();
//		Statistics.logOperationFinish("Classification", LOGGER_);
//		//reasoner.writeTaxonomyToFile(...);
//		reasoner.shutdown();
		
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

		Statistics.logOperationFinish("Testing", LOGGER_);
	}

}
