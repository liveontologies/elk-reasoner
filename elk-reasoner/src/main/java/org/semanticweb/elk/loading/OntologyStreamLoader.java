/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.loading;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.semanticweb.elk.owl.parsing.Owl2ParserFactory;

public class OntologyStreamLoader extends Owl2ParserLoader {

	final InputStream stream;
	final Owl2ParserFactory parserFactory;

	public OntologyStreamLoader(Owl2ParserFactory parserFactory,
			InputStream stream) {
		super(parserFactory.getParser(stream));
		this.stream = stream;
		this.parserFactory = parserFactory;
	}

	public OntologyStreamLoader(Owl2ParserFactory parserFactory, File file)
			throws FileNotFoundException {
		this(parserFactory, new FileInputStream(file));
	}

	public OntologyStreamLoader(Owl2ParserFactory parserFactory, String text) {
		this(parserFactory, new ByteArrayInputStream(text.getBytes()));
	}

	@Override
	protected void finish() {
		super.finish();
		try {
			stream.close();
		} catch (IOException e) {
			exception = new LoadingException(e.toString());
		}
	}

}
