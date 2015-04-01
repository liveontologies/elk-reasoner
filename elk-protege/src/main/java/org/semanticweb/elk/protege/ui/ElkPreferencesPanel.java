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

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;

import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.elk.protege.ElkProtegeLogAppender;
import org.semanticweb.elk.protege.ProtegeMessageAppender;
import org.semanticweb.elk.protege.preferences.ElkGeneralPreferences;
import org.semanticweb.elk.protege.preferences.ElkLogPreferences;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * UI panel for setting preferences for ELK
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class ElkPreferencesPanel extends OWLPreferencesPanel {

	private static final long serialVersionUID = -5568211860560307648L;

	private ElkPanel generalPrefsPane_, warningPrefsPane_, logPane_;

	@Override
	public void initialise() throws Exception {
		JTabbedPane tabbedPane = new JTabbedPane();

		tabbedPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("ELK reasoner settings"),
				BorderFactory.createEmptyBorder(7, 7, 7, 7)));

		generalPrefsPane_ = new ElkGeneralPreferencesPanel().initialize();
		tabbedPane.addTab("General", null, generalPrefsPane_,
				"General ELK settings");
		warningPrefsPane_ = new ElkWarningPreferencesPanel().initialize();
		tabbedPane.addTab("Warnings", null, warningPrefsPane_,
				"Settings for ELK warning messages");
		logPane_ = new ElkLogPreferencesPanel().initialize();
		tabbedPane.addTab("Log", null, logPane_,
				"Settings for ELK log messages");

		setLayout(new BorderLayout());

		add(tabbedPane, BorderLayout.NORTH);

	}

	@Override
	public void applyChanges() {
		generalPrefsPane_.applyChanges();
		warningPrefsPane_.applyChanges();
		logPane_.applyChanges();
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

		ElkProtegeLogAppender.getInstance().reloadLogLevel();
	}

}
