/**
 * 
 */
package org.semanticweb.elk.proofs.transformations.lemmas;
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

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.inferences.Inference;


/**
 * TODO
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public abstract class DerivedExpressionWrap<D extends DerivedExpression> implements DerivedExpression {

	final D expr;
	
	final List<Inference> inferences;
	
	public DerivedExpressionWrap(D expr) {
		this.expr = expr;
		this.inferences = new ArrayList<Inference>();
	}
	
	public void addInference(Inference inf) {
		assert inf.getConclusion() == this;
		
		inferences.add(inf);
	}

	@Override
	public Iterable<? extends Inference> getInferences() throws ElkException {
		//return Operations.concat(expr_.getInferences(), inferences_);
		return inferences;
	}

	@Override
	public boolean equals(Object obj) {
		return expr.equals(obj);
	}

	@Override
	public int hashCode() {
		return expr.hashCode();
	}

	@Override
	public String toString() {
		return expr.toString();
	}
}
