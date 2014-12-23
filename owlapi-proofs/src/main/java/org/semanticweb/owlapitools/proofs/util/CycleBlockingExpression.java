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

import java.util.Collections;
import java.util.Set;

import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

/**
 * Blocks cyclic proofs
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class CycleBlockingExpression extends RecursivelyBlockingExpression {
	
	public CycleBlockingExpression(OWLExpression expr, OWLInferenceGraph ig) throws ProofGenerationException {
		// root constructor
		super(expr, expr, Collections.<OWLExpression>emptySet(), ig);
	}
	
	CycleBlockingExpression(OWLExpression expr, CycleBlockingExpression parentExpression, OWLInferenceGraph infGraph) {
		// child constructor
		super(expr, expr, parentExpression.getFilterCondition().getBlockedExpressions(), infGraph);
	}
	
	CycleBlockingExpression(OWLExpression expr, OWLExpression toBeBlocked, Set<OWLExpression> currentlyBlocked, OWLInferenceGraph iG) {
		// internal constructor
		super(expr, toBeBlocked, currentlyBlocked, iG);
	}
	
	@Override
	protected CycleBlockingInference propagateCondition(OWLInference inf) {
		return new CycleBlockingInference(inf, this, infGraph);
	}
	
	@Override
	public CycleBlockingExpression blockExpression(OWLExpression toBeBlocked) {
		return new CycleBlockingExpression(getExpression(), toBeBlocked, getFilterCondition().getBlockedExpressions(), infGraph);
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
		protected CycleBlockingExpression propagateCondition(OWLExpression premise) {
			return new CycleBlockingExpression(premise, parent_, infGraph_);
		}
		
	}
}
