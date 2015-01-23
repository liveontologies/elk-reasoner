/**
 * 
 */
package org.semanticweb.elk.proofs.transformations;
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

import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.LemmaExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.InferenceRule;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.util.collections.Operations;

/**
 * TODO
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TransformedInference<T extends Operations.Transformation<Inference, Iterable<Inference>>> implements Inference {

	protected final Inference inference;
	
	protected final T transformation;
	
	public TransformedInference(Inference inf, T f) {
		inference = inf;
		transformation = f;
	}
	
	@Override
	public DerivedExpression getConclusion() {
		return inference.getConclusion();
	}

	protected TransformedExpression<?, T> propagateTransformation(DerivedExpression expr) {
		return expr.accept(new ExpressionVisitor<Void, TransformedExpression<?, T>>() {

			@Override
			public TransformedExpression<?, T> visit(DerivedAxiomExpression<?> axiom, Void input) {
				return new TransformedAxiomExpression<T>(axiom, transformation);
			}

			@Override
			public TransformedExpression<?, T> visit(LemmaExpression<?> lemma, Void input) {
				return new TransformedLemmaExpression<T>(lemma, transformation);
			}
			
		}, null);
	}

	@Override
	public Collection<? extends TransformedExpression<?, T>> getPremises() {
		return Operations.map(inference.getPremises(), new Operations.Transformation<DerivedExpression, TransformedExpression<?, T>>() {

			@Override
			public TransformedExpression<?, T> transform(DerivedExpression premise) {
				return propagateTransformation(premise);
			}
			
		});
	}

	@Override
	public String toString() {
		return inference.toString();
	}

	@Override
	public InferenceRule getRule() {
		return inference.getRule();
	}

	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		return inference.accept(visitor, input);
	}
	
}
