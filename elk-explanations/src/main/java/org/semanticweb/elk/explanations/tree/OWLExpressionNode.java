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

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLAxiomExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpressionVisitor;
import org.semanticweb.owlapitools.proofs.expressions.OWLLemmaExpression;

/**
 * TODO need an immutable implementation
 * 
 * @author pavel
 *
 */
public class OWLExpressionNode extends DefaultMutableTreeNode {

	private final OWLExpression expression_;

	private final OWLAxiom axiom_;

	private List<OWLInferenceNode> children_;

	OWLExpressionNode(OWLExpression expr) {
		expression_ = expr;
		axiom_ = expr.accept(new OWLExpressionVisitor<OWLAxiom>() {

			@Override
			public OWLAxiom visit(OWLAxiomExpression e) {
				return e.getAxiom();
			}

			@Override
			public OWLAxiom visit(OWLLemmaExpression e) {
				return null;
			}

		});
	}

	@Override
	public Enumeration<OWLInferenceNode> children() {
		assertChildren();
		
		return Collections.enumeration(children_);
	}
	
	private void assertChildren() {
		try {
			System.err.println("caching children");
			children_ = new ArrayList<OWLInferenceNode>();

			for (OWLInference inf : expression_.getInferences()) {
				children_.add(new OWLInferenceNode(inf));
			}

		} catch (ProofGenerationException e) {
			// TODO render some error nodes saying that inferences can't be
			// obtained?
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return expression_.toString();
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
