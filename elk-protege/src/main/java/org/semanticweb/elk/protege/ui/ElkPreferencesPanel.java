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
/**
 * 
 */
package org.semanticweb.elk.protege.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.JTableHeader;

import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.elk.protege.ElkPreferences;
import org.semanticweb.elk.protege.ElkProtegePluginInstance;
import org.semanticweb.elk.protege.ProtegeSuppressedMessages;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

/**
 * UI panel for setting preferences for ELK
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 * @author Peter Skocovsky
 */
public class ElkPreferencesPanel extends OWLPreferencesPanel {

	private static final long serialVersionUID = -5568211860560307648L;

	private SpinnerNumberModel numberOfWorkersModel_;

	private JCheckBox incrementalCheckbox_, syncCheckbox_,
			suppressAllWarningsCheckbox_;

	private WarningTableModel warningTypes_;

	private JComboBox<String> logLevel_;

	private SpinnerNumberModel logCharacterLimitModel_;

	@Override
	public void initialise() throws Exception {
		setLayout(new BorderLayout());
		PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
		add(panel, BorderLayout.NORTH);
		ElkPreferences prefs = new ElkPreferences().load();

		panel.addGroup("Number of worker threads");
		numberOfWorkersModel_ = new SpinnerNumberModel(prefs.numberOfWorkers, 1,
				999, 1);
		JComponent spinner = new JSpinner(numberOfWorkersModel_);
		spinner.setMaximumSize(spinner.getPreferredSize());
		spinner.setToolTipText(
				"The number of threads that ELK can use for performing parallel computations");
		panel.addGroupComponent(spinner);

		incrementalCheckbox_ = new JCheckBox("Incremental reasoning",
				prefs.incrementalMode);
		incrementalCheckbox_.setToolTipText(
				"If checked, ELK tries to recompute only the results caused by the changes in the ontology");
		panel.addGroupComponent(incrementalCheckbox_);

		syncCheckbox_ = new JCheckBox("Auto-syncronization",
				prefs.autoSynchronization);
		syncCheckbox_.setToolTipText(
				"If checked, ELK will always be in sync with the ontology (requires reasoner restart)");
		syncCheckbox_.setEnabled(incrementalCheckbox_.isSelected());
		incrementalCheckbox_.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				syncCheckbox_.setEnabled(incrementalCheckbox_.isSelected());
			}
		});
		panel.addGroupComponent(syncCheckbox_);
		panel.addSeparator();

		panel.addGroup("Suppressed warning types");
		ProtegeSuppressedMessages suppressedMessages = ProtegeSuppressedMessages
				.getInstance().reload();
		warningTypes_ = new WarningTableModel();
		JTable table = new JTable(warningTypes_) {
			private static final long serialVersionUID = 713203038036137721L;

			// Implement table header tool tips.
			@Override
			protected JTableHeader createDefaultTableHeader() {
				return new JTableHeader(columnModel) {
					private static final long serialVersionUID = 489086430119911536L;

					@Override
					public String getToolTipText(MouseEvent e) {
						java.awt.Point p = e.getPoint();
						int index = columnModel.getColumnIndexAtX(p.x);
						int realIndex = columnModel.getColumn(index)
								.getModelIndex();
						return WarningTableModel.COLUMN_TOOLTIPS[realIndex];
					}
				};
			}
		};
		table.getColumnModel().getColumn(1).setMaxWidth(50);
		for (String warningType : prefs.suppressedWarningTypes) {
			warningTypes_.addWarningType(warningType,
					suppressedMessages.getCount(warningType));
		}
		JScrollPane tableScroller = new JScrollPane(table);
		tableScroller.setPreferredSize(new Dimension(400, 100));
		panel.addGroupComponent(tableScroller);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		JButton clearButton = new JButton(new AbstractAction() {
			private static final long serialVersionUID = 5828364975956635366L;

			@Override
			public void actionPerformed(ActionEvent e) {
				warningTypes_.clear();
			}
		});
		clearButton.setText("Clear");
		clearButton.setToolTipText("Remove all suppressed warning types");
		JButton removeButton = new JButton(new AbstractAction() {
			private static final long serialVersionUID = 7125300829305229857L;

			@Override
			public void actionPerformed(ActionEvent e) {
				warningTypes_.removeSelectedRows(table.getSelectionModel());
			}
		});
		removeButton.setText("Remove selected");
		removeButton
				.setToolTipText("Remove all selected suppressed warning types");
		JButton resetCountsButton = new JButton(new AbstractAction() {
			private static final long serialVersionUID = 7918203938390550678L;

			@Override
			public void actionPerformed(ActionEvent e) {
				warningTypes_.resetCounts();
			}
		});
		resetCountsButton.setText("Reset counters");
		resetCountsButton
				.setToolTipText("Sets the values of all counters to 0");
		buttonPane.add(clearButton);
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(removeButton);
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(resetCountsButton);
		panel.addGroupComponent(buttonPane);

		suppressAllWarningsCheckbox_ = new JCheckBox("Suppress all warnings",
				prefs.suppressAllWarnings);
		suppressAllWarningsCheckbox_.setToolTipText(
				"If checked, all ELK warnings will be silently ignored; the information about them will still be included above");
		panel.addGroupComponent(suppressAllWarningsCheckbox_);

		panel.addGroup("Log level");
		logLevel_ = new JComboBox<>(new String[] { Level.OFF.toString(),
				Level.ERROR.toString(), Level.WARN.toString(),
				Level.INFO.toString(), Level.DEBUG.toString(),
				Level.TRACE.toString(), Level.ALL.toString() });
		logLevel_.setSelectedItem(prefs.logLevel);
		logLevel_.setToolTipText(
				"Messages with level equal to or grater than this level will appear in the log");
		panel.addGroupComponent(logLevel_);

		panel.addGroup("Maximal number of character in the log");
		logCharacterLimitModel_ = new SpinnerNumberModel(
				prefs.logCharacterLimit, 1, Integer.MAX_VALUE, 10);
		spinner = new JSpinner(logCharacterLimitModel_);
		spinner.setToolTipText(
				"The maximal number of characters displayed in the log");
		panel.addGroupComponent(spinner);
	}

	@Override
	public void applyChanges() {
		ElkPreferences prefs = new ElkPreferences().load();
		prefs.numberOfWorkers = numberOfWorkersModel_.getNumber().intValue();
		prefs.incrementalMode = incrementalCheckbox_.isSelected();
		prefs.autoSynchronization = syncCheckbox_.isSelected();
		prefs.suppressedWarningTypes = new ArrayList<String>(
				warningTypes_.getRowCount());
		for (int i = 0; i < warningTypes_.getRowCount(); i++) {
			prefs.suppressedWarningTypes.add(warningTypes_.getWarningTypeAt(i));
		}
		prefs.suppressAllWarnings = suppressAllWarningsCheckbox_.isSelected();
		prefs.logLevel = logLevel_.getSelectedItem().toString();
		prefs.logCharacterLimit = logCharacterLimitModel_.getNumber()
				.intValue();
		prefs.save();
	}

	@Override
	public void dispose() throws Exception {
		// if the reasoner is ELK and has already been created, load the
		// preferences
		OWLReasoner reasoner = getOWLModelManager().getOWLReasonerManager()
				.getCurrentReasoner();
		if (reasoner instanceof ElkReasoner) {
			((ElkReasoner) reasoner)
					.setConfigurationOptions(ElkPreferences.getElkConfig());
		}
		ProtegeSuppressedMessages.getInstance().reload();
		final ElkPreferences prefs = new ElkPreferences().load();
		final Logger logger = LoggerFactory
				.getLogger(ElkProtegePluginInstance.ELK_PACKAGE_);
		if (logger instanceof ch.qos.logback.classic.Logger) {
			ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) logger;
			logbackLogger.setLevel(Level.toLevel(prefs.logLevel, Level.WARN));
		}
		ElkProtegePluginInstance.ELK_LOG_CONTROLLER
				.setCharacterLimit(prefs.logCharacterLimit);
	}

}
