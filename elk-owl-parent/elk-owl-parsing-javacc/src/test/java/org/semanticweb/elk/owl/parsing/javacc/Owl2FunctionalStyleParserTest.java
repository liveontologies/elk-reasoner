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
package org.semanticweb.elk.owl.parsing.javacc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.semanticweb.elk.owl.ElkAxiomProcessor;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.iris.ElkPrefixDeclarations;
import org.semanticweb.elk.owl.iris.ElkPrefixDeclarationsImpl;
import org.semanticweb.elk.owl.parsing.AbstractOwl2FunctionalSyntaxParseTest;
import org.semanticweb.elk.owl.parsing.Owl2Parser;

/**
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 */
public class Owl2FunctionalStyleParserTest extends AbstractOwl2FunctionalSyntaxParseTest {

	class DummyElkAxiomProcessor implements ElkAxiomProcessor {
		public final List<ElkAxiom> axiomList = new ArrayList<ElkAxiom>();

		public void process(ElkAxiom axiom) {
			axiomList.add(axiom);
		}
	}

	protected Owl2FunctionalStyleParser getParserForString(String testString,
			boolean defaultPrefixes) {
		InputStream stream = new ByteArrayInputStream(testString.getBytes());
		Owl2FunctionalStyleParser parser = new Owl2FunctionalStyleParser(stream);
		if (defaultPrefixes) {
			ElkPrefixDeclarations prefixDeclarations = new ElkPrefixDeclarationsImpl();
			prefixDeclarations.addOwlDefaultPrefixes();
			parser.setPrefixDeclarations(prefixDeclarations);
		}
		return parser;
	}

	protected ElkClass parseElkClass(String testString) throws ParseException,
			InterruptedException, ExecutionException {
		return getParserForString(testString, true).clazz();
	}

	@Test
	public void testOwlThing() throws InterruptedException, ExecutionException,
			ParseException {
		ElkClass clazz = parseElkClass("owl:Thing");
		assertNotNull(clazz);
		ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();
		assertEquals(objectFactory.getOwlThing().getIri(), clazz.getIri());
	}

	protected ElkLiteral parseElkLiteral(String testString)
			throws ParseException, InterruptedException, ExecutionException {
		return getParserForString(testString, true).literal();
	}

	@Test
	public void testPlainLiterals() throws InterruptedException,
			ExecutionException, ParseException {
		ElkLiteral literal1 = parseElkLiteral("\"Test\"@en");
		assertNotNull(literal1);
		ElkLiteral literal2 = parseElkLiteral("\"Test@en\"^^rdf:PlainLiteral");
		assertNotNull(literal2);
		assertEquals(literal1.getLexicalForm(), "Test@en");
		assertEquals(literal2.getLexicalForm(), "Test@en");
		ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();
		assertEquals(literal1.getDatatype(),
				objectFactory.getDatatypeRdfPlainLiteral());
		assertEquals(literal1.getDatatype().getIri().asString(),
				"http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral");
		assertEquals(literal2.getDatatype().getIri().asString(),
				"http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral");
		assertEquals(literal1.getDatatype().getIri(), literal2.getDatatype().getIri());
		// assertEquals(literal1, literal2);
	}

	
	@Override
	protected Owl2Parser instantiateParser(InputStream stream) {
		return new Owl2FunctionalStyleParser(stream);
	}
	
	@Override
	protected Owl2Parser instantiateParser(Reader reader) {
		return new Owl2FunctionalStyleParser(reader);
	}

}
