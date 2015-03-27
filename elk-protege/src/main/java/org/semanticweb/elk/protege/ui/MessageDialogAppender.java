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
package org.semanticweb.elk.protege.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.semanticweb.elk.protege.ElkProtegePreferences;
import org.semanticweb.elk.util.logging.ElkMessage;

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
 * @author "Yevgeny Kazakov"
 * 
 */
public class MessageDialogAppender extends AppenderSkeleton implements Runnable {

	private final ConcurrentLinkedQueue<LoggingEvent> eventBuffer_ = new ConcurrentLinkedQueue<LoggingEvent>();

	private final AtomicReference<String> messengerThreadName_ = new AtomicReference<String>(
			"");

	private Set<String> ignoredMessageTypes_;

	public MessageDialogAppender() {
		super();
		threshold = Level.WARN;
		reloadIgnoredMessageTypes();
	}

	/**
	 * Shut down. This discards all events, to ensure that the message reporting
	 * thread will die too.
	 */
	@Override
	public void close() {
		synchronized (eventBuffer_) {
			eventBuffer_.clear(); // shoot the messenger
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
		if (!Thread.currentThread().getName()
				.equals(messengerThreadName_.get())) {
			eventBuffer_.add(event);
		}
		// Else: drop event. Recursive message creation is thus blocked; even if
		// displaying the message would create new events, they will not lead to
		// endless reporting (unless the messenger creates new threads; we
		// cannot prevent this).
		// Also note that get() above is needed.

		ensureMessengerRuns();
	}

	public void reloadIgnoredMessageTypes() {
		ElkProtegePreferences elkProtegePrefs = new ElkProtegePreferences()
				.load();
		ignoredMessageTypes_ = new HashSet<String>(
				elkProtegePrefs.suppressedWarningTypes);
	}

	/**
	 * Make sure that a messenger thread is run.
	 */
	protected void ensureMessengerRuns() {
		if (messengerThreadName_.compareAndSet("", "Initialising thread ...")) {
			Thread messengerThread = new Thread(this);
			messengerThreadName_.set(messengerThread.getName());
			messengerThread.start();
		}
	}

	/**
	 * Generate the additional check box message specific to the given event
	 * 
	 * @param event
	 *            the event for which the check box message should be generated
	 * @return the generated check box message
	 */
	@SuppressWarnings("static-method")
	protected String getCheckboxMessage(LoggingEvent event) {
		return "Do not show further messages of this kind";
	}

	/**
	 * Display a dialog window to inform the user about one message event.
	 * 
	 * @param event
	 *            the event for which to display the message
	 * @return {@code true} if the message has been shown
	 */
	protected boolean showMessage(LoggingEvent event) {
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

		ElkMessage elkMessage = ElkMessage.deserialize(event
				.getRenderedMessage());
		String messageType = null;

		if (elkMessage != null) {
			messageType = elkMessage.getMessageType();

			if (ignoredMessageTypes_.contains(messageType)) {
				return false;
			}
		}

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		String messageText = event.getRenderedMessage();
		// truncating too long message text
		if (messageText.length() > 520) {
			messageText = messageText.substring(0, 500) + "...";
		}

		WrappingLabel label = new WrappingLabel(messageText, 600);

		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(label);

		// it is important that the checkbox message is not too wide
		JCheckBox ignoreMessageButton = new JCheckBox(String.format(
				getCheckboxMessage(event), 450));

		if (messageType != null) {
			ignoreMessageButton.setAlignmentX(Component.LEFT_ALIGNMENT);
			panel.add(Box.createRigidArea(new Dimension(0, 10)));
			panel.add(ignoreMessageButton);
		}

		// // Later, it could be possible to abort the reasoner here:
		// Object[] options = { "Continue", "Abort Reasoner" };
		// int result = JOptionPane.showOptionDialog(null, radioPanel,
		// messageTitle,
		// JOptionPane.DEFAULT_OPTION, messageLevel, null, options,
		// options[0]);

		JOptionPane.showMessageDialog(null, panel, messageTitle, messageLevel);

		if (ignoreMessageButton.isSelected()) {
			ignoredMessageTypes_.add(messageType);
			ElkProtegePreferences elkProtegePrefs = new ElkProtegePreferences()
					.load();
			elkProtegePrefs.suppressedWarningTypes.add(messageType);
			elkProtegePrefs.save();
		}

		return true;

	}

	/**
	 * Display messages until none are left to display. Then reset the
	 * registered thread name and die.
	 */
	@Override
	public void run() {
		while (!eventBuffer_.isEmpty()) {
			showMessage(eventBuffer_.poll());
		}
		messengerThreadName_.set("");
		// If another thread has added new events just before the
		// messengerThreadName was reset here, then it could happen that the
		// messenger dies while there is still work to do. To avoid this, we
		// check again one last time, and create a new messenger if needed:
		if (!eventBuffer_.isEmpty()) {
			ensureMessengerRuns();
		}
	}

}

class WrappingLabel extends JTextArea {

	private static final long serialVersionUID = -1028283148775499046L;

	public WrappingLabel(String text, int width) {
		super(text);

		setBackground(null);
		setEditable(false);
		setBorder(null);
		setFocusable(false);

		setLineWrap(true);
		setWrapStyleWord(true);
		setText(text);
		setSize(width, 1);
		setSize(getPreferredSize());

	}
}
