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

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpressionVisitor;
import org.semanticweb.owlapitools.proofs.expressions.OWLLemmaExpression;

/**
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class MockOWLLemmaExpression implements OWLLemmaExpression {

	private final String lemmaRendering_;
	
	private final List<MockOWLInference> inferences_;
	
	public MockOWLLemmaExpression(String lemmaRendering) {
		lemmaRendering_ = lemmaRendering;
		inferences_ = new ArrayList<MockOWLInference>();
	}
	
	public MockOWLLemmaExpression addInference(MockOWLInference inf) {
		inferences_.add(inf);
		
		return this;
	}
	
	@Override
	public <O> O accept(OWLExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public List<MockOWLInference> getInferences() throws ProofGenerationException {
		return inferences_;
	}

	@Override
	public String toString() {
		return lemmaRendering_;
	}

}
