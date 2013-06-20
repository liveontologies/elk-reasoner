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
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.parsing.Owl2ParserFactory;

public class Owl2FunctionalStyleParserFactory implements Owl2ParserFactory {

	private final static JavaCCLexerFactory<AbstractOwl2FunctionalStyleParserTokenManager> DEFAULT_LEXER_FACTORY = new ConcurrentJavaCCLexerFactory();

	/**
	 * the {@link ElkObjectFactory} used with this {@link Owl2ParserFactory}
	 */
	private final ElkObjectFactory objectFactory_;

	private final JavaCCLexerFactory<AbstractOwl2FunctionalStyleParserTokenManager> lexerFactory_;

	public Owl2FunctionalStyleParserFactory(ElkObjectFactory objectFactory,
			JavaCCLexerFactory<AbstractOwl2FunctionalStyleParserTokenManager> lexerFactory) {
		this.objectFactory_ = objectFactory;
		this.lexerFactory_ = lexerFactory;
	}

	public Owl2FunctionalStyleParserFactory(ElkObjectFactory objectFactory) {
		this(objectFactory, DEFAULT_LEXER_FACTORY);
	}

	public Owl2FunctionalStyleParserFactory() {
		this(new ElkObjectFactoryImpl(), DEFAULT_LEXER_FACTORY);
	}

	@Override
	public Owl2Parser getParser(InputStream stream) {
		return new Owl2FunctionalStyleParser(stream, objectFactory_,
				lexerFactory_);
	}

	@Override
	public Owl2Parser getParser(Reader reader) {
		return new Owl2FunctionalStyleParser(reader, objectFactory_,
				lexerFactory_);
	}

	
	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private static class Owl2FunctionalStyleParser extends AbstractOwl2FunctionalStyleParser {

		private final ElkObjectFactory objectFactory_;

		private final ElkPrefixDeclarations prefixDeclarations_;

		private final JavaCCLexerFactory<AbstractOwl2FunctionalStyleParserTokenManager> lexerFactory_;

		private Owl2FunctionalStyleParser(InputStream stream,
				ElkObjectFactory objectFactory,
				ElkPrefixDeclarations prefixDeclarations,
				JavaCCLexerFactory<AbstractOwl2FunctionalStyleParserTokenManager> factory) {
			super(stream);
			this.objectFactory_ = objectFactory;
			this.prefixDeclarations_ = prefixDeclarations;
			this.lexerFactory_ = factory;

			wrapLexer();
		}

		Owl2FunctionalStyleParser(InputStream stream,
				ElkObjectFactory objectFactory, JavaCCLexerFactory<AbstractOwl2FunctionalStyleParserTokenManager> lexerFactory) {
			this(stream, objectFactory, new ElkPrefixDeclarationsImpl(),
					lexerFactory);
		}

		Owl2FunctionalStyleParser(Reader reader,
				ElkObjectFactory objectFactory,
				ElkPrefixDeclarations prefixDeclarations,
				JavaCCLexerFactory<AbstractOwl2FunctionalStyleParserTokenManager> factory) {
			super(reader);
			this.objectFactory_ = objectFactory;
			this.prefixDeclarations_ = prefixDeclarations;
			this.lexerFactory_ = factory;

			wrapLexer();
		}

		Owl2FunctionalStyleParser(Reader reader,
				ElkObjectFactory objectFactory, JavaCCLexerFactory<AbstractOwl2FunctionalStyleParserTokenManager> lexerFactory) {
			this(reader, objectFactory, new ElkPrefixDeclarationsImpl(),
					lexerFactory);
		}

		@Override
		protected ElkPrefixDeclarations getElkPrefixDeclarations() {
			return prefixDeclarations_;
		}

		@Override
		protected ElkObjectFactory getElkObjectFactory() {
			return this.objectFactory_;
		}

		@Override
		public void ReInit(InputStream stream) {
			super.ReInit(stream);

			wrapLexer();
		}

		@Override
		public void ReInit(InputStream stream, String encoding) {
			super.ReInit(stream, encoding);

			wrapLexer();
		}

		@Override
		public void ReInit(Reader stream) {
			super.ReInit(stream);

			wrapLexer();
		}

		@Override
		public void ReInit(AbstractOwl2FunctionalStyleParserTokenManager tm) {
			super.ReInit(tm);

			wrapLexer();
		}

		private void wrapLexer() {
			token_source = lexerFactory_ != null ? lexerFactory_
					.createLexer(token_source) : token_source;
		}

	}

}