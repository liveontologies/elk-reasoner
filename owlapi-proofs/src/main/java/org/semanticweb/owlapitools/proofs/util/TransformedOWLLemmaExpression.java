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

import org.semanticweb.owlapitools.proofs.expressions.OWLExpressionVisitor;
import org.semanticweb.owlapitools.proofs.expressions.OWLLemmaExpression;

/**
 * TODO
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 */
public class TransformedOWLLemmaExpression<T extends OWLInferenceTransformation> extends TransformedOWLExpression<OWLLemmaExpression, T> implements OWLLemmaExpression {

	public TransformedOWLLemmaExpression(OWLLemmaExpression expr, T f) {
		super(expr, f);
	}
	
	@Override
	public <O> O accept(OWLExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	

	
}
