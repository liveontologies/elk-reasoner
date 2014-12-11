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
import java.util.Set;

import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

/**
 * Blocks inferences which use blocked expressions as premises.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class BlockingCondition implements Condition<OWLInference> {

	private final Set<OWLExpression> blocked_;
	
	public BlockingCondition(Set<OWLExpression> blocked) {
		blocked_ = blocked;
	}
	
	public BlockingCondition() {
		this(new HashSet<OWLExpression>());
	}
	
	@Override
	public boolean holds(OWLInference inf) {
		boolean result = true;
		
		for (OWLExpression premise : inf.getPremises()) {
			if (inf.getConclusion().equals(premise)) {
				// filtering single cycles
				return false;
			}
			
			if (blocked_.contains(premise)) {
				return false;
			}
		}
		
		return result;
	}

	public Set<OWLExpression> getBlockedExpressions() {
		return blocked_;
	}
}
