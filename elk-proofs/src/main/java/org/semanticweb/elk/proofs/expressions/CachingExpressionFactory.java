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
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemma;
import org.semanticweb.elk.proofs.inferences.InferenceReader;
import org.semanticweb.elk.util.collections.entryset.EntryCollection;

/**
 * TODO
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class CachingExpressionFactory implements ExpressionFactory {

	private final EntryCollection<AxiomExpressionImpl<?>> axiomLookup_;
	
	private final EntryCollection<LemmaExpressionImpl<?>> lemmaLookup_;
	
	private final InferenceReader reader_;

	public CachingExpressionFactory(InferenceReader reader) {
		axiomLookup_ = new EntryCollection<AxiomExpressionImpl<?>>(128);
		lemmaLookup_ = new EntryCollection<LemmaExpressionImpl<?>>(32);
		reader_ = reader;
	}
	
	@Override
	public <E extends ElkAxiom> AxiomExpression<E> create(E axiom) {
		AxiomExpressionImpl<E> newExpr = new AxiomExpressionImpl<E>(axiom, reader_);
		return merge(newExpr);
	}

	@Override
	public <L extends ElkLemma> LemmaExpression<L> create(L lemma) {
		LemmaExpressionImpl<L> newExpr = new LemmaExpressionImpl<L>(lemma, reader_);
		return merge(newExpr);		
	}

	@Override
	public <E extends ElkAxiom> AxiomExpressionImpl<E> createAsserted(E axiom) {
		AxiomExpressionImpl<E> newExpr = new AxiomExpressionImpl<E>(axiom, reader_, true);
		AxiomExpressionImpl<E> oldExpr = merge(newExpr);
		
		if (!oldExpr.isAsserted()) {
			oldExpr.setAsserted(axiom);
		}
		
		return oldExpr;
	}
	
	private <E extends ElkAxiom> AxiomExpressionImpl<E> merge(AxiomExpressionImpl<E> newExpr) {
		@SuppressWarnings("unchecked")
		AxiomExpressionImpl<E> result = (AxiomExpressionImpl<E>) axiomLookup_.findStructural(newExpr);
		if (result == null) {
			axiomLookup_.addStructural(newExpr);
			result = newExpr;
		}		
		return result;		
	}
	
	private <L extends ElkLemma> LemmaExpression<L> merge(LemmaExpressionImpl<L> newExpr) {
		@SuppressWarnings("unchecked")
		LemmaExpressionImpl<L> result = (LemmaExpressionImpl<L>) lemmaLookup_.findStructural(newExpr); 
		if (result == null) {
			lemmaLookup_.addStructural(newExpr);
			result = newExpr;
		}
		return result;
	}

}
