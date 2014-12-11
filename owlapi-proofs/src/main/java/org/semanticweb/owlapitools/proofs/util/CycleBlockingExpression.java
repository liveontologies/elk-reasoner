/**
 * 
 */
package org.semanticweb.owlapitools.proofs.util;
/*
 * #%L
 * OWL API Proofs Model
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

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

/**
 * Recursively blocks expressions which were used to derive this or parent
 * expression.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class CycleBlockingExpression extends FilteredOWLExpression<BlockingCondition> {

	private final OWLInferenceGraph infGraph_;
	
	public CycleBlockingExpression(OWLExpression expr, OWLInferenceGraph ig) {
		// root constructor
		super(expr, new BlockingCondition(saturateBlockedExpressions(expr, Collections.<OWLExpression>emptySet(), ig)));
		infGraph_ = ig;
	}
	
	CycleBlockingExpression(OWLExpression expr, CycleBlockingExpression parentExpression, OWLInferenceGraph infGraph) {
		// child constructor
		super(expr, new BlockingCondition(saturateBlockedExpressions(expr, parentExpression.getFilterCondition().getBlockedExpressions(), infGraph)));
		infGraph_ = infGraph;
	}
	
	@Override
	protected FilteredOWLInference<BlockingCondition> propagateCondition(OWLInference inf) {
		return new CycleBlockingInference(inf, this, infGraph_);
	}

	// recursively blocks all expressions which are derived using the given expression or any expression blocked by the parent expression.
	private static Set<OWLExpression> saturateBlockedExpressions(OWLExpression newExpr, Set<OWLExpression> parentBlocked, OWLInferenceGraph infGraph) {
		// TODO avoid copying, use something like Lisp-style lists or lazy set unions
		Set<OWLExpression> newSet = new HashSet<OWLExpression>(parentBlocked);
		Queue<OWLExpression> toDo = new ArrayDeque<OWLExpression>();
		
		toDo.add(newExpr);
		
		//System.err.println("Blocking for: " + newExpr);
		//System.err.println("Parent blocked: " + parentBlocked);
		
		for (;;) {
			OWLExpression next = toDo.poll();
			
			if (next == null) {
				break;
			}
			
			if (newSet.add(next)) {
				//System.err.println("Blocked: " + next);
				
				for (OWLInference inf : infGraph.getInferencesForPremise(next)) {
					toDo.add(inf.getConclusion());
				}
			}
		}
		
		return newSet;
	}
	
	private static class CycleBlockingInference extends FilteredOWLInference<BlockingCondition> {

		private final OWLInferenceGraph infGraph_;
		
		private final CycleBlockingExpression parent_;
		
		public CycleBlockingInference(OWLInference inf, CycleBlockingExpression parent, OWLInferenceGraph ig) {
			super(inf, parent.getFilterCondition());
			parent_ = parent;
			infGraph_ = ig;
		}

		@Override
		protected FilteredOWLExpression<BlockingCondition> propagateCondition(	OWLExpression premise) {
			return new CycleBlockingExpression(premise, parent_, infGraph_);
		}
		
	}
}
