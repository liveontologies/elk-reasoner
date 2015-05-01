/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;
/*
 * #%L
 * ELK Proofs Package
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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.proofs.expressions.entries.StructuralEquivalenceChecker;
import org.semanticweb.elk.proofs.expressions.entries.StructuralEquivalenceHasher;
import org.semanticweb.elk.proofs.inferences.InferenceReader;
import org.semanticweb.elk.util.collections.entryset.Entry;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class AxiomExpressionImpl<E extends ElkAxiom> extends AbstractExpression implements AxiomExpression<E>,
    Entry<AxiomExpressionImpl<?>, AxiomExpressionImpl<?>> {

	private E axiom_;
	
	private boolean asserted_;
	
	private AxiomExpressionImpl<?> next_;
	
	public AxiomExpressionImpl(E ax, InferenceReader r) {
		this(ax, r, false);
	}
	
	public AxiomExpressionImpl(E ax, InferenceReader r, boolean asserted) {
		super(r);
		axiom_ = ax;
		asserted_ = asserted;
		
		assert ax != null : "cannot create axiom expressions without an axiom";
	}
	
	@Override
	public E getAxiom() {
		return axiom_;
	}
	
	void setAsserted(E axiom) {
		axiom_ = axiom;
		asserted_ = true;
	}
	
	@Override
	public boolean isAsserted() {
		return asserted_;
	}
	
	@Override
	public String toString() {
		return OwlFunctionalStylePrinter.toString(axiom_) + (asserted_ ? "*" : "");
	}

	@Override
	public <I, O> O accept(ExpressionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public void setNext(AxiomExpressionImpl<?> next) {
		this.next_ = next;
		
	}

	@Override
	public AxiomExpressionImpl<?> getNext() {
		return next_;
	}

	@Override
	public AxiomExpressionImpl<?> structuralEquals(Object other) {
		if (this == other)
			return this;
		if (other instanceof AxiomExpressionImpl<?>) {
			AxiomExpressionImpl<?> otherExpression = (AxiomExpressionImpl<?>) other;
			if (StructuralEquivalenceChecker.equal(axiom_,otherExpression.axiom_)) {
				return otherExpression;
			};
		}
		// else
		return null;
	}

	@Override
	public int structuralHashCode() {		
		return StructuralEquivalenceHasher.hashCode(axiom_);
	}
	
}
