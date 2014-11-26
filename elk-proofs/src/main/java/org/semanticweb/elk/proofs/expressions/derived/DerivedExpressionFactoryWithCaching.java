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

	private final KeyEntryHashSet<DerivedAxiomExpression<ElkAxiom>> axiomLookup_;
	
	private final KeyEntryHashSet<DerivedLemmaExpression> lemmaLookup_;
	
	private final InferenceReader reader_;

	public DerivedExpressionFactoryWithCaching(InferenceReader reader) {
		axiomLookup_ = new KeyEntryHashSet<DerivedAxiomExpression<ElkAxiom>>(new ExpressionEntryFactory<DerivedAxiomExpression<ElkAxiom>>(), 128);
		lemmaLookup_ = new KeyEntryHashSet<DerivedLemmaExpression>(new ExpressionEntryFactory<DerivedLemmaExpression>(), 32);
		reader_ = reader;
	}
	
	@Override
	public DerivedAxiomExpression<?> create(ElkAxiom axiom) {
		DerivedAxiomExpression<ElkAxiom> newExpr = new DerivedAxiomExpression<ElkAxiom>(axiom, reader_);
		
		return axiomLookup_.merge(newExpr);
	}

	@Override
	public DerivedLemmaExpression create(ElkLemma lemma) {
		DerivedLemmaExpression newExpr = new DerivedLemmaExpression(lemma, reader_);
		
		return lemmaLookup_.merge(newExpr);
	}

	@Override
	public DerivedAxiomExpression<?> createAsserted(ElkAxiom axiom) {
		DerivedAxiomExpression<ElkAxiom> newExpr = new DerivedAxiomExpression<ElkAxiom>(axiom, reader_, true);
		DerivedAxiomExpression<ElkAxiom> oldExpr = axiomLookup_.merge(newExpr);
		
		if (!oldExpr.isAsserted()) {
			oldExpr.setAsserted(axiom);
		}
		
		return oldExpr;
	}

}
