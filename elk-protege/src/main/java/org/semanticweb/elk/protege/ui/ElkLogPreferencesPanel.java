package org.semanticweb.elk.protege.ui;

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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.semanticweb.elk.protege.ElkProtegeLogAppender;
import org.semanticweb.elk.protege.preferences.ElkLogPreferences;

public class ElkLogPreferencesPanel extends ElkPanel {

	private static final long serialVersionUID = -7309958159114238294L;

	private static final Level[] LOG_LEVELS_ = { Level.ERROR, Level.WARN,
			Level.INFO, Level.DEBUG, Level.TRACE };

	private JComboBox logLevelList_;

	private JTextArea logTextArea_;

	@Override
	public ElkLogPreferencesPanel initialize() {
		ElkLogPreferences prefs = new ElkLogPreferences().load();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(buildLogLevelComponent(prefs.getLogLevel()));
		add(buildLogComponent());
		add(buildClearLogComponent());
		return this;
	}

	@Override
	public ElkLogPreferencesPanel applyChanges() {
		ElkLogPreferences prefs = new ElkLogPreferences().load();
		prefs.setLogLevel(LOG_LEVELS_[logLevelList_.getSelectedIndex()]);
		prefs.save();
		if (logTextArea_.getText().isEmpty()) {
			ElkProtegeLogAppender.getInstance().clear();
		}
		return this;
	}

	private Component buildLogLevelComponent(Level logLevel) {
		JPanel logLevelPane = new JPanel();
		logLevelPane
				.setLayout(new BoxLayout(logLevelPane, BoxLayout.LINE_AXIS));
		JLabel label = new JLabel("Log level:");
		logLevelList_ = new JComboBox(LOG_LEVELS_);
		logLevelList_.setSelectedItem(logLevel);
		logLevelPane.add(label);
		logLevelPane.add(Box.createRigidArea(new Dimension(10, 0)));
		logLevelPane.add(logLevelList_);
		logLevelPane.add(Box.createHorizontalGlue());

		return logLevelPane;
	}

	private Component buildLogComponent() {
		logTextArea_ = new JTextArea(25, 50);
		JScrollPane scrollPane = new JScrollPane(logTextArea_);
		logTextArea_.setEditable(false);
		ElkProtegeLogAppender elkLog = ElkProtegeLogAppender.getInstance();
		for (LoggingEvent event : elkLog.getEvents()) {
			logTextArea_.append(event.getRenderedMessage() + "\n");
		}
		return scrollPane;
	}

	private Component buildClearLogComponent() {
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		JButton clearButton = new JButton(new AbstractAction() {
			private static final long serialVersionUID = 9154688289786746140L;

			@Override
			public void actionPerformed(ActionEvent e) {
				logTextArea_.setText(null);
			}
		});
		clearButton.setText("Clear");
		clearButton.setToolTipText("Remove all printed log messages");
		buttonPane.add(clearButton);
		buttonPane.add(Box.createHorizontalGlue());

		return buttonPane;
	}

}
