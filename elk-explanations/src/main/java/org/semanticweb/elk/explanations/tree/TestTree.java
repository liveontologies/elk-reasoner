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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLAxiomExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpressionVisitor;
import org.semanticweb.owlapitools.proofs.expressions.OWLLemmaExpression;
 
public class TestTree extends ComponentAdapter {
    JTree tree;
 
    public void componentResized(ComponentEvent e) {
        /*if(tree.isVisible()) {
            registerUI();
        }*/
    	registerUI();
    	//tree.updateUI();
    }
 
    private JScrollPane getContent() {
        tree = getTree();
        tree.setRowHeight(0);
        tree.addComponentListener(this);
        
        return new JScrollPane(tree);
    }
 
    private JTree getTree() {
        /*DefaultMutableTreeNode root = new DefaultMutableTreeNode(getNode("root"));
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(getNode("Node 1"));
        root.insert(node, 0);
        DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(getNode("Node 11"));
        node.insert(subNode, 0);
        node = new DefaultMutableTreeNode(getNode("Node 2 (test test test test test test test test)"));
        root.insert(node, 1);
        subNode = new DefaultMutableTreeNode(getNode("Node 21"));
        node.insert(subNode, 0);
        subNode = new DefaultMutableTreeNode(getNode("Node 22"));
        node.insert(subNode, 1);
        subNode = new DefaultMutableTreeNode(getNode("Node 23"));
        node.insert(subNode, 2);
        
        JTree tree = new JTree();
        
        tree.setModel(new DefaultTreeModel(root));
        
        return tree;*/
    	
    	JTree tree = new JTree();
        
        tree.setModel(new DefaultTreeModel(getNode("root")));
        
        return tree;
    }
 
    private PanelNode getNode(String s) {
        return new PanelNode(s, Color.black, Color.pink, null);
    }
 
    private void registerUI() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                tree.setUI(new BasicWideNodeTreeUI());
            }
        });
    }
 
    public static void main(String[] args) {
    	TestTree test = new TestTree();
        JFrame f = new JFrame();
        
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(test.getContent());
        f.setSize(400,400);
        f.setLocation(200,200);
        f.setVisible(true);
        
        test.registerUI();
    }
    
    /**
     * Code copied/adapted from BasicTreeUI source code.
     */
    class BasicWideNodeTreeUI extends BasicTreeUI {
     
        protected TreeCellRenderer createDefaultCellRenderer() {
            return new PanelNodeRenderer();
        }
     
        @Override
        protected AbstractLayoutCache.NodeDimensions createNodeDimensions() {
            return new NodeDimensionsHandler() {
            	
                @Override
                public Rectangle getNodeDimensions(Object value, int row, int depth, boolean expanded, Rectangle size) {
                    Rectangle dimensions = super.getNodeDimensions(value, row, depth, expanded, size);
                    
                    dimensions.width = tree.getParent().getWidth() - getRowX(row, depth);
                    
                    int height = getContentHeight(PanelNode.getText(value), dimensions.width);
                    
                    //System.err.println(height);
                    
                    dimensions.height = height; 
                    
                    return dimensions;
                }
            };
        }
        
        public int getContentHeight(String content, int width) {
            JEditorPane dummyEditorPane=new JEditorPane();
            
            dummyEditorPane.setSize(width, Short.MAX_VALUE);
            dummyEditorPane.setText(content);
            
            return dummyEditorPane.getPreferredSize().height;
        }
        
    }
}
 

 
class PanelNodeRenderer implements TreeCellRenderer {
    JPanel panel;
    JTextPane textPane;
 
    public PanelNodeRenderer() {
        panel = new JPanel();
        textPane = new JTextPane();
        panel.setLayout(new BorderLayout());
        textPane.setOpaque(false);
        panel.add(textPane);
    }
 
    public Component getTreeCellRendererComponent(JTree tree,
                                                  Object value,
                                                  boolean selected,
                                                  boolean expanded,
                                                  boolean leaf,
                                                  int row,
                                                  boolean hasFocus) {
        /*DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        PanelNode panelNode = (PanelNode)node.getUserObject();*/
        
    	PanelNode panelNode = (PanelNode) value;
    	
        textPane.setText(panelNode.text);
        textPane.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        
        panel.setBorder(BorderFactory.createLineBorder(panelNode.borderColor));
        panel.setBackground(panelNode.background);
        
        return panel;
    }
}
 
/*class PanelNode {
    String text;
    Color borderColor;
    Color background;
    
    public PanelNode(String text, Color bc, Color bg) {
        this.text = text;
        borderColor = bc;
        background = bg;
    }
    
    static String getText(Object value) {
    	DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        PanelNode panelNode = (PanelNode)node.getUserObject();
        
        return panelNode.text;
    }
}*/

class PanelNode extends DefaultMutableTreeNode {
    String text;
    Color borderColor;
    Color background;
    
    List<PanelNode> children_;
    
    public PanelNode(String text, Color bc, Color bg, PanelNode p) {
    	super(p);
        this.text = text;
        borderColor = bc;
        background = bg;
    }
    
    static String getText(Object value) {
    	/*DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        PanelNode panelNode = (PanelNode)node.getUserObject();
        
        return panelNode.text;*/
    	
    	PanelNode node = (PanelNode) value;
        
        return node.text;
    }
    
	@Override
	public Enumeration<PanelNode> children() {
		assertChildren();
		
		//return Collections.enumeration(children_);
		return super.children();
	}
	
	private void assertChildren() {
		if (children_ != null) {
			return;
		}
		
		children_ = new ArrayList<PanelNode>();
		//children_.add(new PanelNode(text + ".child 1", borderColor, background, this));
		//children_.add(new PanelNode(text + ".child 2", borderColor, background, this));
		//children_.add(new PanelNode(text + ".child 3", borderColor, background, this));
		add(new PanelNode(text + ".child 1", borderColor, background, this));
		add(new PanelNode(text + ".child 1", borderColor, background, this));
		add(new PanelNode(text + ".child 1", borderColor, background, this));
	}

	@Override
	public String toString() {
		return text;
	}

	@Override
	public boolean isLeaf() {
		assertChildren();
		//return children_.isEmpty();
		return super.isLeaf();
	}

	@Override
	public int getChildCount() {
		assertChildren();
		//return children_.size();
		return super.getChildCount();
	}

	@Override
	public TreeNode getChildAt(int index) {
		assertChildren();
		//return children_.isEmpty() ? null : children_.get(index);
		return super.getChildAt(index);
	}
	
}
