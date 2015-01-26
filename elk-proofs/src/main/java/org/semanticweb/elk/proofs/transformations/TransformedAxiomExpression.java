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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;

/**
 * TODO
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class TransformedAxiomExpression<T extends InferenceTransformation, E extends ElkAxiom> extends TransformedExpression<DerivedAxiomExpression<E>, T> implements DerivedAxiomExpression<E> {

	public TransformedAxiomExpression(DerivedAxiomExpression<E> expr, T f) {
		super(expr, f);
	}

	@Override
	public E getAxiom() {
		return expression.getAxiom();
	}

	@Override
	public boolean isAsserted() {
		return expression.isAsserted();
	}
	
	@Override
	public <I, O> O accept(ExpressionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

}
