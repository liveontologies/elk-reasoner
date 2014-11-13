/**
 * 
 */
package org.semanticweb.elk.owlapi.proofs;
/*
 * #%L
 * ELK OWL API Binding
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

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.expressions.derived.entries.StructuralEquivalenceChecker;
import org.semanticweb.elk.proofs.expressions.derived.entries.StructuralEquivalenceHasher;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class BaseDerivedExpressionWrap<E extends DerivedExpression> implements OWLExpression {

	final E expression;
	
	BaseDerivedExpressionWrap(E expr) {
		expression = expr;
	}
	
	@Override
	public Iterable<OWLInference> getInferences() throws ProofGenerationException {
		try {
			return Operations.map(expression.getInferences(), new Operations.Transformation<Inference, OWLInference>() {

				@Override
				public OWLInference transform(Inference inference) {
					return ElkToOwlProofConverter.convert(inference);
				}
			});
		} catch (ElkException e) {
			throw new ProofGenerationException(e);
		}
	}

	@Override
	public int hashCode() {
		return new StructuralEquivalenceHasher().hashCode(expression);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (obj instanceof BaseDerivedExpressionWrap) {
			BaseDerivedExpressionWrap<?> expr = (BaseDerivedExpressionWrap<?>) obj;
			
			return expression.accept(new StructuralEquivalenceChecker(), expr.expression);
		}
		
		return false;
	}

	@Override
	public String toString() {
		return expression.toString();
	}	
	
	
}
