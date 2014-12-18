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

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.elk.explanations.list.ProofFrame;
import org.semanticweb.elk.explanations.list.ProofFrameList;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

/**
 * 
 * @author pavel
 *
 */
@SuppressWarnings("serial")
public class ProofFrameExplanationDisplay extends JPanel implements ExplanationDisplay, AxiomSelectionListener {

    /*private Explanation<OWLAxiom> explanation;
    
     public ProofFrameExplanationDisplay(OWLEditorKit editorKit, AxiomSelectionModel selectionModel, WorkbenchManager workbenchManager, OWLExpression proofRoot) {
    	ProofTree tree = new ProofTree(editorKit, proofRoot);
    	
    	setLayout(new BorderLayout());
        
    	tree.setUI(new ProofTreeUI());
        add(new JScrollPane(tree), BorderLayout.CENTER);
        tree.setVisible(true);
    	add(new ProofTreeFrame(editorKit, proofRoot));
    }

    public Explanation<OWLAxiom> getExplanation() {
        return explanation;
    }

    public void dispose() {
    }

    public void axiomAdded(AxiomSelectionModel source, OWLAxiom axiom) {
    }

    public void axiomRemoved(AxiomSelectionModel source, OWLAxiom axiom) {
    }*/
	
	

    //private Explanation<OWLAxiom> explanation;
    
    private ProofFrame frame;

    private final ProofFrameList frameList;

    private AxiomSelectionModel axiomSelectionModel;
    
    private boolean transmittingSelectionToModel = false;

    public ProofFrameExplanationDisplay(OWLEditorKit editorKit, AxiomSelectionModel selectionModel, WorkbenchManager workbenchManager, OWLExpression root) {
        this.axiomSelectionModel = selectionModel;
        
        frame = new ProofFrame(editorKit, root);
        setLayout(new BorderLayout());
        frameList =  new ProofFrameList(editorKit, selectionModel, workbenchManager, frame);
        
        add(frameList, BorderLayout.NORTH);
        
        frame.setRootObject(root);
        frameList.setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));

        frameList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                transmitSelectionToModel();
            }
        });

        axiomSelectionModel.addAxiomSelectionListener(new AxiomSelectionListener() {
            public void axiomAdded(AxiomSelectionModel source, OWLAxiom axiom) {
                respondToAxiomSelectionChange();
            }

            public void axiomRemoved(AxiomSelectionModel source, OWLAxiom axiom) {
                respondToAxiomSelectionChange();
            }
        });
    }

    private void respondToAxiomSelectionChange() {
        if(!transmittingSelectionToModel) {
            frameList.clearSelection();
            frameList.repaint(frameList.getVisibleRect());
        }
        frameList.repaint(frameList.getVisibleRect());
    }


    private void transmitSelectionToModel() {
        try {
            transmittingSelectionToModel = true;
            for(int i = 1; i < frameList.getModel().getSize(); i++) {
                Object element = frameList.getModel().getElementAt(i);
                if(element instanceof JustificationFrameSectionRow) {
                    JustificationFrameSectionRow row = (JustificationFrameSectionRow) element;
                    OWLAxiom ax = row.getAxiom();
                    axiomSelectionModel.setAxiomSelected(ax, frameList.isSelectedIndex(i));
                }
            }
        }
        finally {
            transmittingSelectionToModel = false;
        }
    }

    public Explanation<OWLAxiom> getExplanation() {
    	// TODO get all used axioms?
        return null;
    }

    public void dispose() {
        frame.dispose();
    }

    public void axiomAdded(AxiomSelectionModel source, OWLAxiom axiom) {
        System.out.println("SEL: " + axiom);
    }

    public void axiomRemoved(AxiomSelectionModel source, OWLAxiom axiom) {
    }	
}
