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
import org.semanticweb.owlapitools.proofs.util.OWLProofUtils;

/**
 * TODO need an immutable implementation
 * 
 * @author pavel
 *
 */
@SuppressWarnings("serial")
public class OWLExpressionNode extends DefaultMutableTreeNode {

	private final OWLExpression expression_;

	private final OWLAxiom axiom_;

	private boolean childrenComputed_ = false;

	OWLExpressionNode(OWLExpression root) {
		this(root, null);
	}
	
	OWLExpressionNode(OWLExpression expr, TreeNode parent) {
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
	public Enumeration<?> children() {
		assertChildren();
		
		return super.children();
	}
	
	private void assertChildren() {
		if (childrenComputed_) {
			return;
		}
		
		childrenComputed_ = true;
		
		try {
			for (OWLInference inf : expression_.getInferences()) {
				// the separating node
				add(new DefaultMutableTreeNode(inf.getName()));
				
				for (OWLExpression premise : inf.getPremises()) {
					add(new OWLExpressionNode(premise, this));
				}
				
			}
		} catch (ProofGenerationException e) {
			// TODO render some error nodes saying that inferences can't be obtained?
			e.printStackTrace();
		}
	}

	public OWLExpression getExpression() {
		return expression_;
	}
	
	public OWLAxiom getAxiom() {
		return axiom_;
	}
	
	public boolean isAsserted() {
		return OWLProofUtils.isAsserted(expression_);
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
	
	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	/*private static class CycleBlockingExpression extends FilteredOWLExpression<Condition<OWLInference>> {

		public CycleBlockingExpression(final OWLExpression expr, final TreeNode[] pathFromRoot) {
			super(expr, new Condition<OWLInference>() {
				
				@Override
				public boolean holds(OWLInference inf) {
					for (OWLExpression premise : inf.getPremises()) {
						// TODO create a set instead of traversing the path each time
						if (inf.getConclusion().equals(premise) || occursInPath(premise)) {
							return false;
						}
					}
					
					return true;
				}

				private boolean occursInPath(OWLExpression expr) {
					for (TreeNode pathNode : pathFromRoot) {
						OWLExpression pathExpr = ((OWLExpressionNode) pathNode).getExpression();
						
						if (pathExpr.equals(expr)) {
							return true;
						}
					}
					
					return false;
				}
				
			});
		}
		
	}*/
}
