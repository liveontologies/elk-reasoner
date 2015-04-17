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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

public class ElkProtegeLogAppender extends AppenderSkeleton {

	private static final String ELK_PACKAGE_ = "org.semanticweb.elk";

	private static final ElkProtegeLogAppender INSTANCE_ = new ElkProtegeLogAppender(
			Logger.getLogger(ELK_PACKAGE_));

	private final Logger logger_;

	private LoggingEvent[] buffer_;

	private int cursor_ = 0;

	ElkProtegeLogAppender(Logger logger) {
		this.logger_ = logger;
		logger.addAppender(this);
	}

	public static ElkProtegeLogAppender getInstance() {
		return INSTANCE_;
	}

	public Iterable<LoggingEvent> getEvents() {
		return new Iterable<LoggingEvent>() {

			@Override
			public Iterator<LoggingEvent> iterator() {
				return new EventIterator();
			}

		};
	}

	public void setLogLevel(Level logLevel) {
		logger_.setLevel(logLevel);
	}

	public void setBufferSize(int bufferSize) {
		if (bufferSize <= 0)
			throw new IllegalArgumentException(
					"The buffer size should be positive!");
		LoggingEvent[] newBuffer = new LoggingEvent[bufferSize];
		if (buffer_ != null) {
			if (bufferSize == buffer_.length)
				return;
			// else, copy the messages from the old buffer
			int newCursor = 0;
			for (LoggingEvent event : getEvents()) {
				newBuffer[newCursor] = event;
				newCursor++;
				if (newCursor == bufferSize)
					newCursor = 0;
			}
			this.cursor_ = newCursor;
		}
		this.buffer_ = newBuffer;
	}

	public void clear() {
		if (buffer_ == null)
			return;
		int bufferSize = buffer_.length;
		this.buffer_ = null;
		setBufferSize(bufferSize);
	}

	@Override
	public void close() {
		buffer_ = null;
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	@Override
	protected void append(LoggingEvent event) {
		if (buffer_ == null)
			return;
		buffer_[cursor_] = event;
		cursor_++;
		if (cursor_ == buffer_.length)
			cursor_ = 0;
	}

	private class EventIterator implements Iterator<LoggingEvent> {

		int pos = buffer_[cursor_] == null ? 0 : cursor_;
		LoggingEvent next = buffer_[pos];

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public LoggingEvent next() {
			if (next == null)
				throw new NoSuchElementException();
			LoggingEvent result = next;
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
