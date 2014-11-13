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

import java.util.Collections;
import java.util.Enumeration;

import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

/**
 * TODO A cycle has been detected so this node is blocked and doesn't have children.
 * 
 * @author pavel
 *
 */
public class BlockedOWLExpressionNode extends OWLExpressionNode {


	BlockedOWLExpressionNode(OWLExpression expr) {
		super(expr);
	}

	@Override
	public Enumeration<OWLInferenceNode> children() {
		return Collections.enumeration(Collections.<OWLInferenceNode>emptyList());
	}
	
	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public int getChildCount() {
		return 0;
	}

}
