/**
 * 
 */
package org.semanticweb.elk.proofs.transformations.lemmas;
/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.owl.visitors.NoOpElkAxiomVisitor;
import org.semanticweb.elk.proofs.expressions.AxiomExpression;
import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.LemmaExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemmaVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkReflexivePropertyChainLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubClassOfLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubPropertyChainOfLemma;

/**
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class BaseExpressionVisitor<I, O> extends NoOpElkAxiomVisitor<O> implements ExpressionVisitor<I, O>, ElkLemmaVisitor<I, O> {

	protected AxiomExpression<? extends ElkAxiom> axiomExpression;
	
	protected LemmaExpression<? extends ElkLemma> lemmaExpression;
	
	@Override
	public O visit(AxiomExpression<? extends ElkAxiom> expr, I input) {
		axiomExpression = expr;
		lemmaExpression = null;
		
		return expr.getAxiom().accept(this);
	}

	@Override
	public O visit(LemmaExpression<? extends ElkLemma> expr, I input) {
		axiomExpression = null;
		lemmaExpression = expr;
		
		return expr.getLemma().accept(this, input);
	}
	
	protected O defaultLemmaVisit(ElkLemma l, I input) {
		return null;
	}

	@Override
	public O visit(ElkReflexivePropertyChainLemma lemma, I input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public O visit(ElkSubClassOfLemma lemma, I input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public O visit(ElkSubPropertyChainOfLemma lemma, I input) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
