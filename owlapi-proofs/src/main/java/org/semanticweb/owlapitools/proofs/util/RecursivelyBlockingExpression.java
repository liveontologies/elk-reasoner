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
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

/**
 * Recursively blocks expressions which were used to derive this or parent
 * expression.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RecursivelyBlockingExpression extends TransformedOWLExpression<BlockingCondition> {

	protected final OWLInferenceGraph infGraph;
	
	public RecursivelyBlockingExpression(OWLExpression expr, OWLInferenceGraph iG) throws ProofGenerationException {
		// root constructor
		super(expr, new BlockingCondition(blockRecursively(expr, Collections.<OWLExpression>emptySet(), iG)));
		
		infGraph = iG;
	}
	
	protected RecursivelyBlockingExpression(OWLExpression expr, OWLExpression toBeBlocked, Set<OWLExpression> currentlyBlocked, OWLInferenceGraph iG) {
		// internal constructor
		super(expr, new BlockingCondition(blockRecursively(toBeBlocked, currentlyBlocked, iG)));
		
		infGraph = iG;
	}
	
	// return a new expression which blocks all expressions blocked by the
	// current expression plus all those which use the expression passed as the
	// argument.
	public RecursivelyBlockingExpression blockExpression(OWLExpression toBeBlocked) {
		return new RecursivelyBlockingExpression(getExpression(), toBeBlocked, getFilterCondition().getBlockedExpressions(), infGraph);
	}

	// recursively blocks all expressions which are derived using the given expression or any expression blocked by the parent expression.
	static Set<OWLExpression> blockRecursively(OWLExpression newExpr, Set<OWLExpression> blocked, OWLInferenceGraph infGraph) {
		// TODO avoid copying, use something like Lisp-style lists or lazy set unions
		Set<OWLExpression> newSet = new HashSet<OWLExpression>(blocked);
		Set<OWLInference> blockedInferences = new HashSet<OWLInference>();
		Queue<OWLExpression> toDo = new ArrayDeque<OWLExpression>();
		
		toDo.add(newExpr);
		//FIXME
		//System.err.println("Blocking: " + newExpr);
		//System.err.println("Parent blocks: " + blocked);
		
		for (;;) {
			OWLExpression next = toDo.poll();
			
			if (next == null) {
				break;
			}
			
			if (newSet.add(next)) {
				//FIXME
				//System.err.println("Blocked: " + next);
				
				for (OWLInference inf : infGraph.getInferencesForPremise(next)) {
					// blocking the inference which has at least one blocked premise
					if (blockedInferences.add(inf)) {
						boolean nonBlockedInferenceExists = false;

						try {
							for (OWLInference altInf : inf.getConclusion().getInferences()) {
								if (!blockedInferences.contains(altInf)) {
									nonBlockedInferenceExists = true;
									break;
								}
							}
							// blocking the conclusion of the blocked inference
							// if it doesn't have alternative inferences
							if (!nonBlockedInferenceExists) {
								toDo.add(inf.getConclusion());
							}
						} catch (ProofGenerationException e) {
							// we don't handle it here because if proof
							// generation failed
							// for whatever reason, we wouldn't even start cycle
							// blocking because we couldn't have generated the
							// inference graph
							throw new RuntimeException("Unexpected proof generation error", e);
						}
					}
				}
			}
		}
		
		return newSet;
	}
	
}
