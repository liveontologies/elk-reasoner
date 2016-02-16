package org.semanticweb.elk.protege;

/*
 * #%L
 * ELK Reasoner Protege Plug-in
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class ElkProtegeLogAppender extends AppenderBase<ILoggingEvent> {

	private static final String ELK_PACKAGE_ = "org.semanticweb.elk";

	private static ElkProtegeLogAppender INSTANCE_ = null;

	private final Logger logger_;

	private ILoggingEvent[] buffer_;

	private int cursor_ = 0;

	ElkProtegeLogAppender(Logger logger) {
		this.logger_ = logger;
		setContext(logger.getLoggerContext());
		logger.addAppender(this);
		start();
	}

	public static ElkProtegeLogAppender getInstance() {
		if (INSTANCE_ == null) {
			org.slf4j.Logger logger = LoggerFactory.getLogger(ELK_PACKAGE_);
			if (logger == null || !(logger instanceof Logger)) {
				throw new IllegalArgumentException("Cannot instantiate "
						+ ElkProtegeLogAppender.class.getName() + ", because there is no "
						+ Logger.class.getName() + " for " + ELK_PACKAGE_);
			} else {
				INSTANCE_ = new ElkProtegeLogAppender((Logger) logger);
			}
		}
		return INSTANCE_;
	}

	public Iterable<ILoggingEvent> getEvents() {
		if (buffer_ == null)
			return Collections.emptyList();
		// else
		return new Iterable<ILoggingEvent>() {

			@Override
			public Iterator<ILoggingEvent> iterator() {
				return new EventIterator();
			}

		};
	}

	public ElkProtegeLogAppender setLogLevel(Level logLevel) {
		logger_.setLevel(logLevel);
		return this;
	}

	public ElkProtegeLogAppender setBufferSize(int bufferSize) {
		if (bufferSize <= 0)
			throw new IllegalArgumentException(
					"The buffer size should be positive!");
		ILoggingEvent[] newBuffer = new ILoggingEvent[bufferSize];
		if (buffer_ != null) {
			if (bufferSize == buffer_.length)
				return this;
			// else, copy the messages from the old buffer
			int newCursor = 0;
			for (ILoggingEvent event : getEvents()) {
				newBuffer[newCursor] = event;
				newCursor++;
				if (newCursor == bufferSize)
					newCursor = 0;
			}
			this.cursor_ = newCursor;
		}
		this.buffer_ = newBuffer;
		return this;
	}

	public void clear() {
		if (buffer_ == null)
			return;
		int bufferSize = buffer_.length;
		this.buffer_ = null;
		setBufferSize(bufferSize);
	}

	@Override
	public void stop() {
		super.stop();
		buffer_ = null;
	}

	@Override
	protected void append(ILoggingEvent event) {
		if (buffer_ == null)
			return;
		buffer_[cursor_] = event;
		cursor_++;
		if (cursor_ == buffer_.length)
			cursor_ = 0;
	}

	private class EventIterator implements Iterator<ILoggingEvent> {

		int pos = buffer_[cursor_] == null ? 0 : cursor_;
		ILoggingEvent next = buffer_[pos];

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public ILoggingEvent next() {
			if (next == null)
				throw new NoSuchElementException();
			ILoggingEvent result = next;
			if (++pos == buffer_.length)
				pos = 0;
			next = pos == cursor_ ? null : buffer_[pos];
			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException(
					"Removal of events not supported!");
		}

	}

}
