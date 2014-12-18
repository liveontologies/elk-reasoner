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

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.TreePath;

/**
 * @author pavel
 *
 */
public class ProofTreeUI extends BasicTreeUI {

	@Override
	public int getRowX(int row, int depth) {
		return super.getRowX(row, depth);
	}
	
	static int getNodePreferredWidth(int visibleWidth, int rowX) {
		return Math.max(visibleWidth - rowX - 5, visibleWidth / 3);
	}

	@Override
    protected AbstractLayoutCache.NodeDimensions createNodeDimensions() {
        return new NodeDimensionsHandler() {
        	
            @Override
            public Rectangle getNodeDimensions(Object value, int row, int depth, boolean expanded, Rectangle size) {
                Rectangle dimensions = super.getNodeDimensions(value, row, depth, expanded, size);
                
                if (tree.getParent() != null) {
                	int visibleWidth = tree.getParent().getWidth();
                	
                	dimensions.width = getNodePreferredWidth(visibleWidth, getRowX(row, depth));
                }
                
                return dimensions;
            }

			
        };
    }

	@Override
	protected void paintHorizontalPartOfLeg(Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds, TreePath path, int row, boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf) {
		Object node = path.getLastPathComponent();
		
		if (node instanceof OWLExpressionNode) {
			super.paintHorizontalPartOfLeg(g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf);
		}
	}
	
	
}
