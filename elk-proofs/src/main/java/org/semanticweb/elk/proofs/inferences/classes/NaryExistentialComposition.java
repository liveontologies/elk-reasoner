/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.classes;
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

import java.util.List;

import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.inferences.AbstractInference;
import org.semanticweb.elk.proofs.inferences.InferenceRule;

/**
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public abstract class NaryExistentialComposition<D extends DerivedExpression> extends AbstractInference<D> {

	protected final List<? extends DerivedExpression> existentialPremises;
	
	protected NaryExistentialComposition(D conclusion, List<? extends DerivedExpression> exPremises) {
		super(conclusion);
		
		existentialPremises = exPremises;
	}

	public List<? extends DerivedExpression> getExistentialPremises() {
		return existentialPremises;
	}
	
	@Override
	public InferenceRule getRule() {
		return InferenceRule.R_EXIST_CHAIN_COMPOSITION;
	}
}
