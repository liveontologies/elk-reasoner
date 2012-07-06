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

import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.iris.ElkPrefixDeclarations;
import org.semanticweb.elk.owl.iris.ElkPrefixDeclarationsImpl;

public class Owl2FunctionalStyleParser extends
		AbstractOwl2FunctionalStyleParser {

	private final ElkPrefixDeclarations prefixDeclarations;

	private final ElkObjectFactory objectFactory;

	private Owl2FunctionalStyleParser(InputStream stream,
			ElkPrefixDeclarations prefixDeclarations,
			ElkObjectFactory objectFactory) {
		super(stream);
		this.prefixDeclarations = prefixDeclarations;
		this.objectFactory = objectFactory;
	}

	Owl2FunctionalStyleParser(InputStream stream,
			ElkPrefixDeclarations prefixDeclarations) {
		this(stream, prefixDeclarations, new ElkObjectFactoryImpl());
	}

	Owl2FunctionalStyleParser(InputStream stream) {
		this(stream, new ElkPrefixDeclarationsImpl());
	}

	Owl2FunctionalStyleParser(Reader reader,
			ElkPrefixDeclarations prefixDeclarations,
			ElkObjectFactory objectFactory) {
		super(reader);
		this.prefixDeclarations = prefixDeclarations;
		this.objectFactory = objectFactory;
	}

	Owl2FunctionalStyleParser(Reader reader,
			ElkPrefixDeclarations prefixDeclarations) {
		this(reader, prefixDeclarations, new ElkObjectFactoryImpl());
	}

	Owl2FunctionalStyleParser(Reader reader) {
		this(reader, new ElkPrefixDeclarationsImpl());
	}

	@Override
	protected ElkPrefixDeclarations getElkPrefixDeclarations() {
		return prefixDeclarations;
	}

	@Override
	protected ElkObjectFactory getElkObjectFactory() {
		return this.objectFactory;
	}

}
