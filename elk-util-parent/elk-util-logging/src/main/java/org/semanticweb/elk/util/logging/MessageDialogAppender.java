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
package org.semanticweb.elk.util.logging;

import javax.swing.JOptionPane;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A Log4J Appender that creates dialogs in order to "log" messages.
 * 
 * Like all Log4J loggers, this logger can be set to report only messages above
 * a certain threshold using the setThreshold(). The default threshold is WARN.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class MessageDialogAppender extends AppenderSkeleton {

	public MessageDialogAppender() {
		super();
		initConfiguration();
	}

	public MessageDialogAppender(boolean isActive) {
		super(isActive);
		initConfiguration();
	}

	protected void initConfiguration() {
		threshold = Level.WARN;
	}

	@Override
	public void close() {
		// Nothing to do.
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	@Override
	protected void append(LoggingEvent event) {
		String messageTitle;
		int messageLevel;
		if (event.getLevel().isGreaterOrEqual(Level.ERROR)) {
			messageTitle = "Error";
			messageLevel = JOptionPane.ERROR_MESSAGE;
		} else if (event.getLevel().isGreaterOrEqual(Level.WARN)) {
			messageTitle = "Warning";
			messageLevel = JOptionPane.WARNING_MESSAGE;
		} else {
			messageTitle = "Information";
			messageLevel = JOptionPane.INFORMATION_MESSAGE;
		}
		JOptionPane.showMessageDialog(null, event.getRenderedMessage(),
				messageTitle, messageLevel);
	}

}
