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

import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.AbstractLayoutCache;

/**
 * @author pavel
 *
 */
public class ProofTreeUI extends BasicTreeUI {

	@Override
	public int getRowX(int row, int depth) {
		return super.getRowX(row, depth);
	}

	@Override
    protected AbstractLayoutCache.NodeDimensions createNodeDimensions() {
        return new NodeDimensionsHandler() {
        	
            @Override
            public Rectangle getNodeDimensions(Object value, int row, int depth, boolean expanded, Rectangle size) {
                Rectangle dimensions = super.getNodeDimensions(value, row, depth, expanded, size);
                
                /*Component component = currentCellRenderer.getTreeCellRendererComponent  
                        (tree, value, tree.isRowSelected(row),  
                                expanded, treeModel.isLeaf(value), row,  
                                false);*/  
                
                if (tree.getParent() != null) {
                	dimensions.width = tree.getParent().getWidth() - getRowX(row, depth) - 5;
                	//dimensions.height = component.getPreferredSize().height;
                }
                
                return dimensions;
            }

			
        };
    }
}
