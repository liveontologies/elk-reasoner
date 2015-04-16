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
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import org.semanticweb.elk.protege.ProtegeSuppressedMessages;
import org.semanticweb.elk.protege.preferences.ElkWarningPreferences;

public class ElkWarningPreferencesPanel extends ElkPanel {

	private static final long serialVersionUID = -2161290012849409729L;

	private WarningTableModel warningTypes_;
	private ListSelectionModel warningSelection_;

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
		ProtegeSuppressedMessages suppressedMessages = ProtegeSuppressedMessages
				.getInstance().reload();
		warningTypes_ = new WarningTableModel();
		JTable table = new JTable(warningTypes_);
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
		JButton removeButton = new JButton(new AbstractAction() {
			private static final long serialVersionUID = 7125300829305229857L;

			@Override
			public void actionPerformed(ActionEvent e) {
				warningTypes_.removeSelectedRows(warningSelection_);
			}
		});
		removeButton.setText("Remove");
		buttonPane.add(clearButton);
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(removeButton);

		return buttonPane;
	}

	private static class WarningTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -384343581021434074L;

		private static final String[] COLUMN_NAMES_ = {
				"Supprssed warning type", "count" };

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
