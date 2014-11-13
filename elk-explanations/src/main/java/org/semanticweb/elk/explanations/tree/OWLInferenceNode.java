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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

/**
 * @author pavel
 *
 */
public class OWLInferenceNode extends DefaultMutableTreeNode {

	private final OWLInference inference_;
	
	private List<OWLExpressionNode> children_;
	
	OWLInferenceNode(OWLInference inf) {
		inference_ = inf;
	}

	@Override
	public Enumeration<OWLExpressionNode> children() {
		assertChildren();
		
		return Collections.enumeration(children_);
	}

	@Override
	public String toString() {
		return inference_.getName();
	}

	private void assertChildren() {
		if (children_ == null) {
			children_ = new ArrayList<OWLExpressionNode>();
			
			for (OWLExpression premise : inference_.getPremises()) {
				children_.add(new OWLExpressionNode(premise));
			}
			
		}
	}

	@Override
	public boolean isLeaf() {
		assertChildren();
		return children_.isEmpty();
	}

	@Override
	public int getChildCount() {
		assertChildren();
		return children_.size();
	}

	@Override
	public TreeNode getChildAt(int index) {
		assertChildren();
		return children_.isEmpty() ? null : children_.get(index);
	}
	
}
