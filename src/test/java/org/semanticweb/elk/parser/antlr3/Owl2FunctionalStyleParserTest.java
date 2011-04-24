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

import junit.framework.TestCase;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.UnbufferedTokenStream;
import org.semanticweb.elk.syntax.ElkAxiom;
import org.semanticweb.elk.syntax.ElkClass;

/**
 * @author Yevgeny Kazakov
 * 
 */
public class Owl2FunctionalStyleParserTest extends TestCase {
	public Owl2FunctionalStyleParserTest(String testName) {
		super(testName);
	}

	public Owl2FunctionalStyleParser getParser(String string) throws RecognitionException {
		Owl2FunctionalStyleLexer lex = new Owl2FunctionalStyleLexer(
				new ANTLRStringStream(string));
		UnbufferedTokenStream tokens = new UnbufferedTokenStream(lex);
		return new Owl2FunctionalStyleParser(tokens);
	}

	public void testClazz() {		
		try {
			Owl2FunctionalStyleParser parser = getParser("owl:Thing");
			ElkClass clazz = parser.clazz();
			assertNotNull(clazz);
			assertSame(ElkClass.ELK_OWL_THING, clazz);
		} catch (RecognitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertFalse(true);
		}
		
	}
	
	public void testTransitiveObjectPropertyAxiom() {
		try {
			Owl2FunctionalStyleParser parser = getParser("TransitiveObjectProperty(<R>)");
			ElkAxiom axiom = parser.axiom();
			assertNotNull(axiom);
		} catch (RecognitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertFalse(true);
		}
	}
	
}
