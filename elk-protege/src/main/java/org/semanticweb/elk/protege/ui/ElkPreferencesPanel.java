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
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.protege.editor.owl.model.inference.ProtegeOWLReasonerInfo;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.semanticweb.elk.protege.ProtegeReasonerFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;

/**
 * UI panel for setting preferences for ELK
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ElkPreferencesPanel extends OWLPreferencesPanel {

	private static final long serialVersionUID = -5568211860560307648L;
	
	private JSpinner nwSpinner;

	@Override
	public void initialise() throws Exception {
		//Create a simple JPanel with the ELK's settings
		//pre-populate with default settings
		ReasonerConfiguration elkConfig = ReasonerConfiguration.getConfiguration();
		
		setLayout(new BorderLayout());
        
		JPanel numOfWorkersPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        final int numOfWorkers = elkConfig.getParameterAsInt(ReasonerConfiguration.NUM_OF_WORKING_THREADS);
        nwSpinner = new JSpinner(new SpinnerNumberModel(numOfWorkers, 1, 100, 1));
        numOfWorkersPanel.add(new JLabel("Number of working threads"));
        numOfWorkersPanel.add(nwSpinner);

        Box mainPanel = new Box(BoxLayout.PAGE_AXIS);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Reasoner settings"),
                                                                 BorderFactory.createEmptyBorder(7, 7, 7, 7)));
        mainPanel.add(numOfWorkersPanel);
        mainPanel.setAlignmentX(LEFT_ALIGNMENT);
        
        Box holder = new Box(BoxLayout.PAGE_AXIS);
        
        holder.add(mainPanel);
        add(holder, BorderLayout.NORTH);
	}

	@Override
	public void applyChanges() {
		ProtegeOWLReasonerInfo reasonerInfo = getOWLModelManager().getOWLReasonerManager().getCurrentReasonerFactory();
		
		if (!(reasonerInfo instanceof ProtegeReasonerFactory)) {
			//TODO Log it?
			return;
		}
		//pass the settings to the factory
		ProtegeReasonerFactory elkFactory = (ProtegeReasonerFactory) reasonerInfo;
		ReasonerConfiguration elkConfig = elkFactory.getElkConfiguration();
		
		elkConfig.setParameter(ReasonerConfiguration.NUM_OF_WORKING_THREADS, nwSpinner.getValue().toString());
	}

	@Override
	public void dispose() throws Exception {}	
}
