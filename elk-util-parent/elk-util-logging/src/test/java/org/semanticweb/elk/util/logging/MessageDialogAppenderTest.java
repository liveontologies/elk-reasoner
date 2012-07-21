/*
 * #%L
 * ELK Utilities for Logging
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
/**
 * 
 */
package org.semanticweb.elk.util.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Not a JUnit test yet, but it will be at some point
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class MessageDialogAppenderTest {

	//TODO Turn into a test
	@SuppressWarnings("static-method")
	@Test
	@Ignore
	public void testDialog() {
		MessageDialogAppender appender = new MessageDialogAppender();
		Logger logger = Logger.getLogger(MessageDialogAppenderTest.class);

		logger.setLevel(Level.DEBUG);
		logger.addAppender(appender);
		logger.warn(new ElkMessage(
				"Soooooooooooooooooooooooommmmme very very very very very very very very very very very very long string",
				"Some type"));

		appender.run();
	}
}
