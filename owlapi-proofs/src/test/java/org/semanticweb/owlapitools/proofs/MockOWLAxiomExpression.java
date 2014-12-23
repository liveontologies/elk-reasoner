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

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLAxiomExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpressionVisitor;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpressionWrap;

/**
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class MockOWLAxiomExpression implements OWLAxiomExpression {

	private final OWLAxiom axiom_;
	
	private final boolean asserted_;
	
	private final List<MockOWLInference> inferences_;
	
	public MockOWLAxiomExpression(OWLAxiom ax, boolean asserted, List<MockOWLInference> inferences) {
		axiom_ = ax;
		asserted_ = asserted;
		inferences_ = inferences;
	}
	
	public MockOWLAxiomExpression(OWLAxiom ax, boolean asserted) {
		this(ax, asserted, new ArrayList<MockOWLInference>());
	}
	
	public MockOWLAxiomExpression(OWLAxiom ax) {
		this(ax, true, new ArrayList<MockOWLInference>());
	}
	
	public MockOWLAxiomExpression addInference(MockOWLInference inf) {
		inferences_.add(inf);
		
		return this;
	}
	
	@Override
	public OWLAxiom getAxiom() {
		return axiom_;
	}

	@Override
	public boolean isAsserted() {
		return asserted_;
	}

	@Override
	public <O> O accept(OWLExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public Iterable<MockOWLInference> getInferences() throws ProofGenerationException {
		return inferences_;
	}

	@Override
	public String toString() {
		return axiom_.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((axiom_ == null) ? 0 : axiom_.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		
		if (obj instanceof OWLExpressionWrap) {
			// unwrapping
			return equals(((OWLExpressionWrap) obj).getExpression());
		}
		
		if (!(obj instanceof OWLAxiomExpression))
			return false;
		OWLAxiomExpression other = (OWLAxiomExpression) obj;
		if (axiom_ == null) {
			if (other.getAxiom() != null)
				return false;
		} else if (!axiom_.equals(other.getAxiom()))
			return false;
		return true;
	}

	
}
