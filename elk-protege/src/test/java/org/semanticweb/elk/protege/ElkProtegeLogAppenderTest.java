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

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class ElkProtegeLogAppenderTest extends TestCase {

	@Test
	public static void testLogClear() {
		org.slf4j.Logger slfLogger = LoggerFactory.getLogger(ElkProtegeLogAppenderTest.class);
		if (slfLogger == null || !(slfLogger instanceof Logger)) {
			fail("Cannot get a " + Logger.class.getName());
		} else {
			Logger logger = (Logger) slfLogger;

			ElkProtegeLogAppender appender = new ElkProtegeLogAppender(logger);
			logger.addAppender(appender);

			appender.setLogLevel(Level.DEBUG);
			int bufferSize = 3;
			appender.setBufferSize(bufferSize);

			String[] messages = { "first message", "second message",
					"third message", "forth message" };

			String[] furtherMessages = { "fifth message", "sixth message" };

			int i = 0;
			for (i = 0; i < messages.length; i++) {
				logger.debug(messages[i]);
			}

			i = messages.length - Math.min(messages.length, bufferSize);
			for (ILoggingEvent event : appender.getEvents()) {
				assertEquals("Missing message:", event.getMessage(), messages[i++]);
			}

			assertEquals("The last message should be in the buffer",
					messages.length, i);

			appender.clear();

			i = 0;
			for (@SuppressWarnings("unused")
			ILoggingEvent event : appender.getEvents()) {
				i++;
			}

			assertEquals("After clear there should be no messages", 0, i);

			for (i = 0; i < furtherMessages.length; i++) {
				logger.debug(furtherMessages[i]);
			}

			i = messages.length - Math.min(furtherMessages.length, bufferSize);
			for (ILoggingEvent event : appender.getEvents()) {
				assertEquals("Missing message:", event.getMessage(),
						furtherMessages[i++]);
			}

			assertEquals("The last message should be in the buffer",
					furtherMessages.length, i);

		}
	}
}
