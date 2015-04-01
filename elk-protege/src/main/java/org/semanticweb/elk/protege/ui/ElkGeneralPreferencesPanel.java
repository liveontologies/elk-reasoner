package org.semanticweb.elk.protege.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.semanticweb.elk.protege.preferences.ElkGeneralPreferences;

public class ElkGeneralPreferencesPanel extends ElkPanel {

	private static final long serialVersionUID = -1327423520858030577L;

	private SpinnerNumberModel numberOfWorkersModel_;

	private JCheckBox incrementalCheckbox_, syncCheckbox_;

	@Override
	public ElkGeneralPreferencesPanel initialize() {
		ElkGeneralPreferences prefs = new ElkGeneralPreferences().load();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(buildNumberOfWorkersComponent(prefs.numberOfWorkers));
		add(buildIncrementalReasoningComponent(prefs.incrementalMode));
		add(buildAutoSyncComponent(prefs.autoSynchronization));
		add(Box.createVerticalGlue());
		add(buildResetComponent());
		return this;
	}

	@Override
	public ElkGeneralPreferencesPanel applyChanges() {
		ElkGeneralPreferences prefs = new ElkGeneralPreferences().load();
		prefs.numberOfWorkers = Integer.parseInt(numberOfWorkersModel_
				.getNumber().toString());
		prefs.incrementalMode = incrementalCheckbox_.isSelected();
		prefs.autoSynchronization = syncCheckbox_.isSelected();
		prefs.save();
		return this;
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

	private Component buildResetComponent() {
		JButton resetButton = new JButton(new AbstractAction() {
			private static final long serialVersionUID = 6257131701636338334L;

			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		resetButton.setText("Reset");
		resetButton
				.setToolTipText("Resets all general ELK settings to default values");

		return resetButton;
	}

	private void reset() {
		ElkGeneralPreferences elkProtegePrefs = new ElkGeneralPreferences()
				.reset().load();
		numberOfWorkersModel_.setValue(elkProtegePrefs.numberOfWorkers);
		incrementalCheckbox_.setSelected(elkProtegePrefs.incrementalMode);
		syncCheckbox_.setEnabled(incrementalCheckbox_.isSelected());
		syncCheckbox_.setSelected(elkProtegePrefs.autoSynchronization);
	}

}
