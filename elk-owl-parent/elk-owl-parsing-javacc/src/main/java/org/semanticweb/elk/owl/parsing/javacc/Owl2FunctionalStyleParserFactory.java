/*
 * #%L
 * ELK OWL JavaCC Parser
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
package org.semanticweb.elk.owl.parsing.javacc;

import java.io.InputStream;
import java.io.Reader;

import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.parsing.Owl2ParserFactory;

public class Owl2FunctionalStyleParserFactory implements Owl2ParserFactory {

	@Override
	public Owl2Parser getParser(InputStream stream) {
		return new Owl2FunctionalStyleParser(stream);
	}

	@Override
	public Owl2Parser getParser(Reader reader) {
		return new Owl2FunctionalStyleParser(reader);
	}

}
