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
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Optional;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.protege.editor.core.plugin.PluginUtilities;
import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;
import org.protege.editor.core.update.PluginDocumentParseException;
import org.protege.editor.core.update.PluginInfo;
import org.protege.editor.core.update.PluginInfoDocumentParser;
import org.protege.editor.core.update.PluginInstaller;
import org.protege.editor.core.update.PluginRegistryImpl;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.elk.protege.ElkPreferences;
import org.semanticweb.elk.protege.ProtegeSuppressedMessages;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElkProofPreferencesPanel extends OWLPreferencesPanel {

	// TODO: get those values from the dependencies
	private static final String PROOF_PLUGIN_NAME_ = "Protege Proof-Based Explanation",
			PROOF_PLUGIN_UPDATE_URL_ = "https://raw.githubusercontent.com/liveontologies/protege-proof-explanation/release/p5.update.properties";

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ElkPreferencesPanel.class);

	private JCheckBox inlineInferencesCheckbox_;

	@Override
	public void initialise() throws Exception {
		setLayout(new BorderLayout());
		PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
		add(panel, BorderLayout.NORTH);
		ElkPreferences prefs = new ElkPreferences().load();

		panel.addGroup("Proof-based explanations");
		if (getProofPluginBundle() == null) {
			final JButton button = new JButton();
			button.addActionListener(new AbstractAction() {
				private static final long serialVersionUID = 8292735589343462276L;

				@Override
				public void actionPerformed(ActionEvent e) {
					installPlugin(button);
				}
			});
			button.setText("Install Plugin");
			button.setToolTipText("Install the " + PROOF_PLUGIN_NAME_
					+ " plugin to display proofs provided by ELK");
			panel.addGroupComponent(button);
		} else {
			inlineInferencesCheckbox_ = new JCheckBox("Inline inferences",
					prefs.inlineInferences);
			inlineInferencesCheckbox_.setToolTipText(
					"If checked, try to rewrite nested inferences into one");
			panel.addGroupComponent(inlineInferencesCheckbox_);
		}
		panel.addSeparator();
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

	private Bundle getProofPluginBundle() {
		BundleContext context = PluginUtilities.getInstance()
				.getApplicationContext();
		Bundle[] bundles = context.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			Bundle bundle = bundles[i];
			String updateLocation = (String) bundle.getHeaders()
					.get(PluginRegistryImpl.UPDATE_URL);
			if (updateLocation == null) {
				continue;
			}
			if (updateLocation.equals(PROOF_PLUGIN_UPDATE_URL_)) {
				return bundle;
			}
		}
		// not found
		return null;
	}

	private void installPlugin(JButton button) {
		try {
			final PluginInfoDocumentParser pluginInfoDocumentParser = new PluginInfoDocumentParser(
					new URL(PROOF_PLUGIN_UPDATE_URL_));
			PluginInfo info = pluginInfoDocumentParser
					.parseDocument(Optional.<Bundle> empty());
			PluginInstaller installer = new PluginInstaller(
					Collections.singletonList(info));
			installer.run();
			button.setEnabled(false);
		} catch (PluginDocumentParseException e) {
			JOptionPane.showMessageDialog(this,
					"<html><body width='350'>" + e.getMessage()
							+ "</body></html>",
					"Plugin Installation error", JOptionPane.ERROR_MESSAGE);
		} catch (MalformedURLException e) {
			// this should not happen
			LOGGER_.error(ElkPreferences.MARKER,
					"The update URL for {} plugin is malformed: {}",
					PROOF_PLUGIN_NAME_, e);
		}
	}
	
}
