package org.semanticweb.elk.explanations.tree;
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

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;


/**
 * 
 */
public class ProofTree extends JTree {//extends OWLLinkedObjectTree {

    private static final long serialVersionUID = 2978742855867968571L;
    
    public ProofTree(OWLEditorKit owlEditorKit, OWLExpression proofRoot) {
        //super(owlEditorKit);
        
        TreeModel model = new DefaultTreeModel(new OWLExpressionNode(proofRoot));
        
        setModel(model);
        setCellRenderer(new ProofNodeRenderer(owlEditorKit));
        setRowHeight(-1);
        
        addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
		    	if (isVisible()) {
		    		registerUI();
		    	}
			}
        	
        });
    }
    
	public void registerUI() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setUI(new ProofTreeUI());
			}
		});
	}
	   
}
