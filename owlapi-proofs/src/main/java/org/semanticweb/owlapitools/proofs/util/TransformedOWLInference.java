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

import java.util.Collection;

import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.expressions.OWLAxiomExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpressionVisitor;
import org.semanticweb.owlapitools.proofs.expressions.OWLLemmaExpression;

/**
 * TODO
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TransformedOWLInference<T extends OWLInferenceTransformation> implements OWLInference {

	protected final OWLInference inference;
	
	protected final T transformation;
	
	public TransformedOWLInference(OWLInference inf, T f) {
		inference = inf;
		transformation = f;
	}
	
	@Override
	public OWLExpression getConclusion() {
		return transform(inference.getConclusion(), transformation);
	}

	protected TransformedOWLExpression<?, T> propagateTransformation(OWLExpression premise) {
		// FIXME get rid of the cast later
		final T updated = (T) transformation.update(inference, premise);
		
		return transform(premise, updated);
	}
	
	private TransformedOWLExpression<?, T> transform(final OWLExpression expr, final T transfrm) {
		return expr.accept(new OWLExpressionVisitor<TransformedOWLExpression<?, T>>() {

			@Override
			public TransformedOWLExpression<?, T> visit(OWLAxiomExpression expression) {
				return new TransformedOWLAxiomExpression<T>(expression, transfrm);
			}

			@Override
			public TransformedOWLExpression<?, T> visit(OWLLemmaExpression expression) {
				return new TransformedOWLLemmaExpression<T>(expression, transfrm);
			}
			
		});
	}

	@Override
	public Collection<? extends TransformedOWLExpression<?, T>> getPremises() {
		return Operations.map(inference.getPremises(), new Operations.Transformation<OWLExpression, TransformedOWLExpression<?, T>>() {

			@Override
			public TransformedOWLExpression<?, T> transform(OWLExpression premise) {
				return propagateTransformation(premise);
			}
			
		});
	}

	@Override
	public String getName() {
		return inference.getName();
	}

	@Override
	public String toString() {
		return inference.toString();
	}
	
}
