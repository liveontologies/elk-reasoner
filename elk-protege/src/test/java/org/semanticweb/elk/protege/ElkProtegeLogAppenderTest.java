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
package org.semanticweb.elk.protege;

import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;

public class ElkProtegeLogAppenderTest extends TestCase {

	@Test
	public static void testLogClear() {
		Logger logger = Logger.getLogger(ElkProtegeLogAppenderTest.class);

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
		for (LoggingEvent event : appender.getEvents()) {
			assertEquals("Missing message:", event.getMessage(), messages[i++]);
		}

		assertEquals("The last message should be in the buffer",
				messages.length, i);

		appender.clear();

		i = 0;
		for (@SuppressWarnings("unused")
		LoggingEvent event : appender.getEvents()) {
			i++;
		}

		assertEquals("After clear there should be no messages", 0, i);

		for (i = 0; i < furtherMessages.length; i++) {
			logger.debug(furtherMessages[i]);
		}

		i = messages.length - Math.min(furtherMessages.length, bufferSize);
		for (LoggingEvent event : appender.getEvents()) {
			assertEquals("Missing message:", event.getMessage(),
					furtherMessages[i++]);
		}

		assertEquals("The last message should be in the buffer",
				furtherMessages.length, i);

	}
}
