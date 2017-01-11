package org.semanticweb.elk.protege.ui;

/*-
 * #%L
 * ELK Reasoner Protege Plug-in
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

class WarningTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -384343581021434074L;

	static final String[] COLUMN_NAMES = { "Warning", "count" };
	static final String[] COLUMN_TOOLTIPS = {
			"Warning types for which dialog messages should not be displayed",
			"The number of warning messages suppressed in this session" };

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
		return COLUMN_NAMES[col];
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
		return COLUMN_NAMES.length;
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