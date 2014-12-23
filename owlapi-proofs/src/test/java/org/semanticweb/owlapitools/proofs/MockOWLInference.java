/**
 * 
 */
package org.semanticweb.owlapitools.proofs;
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

import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

/**
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class MockOWLInference implements OWLInference {

	private final String name_;
	
	private final OWLExpression conclusion_;
	
	private final Collection<? extends OWLExpression> premises_;
	
	public MockOWLInference(String name, OWLExpression conclusion, Collection<? extends OWLExpression> premises) {
		name_ = name;
		conclusion_ = conclusion;
		premises_ = premises;
	}
	
	@Override
	public OWLExpression getConclusion() {
		return conclusion_;
	}

	@Override
	public Collection<? extends OWLExpression> getPremises() {
		return premises_;
	}

	@Override
	public String getName() {
		return name_;
	}

	
}
