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

import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpressionVisitor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class FilteredOWLExpression implements OWLExpression {

	protected final OWLExpression expression;
	
	protected final Condition<OWLInference> filter; 
	
	public FilteredOWLExpression(OWLExpression expr, Condition<OWLInference> f) {
		expression = expr;
		filter = f;
	}
	
	@Override
	public <O> O accept(OWLExpressionVisitor<O> visitor) {
		return expression.accept(visitor);
	}

	@Override
	public Iterable<OWLInference> getInferences() throws ProofGenerationException {
		return Operations.map(expression.getInferences(), new Operations.Transformation<OWLInference, OWLInference>() {

			@Override
			public OWLInference transform(OWLInference inf) {
				if (filter.holds(inf)) {
					return propagateFilter(inf);
				}
				
				return null;
			}
			
		});
	}

	private OWLInference propagateFilter(OWLInference inf) {
		return new FilteredOWLInference(inf, filter);
	}
}
