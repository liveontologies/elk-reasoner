package org.semanticweb.elk.explanations;
/*
 * #%L
 * Explanation Workbench
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;

import org.protege.editor.core.Disposable;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owl.explanation.api.ExplanationException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapitools.proofs.util.CycleFreeProofRoot;
/*
 * Copyright (C) 2008, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
@SuppressWarnings("serial")
public class ProofWorkbenchPanel extends JPanel implements Disposable, EntailmentSelectionListener, OWLModelManagerListener {

    private final OWLEditorKit editorKit;

    private final JComponent explanationDisplayHolder;

    private final JScrollPane scrollPane;

    private ProofFrameExplanationDisplay proofDisplay = null; 

    private final WorkbenchManager workbenchManager;

    // proof workbench panel for subsumption
    public ProofWorkbenchPanel(OWLEditorKit ek, OWLAxiom entailment) {
    	this(ek, new WorkbenchManager(ProofManager.getExplanationManager(ek.getOWLModelManager()), entailment));
    }
    
    // proof workbench panel for inconsistency
    public ProofWorkbenchPanel(OWLEditorKit ek) {
    	this(ek, new WorkbenchManager(ProofManager.getExplanationManager(ek.getOWLModelManager())));
    }
    
    private ProofWorkbenchPanel(OWLEditorKit ek, WorkbenchManager wbManager) {
        this.editorKit = ek;
        this.workbenchManager = wbManager;
        
        setLayout(new BorderLayout());
        editorKit.getModelManager().addListener(this);
        explanationDisplayHolder = new Box(BoxLayout.Y_AXIS);

        JPanel pan = new HolderPanel(new BorderLayout());
        pan.add(explanationDisplayHolder, BorderLayout.NORTH);
        scrollPane = new JScrollPane(pan);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(null);
        scrollPane.setOpaque(false);


        JPanel rhsPanel = new JPanel(new BorderLayout(7, 7));
        JPanel explanationListPanel = new JPanel(new BorderLayout());
        explanationListPanel.add(scrollPane);
        explanationListPanel.setMinimumSize(new Dimension(10, 10));


        JComponent headerPanel = createHeaderPanel();
        JPanel headerPanelHolder = new JPanel(new BorderLayout());
        headerPanelHolder.add(headerPanel, BorderLayout.WEST);
        explanationListPanel.add(headerPanelHolder, BorderLayout.NORTH);

        rhsPanel.add(explanationListPanel);
        add(rhsPanel);

        refill();
    }

    private JComponent createHeaderPanel() {
        GridBagLayout layout = new GridBagLayout();
        JComponent headerPanel = new JPanel(layout);

        return headerPanel;
    }


    public Dimension getMinimumSize() {
        return new Dimension(10, 10);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private class HolderPanel extends JPanel implements Scrollable {

        public HolderPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }


        public Dimension getPreferredScrollableViewportSize() {
            return super.getPreferredSize();
        }


        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 30;
        }


        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 30;
        }


        public boolean getScrollableTracksViewportWidth() {
            return true;
        }


        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void selectionChanged() {
        refill();
    }

    protected ProofFrameExplanationDisplay createProofExplanationDisplay(CycleFreeProofRoot proofRoot, boolean allProofs, String displayTitle) {
    	return new ProofFrameExplanationDisplay(editorKit, workbenchManager, proofRoot, displayTitle);    	
    }


    private void refill() {
        try {
        	if (proofDisplay != null) {
        		proofDisplay.dispose();
        	}
        	
            explanationDisplayHolder.removeAll();
            explanationDisplayHolder.validate();

            OWLAxiom entailment = workbenchManager.getEntailment();
            ProofManager proofManager = workbenchManager.getProofManager();
            
            proofDisplay = entailment == null 
            				? createProofExplanationDisplay(proofManager.getProofRootForInconsistency(), true, ProofFrameExplanationDisplay.INCONSISTENCY_TITLE)
            				: createProofExplanationDisplay(proofManager.getProofRoot(entailment), true, ProofFrameExplanationDisplay.SUBSUMPTION_TITLE);
            //GUI
            ExplanationDisplayList list = new ExplanationDisplayList(editorKit, workbenchManager, proofDisplay, 1);
            
            list.setBorder(BorderFactory.createEmptyBorder(2, 0, 10, 0));
            explanationDisplayHolder.add(list);
            scrollPane.validate();
        }
        catch (ExplanationException e) {
            ProtegeApplication.getErrorLog().logError(e);
        }
    }
    
    private void updateProofRoot() {
    	OWLAxiom entailment = workbenchManager.getEntailment();
        ProofManager proofManager = workbenchManager.getProofManager();
        CycleFreeProofRoot root = entailment == null ? proofManager.getProofRootForInconsistency() : proofManager.getProofRoot(entailment);
        
        proofDisplay.update(root);
        scrollPane.validate();
    }

    @Override
    public void dispose() {
		// nice, but Protege won't call this because OWLModelManager.dispose()
		// doesn't call dispose on ExplanationManager which would let all loaded
		// explanation plugins dispose of their resources
        editorKit.getModelManager().removeListener(this);
        
    	if (proofDisplay != null) {
    		proofDisplay.dispose();
    	}
    }


    public void handleChange(OWLModelManagerChangeEvent event) {
    	switch (event.getType()) {
		case ONTOLOGY_CLASSIFIED:
			// update the proof model
			if (proofDisplay != null) {
				updateProofRoot();
			}
			else {
				refill();
			}
			
			break;
		// TODO handle also ontology change and reload events	
		default:
			break;
		
		}
    }


    @Override
    public Dimension getPreferredSize() {
        Dimension workspaceSize = editorKit.getWorkspace().getSize();
        int width = (int) (workspaceSize.getWidth() * 0.8);
        int height = (int) (workspaceSize.getHeight() * 0.7);
        return new Dimension(width, height);
    }
}
