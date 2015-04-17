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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.semanticweb.elk.protege.ElkProtegeLogAppender;
import org.semanticweb.elk.protege.preferences.ElkLogPreferences;

public class ElkLogPreferencesPanel extends ElkPanel {

	private static final long serialVersionUID = -7309958159114238294L;

	private static final Level[] LOG_LEVELS_ = { Level.ERROR, Level.WARN,
			Level.INFO, Level.DEBUG, Level.TRACE };

	private JComboBox logLevelList_;

	private SpinnerNumberModel bufferSizeModel_;

	private JTextArea logTextArea_;

	@Override
	public ElkLogPreferencesPanel initialize() {
		ElkLogPreferences prefs = new ElkLogPreferences().load();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		JPanel headPane = new JPanel();
		headPane.setLayout(new BoxLayout(headPane, BoxLayout.LINE_AXIS));
		headPane.add(buildLogLevelComponent(prefs.getLogLevel()));
		headPane.add(Box.createRigidArea(new Dimension(10, 0)));
		headPane.add(buildBufferSizeComponent(prefs.logBufferSize));				
		headPane.add(Box.createHorizontalGlue());
		headPane.add(buildResetComponent());
		add(headPane);
		add(buildLogComponent());
		add(buildClearLogComponent());
		return this;
	}

	@Override
	public ElkLogPreferencesPanel applyChanges() {
		ElkLogPreferences prefs = new ElkLogPreferences().load();
		prefs.setLogLevel(LOG_LEVELS_[logLevelList_.getSelectedIndex()]);
		prefs.logBufferSize = bufferSizeModel_.getNumber().intValue();
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
		logLevelList_.setMaximumSize(logLevelList_.getPreferredSize());
		logLevelPane.add(label);
		logLevelPane.add(Box.createRigidArea(new Dimension(10, 0)));
		logLevelPane.add(logLevelList_);

		return logLevelPane;
	}

	private Component buildBufferSizeComponent(int bufferSize) {
		JPanel pane = new JPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.LINE_AXIS));
		JLabel label = new JLabel("Log buffer size:");
		bufferSizeModel_ = new SpinnerNumberModel(bufferSize, 1, 999, 1);
		JComponent spinner = new JSpinner(bufferSizeModel_);
		spinner.setMaximumSize(spinner.getPreferredSize());
		pane.add(label);
		pane.add(Box.createRigidArea(new Dimension(10, 0)));
		pane.add(spinner);
		label.setLabelFor(spinner);
		String tooltip = "The maximal number of last log messages saved.";
		pane.setToolTipText(tooltip);
		spinner.setToolTipText(tooltip);
		pane.setAlignmentX(LEFT_ALIGNMENT);

		return pane;
	}

	private Component buildResetComponent() {
		JButton resetButton = new JButton(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		resetButton.setText("Reset");
		resetButton
				.setToolTipText("Resets all settings for ELK log messages to default values");

		return resetButton;
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

	private void reset() {
		ElkLogPreferences prefs = new ElkLogPreferences().reset();
		logLevelList_.setSelectedItem(prefs.getLogLevel());
		bufferSizeModel_.setValue(prefs.logBufferSize);
	}

}
