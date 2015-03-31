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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;

import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.elk.protege.ElkGeneralPreferences;
import org.semanticweb.elk.protege.ElkWarningPreferences;
import org.semanticweb.elk.protege.ProtegeMessageAppender;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * UI panel for setting preferences for ELK
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ElkPreferencesPanel extends OWLPreferencesPanel {

	private static final long serialVersionUID = -5568211860560307648L;

	private SpinnerNumberModel numberOfWorkersModel_;

	private JCheckBox incrementalCheckbox_, syncCheckbox_;

	private DefaultListModel warningTypes_;

	@Override
	public void initialise() throws Exception {
		// Create a simple JPanel with the ELK's settings
		ElkGeneralPreferences elkGeneralPrefs = new ElkGeneralPreferences()
				.load();

		JTabbedPane tabbedPane = new JTabbedPane();

		tabbedPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("ELK reasoner settings"),
				BorderFactory.createEmptyBorder(7, 7, 7, 7)));

		JPanel generalPrefsPane = new JPanel();
		generalPrefsPane.setLayout(new BoxLayout(generalPrefsPane,
				BoxLayout.PAGE_AXIS));
		tabbedPane.addTab("General", null, generalPrefsPane,
				"General ELK settings");

		generalPrefsPane
				.add(buildNumberOfWorkersComponent(elkGeneralPrefs.numberOfWorkers));
		generalPrefsPane
				.add(buildIncrementalReasoningComponent(elkGeneralPrefs.incrementalMode));
		generalPrefsPane
				.add(buildAutoSyncComponent(elkGeneralPrefs.autoSynchronization));
		generalPrefsPane.add(Box.createVerticalGlue());
		generalPrefsPane.add(buildResetGeneralSettingsComponent());

		ElkWarningPreferences elkWarningPrefs = new ElkWarningPreferences()
				.load();

		JPanel warningPrefsPane = new JPanel();
		warningPrefsPane.setLayout(new BoxLayout(warningPrefsPane,
				BoxLayout.PAGE_AXIS));

		tabbedPane.addTab("Warnings", null, warningPrefsPane,
				"Settings for ELK warning messages");

		warningPrefsPane
				.add(buildWarningTypesComponent(elkWarningPrefs.suppressedWarningTypes));

		JPanel logPane = new JPanel();
		logPane.setLayout(new BoxLayout(logPane, BoxLayout.PAGE_AXIS));

		tabbedPane
				.addTab("Log", null, logPane, "Settings for ELK log messages");

		setLayout(new BorderLayout());

		add(tabbedPane, BorderLayout.NORTH);

	}

	private Component buildNumberOfWorkersComponent(int numberOfWorkers) {
		JPanel workersPane = new JPanel();
		workersPane.setLayout(new BoxLayout(workersPane, BoxLayout.LINE_AXIS));
		JLabel label = new JLabel("Number of working threads:");
		numberOfWorkersModel_ = new SpinnerNumberModel(numberOfWorkers, 1, 999,
				1);
		JComponent spinner = new JSpinner(numberOfWorkersModel_);
		spinner.setMaximumSize(spinner.getPreferredSize());
		workersPane.add(label);
		workersPane.add(Box.createRigidArea(new Dimension(10, 0)));
		workersPane.add(spinner);
		label.setLabelFor(spinner);
		String tooltip = "The number of threads that ELK can use for performing parallel computations.";
		workersPane.setToolTipText(tooltip);
		spinner.setToolTipText(tooltip);
		workersPane.setAlignmentX(LEFT_ALIGNMENT);

		return workersPane;
	}

	private Component buildIncrementalReasoningComponent(boolean incrementalMode) {
		incrementalCheckbox_ = new JCheckBox("Incremental reasoning",
				incrementalMode);
		incrementalCheckbox_
				.setToolTipText("If checked, ELK tries to recompute only the results caused by the changes in the ontology");
		return incrementalCheckbox_;
	}

	private Component buildAutoSyncComponent(boolean autoSynchronization) {
		syncCheckbox_ = new JCheckBox("Auto-syncronization",
				autoSynchronization);
		syncCheckbox_
				.setToolTipText("If checked, ELK will always be in sync with the ontology (requires reasoner restart)");
		syncCheckbox_.setEnabled(incrementalCheckbox_.isSelected());
		incrementalCheckbox_.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				syncCheckbox_.setEnabled(incrementalCheckbox_.isSelected());
			}
		});

		return syncCheckbox_;
	}

	private Component buildResetGeneralSettingsComponent() {
		JButton resetButton = new JButton(new AbstractAction() {
			private static final long serialVersionUID = 6257131701636338334L;

			@Override
			public void actionPerformed(ActionEvent e) {
				resetGeneralSettings();
			}
		});
		resetButton.setText("Reset");
		resetButton
				.setToolTipText("Resets all general ELK settings to default values");

		return resetButton;
	}

	private Component buildWarningTypesComponent(
			List<String> suppressedWarningTypes) {
		warningTypes_ = new DefaultListModel();
		for (String warningType : suppressedWarningTypes) {
			warningTypes_.addElement(warningType);
		}
		JList list = new JList(warningTypes_);
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(300, 100));
		JPanel listPane = new JPanel();
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
		listPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JLabel label = new JLabel("Suppressed warning types:");
		listPane.add(label);
		listPane.add(listScroller);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		JButton clearButton = new JButton(new AbstractAction() {
			private static final long serialVersionUID = 5828364975956635366L;

			@Override
			public void actionPerformed(ActionEvent e) {
				clearSuppressedWarningTypes();
			}
		});
		clearButton.setText("Clear");
		JButton removeButton = new JButton("Remove");
		buttonPane.add(clearButton);
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(removeButton);
		listPane.add(buttonPane);

		return listPane;
	}

	private void resetGeneralSettings() {
		ElkGeneralPreferences elkProtegePrefs = new ElkGeneralPreferences()
				.reset().load();
		numberOfWorkersModel_.setValue(elkProtegePrefs.numberOfWorkers);
		incrementalCheckbox_.setSelected(elkProtegePrefs.incrementalMode);
		syncCheckbox_.setEnabled(incrementalCheckbox_.isSelected());
		syncCheckbox_.setSelected(elkProtegePrefs.autoSynchronization);
	}

	private void clearSuppressedWarningTypes() {
		ElkWarningPreferences elkWarningPrefs = new ElkWarningPreferences()
				.reset().load();
		warningTypes_.clear();
		for (String warningType : elkWarningPrefs.suppressedWarningTypes) {
			warningTypes_.addElement(warningType);
		}
	}

	@Override
	public void applyChanges() {
		ElkGeneralPreferences elkGeneralPrefs = new ElkGeneralPreferences()
				.load();
		elkGeneralPrefs.numberOfWorkers = Integer
				.parseInt(numberOfWorkersModel_.getNumber().toString());
		elkGeneralPrefs.incrementalMode = incrementalCheckbox_.isSelected();
		elkGeneralPrefs.autoSynchronization = syncCheckbox_.isSelected();
		elkGeneralPrefs.save();

		ElkWarningPreferences elkWarningPrefs = new ElkWarningPreferences()
				.load();
		elkWarningPrefs.suppressedWarningTypes = new ArrayList<String>(
				warningTypes_.size());
		for (int i = 0; i < warningTypes_.size(); i++) {
			elkWarningPrefs.suppressedWarningTypes.add((String) warningTypes_
					.getElementAt(i));
		}
		elkWarningPrefs.save();
	}

	@Override
	public void dispose() throws Exception {
		// if the reasoner is ELK and has already been created, load the
		// preferences
		OWLReasoner reasoner = getOWLModelManager().getOWLReasonerManager()
				.getCurrentReasoner();
		if (!(reasoner instanceof ElkReasoner))
			return;
		ElkGeneralPreferences elkGeneralPrefs = new ElkGeneralPreferences()
				.load();
		((ElkReasoner) reasoner).setConfigurationOptions(elkGeneralPrefs
				.getElkConfig());
		ProtegeMessageAppender.getInstance().reloadSuppressedMessageTypes();
	}

}
