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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Optional;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.protege.editor.core.plugin.PluginUtilities;
import org.protege.editor.core.update.PluginDocumentParseException;
import org.protege.editor.core.update.PluginInfo;
import org.protege.editor.core.update.PluginInfoDocumentParser;
import org.protege.editor.core.update.PluginInstaller;
import org.protege.editor.core.update.PluginRegistryImpl;
import org.semanticweb.elk.protege.preferences.ElkPreferences;
import org.semanticweb.elk.protege.preferences.ElkProofPreferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElkProofPreferencesPanel extends ElkPanel {

	private static final long serialVersionUID = 358149648550711367L;

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ElkProofPreferencesPanel.class);

	// TODO: get those values from the dependencies
	private static final String PROOF_PLUGIN_NAME_ = "Protege Proof-Based Explanation";

	private static final String PROOF_PLUGIN_UPDATE_URL_ = "https://raw.githubusercontent.com/liveontologies/protege-proof-explanation/release/p5.update.properties";

	private JCheckBox flattenInferencesCheckbox_;

	@Override
	public ElkPanel initialize() {
		ElkProofPreferences prefs = new ElkProofPreferences().load();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		if (getProofPluginBundle() == null) {
			add(buildInstallPluginComponent());
		} else {
			add(buildFlattenInferencesComponent(prefs.flattenInferences));			
		}
		add(Box.createVerticalGlue());
		add(buildResetComponent());
		return this;
	}

	@Override
	public ElkPanel applyChanges() {
		ElkProofPreferences prefs = new ElkProofPreferences().load();
		if (flattenInferencesCheckbox_ != null) {
			prefs.flattenInferences = flattenInferencesCheckbox_.isSelected();
		}
		prefs.save();
		return this;
	}

	private Component buildFlattenInferencesComponent(boolean incrementalMode) {
		flattenInferencesCheckbox_ = new JCheckBox("Flatten inferences",
				incrementalMode);
		flattenInferencesCheckbox_.setToolTipText(
				"If checked, nested inferences will be rewritten into one, if possible (could be slow)");
		return flattenInferencesCheckbox_;
	}

	private Component buildInstallPluginComponent() {
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

		return button;
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
