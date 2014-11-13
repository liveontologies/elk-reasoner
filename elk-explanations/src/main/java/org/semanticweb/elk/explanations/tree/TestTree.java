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


import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class TestTree extends JPanel {
  JTree tree;
  DefaultMutableTreeNode root, node1, node2, node3, node4;
  public TestTree() {
    root = new DefaultMutableTreeNode("root", true);
    node1 = new DefaultMutableTreeNode("node 1", true);
    node2 = new DefaultMutableTreeNode("node 2" , true);
    node3 = new DefaultMutableTreeNode("node 3" , true);
    node4 = new DefaultMutableTreeNode("node 4" , true);
    root.add(node1);
    node1.add(node2);
    root.add(node3);
    node3.add(node4);
    setLayout(new BorderLayout());
    tree = new JTree(root);
    add(new JScrollPane((JTree)tree),"Center");
    }

  public Dimension getPreferredSize(){
    return new Dimension(200, 120);
    }

  public static void main(String s[]){
    MyJFrame frame = new MyJFrame("Tree Collapse Expand");
    }
  }

class WindowCloser extends WindowAdapter {
  public void windowClosing(WindowEvent e) {
    Window win = e.getWindow();
    win.setVisible(false);
    System.exit(0);
    }
  }

class MyJFrame extends JFrame implements ActionListener {
  JButton b1, b2, b3;
  TestTree panel;
  MyJFrame(String s) {
    super(s);
    setForeground(Color.black);
    setBackground(Color.lightGray);
    panel = new TestTree();
    expandAll(panel.tree);
    getContentPane().add(panel,"Center");

    b1 = new JButton("Expand");
    b3 = new JButton("Expand to last");
    b2 = new JButton("Collapse");

    b1.addActionListener(this);
    b2.addActionListener(this);
    b3.addActionListener(this);
    getContentPane().add(b1,"West");
    getContentPane().add(b3,"North");
    getContentPane().add(b2,"East");
    setSize(300,300);
    setVisible(true);
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowCloser());
    }

  public void actionPerformed(ActionEvent ae) {
    if (ae.getSource() == b1) expandAll(panel.tree);
    if (ae.getSource() == b3) expandToLast(panel.tree);
    if (ae.getSource() == b2) collapseAll(panel.tree);
    }


  public void expandAll(JTree tree) {
    int row = 0;
    while (row < tree.getRowCount()) {
      tree.expandRow(row);
      row++;
      }
    }


  public void expandToLast(JTree tree) {
    // expand to the last leaf from the root
    DefaultMutableTreeNode  root;
    root = (DefaultMutableTreeNode) tree.getModel().getRoot();
    tree.scrollPathToVisible(new TreePath(root.getLastLeaf().getPath()));
    }


  /*
  // alternate version, suggested by C.Kaufhold
  public void expandToLast(JTree tree) {
    TreeModel data = tree.getModel();
    Object node = data.getRoot();

    if (node == null) return;

    TreePath p = new TreePath(node);
    while (true) {
         int count = data.getChildCount(node);
         if (count == 0) break;
         node = data.getChild(node, count - 1);
         p = p.pathByAddingChild(node);
    }
    tree.scrollPathToVisible(p);
  }
  */


  public void collapseAll(JTree tree) {
    int row = tree.getRowCount() - 1;
    while (row >= 0) {
      tree.collapseRow(row);
      row--;
      }
    }
 }