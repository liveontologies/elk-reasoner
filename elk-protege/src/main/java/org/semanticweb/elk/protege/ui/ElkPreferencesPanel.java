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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.elk.protege.ElkProtegePreferences;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * UI panel for setting preferences for ELK
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ElkPreferencesPanel extends OWLPreferencesPanel {

	private static final long serialVersionUID = -5568211860560307648L;

	private JSpinner nwSpinner_;

	private JCheckBox incCheckbox_;

	private JCheckBox syncCheckbox_;

	@Override
	public void initialise() throws Exception {
		// Create a simple JPanel with the ELK's settings
		ElkProtegePreferences elkProtegePrefs = new ElkProtegePreferences()
				.load();

		setLayout(new BorderLayout());

		JPanel numOfWorkersPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		int gridybase = 0;

		nwSpinner_ = new JSpinner(new SpinnerNumberModel(
				elkProtegePrefs.numberOfWorkers, 1, 100, 1));
		gridybase = buildNumOfWorkers(numOfWorkersPanel, c, gridybase);

		incCheckbox_ = new JCheckBox("", elkProtegePrefs.incrementalMode);

		gridybase = buildIncrementalModeAllowed(numOfWorkersPanel, c, gridybase);

		syncCheckbox_ = new JCheckBox("", !elkProtegePrefs.bufferringMode);

		gridybase = buildBufferringMode(numOfWorkersPanel, c, gridybase);

		Box mainPanel = new Box(BoxLayout.PAGE_AXIS);
		mainPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("ELK reasoner settings"),
				BorderFactory.createEmptyBorder(7, 7, 7, 7)));
		mainPanel.add(numOfWorkersPanel);
		mainPanel.setAlignmentX(LEFT_ALIGNMENT);

		Box holder = new Box(BoxLayout.PAGE_AXIS);

		holder.add(mainPanel);
		add(holder, BorderLayout.NORTH);

		applyChanges();
	}

	private int buildNumOfWorkers(JPanel panel, GridBagConstraints c,
			int gridybase) {
		c.gridx = 0;
		c.gridy = ++gridybase;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0, 0, 0, 12);
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 0.0;
		JLabel name = new JLabel("Number of working threads:");
		String description = "The number of threads that ELK can use for performing parallel computations.";
		panel.add(name, c);
		name.setToolTipText(description);
		nwSpinner_.setToolTipText(description);

		c.gridx = 1;
		c.gridy = gridybase;
		c.insets = new Insets(0, 0, 5, 0);
		c.weightx = 1.0;
		panel.add(nwSpinner_, c);

		return gridybase;
	}

	private int buildIncrementalModeAllowed(JPanel panel, GridBagConstraints c,
			int gridybase) {
		c.gridx = 0;
		c.gridy = ++gridybase;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0, 0, 0, 12);
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 0.0;
		JLabel name = new JLabel("Incremental reasoning");
		String description = "ELK tries to recompute only the results caused by the changes in the ontology";
		panel.add(name, c);
		name.setToolTipText(description);
		incCheckbox_.setToolTipText(description);

		c.gridx = 1;
		c.gridy = gridybase;
		c.insets = new Insets(0, 0, 5, 0);
		c.weightx = 1.0;
		panel.add(incCheckbox_, c);
		panel.add((new JLabel(
				)), c);

		return gridybase;
	}

	private int buildBufferringMode(JPanel panel, GridBagConstraints c,
			int gridybase) {
		c.gridx = 0;
		c.gridy = ++gridybase;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0, 0, 0, 12);
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 0.0;
		JLabel name = new JLabel("<html>Auto-syncronization<br/>(requires reasoner restart)</html>");
		String description = "ELK will be always in sync with the ontology";
		panel.add(name, c);
		name.setToolTipText(description);
		syncCheckbox_.setToolTipText(description);

		c.gridx = 1;
		c.gridy = gridybase;
		c.insets = new Insets(0, 0, 5, 0);
		c.weightx = 1.0;
		panel.add(syncCheckbox_, c);

		return gridybase;
	}

	@Override
	public void applyChanges() {
		ElkProtegePreferences elkProtegePrefs = new ElkProtegePreferences()
				.load();

		elkProtegePrefs.numberOfWorkers = Integer.parseInt(nwSpinner_
				.getValue().toString());
		elkProtegePrefs.incrementalMode = incCheckbox_.isSelected();
		elkProtegePrefs.bufferringMode = !syncCheckbox_.isSelected();

		elkProtegePrefs.save();

		// if the reasoner is ELK and has already been created, load the
		// preferences
		OWLReasoner reasoner = getOWLModelManager().getOWLReasonerManager()
				.getCurrentReasoner();

		if (reasoner instanceof ElkReasoner) {
			((ElkReasoner) reasoner).setConfigurationOptions(elkProtegePrefs
					.getElkConfig());
		}
	}

	@Override
	public void dispose() throws Exception {
		// nothing to do
	}

}
