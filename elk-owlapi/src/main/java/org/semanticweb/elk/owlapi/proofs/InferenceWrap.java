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

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

/**
 * Wrapper around {@link Inference} to expose all expressions (premises and
 * conclusion) as {@link OWLExpression}s.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class InferenceWrap implements OWLInference {

	private final Inference inference_;
	
	InferenceWrap(Inference inf) {
		inference_ = inf;
	}
	
	@Override
	public OWLExpression getConclusion() {
		return ElkToOwlProofConverter.convert(inference_.getConclusion());
	}

	@Override
	public Collection<OWLExpression> getPremises() {
		final Collection<? extends DerivedExpression> premises = inference_.getPremises();
		
		return new AbstractCollection<OWLExpression>() {

			@Override
			public Iterator<OWLExpression> iterator() {
				return new Iterator<OWLExpression>() {

					private final Iterator<? extends DerivedExpression> iter_ = premises.iterator();
					
					@Override
					public boolean hasNext() {
						return iter_.hasNext();
					}

					@Override
					public OWLExpression next() {
						return ElkToOwlProofConverter.convert(iter_.next());
					}

					@Override
					public void remove() {
						iter_.remove();
					}
					
				};
			}

			@Override
			public int size() {
				return premises.size();
			}
			
		};
	}

	@Override
	public String toString() {
		return inference_.toString();
	}

}
