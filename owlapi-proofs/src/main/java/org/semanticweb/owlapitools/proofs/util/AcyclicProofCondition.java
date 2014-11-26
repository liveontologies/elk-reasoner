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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class AcyclicProofCondition implements Condition<OWLInference> {

	private final OWLInferenceGraph inferenceGraph_;
	
	private final Set<OWLExpression> blockedExpressions_;
	
	public AcyclicProofCondition(OWLInferenceGraph ig) {
		inferenceGraph_ = ig;
		blockedExpressions_ = new HashSet<OWLExpression>();
	}
	
	@Override
	public boolean holds(OWLInference inference) {
		if (blockedExpressions_.contains(inference.getConclusion())) {
			return false;
		}
		
		blockRecursively(inference.getConclusion());
		
		// TODO filter such inferences out elsewhere?
		if (checkSingleCycle(inference)) {
			blockedExpressions_.add(inference.getConclusion());
			return false;
		}
		
		return true;
	}

	private boolean checkSingleCycle(OWLInference inference) {
		for (OWLExpression premise : inference.getPremises()) {
			if (premise.equals(inference.getConclusion())) {
				return true;
			}
		}
		
		return false;
	}

	private void blockRecursively(OWLExpression start) {
		Queue<OWLExpression> toDo = new LinkedList<OWLExpression>();
		
		toDo.add(start);
		
		for (;;) {
			OWLExpression next = toDo.poll();
			
			if (next == null) {
				break;
			}
			
			if (blockedExpressions_.add(next)) {
				for (OWLInference inf : inferenceGraph_.getInferencesForPremise(next)) {
					toDo.addAll(inf.getPremises());
				}
			}
		}
	}

}
