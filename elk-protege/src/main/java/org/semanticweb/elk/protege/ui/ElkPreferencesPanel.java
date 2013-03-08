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

import org.protege.editor.owl.model.inference.ProtegeOWLReasonerInfo;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.elk.protege.ElkProtegeConfigurationUtils;
import org.semanticweb.elk.protege.ProtegeReasonerFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
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

	private JSpinner nwSpinner;

	private JCheckBox incCheckbox_;

	@Override
	public void initialise() throws Exception {
		// Create a simple JPanel with the ELK's settings
		// pre-populate with default settings
		ReasonerConfiguration elkConfig = ElkProtegeConfigurationUtils
				.loadConfiguration();

		setLayout(new BorderLayout());

		JPanel numOfWorkersPanel = new JPanel(new GridBagLayout());
    	GridBagConstraints c = new GridBagConstraints();
    	int gridybase = 0;
		
		final int numOfWorkers = elkConfig
				.getParameterAsInt(ReasonerConfiguration.NUM_OF_WORKING_THREADS);
		nwSpinner = new JSpinner(
				new SpinnerNumberModel(numOfWorkers, 1, 100, 1));
		gridybase = buildNumOfWorkers(numOfWorkersPanel, c, gridybase);

		final boolean incrementalModeAllowed = elkConfig
				.getParameterAsBoolean(ReasonerConfiguration.INCREMENTAL_MODE_ALLOWED);

		incCheckbox_ = new JCheckBox("", incrementalModeAllowed);
		
		gridybase = buildIncrementalModeAllowed(numOfWorkersPanel, c, gridybase);

		Box mainPanel = new Box(BoxLayout.PAGE_AXIS);
		mainPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Reasoner settings"),
				BorderFactory.createEmptyBorder(7, 7, 7, 7)));
		mainPanel.add(numOfWorkersPanel);
		mainPanel.setAlignmentX(LEFT_ALIGNMENT);

		Box holder = new Box(BoxLayout.PAGE_AXIS);

		holder.add(mainPanel);
		add(holder, BorderLayout.NORTH);

		applyChanges();
	}
	
	private int buildNumOfWorkers(JPanel panel, GridBagConstraints c, int gridybase) {
        c.gridx = 0;
        c.gridy = ++gridybase;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0,0,0,12);
    	c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 0.0; 
        panel.add((new JLabel("Number of working threads:")), c);
        
        c.gridx = 1;
        c.gridy = gridybase;
        c.insets = new Insets(0,0,5,0);
        c.weightx = 1.0;
        panel.add(nwSpinner, c);

        return gridybase;
    }	
	
	
	private int buildIncrementalModeAllowed(JPanel panel, GridBagConstraints c, int gridybase) {
        c.gridx = 0;
        c.gridy = ++gridybase;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0,0,0,12);
    	c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 0.0; 
        panel.add((new JLabel("<html>Allow incremental reasoning<br/>(this feature is experimental)</html>")), c);
        
        c.gridx = 1;
        c.gridy = gridybase;
        c.insets = new Insets(0,0,5,0);
        c.weightx = 1.0;
        panel.add(incCheckbox_, c);
        
        return gridybase;
    }
		
	@Override
	public void applyChanges() {
		ReasonerConfiguration elkConfig = getCurrentReasonerConfiguration();

		if (elkConfig != null) {
			elkConfig.setParameter(
					ReasonerConfiguration.NUM_OF_WORKING_THREADS, nwSpinner
							.getValue().toString());
			elkConfig.setParameter(
					ReasonerConfiguration.INCREMENTAL_MODE_ALLOWED,
					String.valueOf(incCheckbox_.isSelected()));
		}
		// if the reasoner is ELK and has already been created
		OWLReasoner reasoner = getOWLModelManager().getOWLReasonerManager().getCurrentReasoner();
		
		if (reasoner instanceof ElkReasoner) {
			((ElkReasoner)reasoner).setConfigurationOptions(elkConfig);
		}
	}

	@Override
	public void dispose() throws Exception {
		ElkProtegeConfigurationUtils
				.saveConfiguration(getCurrentReasonerConfiguration());
	}

	private ReasonerConfiguration getCurrentReasonerConfiguration() {
		ProtegeOWLReasonerInfo reasonerInfo = getOWLModelManager()
				.getOWLReasonerManager().getCurrentReasonerFactory();

		if (!(reasonerInfo instanceof ProtegeReasonerFactory)) {
			// TODO Log it?
			return null;
		}
		// pass the settings to the factory
		ProtegeReasonerFactory elkFactory = (ProtegeReasonerFactory) reasonerInfo;

		return elkFactory.getElkConfiguration();
	}
}
