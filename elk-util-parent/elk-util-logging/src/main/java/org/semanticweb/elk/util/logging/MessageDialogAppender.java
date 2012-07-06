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

import java.awt.Dimension;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A Log4J Appender that creates dialogs in order to "log" messages.
 * 
 * Like all Log4J loggers, this logger can be set to report only messages above
 * a certain threshold using setThreshold(). The default threshold is WARN.
 * 
 * Only one dialog is shown at any single time: the user has to close it before
 * getting the next. The display of message dialogs is controlled by an
 * independent thread, so that the code that reports a message does not have to
 * wait for the user to close dialogs. Incoming events are queued and processed
 * in order.
 * 
 * For events of type ElkMessage (rather than plain String), the appender allows
 * to filter by message type, that is, it offers the user the option to not show
 * such messages again. Here, "such messages" means messages of the same message
 * type.
 * 
 * @author Markus Kroetzsch
 * 
 *         This is now a singleton class to avoid problems with Protege creating
 *         multiple instances, which results in many popup windows
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class MessageDialogAppender extends AppenderSkeleton implements Runnable {

	private static MessageDialogAppender INSTANCE_ = new MessageDialogAppender();

	public static MessageDialogAppender getInstance() {
		return INSTANCE_;
	}

	protected final ConcurrentLinkedQueue<LoggingEvent> eventBuffer = new ConcurrentLinkedQueue<LoggingEvent>();

	protected final AtomicReference<String> messengerThreadName = new AtomicReference<String>(
			"");

	protected final Set<String> ignoredMessageTypes = new HashSet<String>();

	private MessageDialogAppender() {
		super();
		initConfiguration();
	}

	protected void initConfiguration() {
		threshold = Level.WARN;
	}

	/**
	 * Shut down. This discards all events, to ensure that the message reporting
	 * thread will die too.
	 */
	@Override
	public void close() {
		synchronized (eventBuffer) {
			eventBuffer.clear(); // shoot the messenger
		}
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	/**
	 * Append a logging event. This is what log4j calls to log an event.
	 */
	@Override
	protected void append(LoggingEvent event) {
		if (!Thread.currentThread().getName().equals(messengerThreadName.get())) {
			eventBuffer.add(event);
		}
		// Else: drop event. Recursive message creation is thus blocked; even if
		// displaying the message would create new events, they will not lead to
		// endless reporting (unless the messenger creates new threads; we
		// cannot prevent this).
		// Also note that get() above is needed.

		ensureMessengerRuns();
	}

	/**
	 * Make sure that a messenger thread is run.
	 */
	protected void ensureMessengerRuns() {
		if (messengerThreadName.compareAndSet("", "Initialising thread ...")) {
			Thread messengerThread = new Thread(this);
			messengerThreadName.set(messengerThread.getName());
			messengerThread.start();
		}
	}

	/**
	 * Display a dialog window to inform the user about one message event.
	 * 
	 * @param event
	 */
	protected void showMessage(LoggingEvent event) {
		String messageTitle;
		int messageLevel;
		if (event.getLevel().isGreaterOrEqual(Level.ERROR)) {
			messageTitle = "ELK Error";
			messageLevel = JOptionPane.ERROR_MESSAGE;
		} else if (event.getLevel().isGreaterOrEqual(Level.WARN)) {
			messageTitle = "ELK Warning";
			messageLevel = JOptionPane.WARNING_MESSAGE;
		} else {
			messageTitle = "ELK Information";
			messageLevel = JOptionPane.INFORMATION_MESSAGE;
		}

		Object Message = event.getMessage();
		String messageType;
		if (Message instanceof ElkMessage) {
			messageType = ((ElkMessage) Message).getMessageType();
			if (ignoredMessageTypes.contains(messageType)) {
				return;
			}
		} else {
			messageType = null;
		}

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		String displayLabel = event.getRenderedMessage();
		// A simple heuristic to force line breaks into very long messages:
		if (displayLabel.length() > 80) {
			displayLabel = String.format(
					"<html><div style=\"width:%dpx;\">%s</div></html>", 500,
					displayLabel);
		}
		panel.add(new JLabel(displayLabel));

		JCheckBox ignoreMessageButton = new JCheckBox(
				"Do not show further messages of this kind in this session");
		if (messageType != null) {
			panel.add(Box.createRigidArea(new Dimension(0, 10)));
			panel.add(ignoreMessageButton);
		}

		// // Later, it could be possible to abort the reasoner here:
		// Object[] options = { "Continue", "Abort Reaoner" };
		// int result = JOptionPane.showOptionDialog(null, radioPanel,
		// messageTitle,
		// JOptionPane.DEFAULT_OPTION, messageLevel, null, options,
		// options[0]);

		JOptionPane.showMessageDialog(null, panel, messageTitle, messageLevel);

		if (ignoreMessageButton.isSelected()) {
			ignoredMessageTypes.add(messageType);
		}
	}

	/**
	 * Display messages until none are left to display. Then reset the
	 * registered thread name and die.
	 */
	@Override
	public void run() {
		while (!eventBuffer.isEmpty()) {
			showMessage(eventBuffer.poll());
		}
		messengerThreadName.set("");
		// If another thread has added new events just before the
		// messengerThreadName was reset here, then it could happen that the
		// messenger dies while there is still work to do. To avoid this, we
		// check again one last time, and create a new messenger if needed:
		if (!eventBuffer.isEmpty()) {
			ensureMessengerRuns();
		}
	}

}
