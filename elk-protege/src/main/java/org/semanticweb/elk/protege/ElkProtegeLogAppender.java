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

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.semanticweb.elk.protege.preferences.ElkLogPreferences;

public class ElkProtegeLogAppender extends AppenderSkeleton implements Runnable {

	private static final int DEFAULT_BUFFER_SIZE_ = 256;

	private static final String ELK_PACKAGE_ = "org.semanticweb.elk";

	private static final ElkProtegeLogAppender INSTANCE_ = new ElkProtegeLogAppender();

	private final LoggingEvent[] buffer_ = new LoggingEvent[DEFAULT_BUFFER_SIZE_];

	private int cursor_ = 0;

	public ElkProtegeLogAppender() {
		reloadLogLevel();
	}

	public static ElkProtegeLogAppender getInstance() {
		return INSTANCE_;
	}

	public LoggingEvent[] getEvents() {
		return this.buffer_;
	}

	public int getCursor() {
		return this.cursor_;
	}

	public void reloadLogLevel() {
		ElkLogPreferences elkLogPrefs = new ElkLogPreferences().load();
		Logger.getLogger(ELK_PACKAGE_).setLevel(elkLogPrefs.getLogLevel());
	}

	public void clear() {
		this.cursor_ = 0;
		buffer_[cursor_] = null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean requiresLayout() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void append(LoggingEvent event) {
		buffer_[cursor_] = event;
		cursor_++;
		if (cursor_ == buffer_.length)
			cursor_ = 0;
	}
}
