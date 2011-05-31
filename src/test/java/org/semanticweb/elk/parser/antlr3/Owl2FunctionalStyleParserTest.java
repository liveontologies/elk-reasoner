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
 * @author Yevgeny Kazakov, Apr 20, 2011
 */
package org.semanticweb.elk.parser.antlr3;

import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.UnbufferedTokenStream;
import org.semanticweb.elk.parser.antlr3.Owl2FunctionalStyleLexer;
import org.semanticweb.elk.parser.antlr3.Owl2FunctionalStyleParser;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.syntax.ElkClass;

/**
 * @author Yevgeny Kazakov
 * 
 */
public class Owl2FunctionalStyleParserTest extends TestCase {
	public Owl2FunctionalStyleParserTest(String testName) {
		super(testName);
	}

	public void parseOntologyDocument(String testString)
			throws RecognitionException {
		Owl2FunctionalStyleLexer lex = new Owl2FunctionalStyleLexer(
				new ANTLRStringStream(testString));
		UnbufferedTokenStream tokens = new UnbufferedTokenStream(lex);
		Owl2FunctionalStyleParser parser = new Owl2FunctionalStyleParser(tokens);
		parser.ontologyDocument(new Reasoner());
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
		} catch (RecognitionException e) {
			assertFalse(true);
			e.printStackTrace();
		}
	}

	public ElkClass parseElkClass(String testString)
			throws RecognitionException, InterruptedException,
			ExecutionException {
		Owl2FunctionalStyleLexer lex = new Owl2FunctionalStyleLexer(
				new ANTLRStringStream(testString));
		UnbufferedTokenStream tokens = new UnbufferedTokenStream(lex);
		Owl2FunctionalStyleParser parser = new Owl2FunctionalStyleParser(tokens);
		return parser.clazz().get();
	}

	public void testClazz() throws InterruptedException, ExecutionException {
		try {
			ElkClass clazz = parseElkClass("owl:Thing");
			assertNotNull(clazz);
			assertSame(ElkClass.ELK_OWL_THING, clazz);
		} catch (RecognitionException e) {
			assertFalse(true);
			e.printStackTrace();
		}

	}

}
