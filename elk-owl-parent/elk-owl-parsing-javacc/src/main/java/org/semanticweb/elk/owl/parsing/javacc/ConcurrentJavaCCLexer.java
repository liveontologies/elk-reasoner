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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
class ConcurrentJavaCCLexer extends
		AbstractOwl2FunctionalStyleParserTokenManager {

	/**
	 * how many tokens are in the batch
	 */
	private final static int DEFAULT_BATCH_LENGTH_ = 4096;

	/**
	 * an object from which messages are received from the lexer thread
	 */
	private final BlockingQueue<LexerMessage> messagePipe_;
	/**
	 * the processor of lexer messages
	 */
	private final LexerMessageVisitor messageProcessor_;
	/**
	 * the reference to the last batch of tokens taken from the lexer
	 */
	private LexerBatch lastBatch_;
	/**
	 * the cached size of the batch
	 */
	private int batchSize_;
	/**
	 * the position of the next token to take from the batch
	 */
	private int pos_;

	/**
	 * cached last token taken
	 */
	private Token lastToken_ = null;

	public ConcurrentJavaCCLexer(
			AbstractOwl2FunctionalStyleParserTokenManager nativeLexer) {
		super(null);

		messagePipe_ = new SynchronousQueue<LexerMessage>();
		messageProcessor_ = new LexerMessageProcessor();

		pos_ = 0;
		batchSize_ = 0;
		lastBatch_ = null;

		Thread lexerThread = new Thread(new Lexer(nativeLexer, messagePipe_,
				DEFAULT_BATCH_LENGTH_), "elk-lexer-thread");
		lexerThread.setDaemon(true);
		lexerThread.start();
	}

	@Override
	public Token getNextToken() {
		for (;;) {
			if (pos_ < batchSize_) {
				lastToken_ = lastBatch_.get(pos_++);
				return lastToken_;
			}
			// else
			try {
				messagePipe_.take().accept(messageProcessor_);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new Error("ELK lexer was interrupted", e);
			}
			// new batch; update the position
			batchSize_ = lastBatch_.size();
			pos_ = 0;
		}
	}

	/**
	 * Processing messages from the lexer thread
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private class LexerMessageProcessor implements LexerMessageVisitor {

		@Override
		public void visit(LexerBatch batch) {
			lastBatch_ = batch;
		}

		@Override
		public void visit(LexerError error) {
			throw error.getError();
		}

	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private static class Lexer implements Runnable {

		/**
		 * the lexer used to generate the tokens
		 */
		private final AbstractOwl2FunctionalStyleParserTokenManager lexer_;
		/**
		 * an object through which the messages are sent
		 */
		private final BlockingQueue<LexerMessage> messagePipe_;
		/**
		 * the length of batches
		 */
		private final int batchLength_;
		/**
		 * the next batch of axioms that should be filled
		 */
		private LexerBatch nextBatch_;

		Lexer(AbstractOwl2FunctionalStyleParserTokenManager lexer,
				BlockingQueue<LexerMessage> messagePipe, int batchLength) {
			this.lexer_ = lexer;
			this.messagePipe_ = messagePipe;
			this.batchLength_ = batchLength;
			nextBatch_ = new LexerBatch(batchLength_);
		}

		@Override
		public void run() {
			try {
				for (;;) {
					Token nextToken = lexer_.getNextToken();
					nextBatch_.add(nextToken);
					if (nextBatch_.size() == batchLength_) {
						messagePipe_.put(nextBatch_);
						nextBatch_ = new LexerBatch(batchLength_);
					}
					if (nextToken.kind == AbstractOwl2FunctionalStyleParserTokenManager.EOF) {
						messagePipe_.put(nextBatch_);
						return;
					}
				}
			} catch (InterruptedException e) {
				for (;;) {
					try {
						messagePipe_.put(new LexerError(new Error(
								"ELK lexer was interrupted", e)));
						return;
					} catch (InterruptedException e1) {
						// continue; we have to send the error despite anything
					}
				}
			} catch (TokenMgrError err) {
				for (;;) {
					try {
						messagePipe_.put(new LexerError(err));
						return;
					} catch (InterruptedException e) {
						// continue; we have to send the error despite anything
					}
				}
			}
		}
	}

}
