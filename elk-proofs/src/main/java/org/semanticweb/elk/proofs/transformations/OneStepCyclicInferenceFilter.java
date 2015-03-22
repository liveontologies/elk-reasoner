/**
 * 
 */
package org.semanticweb.elk.proofs.transformations;
/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.entries.StructuralEquivalenceChecker;
import org.semanticweb.elk.proofs.inferences.Inference;

/**
 * Filters out tautological inferences where the conclusion is derived from itself.
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class OneStepCyclicInferenceFilter implements InferenceTransformation {

	@Override
	public Iterable<? extends Inference> transform(Inference inf) {
		Expression conclusion = inf.getConclusion();
		StructuralEquivalenceChecker eqChecker = new StructuralEquivalenceChecker();
		
		for (Expression premise : inf.getPremises()) {
			if (eqChecker.equal(premise, conclusion)) {
				return Collections.emptyList();
			}
		}
		
		return Collections.singleton(inf);
	}

	@Override
	public boolean mayIntroduceDuplicates() {
		return false;
	}

}
