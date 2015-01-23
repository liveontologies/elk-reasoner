/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.derived;
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
import org.semanticweb.elk.proofs.expressions.LemmaExpression;
import org.semanticweb.elk.proofs.expressions.derived.entries.ExpressionEntryFactory;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemma;
import org.semanticweb.elk.proofs.inferences.readers.InferenceReader;
import org.semanticweb.elk.util.collections.entryset.KeyEntryHashSet;

/**
 * TODO
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class DerivedExpressionFactoryWithCaching implements DerivedExpressionFactory {

	private final KeyEntryHashSet<DerivedAxiomExpressionImpl<?>> axiomLookup_;
	
	private final KeyEntryHashSet<LemmaExpressionImpl<?>> lemmaLookup_;
	
	private final InferenceReader reader_;

	public DerivedExpressionFactoryWithCaching(InferenceReader reader) {
		axiomLookup_ = new KeyEntryHashSet<DerivedAxiomExpressionImpl<?>>(new ExpressionEntryFactory<DerivedAxiomExpressionImpl<?>>(), 128);
		lemmaLookup_ = new KeyEntryHashSet<LemmaExpressionImpl<?>>(new ExpressionEntryFactory<LemmaExpressionImpl<?>>(), 32);
		reader_ = reader;
	}
	
	@Override
	public <E extends ElkAxiom> DerivedAxiomExpression<E> create(E axiom) {
		DerivedAxiomExpressionImpl<E> newExpr = new DerivedAxiomExpressionImpl<E>(axiom, reader_);
		
		return (DerivedAxiomExpression<E>) axiomLookup_.merge(newExpr);
	}

	@Override
	public <L extends ElkLemma> LemmaExpression<L> create(L lemma) {
		LemmaExpressionImpl<L> newExpr = new LemmaExpressionImpl<L>(lemma, reader_);
		
		return (LemmaExpression<L>) lemmaLookup_.merge(newExpr);
	}

	@Override
	public <E extends ElkAxiom> DerivedAxiomExpressionImpl<E> createAsserted(E axiom) {
		DerivedAxiomExpressionImpl<E> newExpr = new DerivedAxiomExpressionImpl<E>(axiom, reader_, true);
		DerivedAxiomExpressionImpl<E> oldExpr = (DerivedAxiomExpressionImpl<E>) axiomLookup_.merge(newExpr);
		
		if (!oldExpr.isAsserted()) {
			oldExpr.setAsserted(axiom);
		}
		
		return oldExpr;
	}

}
