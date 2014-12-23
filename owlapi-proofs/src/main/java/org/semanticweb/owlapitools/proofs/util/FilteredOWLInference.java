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
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class FilteredOWLInference<C extends Condition<OWLInference>> implements OWLInference {

	protected final OWLInference inference;
	
	protected final C filter;
	
	public FilteredOWLInference(OWLInference inf, C f) {
		inference = inf;
		filter = f;
	}
	
	@Override
	public OWLExpression getConclusion() {
		return inference.getConclusion();
	}

	protected FilteredOWLExpression<C> propagateCondition(OWLExpression expr) {
		return new FilteredOWLExpression<C>(expr, filter);
	}

	@Override
	public Collection<? extends FilteredOWLExpression<C>> getPremises() {
		return Operations.map(inference.getPremises(), new Operations.Transformation<OWLExpression, FilteredOWLExpression<C>>() {

			@Override
			public FilteredOWLExpression<C> transform(OWLExpression premise) {
				return propagateCondition(premise);
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
