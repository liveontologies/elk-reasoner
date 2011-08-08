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
/**
 * @author Yevgeny Kazakov, Apr 22, 2011
 */
package org.semanticweb.elk.syntax.parsing.javacc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import junit.framework.TestCase;

import org.semanticweb.elk.syntax.interfaces.ElkAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkClass;
import org.semanticweb.elk.syntax.parsing.FutureElkAxiomConsumer;

/**
 * @author Yevgeny Kazakov
 * 
 */
public class Owl2FunctionalStyleParserTest extends TestCase {

	public Owl2FunctionalStyleParserTest(String testName) {
		super(testName);
	}

	class DummyFutureElkAxiomConsumer implements FutureElkAxiomConsumer {
		public void submit(Future<? extends ElkAxiom> futureAxiom) {
		}
	}

	public void parseOntologyDocument(String testString) throws ParseException {
		InputStream stream = new ByteArrayInputStream(testString.getBytes());
		Owl2FunctionalStyleParser parser = new Owl2FunctionalStyleParser(stream);
		parser.ontologyDocument(new DummyFutureElkAxiomConsumer());
	}

	public void testOntologyDocument() {
		String testString = "Ontology(<http://www.example.org/>"
				// Testing if literal parsing is ambiguous
				+ "Annotation(rdfs:comment \"String literal with langauge\"@en)"
				+ "Annotation(rdfs:comment \"String literal no language\")"
				+ "Annotation(rdfs:label \"Typed literal\"^^xsd:string)"
				// Testing if DataSomeValuesFrom parsing is ambiguous
				// + "SubClassOf(a:2DFigure \n"
				// + "   DataSomeValuesFrom(a:hasWidth a:hasLength xsd:integer)"
				// + ")\n"
				// + "SubClassOf(a:1DFigure "
				// + "   DataSomeValuesFrom(a:hasLength xsd:integer)"
				// + ")"
				+ ")";

		try {
			parseOntologyDocument(testString);
		} catch (ParseException e) {
			assertFalse(true);
			e.printStackTrace();
		}
	}

	public ElkClass parseElkClass(String testString) throws ParseException,
			InterruptedException, ExecutionException {
		InputStream stream = new ByteArrayInputStream(testString.getBytes());
		Owl2FunctionalStyleParser parser = new Owl2FunctionalStyleParser(stream);
		return parser.clazz().get();
	}

	public void testClazz() throws InterruptedException, ExecutionException {
		try {
			ElkClass clazz = parseElkClass("owl:Thing");
			assertNotNull(clazz);
			assertSame(ElkClass.ELK_OWL_THING, clazz);
		} catch (ParseException e) {
			assertFalse(true);
			e.printStackTrace();
		}

	}

}
