/**
 * 
 */
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

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRendererSimple;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLAxiomExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpressionVisitor;
import org.semanticweb.owlapitools.proofs.expressions.OWLLemmaExpression;

/**
 * @author pavel
 *
 */
public class ProofTreeFrame extends JPanel {//extends AbstractOWLFrameSectionRow<OWLExpression, OWLAxiom, OWLAxiom>{

	private static final long serialVersionUID = 1L;
	
	private JTree tree;
	
    public ProofTreeFrame(OWLEditorKit owlEditorKit, OWLExpression proofRoot) {
        //DefaultMutableTreeNode root = new DefaultMutableTreeNode(getAxiom(proofRoot));

        //addChildNodes(root, proofRoot);
        
        //create the tree by passing in the root node
        tree = new JTree(new OWLExpressionNode(proofRoot));
        // TODO figure out how to render expressions nicely
        tree.setCellRenderer(new OWLCellRendererSimple(owlEditorKit));
        tree.addTreeWillExpandListener(new ExpansionListener());
        
        add(tree);
         
        //this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //this.setTitle("Proof tree example");       
        //this.pack();
        this.setVisible(true);
    }
    
/*    private DefaultMutableTreeNode addChildNodes(DefaultMutableTreeNode parent, OWLExpression parentExpression) {
    	// TODO cycle checking
    	try {
			for (OWLInference inf : parentExpression.getInferences()) {
				DefaultMutableTreeNode infNode = new DefaultMutableTreeNode(inf.getName()); 
				
				parent.add(infNode);
				// adding premise nodes for each inference
				for (final OWLExpression premise : inf.getPremises()) {
					DefaultMutableTreeNode premiseNode = new DefaultMutableTreeNode(getAxiom(premise)) {

						@Override
						public boolean isLeaf() {
							return premise.accept(new OWLExpressionVisitor<Boolean>() {

								@Override
								public Boolean visit(OWLAxiomExpression e) {
									return e.isAsserted();
								}

								@Override
								public Boolean visit(OWLLemmaExpression e) {
									return false;
								}
								
							});
						}
						
					};
					
					infNode.add(premiseNode);
				}
			}
		} catch (ProofGenerationException e) {
			// TODO draw some errors, perhaps as nodes?
			e.printStackTrace();
		}
    	
    	return parent;
    }
    
    private OWLAxiom getAxiom(OWLExpression expression) {
    	return expression.accept(new OWLExpressionVisitor<OWLAxiom>() {

			@Override
			public OWLAxiom visit(OWLAxiomExpression e) {
				return e.getAxiom();
			}

			@Override
			public OWLAxiom visit(OWLLemmaExpression expression) {
				// TODO return something which can be rendered nicely
				return null;
			}
    		
		});
    }*/
    
    /**
     * 
     * @author pavel
     *
     */
    private class ExpansionListener implements TreeWillExpandListener, TreeExpansionListener {

		@Override
		public void treeWillCollapse(TreeExpansionEvent event)
				throws ExpandVetoException {
			
		}

		@Override
		public void treeWillExpand(TreeExpansionEvent event)
				throws ExpandVetoException {
			TreeNode parent = (TreeNode) event.getPath().getLastPathComponent();
			
			System.err.println("will expand: " + parent + ", class: " + parent.getClass().getSimpleName());
			
		}

		@Override
		public void treeCollapsed(TreeExpansionEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void treeExpanded(TreeExpansionEvent event) {
			Object parent = event.getPath().getLastPathComponent();
			
			System.err.println("expanded: " + parent + ", class: " + parent.getClass().getSimpleName());
		}
    	
    }
}
