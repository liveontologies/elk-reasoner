/**
 * 
 */
package org.semanticweb.elk.reasoner.datatypes.valuespaces;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.io.StringReader;

import org.junit.Before;
import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.iris.ElkPrefixDeclarations;
import org.semanticweb.elk.owl.iris.ElkPrefixDeclarationsImpl;
import org.semanticweb.elk.owl.parsing.javacc.AbstractOwl2FunctionalStyleParser;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.owl.parsing.javacc.ParseException;
import org.semanticweb.elk.reasoner.datatypes.handlers.ElkDatatypeHandler;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkIndexingException;

/**
 * Abstract super class for low-level testing of {@link ValueSpace}s.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class AbstractValueSpaceTest {

	private final Owl2FunctionalStyleParserFactory parserFactory_ = new Owl2FunctionalStyleParserFactory();
	private final ElkPrefixDeclarations prefixes_ = new ElkPrefixDeclarationsImpl();

	@Before
	public void setUpPrefixes() {
		prefixes_.addPrefix(new ElkPrefix("rdf:", new ElkFullIri(
				"http://www.w3.org/1999/02/22-rdf-syntax-ns#")));
		prefixes_.addPrefix(new ElkPrefix("xsd:", new ElkFullIri(
				"http://www.w3.org/2001/XMLSchema#")));
		prefixes_.addPrefix(new ElkPrefix("owl:", new ElkFullIri(
				"http://www.w3.org/2002/07/owl#")));
	}	
	
	protected boolean contains(String range1, String range2)
			throws ParseException {
		return dataRange(range1).contains(dataRange(range2));
	}
	
	/*
	 * we do need the javaCC parser here to parse specific OWL 2 expression
	 * (e.g., datatype restrictions) if we start getting CCEs here, that means
	 * the javaCC parser need to be obtained in other ways
	 */
	protected AbstractOwl2FunctionalStyleParser getJavaCCParser(
			StringReader reader) {
		return (AbstractOwl2FunctionalStyleParser) parserFactory_.getParser(
				reader, prefixes_);
	}

	/*
	 * parses the data range
	 */
	protected ValueSpace<?> dataRange(String string) throws ParseException {
		AbstractOwl2FunctionalStyleParser parser = getJavaCCParser(new StringReader(
				string));
		ElkDataRange dataRange = parser.dataRange();

		return ElkDatatypeHandler.getInstance().createValueSpace(dataRange);
	}	
	
	protected boolean tryDataRange(String string) throws ParseException {
		try {
			dataRange(string);

			return true;
		} catch (ElkIndexingException e) {
			return false;
		}
	}	
}
