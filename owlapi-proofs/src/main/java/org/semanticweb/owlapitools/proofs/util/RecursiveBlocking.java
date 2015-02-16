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
public class RecursiveBlocking extends BlockingCondition {

	protected final OWLInferenceGraph infGraph;

	public RecursiveBlocking(OWLExpression expr, Set<OWLExpression> blocked, OWLInferenceGraph iGraph) {
		super(blockRecursively(expr, blocked, iGraph));

		infGraph = iGraph;
	}

	OWLInferenceGraph getOWLInferenceGraph() {
		return infGraph;
	}

	// recursively blocks all expressions which are derived using the given expression or any expression blocked by the parent expression.
	static Set<OWLExpression> blockRecursively(OWLExpression newExpr, Set<OWLExpression> blocked, OWLInferenceGraph infGraph) {
		// TODO avoid copying, use something like Lisp-style lists or lazy set unions
		Set<OWLExpression> nowBlocked = new HashSet<OWLExpression>(blocked);
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

			if (nowBlocked.add(next)) {
				//FIXME
				//System.err.println("Blocked: " + next);

				for (OWLInference inf : infGraph.getInferencesForPremise(next)) {
					boolean nonBlockedInferenceExists = false;
					OWLExpression conclusion = inf.getConclusion();
					
					if (OWLProofUtils.isAsserted(conclusion)) {
						// never block asserted expressions
						continue;
					}

					try {
						for (OWLInference altInf : conclusion.getInferences()) {
							boolean nonBlockedInference = true;

							for (OWLExpression premise : altInf.getPremises()) {
								if (nowBlocked.contains(premise)) {
									nonBlockedInference = false;
									break;
								}
							}

							if (nonBlockedInference) {
								nonBlockedInferenceExists = true;
								break;
							}
						}
						// blocking the conclusion of the blocked inference
						// if it doesn't have alternative inferences
						if (!nonBlockedInferenceExists) {
							if (!nowBlocked.contains(conclusion)) {
								toDo.add(conclusion);
							}
						}
					} catch (ProofGenerationException e) {
						// we don't handle it here because if proof
						// generation failed for whatever reason, we wouldn't even start cycle
						// blocking because we couldn't have generated the inference graph
						throw new RuntimeException("Unexpected proof generation error", e);
					}
				}
			}
		}

		return nowBlocked;
	}

}
