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
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.elk.explanations.tree.ProofTree;
import org.semanticweb.elk.explanations.tree.ProofTreeUI;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

/**
 * 
 * @author pavel
 *
 */
public class ProofFrameExplanationDisplay extends JPanel implements ExplanationDisplay, AxiomSelectionListener {

    private Explanation<OWLAxiom> explanation;
    
     public ProofFrameExplanationDisplay(OWLEditorKit editorKit, AxiomSelectionModel selectionModel, WorkbenchManager workbenchManager, OWLExpression proofRoot) {
    	ProofTree tree = new ProofTree(editorKit, proofRoot);
    	
    	setLayout(new BorderLayout());
        
    	tree.setBorder(BorderFactory.createLineBorder(Color.RED));
    	tree.setUI(new ProofTreeUI());
        add(new JScrollPane(tree), BorderLayout.CENTER);
        tree.setVisible(true);
    }

    public Explanation<OWLAxiom> getExplanation() {
        return explanation;
    }

    public void dispose() {
    }

    public void axiomAdded(AxiomSelectionModel source, OWLAxiom axiom) {
        System.out.println("SEL: " + axiom);
    }

    public void axiomRemoved(AxiomSelectionModel source, OWLAxiom axiom) {
    }
}
