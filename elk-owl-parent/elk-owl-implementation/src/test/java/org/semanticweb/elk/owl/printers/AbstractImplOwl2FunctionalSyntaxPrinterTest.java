/*
 * #%L
 * ELK OWL Model Implementation
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
package org.semanticweb.elk.owl.printers;

import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.parsing.ElkTestAxiomProcessor;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;

/**
 * Test for the printer which uses this concrete implementation of the OWL
 * model. Unfortunately, it's still abstract because we also need some
 * implementation of the parser which live in other modules
 * 
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 */
public abstract class AbstractImplOwl2FunctionalSyntaxPrinterTest extends
		ModelOwl2FunctionalSyntaxPrinterTest {

	@Override
	protected List<? extends ElkObject> getOriginalElkObjects(List<ElkPrefix> prefixes) {
		InputStream input = getClass().getClassLoader().getResourceAsStream(
				"owl2primer.owl");

		assertNotNull(input);

		return parseAxioms(new InputStreamReader(input), prefixes, false);
	}

	@Override
	protected List<? extends ElkObject> loadPrintedElkObjects(String input, List<ElkPrefix> prefixes) {
		String ontology = " Ontology(<http://example.com/owl/> \n" + input
				+ "\n)";

		return parseAxioms(new StringReader(ontology), prefixes, true);
	}

	protected List<? extends ElkObject> parseAxioms(Reader reader, final List<ElkPrefix> prefixes,
			boolean addDefaultDecl) {
		final Owl2Parser parser = instantiateParser(reader);
		ElkTestAxiomProcessor counter = new ElkTestAxiomProcessor();

		if (addDefaultDecl) {
			for (ElkPrefix prefix : prefixes) {
				parser.declarePrefix(prefix);
			}
		}

		try {
			parser.accept(counter);
			prefixes.addAll(counter.getDeclaredPrefixes());
		} catch (Owl2ParseException e) {
			throw new RuntimeException("Failed to load axioms for testing", e);
		}

		return counter.getAllAxioms();
	}

	protected abstract Owl2Parser instantiateParser(Reader reader);
}
