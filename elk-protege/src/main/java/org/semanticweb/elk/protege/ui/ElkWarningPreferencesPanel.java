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
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.semanticweb.elk.protege.preferences.ElkWarningPreferences;

public class ElkWarningPreferencesPanel extends ElkPanel {

	private static final long serialVersionUID = -2161290012849409729L;

	private WarningTableModel warningTypes_;

	@Override
	public ElkWarningPreferencesPanel initialize() {
		ElkWarningPreferences prefs = new ElkWarningPreferences().load();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(buildWarningTypesComponent(prefs.suppressedWarningTypes));
		add(buildButtonsComponent());
		return this;
	}

	@Override
	public ElkWarningPreferencesPanel applyChanges() {
		ElkWarningPreferences prefs = new ElkWarningPreferences().load();
		prefs.suppressedWarningTypes = new ArrayList<String>(
				warningTypes_.getRowCount());
		for (int i = 0; i < warningTypes_.getRowCount(); i++) {
			prefs.suppressedWarningTypes.add(warningTypes_.getWarningTypeAt(i));
		}
		prefs.save();
		return this;
	}

	private Component buildWarningTypesComponent(
			List<String> suppressedWarningTypes) {
		warningTypes_ = new WarningTableModel();
		JTable table = new JTable(warningTypes_);
		table.getColumnModel().getColumn(1).setMaxWidth(50);
		for (String warningType : suppressedWarningTypes) {
			warningTypes_.addWarningType(warningType);
		}
		JScrollPane tableScroller = new JScrollPane(table);
		tableScroller.setPreferredSize(new Dimension(300, 100));
		return tableScroller;
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
			warningTypes_.addWarningType(warningType);
		}
	}

	private static class WarningTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -384343581021434074L;

		private static final String[] COLUMN_NAMES_ = {
				"Supprssed warning type", "count" };

		private final List<String> warningTypes_ = new ArrayList<String>();

		private final List<Integer> warningCounts_ = new ArrayList<Integer>();

		public void addWarningType(String warningType) {
			warningTypes_.add(warningType);
			warningCounts_.add(0);
			int newRow = warningType.length();
			fireTableRowsInserted(newRow, newRow);
		}

		public void clear() {
			int lastRow = warningTypes_.size();
			warningTypes_.clear();
			warningCounts_.clear();
			fireTableRowsDeleted(0, lastRow);
		}

		public String getWarningTypeAt(int row) {
			return warningTypes_.get(row);
		}

		public int getWarningCountAt(int row) {
			return warningCounts_.get(row);
		}

		@Override
		public String getColumnName(int col) {
			return COLUMN_NAMES_[col];
		}

		@Override
		public int getColumnCount() {
			return COLUMN_NAMES_.length;
		}

		@Override
		public int getRowCount() {
			return warningCounts_.size();
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}

		@Override
		public Object getValueAt(int row, int col) {
			switch (col) {
			case 0:
				return warningTypes_.get(row);
			case 1:
				return warningCounts_.get(row);
			default:
				throw new IllegalArgumentException("Column value out of bounds");
			}
		}

	}

}
