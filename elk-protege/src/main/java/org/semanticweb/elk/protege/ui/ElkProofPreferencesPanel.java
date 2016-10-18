package org.semanticweb.elk.protege.ui;

/*-
 * #%L
 * ELK Reasoner Protege Plug-in
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import org.semanticweb.elk.protege.preferences.ElkProofPreferences;

public class ElkProofPreferencesPanel extends ElkPanel {

	private static final long serialVersionUID = 358149648550711367L;

	private JCheckBox flattenInferencesCheckbox_;

	@Override
	public ElkPanel initialize() {
		ElkProofPreferences prefs = new ElkProofPreferences().load();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(buildFlattenInferencesComponent(prefs.flattenInferences));
		add(Box.createVerticalGlue());
		add(buildResetComponent());
		return this;
	}

	@Override
	public ElkPanel applyChanges() {
		ElkProofPreferences prefs = new ElkProofPreferences().load();
		prefs.flattenInferences = flattenInferencesCheckbox_.isSelected();
		prefs.save();
		return this;
	}

	private Component buildFlattenInferencesComponent(boolean incrementalMode) {
		flattenInferencesCheckbox_ = new JCheckBox("Flatten inferences",
				incrementalMode);
		flattenInferencesCheckbox_.setToolTipText(
				"If checked, nested inferences will be rewritten into one, if possible");
		return flattenInferencesCheckbox_;
	}

	private Component buildResetComponent() {
		JButton resetButton = new JButton(new AbstractAction() {
			private static final long serialVersionUID = 8292735589343462276L;

			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		resetButton.setText("Reset");
		resetButton.setToolTipText(
				"Resets all ELK proof settings to default values");

		return resetButton;
	}

	private void reset() {
		ElkProofPreferences prefs = new ElkProofPreferences().reset();
		flattenInferencesCheckbox_.setSelected(prefs.flattenInferences);
	}

}
