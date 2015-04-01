package org.semanticweb.elk.protege.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.semanticweb.elk.protege.preferences.ElkWarningPreferences;

public class ElkWarningPreferencesPanel extends ElkPanel {

	private static final long serialVersionUID = -2161290012849409729L;

	private DefaultListModel warningTypes_;

	@Override
	public ElkWarningPreferencesPanel initialize() {
		ElkWarningPreferences prefs = new ElkWarningPreferences().load();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(buildLabelComponent());
		add(buildWarningTypesComponent(prefs.suppressedWarningTypes));
		add(buildButtonsComponent());
		return this;
	}

	@Override
	public ElkWarningPreferencesPanel applyChanges() {
		ElkWarningPreferences prefs = new ElkWarningPreferences().load();
		prefs.suppressedWarningTypes = new ArrayList<String>(
				warningTypes_.size());
		for (int i = 0; i < warningTypes_.size(); i++) {
			prefs.suppressedWarningTypes.add((String) warningTypes_
					.getElementAt(i));
		}
		prefs.save();
		return this;
	}

	private Component buildLabelComponent() {
		return new JLabel("Suppressed warning types:");
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

		return listScroller;
	}

	private Component buildButtonsComponent() {
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

		return buttonPane;
	}

	private void clearSuppressedWarningTypes() {
		ElkWarningPreferences elkWarningPrefs = new ElkWarningPreferences()
				.reset().load();
		warningTypes_.clear();
		for (String warningType : elkWarningPrefs.suppressedWarningTypes) {
			warningTypes_.addElement(warningType);
		}
	}

}
