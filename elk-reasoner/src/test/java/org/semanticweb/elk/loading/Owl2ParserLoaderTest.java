/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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
package org.semanticweb.elk.loading;

import java.io.StringReader;

import org.junit.After;
import org.junit.Test;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.util.concurrent.computation.DummyInterruptMonitor;

/**
 * Simple tests for the async. parser
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class Owl2ParserLoaderTest {

	private void load(String ontology) throws ElkLoadingException {
		StringReader reader = new StringReader(ontology);
		try {
			Owl2ParserLoader loader = new Owl2ParserLoader(
					DummyInterruptMonitor.INSTANCE,
					new Owl2FunctionalStyleParserFactory().getParser(reader));

			ElkAxiomProcessor dummyProcessor = new ElkAxiomProcessor() {

				@Override
				public void visit(ElkAxiom elkAxiom) {
					// does nothing
				}

			};

			loader.load(dummyProcessor, dummyProcessor);
		} finally {
			reader.close();
		}
	}

	@Test(expected = ElkLoadingException.class)
	public void expectedLoadingExceptionOnSyntaxError()
			throws ElkLoadingException {
		String ontology = ""//
				+ "Prefix( : = <http://example.org/> )"//
				+ "Ontology((((()("//
				+ "EquivalentClasses(:B :C)"//
				+ "SubClassOf(:A ObjectSomeValuesFrom(:R :B))"//
				+ "))";

		load(ontology);
	}

	/*
	 * Makes sure we always throw an exception of the expected type when can't
	 * loading something
	 */
	@Test(expected = ElkLoadingException.class)
	public void expectedLoadingExceptionOnLexicalError() throws Exception {
		String ontology = ""//
				+ "Prefix( : = <http://example.org/> )"//
				+ "Ontology-LEXICAL-ERROR("//
				+ "EquivalentClasses(:B :C)"//
				+ "SubClassOf(:A ObjectSomeValuesFrom(:R :B))"//
				+ ")";

		load(ontology);
	}

	@SuppressWarnings("static-method")
	@After
	public void cleanUp() {
		Thread.interrupted();
	}

}
