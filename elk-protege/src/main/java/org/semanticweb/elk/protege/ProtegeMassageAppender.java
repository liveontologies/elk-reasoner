/*
 * #%L
 * ELK Reasoner Protege Plug-in
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
package org.semanticweb.elk.protege;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.elk.util.logging.MessageDialogAppender;

/**
 * A {@link MessageDialogAppender} for Protege that uses additional
 * Protege-specific messages. Currently it displays an information how to switch
 * off some inference types after an error or a warning has been registered
 * within {@link ElkReasoner}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ProtegeMassageAppender extends MessageDialogAppender {

	private static ProtegeMassageAppender INSTANCE_ = new ProtegeMassageAppender();

	public static ProtegeMassageAppender getInstance() {
		return INSTANCE_;
	}

	/**
	 * {@code true} if the information about unsupported OWL API method should
	 * be shown after the first warning message from {@link ElkReasoner}.
	 */
	private boolean showUnsupportedOwlApiMethodInfo = true;

	private ProtegeMassageAppender() {
		super();
	}

	@Override
	protected void showMessage(LoggingEvent event) {
		super.showMessage(event);
		if (showUnsupportedOwlApiMethodInfo
				&& event.getLevel().isGreaterOrEqual(Level.WARN)
				&& event.getLoggerName().equals(
						Logger.getLogger(ElkReasoner.class).getName())) {

			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

			String displayLabel = String
					.format("<html><div style=\"width:%dpx;\">"
							+ "<p>"
							+ "Prot&eacute;g&eacute; has just called an OWL API method that ELK currently does not support. "
							+ "In order to minimize the amount of error messages, it is recommended to switch off some of the inference types."
							+ "</p>"
							+ "<p>"
							+ "Please go to <b>Reasoner -> Configure -> Displayed Inferences</b> and uncheck:"
							+ "<ul type=\"square\">"
							+ "<li>All <b>Displayed Object Property Inferences</b>"
							+ "<li>All <b>Displayed Data Property Inferences</b>"
							+ "<li>All <b>Displayed Individual Inferences</b> except for <b>Types</b> and <b>Object Property Assertions</b>"
							+ "</ul></p>", 500);

			panel.add(new JLabel(displayLabel));

			JCheckBox ignoreMessageButton = new JCheckBox(
					"Do not show this message again in this session");
			panel.add(Box.createRigidArea(new Dimension(0, 10)));
			panel.add(ignoreMessageButton);

			JOptionPane.showMessageDialog(null, panel,
					"Unsupported Inference Types",
					JOptionPane.INFORMATION_MESSAGE);

			if (ignoreMessageButton.isSelected()) {
				showUnsupportedOwlApiMethodInfo = false;
			}

		}

	}
}
