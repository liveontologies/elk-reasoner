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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.semanticweb.elk.cli.IOReasoner;
import org.semanticweb.elk.owl.ElkAxiomProcessor;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.iris.ElkPrefixDeclarationsImpl;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParser;
import org.semanticweb.elk.owl.parsing.javacc.ParseException;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.saturation.elkrulesystem.RuleDecomposition;
import org.semanticweb.elk.reasoner.saturation.rulesystem.InferenceRule;
import org.semanticweb.elk.reasoner.saturation.rulesystem.RuleApplicationEngine;
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomyPrinter;
import org.semanticweb.elk.util.logging.Statistics;

/**
 * @author Markus Kroetzsch
 */
public class MyTests {
	
	static class GenericProcessor<T> {
		boolean processGeneric(Object o) {
			return process((T) o);
		}
		
		boolean process(T t) {
			if (t!= null) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	protected static Writer writer;

	protected final static Logger LOGGER_ = Logger
			.getLogger(TestReasoner.class);
	
	public static class AxiomDumper implements ElkAxiomProcessor {
		
		public void process(ElkAxiom elkAxiom) {
			try {
				OwlFunctionalStylePrinter.append(writer, elkAxiom);
				writer.write("\n");
			} catch ( IOException e ) {
				
			}
		}

	}

	/**
	 * @param args
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void main(String[] args) throws Throwable {
		Statistics.logOperationStart("Testing", LOGGER_);

		doReasoningTests();
		//doReflectionTests();

		Statistics.logOperationFinish("Testing", LOGGER_);
	}
	
	public static boolean testMethod(Integer i) {
		return true;
	}
	
	public static void doReflectionTests() throws NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Class<?> partypes[] = new Class<?>[1];
        partypes[0] = Integer.class;
		Method method = MyTests.class.getMethod("testMethod", partypes);
		Integer num = 42;
		String str = "Test";
		Object obj = num;
		if (method.invoke(null, obj).equals(null)) 
			return;
		System.out.println("Test done.");
		
		List<Integer> list = new ArrayList<Integer>();
		
		GenericProcessor<Integer> intProcessor = new GenericProcessor<Integer>();
		
		HashMap<Class<?>, GenericProcessor<Integer>> testMap = new HashMap<Class<?>, GenericProcessor<Integer>>();
		testMap.put(Reasoner.class, intProcessor);
		testMap.put(Integer.class, intProcessor);
		testMap.put(HashMap.class, intProcessor);
		testMap.put(List.class, intProcessor);
		testMap.put(ArrayList.class, intProcessor);
		
		
		Integer number = new Integer(42);
		for (int i=0; i<142305570; ++i) {
			Class<?> clazz = testMap.getClass();
			GenericProcessor<Integer> value = testMap.get(clazz);
			value.processGeneric(number);
//			if (number != 44) {
//				list.add(number);
//			}
//			Class<?> partypes[] = new Class[0];
//			Constructor<?> constructor = ruleclass.getConstructor(partypes);
//			InferenceRule r = ruleclass.cast(constructor.newInstance());
			//InferenceRule r = new RuleDecomposition();
//			RegistrableRule[] cr = r.getComponentRules();
//			rulelist.add(r);
//			if (rulelist.size() == 100000) {
//				rulelist.clear();
//			}
//			if (cr.length == 100) {
//				return;
//			}
		}
	}
	
	public static void doReasoningTests() throws ParseException, IOException {
		IOReasoner reasoner = new IOReasoner(Executors.newCachedThreadPool(),
				16);
		Statistics.logOperationStart("Loading ontology", LOGGER_);

		//String fileName = "/home/markus/Dokumente/Ontologies/EL-GALEN.owl";
		String fileName = "/home/markus/Dokumente/Ontologies/ElkTesting/snomed_functional.owl";
		InputStream stream = new FileInputStream((new File(fileName)));
		reasoner.reset();
		Owl2FunctionalStyleParser parser = new Owl2FunctionalStyleParser(stream);
		parser.setPrefixDeclarations(new ElkPrefixDeclarationsImpl());
		
//		AxiomDumper ad = new AxiomDumper();
//		FileWriter fstream = new FileWriter("/home/markus/Dokumente/Ontologies/galen/EL-GALEN-n2.owl");
//		writer = new BufferedWriter(fstream);
//		writer.write("Ontology(<http://www.co-ode.org/ontologies/galen>\n");
//		parser.ontologyDocument(ad);
//		writer.write(")\n");
//		writer.close();
		
		parser.ontologyDocument(reasoner.getOntologyIndex().getAxiomInserter());
		stream.close();
		Statistics.logOperationFinish("Loading ontology", LOGGER_);

		Statistics.logOperationStart("Classification", LOGGER_);
		reasoner.classify();
		Statistics.logOperationFinish("Classification", LOGGER_);
		Statistics.logOperationStart("Taxonomy", LOGGER_);
		System.out.print( "Hash string: " );
		System.out.println( ClassTaxonomyPrinter.getHashString( reasoner.getTaxonomy() ) );
		Statistics.logOperationFinish("Taxonomy", LOGGER_);
		// reasoner.writeTaxonomyToFile(...);
		reasoner.shutdown();
	}


}
