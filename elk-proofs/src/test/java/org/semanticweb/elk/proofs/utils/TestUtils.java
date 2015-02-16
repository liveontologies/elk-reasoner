/**
 * 
 */
package org.semanticweb.elk.proofs.utils;
/*
 * #%L
 * ELK Proofs Package
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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.proofs.InferenceGraph;
import org.semanticweb.elk.proofs.ProofReader;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.inferences.Inference;

/**
 * Utilities for testing proofs
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TestUtils {

	// tests that each derived expression is provable. an expression is provable
	// if either it doesn't require a proof (i.e. is a tautology or asserted) or
	// returns at least one inference such that each of the premises is provable.
	public static void provabilityOfSubsumptionTest(ProofReader reader, ElkClassExpression sub, ElkClassExpression sup) throws ElkException {
		DerivedAxiomExpression<?> root = reader.getProofRoot(sub, sup);
		InferenceGraph graph = ProofReader.readInferenceGraph(root);
		
		//FIXME
		//System.out.println("Inference graph for " + root);
		//System.out.println(graph);
		
		if (!graph.getExpressions().contains(root) && !root.isAsserted()) {
			throw new AssertionError(root + " isn't derived!");
		}
		
		provabilityTest(graph, graph.getExpressions());
		
		
	}
	
	public static void provabilityOfInconsistencyTest(ProofReader reader) throws ElkException {
		DerivedExpression root = reader.getProofRootForInconsistency();
		InferenceGraph graph = ProofReader.readInferenceGraph(root);
		// only the expression which corresponds to inconsistency has to be
		// explained, others aren't guaranteed to be derived since inconsistency
		// aborts reasoning
		provabilityTest(graph, Collections.singleton(root));
	}
	
	public static void provabilityTest(InferenceGraph graph, Iterable<? extends DerivedExpression> toCheck) throws ElkException {
		Set<DerivedExpression> proved = new HashSet<DerivedExpression>(graph.getExpressions().size());
		Queue<DerivedExpression> toDo = new LinkedList<DerivedExpression>(graph.getRootExpressions()); 
		
		for (;;) {
			DerivedExpression next = toDo.poll();
			
			if (next == null) {
				break;
			}
			
			if (proved.add(next)) {
				for (Inference inf : graph.getInferencesForPremise(next)) {
					if (proved.containsAll(inf.getPremises())) {
						toDo.add(inf.getConclusion());
						
						//FIXME
						//System.err.println("Proved: " + inf.getConclusion() + " by " + inf);
					}
				}
			}
		}
		
		for (DerivedExpression expr : toCheck) {
			if (!proved.contains(expr) && !ProofUtils.isAsserted(expr)) {
				throw new AssertionError(String.format("There is no acyclic proof of %s", expr));
			}
		}
	}
}
