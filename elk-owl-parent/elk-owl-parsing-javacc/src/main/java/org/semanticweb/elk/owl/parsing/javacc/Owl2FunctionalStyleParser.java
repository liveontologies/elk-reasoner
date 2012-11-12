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

import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.iris.ElkPrefixDeclarations;
import org.semanticweb.elk.owl.iris.ElkPrefixDeclarationsImpl;

public class Owl2FunctionalStyleParser extends
		AbstractOwl2FunctionalStyleParser {

	private final ElkObjectFactory objectFactory_;

	private final ElkPrefixDeclarations prefixDeclarations_;

	private Owl2FunctionalStyleParser(InputStream stream,
			ElkObjectFactory objectFactory,
			ElkPrefixDeclarations prefixDeclarations) {
		super(stream);
		this.objectFactory_ = objectFactory;
		this.prefixDeclarations_ = prefixDeclarations;
	}

	Owl2FunctionalStyleParser(InputStream stream, ElkObjectFactory objectFactory) {
		this(stream, objectFactory, new ElkPrefixDeclarationsImpl());
	}

	Owl2FunctionalStyleParser(Reader reader, ElkObjectFactory objectFactory,
			ElkPrefixDeclarations prefixDeclarations) {
		super(reader);
		this.objectFactory_ = objectFactory;
		this.prefixDeclarations_ = prefixDeclarations;
	}

	Owl2FunctionalStyleParser(Reader reader, ElkObjectFactory objectFactory) {
		this(reader, objectFactory, new ElkPrefixDeclarationsImpl());
	}

	@Override
	protected ElkPrefixDeclarations getElkPrefixDeclarations() {
		return prefixDeclarations_;
	}

	@Override
	protected ElkObjectFactory getElkObjectFactory() {
		return this.objectFactory_;
	}

}
