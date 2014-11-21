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

import java.util.Enumeration;

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

	private boolean childrenComputed_ = false;

	OWLExpressionNode(OWLExpression expr, OWLInferenceNode parent) {
		super(parent);
		
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
		
		return super.children();
	}
	
	private void assertChildren() {
		if (childrenComputed_) {
			return;
		}
		
		childrenComputed_ = true;
		
		//System.err.println("children recomputed for " + axiom_);
		
		try {
			for (OWLInference inf : expression_.getInferences()) {
				add(new OWLInferenceNode(inf, this));
			}
		} catch (ProofGenerationException e) {
			// TODO render some error nodes saying that inferences can't be
			// obtained?
			e.printStackTrace();
		}
	}

	public OWLExpression getExpression() {
		return expression_;
	}
	
	public OWLAxiom getAxiom() {
		return axiom_;
	}
	
	// TODO reuse this visitor?
	public boolean isAsserted() {
		return expression_.accept(new OWLExpressionVisitor<Boolean>() {

			@Override
			public Boolean visit(OWLAxiomExpression expression) {
				return expression.isAsserted();
			}

			@Override
			public Boolean visit(OWLLemmaExpression expression) {
				return false;
			}
			
		});
	}
	
	@Override
	public String toString() {
		return expression_.toString();
	}

	@Override
	public boolean isLeaf() {
		assertChildren();
		return super.isLeaf();
	}

	@Override
	public int getChildCount() {
		assertChildren();
		return super.getChildCount();
	}

	@Override
	public TreeNode getChildAt(int index) {
		assertChildren();
		return super.getChildAt(index);
	}
	
}
