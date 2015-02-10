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
public class CycleBlocking extends RecursiveBlocking {
	
	public CycleBlocking(OWLExpression expr, OWLInferenceGraph ig) throws ProofGenerationException {
		// root constructor
		super(expr, Collections.<OWLExpression>emptySet(), ig);
	}
	
	CycleBlocking(OWLExpression expr, Set<OWLExpression> blockedExpressions, OWLInferenceGraph infGraph) {
		// child constructor
		super(expr, blockedExpressions, infGraph);
	}

	@Override
	public CycleBlocking update(OWLInference inf, OWLExpression expr) {
		// return a new transformation which blocks all expressions blocked by the
		// current transformation plus all those which use the passed expression
		return new CycleBlocking(expr, getBlockedExpressions(), infGraph);
	}
}
