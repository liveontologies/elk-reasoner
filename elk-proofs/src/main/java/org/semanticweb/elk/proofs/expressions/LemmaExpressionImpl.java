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

import org.semanticweb.elk.proofs.expressions.entries.StructuralEquivalenceChecker;
import org.semanticweb.elk.proofs.expressions.entries.StructuralEquivalenceHasher;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemma;
import org.semanticweb.elk.proofs.inferences.InferenceReader;
import org.semanticweb.elk.proofs.utils.ElkLemmaPrinter;
import org.semanticweb.elk.util.collections.entryset.Entry;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class LemmaExpressionImpl<L extends ElkLemma> extends AbstractExpression
		implements LemmaExpression<L>,
		Entry<LemmaExpressionImpl<?>, LemmaExpressionImpl<?>> {

	private final L lemma_;
	
	private LemmaExpressionImpl<?> next_;
	
	public LemmaExpressionImpl(L lemma, InferenceReader r) {
		super(r);
		lemma_ = lemma;
	}
	
	@Override
	public L getLemma() {
		return lemma_;
	}

	@Override
	public String toString() {
		return ElkLemmaPrinter.print(lemma_);
	}
	
	@Override
	public <I, O> O accept(ExpressionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public void setNext(LemmaExpressionImpl<?> next) {
		this.next_ = next;
		
	}

	@Override
	public LemmaExpressionImpl<?> getNext() {
		return next_;
	}

	@Override
	public LemmaExpressionImpl<?> structuralEquals(Object other) {		
		if (this == other) {
			return this;
		}
		if (other instanceof LemmaExpressionImpl<?>) {
			LemmaExpressionImpl<?> otherLemma = (LemmaExpressionImpl<?>) other;
			if (StructuralEquivalenceChecker.equal(otherLemma.lemma_, lemma_)) {
				return otherLemma;
			}
		}
		// else
		return null;
	}

	@Override
	public int structuralHashCode() {
		return StructuralEquivalenceHasher.hashCode(lemma_);
	}	
}
