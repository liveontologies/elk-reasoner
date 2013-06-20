/**
 * 
 */
package org.semanticweb.elk.owl.parsing.javacc;

/*
 * #%L
 * ELK OWL JavaCC Parser
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

import java.util.concurrent.ArrayBlockingQueue;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;

/**
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ConcurrentJavaCCLexerFactory implements JavaCCLexerFactory<AbstractOwl2FunctionalStyleParserTokenManager> {

	@Override
	public AbstractOwl2FunctionalStyleParserTokenManager createLexer(
			AbstractOwl2FunctionalStyleParserTokenManager nativeLexer) {

		return new ConcurrentJavaCCLexer(nativeLexer);
	}

}

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
class ConcurrentJavaCCLexer extends
		AbstractOwl2FunctionalStyleParserTokenManager {

	private final static int TOKEN_ARRAY_SIZE = 1024;
	private final static int TOKEN_QUEUE_SIZE = 255;

	private final AbstractOwl2FunctionalStyleParserTokenManager lexer_;

	private final Thread lexerThread_;
	// These queues may be replaced by a faster sync'ed solution
	// since we know that the array pool is bounded
	private final ArrayBlockingQueue<Token[]> producedTokenQueue_;

	private final ArrayBlockingQueue<Token[]> freeTokenQueue_;

	private boolean finished_ = false;

	private Token[] tokenArray_ = null;

	private int tokenIndex_ = 0;

	private Token[][] tokens_ = new Token[TOKEN_QUEUE_SIZE][TOKEN_ARRAY_SIZE];

	/**
	 * the exception created if something goes wrong
	 */
	protected volatile ElkException exception_;

	public ConcurrentJavaCCLexer(
			AbstractOwl2FunctionalStyleParserTokenManager nativeLexer) {
		super(null);

		lexer_ = nativeLexer;
		lexerThread_ = new Thread(new Lexer(), "elk-lexer-thread");
		producedTokenQueue_ = new ArrayBlockingQueue<Token[]>(
				TOKEN_QUEUE_SIZE + 1);
		freeTokenQueue_ = new ArrayBlockingQueue<Token[]>(TOKEN_QUEUE_SIZE);

		lexerThread_.start();

		for (int i = 0; i < TOKEN_QUEUE_SIZE; i++) {
			freeTokenQueue_.add(tokens_[i]);
		}
	}

	@Override
	public Token getNextToken() {
		if (finished_) {
			return null;
		}

		if (tokenArray_ == null || tokenIndex_ >= tokenArray_.length) {

			try {

				if (tokenArray_ != null) {
					// release the array so the lexer thread can reuse it
					freeTokenQueue_.add(tokenArray_);
				}

				tokenArray_ = producedTokenQueue_.take();
				tokenIndex_ = 0;

				if (tokenArray_.length == 0) {
					//we've been poisoned...
					finished_ = true;

					return null;
				}
			} catch (InterruptedException e) {
				exception_ = new Owl2ParseException(
						"ELK parser was interrupted", e);
				e.printStackTrace();

				return null;
			}
		}

		return tokenArray_[tokenIndex_++];
	}

	public ElkException getError() {
		return exception_;
	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private class Lexer implements Runnable {

		private Token[] producerTokenArray_ = null;

		private int producedIndex_ = 0;

		@Override
		public void run() {
			try {

				producerTokenArray_ = freeTokenQueue_.take();
				// producerTokenArray_ = new Token[TOKEN_ARRAY_SIZE];

				for (;;) {
					boolean putIntoQueue = false;
					Token nextToken = null;

					nextToken = lexer_.getNextToken();

					if (producedIndex_ >= TOKEN_ARRAY_SIZE) {
						// producedTokenQueue_.put(producerTokenArray_);
						// producerTokenArray_ = new Token[TOKEN_ARRAY_SIZE];
						producedTokenQueue_.add(producerTokenArray_);
						producerTokenArray_ = freeTokenQueue_.take();
						producedIndex_ = 0;

						putIntoQueue = true;
					}

					producerTokenArray_[producedIndex_++] = nextToken;

					if (nextToken == null
							|| AbstractOwl2FunctionalStyleParserTokenManager.EOF == nextToken.kind) {
						if (!putIntoQueue) {
							producedTokenQueue_.put(producerTokenArray_);
						}

						break;
					}
				}

				producedTokenQueue_.put(new Token[] {});// poison

			} catch (InterruptedException e) {
				exception_ = new Owl2ParseException(
						"ELK parser was interrupted", e);
			} catch (TokenMgrError err) {
				// Error occurred, need to kill the consumer thread
				exception_ = new Owl2ParseException("Lexical error", err);
				producedTokenQueue_.add(new Token[] {});// poison
			}
		}

	}
}
