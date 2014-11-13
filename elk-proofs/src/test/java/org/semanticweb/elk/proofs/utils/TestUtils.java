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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.proofs.InferenceGraph;
import org.semanticweb.elk.proofs.ProofReader;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.reasoner.Reasoner;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TestUtils {

	// tests that each derived expression is provable. an expression is provable
	// if either it doesn't require a proof (i.e. is a tautology or asserted) or
	// returns at least one inference such that each of the premises is
	// provable.
	public static void provabilityTest(Reasoner reasoner, ElkClassExpression sub,
			ElkClassExpression sup) throws ElkException {
		InferenceGraph graph = ProofReader.readInferenceGraph(reasoner, sub, sup);
		
		//System.out.println(graph);
		
		if (graph.getExpressions().isEmpty()) {
			throw new AssertionError(String.format("There is no proof of %s <= %s", sub, sup));
		}
		
		Set<DerivedExpression> proved = new HashSet<DerivedExpression>(graph.getExpressions().size());
		Queue<DerivedExpression> toDo = new LinkedList<DerivedExpression>(graph.getRootExpressions()); 
		
		for (;;) {
			DerivedExpression next = toDo.poll();
			
			if (next == null) {
				break;
			}
			
			if (proved.add(next)) {
				
				//System.err.println("Proved: " + next);
				
				for (Inference inf : graph.getInferencesForPremise(next)) {
					if (proved.containsAll(inf.getPremises())) {
						toDo.add(inf.getConclusion());
					}
				}
			}
		}
		
		for (DerivedExpression expr : graph.getExpressions()) {
			if (!proved.contains(expr)) {
				throw new AssertionError(String.format("There is no acyclic proof of %s", expr));
			}
		}
	}
}
