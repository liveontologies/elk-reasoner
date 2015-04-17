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
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;

import org.semanticweb.elk.protege.ProtegeSuppressedMessages;
import org.semanticweb.elk.protege.preferences.ElkWarningPreferences;

public class ElkWarningPreferencesPanel extends ElkPanel {

	private static final long serialVersionUID = -2161290012849409729L;

	private WarningTableModel warningTypes_;
	private ListSelectionModel warningSelection_;
	private JCheckBox suppressAllWarningsCheckbox_;

	private static final String[] COLUMN_NAMES_ = { "Suppressed warning types",
			"counts" };
	private static final String[] COLUMN_TOOLTIPS_ = {
			"Messages for these warning types will not be displayed",
			"The number of warning messages suppressed in this session" };

	@Override
	public ElkWarningPreferencesPanel initialize() {
		ElkWarningPreferences prefs = new ElkWarningPreferences().load();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(buildWarningTypesComponent(prefs.suppressedWarningTypes));
		add(buildButtonsComponent());
		add(buildIgnoreAllWarningsComponent(prefs.suppressAllWarnings));
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
		prefs.suppressAllWarnings = suppressAllWarningsCheckbox_.isSelected();
		prefs.save();
		return this;
	}

	private Component buildWarningTypesComponent(
			List<String> suppressedWarningTypes) {
		ProtegeSuppressedMessages suppressedMessages = ProtegeSuppressedMessages
				.getInstance().reload();
		warningTypes_ = new WarningTableModel();
		JTable table = new JTable(warningTypes_) {
			// Implement table header tool tips.
			@Override
			protected JTableHeader createDefaultTableHeader() {
				return new JTableHeader(columnModel) {
					@Override
					public String getToolTipText(MouseEvent e) {
						java.awt.Point p = e.getPoint();
						int index = columnModel.getColumnIndexAtX(p.x);
						int realIndex = columnModel.getColumn(index)
								.getModelIndex();
						return COLUMN_TOOLTIPS_[realIndex];
					}
				};
			}
		};
		warningSelection_ = table.getSelectionModel();
		table.getColumnModel().getColumn(1).setMaxWidth(50);
		for (String warningType : suppressedWarningTypes) {
			warningTypes_.addWarningType(warningType,
					suppressedMessages.getCount(warningType));
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
				warningTypes_.clear();
			}
		});
		clearButton.setText("Clear");
		clearButton.setToolTipText("Remove all suppressed warning types");
		JButton removeButton = new JButton(new AbstractAction() {
			private static final long serialVersionUID = 7125300829305229857L;

			@Override
			public void actionPerformed(ActionEvent e) {
				warningTypes_.removeSelectedRows(warningSelection_);
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

		return buttonPane;
	}

	private Component buildIgnoreAllWarningsComponent(boolean ignoreAllWarnings) {
		JPanel pane = new JPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.LINE_AXIS));
		pane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		suppressAllWarningsCheckbox_ = new JCheckBox("Suppress all warnings",
				ignoreAllWarnings);
		suppressAllWarningsCheckbox_
				.setToolTipText("If checked, all ELK warnings will be silently ignored; the counters will be included in the table above");
		pane.add(suppressAllWarningsCheckbox_);
		pane.add(Box.createHorizontalGlue());
		return pane;
	}

	private static class WarningTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -384343581021434074L;

		private final List<String> warningTypes_ = new ArrayList<String>();

		private final List<Integer> warningCounts_ = new ArrayList<Integer>();

		public void addWarningType(String warningType, int warningCount) {
			warningTypes_.add(warningType);
			warningCounts_.add(warningCount);
			int lastRow = warningTypes_.size();
			fireTableRowsInserted(lastRow, lastRow);
		}

		public void clear() {
			int lastRow = warningTypes_.size();
			warningTypes_.clear();
			warningCounts_.clear();
			fireTableRowsDeleted(0, lastRow);
		}

		public void resetCounts() {
			int lastRow = warningTypes_.size();
			for (int i = 0; i < lastRow; i++) {
				warningCounts_.set(i, 0);
			}
			fireTableRowsUpdated(0, lastRow);
		}

		public void removeSelectedRows(ListSelectionModel selection) {
			int first = selection.getMinSelectionIndex();
			if (first < 0)
				return;
			int last = selection.getMaxSelectionIndex();
			for (int i = last; i >= first; i--) {
				if (!selection.isSelectedIndex(i))
					continue;
				warningTypes_.remove(i);
				warningCounts_.remove(i);
			}
			fireTableRowsDeleted(first, last);
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
		public Class<?> getColumnClass(int col) {
			switch (col) {
			case 0:
				return String.class;
			case 1:
				return Integer.class;
			default:
				throw new IllegalArgumentException("Column value out of bounds");
			}
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
