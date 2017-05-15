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


import java.awt.BorderLayout;

import javax.swing.JCheckBox;

import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.elk.protege.ElkPreferences;
import org.semanticweb.elk.protege.ProtegeSuppressedMessages;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class ElkProofPreferencesPanel extends OWLPreferencesPanel {

	private static final long serialVersionUID = -4991586063619333123L;

	private JCheckBox inlineInferencesCheckbox_;

	@Override
	public void initialise() throws Exception {
		setLayout(new BorderLayout());
		PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
		add(panel, BorderLayout.NORTH);
		ElkPreferences prefs = new ElkPreferences().load();
		inlineInferencesCheckbox_ = new JCheckBox("Inline inferences",
				prefs.inlineInferences);
		inlineInferencesCheckbox_.setToolTipText(
				"If checked, try to rewrite nested inferences into one");
		panel.addGroupComponent(inlineInferencesCheckbox_);
	}

	@Override
	public void applyChanges() {
		ElkPreferences prefs = new ElkPreferences().load();
		if (inlineInferencesCheckbox_ != null) {
			prefs.inlineInferences = inlineInferencesCheckbox_.isSelected();
		}
		prefs.save();
	}

	@Override
	public void dispose() throws Exception {
		// if the reasoner is ELK and has already been created, load the
		// preferences
		OWLReasoner reasoner = getOWLModelManager().getOWLReasonerManager()
				.getCurrentReasoner();
		if (!(reasoner instanceof ElkReasoner))
			return;
		((ElkReasoner) reasoner)
				.setConfigurationOptions(ElkPreferences.getElkConfig());
		ProtegeSuppressedMessages.getInstance().reload();
	}
	
}
