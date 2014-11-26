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
public class FilteredOWLInference implements OWLInference {

	protected final OWLInference inference;
	
	protected final Condition<OWLInference> filter;
	
	public FilteredOWLInference(OWLInference inf, Condition<OWLInference> f) {
		inference = inf;
		filter = f;
	}
	
	@Override
	public OWLExpression getConclusion() {
		return propagateCondition(inference.getConclusion());
	}

	private OWLExpression propagateCondition(OWLExpression e) {
		return new FilteredOWLExpression(e, filter);
	}

	@Override
	public Collection<OWLExpression> getPremises() {
		return Operations.map(inference.getPremises(), new Operations.Transformation<OWLExpression, OWLExpression>() {

			@Override
			public OWLExpression transform(OWLExpression premise) {
				return propagateCondition(premise);
			}
			
		});
	}

	@Override
	public String getName() {
		return inference.getName();
	}

}
