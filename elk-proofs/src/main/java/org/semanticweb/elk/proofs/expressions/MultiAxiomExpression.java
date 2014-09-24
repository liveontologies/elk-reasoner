/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;

import org.semanticweb.elk.util.collections.Operations;
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


/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class MultiAxiomExpression implements Expression {

	private final Iterable<Explanation> explanations_;
	
	public MultiAxiomExpression(Iterable<Explanation> expl) {
		explanations_ = expl;
	}

	@Override
	public Iterable<Explanation> getExplanations() {
		return explanations_;
	}
	
	@Override
	public String toString() {
		return Operations.toString(explanations_);
	}

}
