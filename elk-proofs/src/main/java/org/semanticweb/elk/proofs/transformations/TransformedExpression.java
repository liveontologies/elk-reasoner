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

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.InferenceEntry;
import org.semanticweb.elk.util.collections.Condition;
import org.semanticweb.elk.util.collections.Operations;

/**
 * Generic base class for {@link Expression}s which transform their
 * {@link Inference}s before returning them from the method
 * {@link #getInferences()}. The transformation is done using the provided
 * instance of {@link Operations.Transformation}. A special case of
 * transformation is filtering, i.e., eliminating some inferences from the
 * output.
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 */
abstract class TransformedExpression<D extends Expression, T extends InferenceTransformation> implements Expression {

	protected final D expression;
	
	protected final T transformation; 
	
	protected final Operations.Transformation<Inference, TransformedInference<T>> propagation = new Operations.Transformation<Inference, TransformedInference<T>>() {

		@Override
		public TransformedInference<T> transform(Inference inf) {
			return propagateTransformation(inf);
		}}; 
	
	protected TransformedExpression(D expr, T f) {
		expression = expr;
		transformation = f;
	}
	
	@Override
	public Iterable<? extends Inference> getInferences() throws ElkException {
		Iterable<TransformedInference<T>> transformed = Operations.mapConcat(expression.getInferences(), new Operations.Transformation<Inference, Iterable<TransformedInference<T>>>() {

			@Override
			public Iterable<TransformedInference<T>> transform(Inference inf) {
				Iterable<? extends Inference> transformed = transformation.transform(inf); 
				
				return Operations.map(transformed, propagation);
			}
			
		});
		
		if (!transformation.mayIntroduceDuplicates()) {
			return transformed;
		}
		
		return eliminateDuplicates(transformed);
	}

	private Iterable<? extends Inference> eliminateDuplicates(final Iterable<? extends Inference> inferences) {
		final Set<InferenceEntry<Inference>> unique = new HashSet<InferenceEntry<Inference>>();
		
		return Operations.filter(inferences, new Condition<Inference>() {

			@Override
			public boolean holds(Inference inf) {
				return unique.add(new InferenceEntry<Inference>(inf));
			}
			
		});		
	}

	protected TransformedInference<T> propagateTransformation(Inference inf) {
		return new TransformedInference<T>(inf, transformation);
	}
	
	public T getFilterCondition() {
		return transformation;
	}

	@Override
	public String toString() {
		return expression.toString();
	}

	@Override
	public boolean equals(Object obj) {
		return expression.equals(obj);
	}

	@Override
	public int hashCode() {
		return expression.hashCode();
	}
}
