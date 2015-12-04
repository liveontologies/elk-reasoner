package org.semanticweb.elk.proofs.utils;
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
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.visitors.DummyElkAxiomVisitor;
import org.semanticweb.elk.proofs.expressions.AxiomExpression;
import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.LemmaExpression;
import org.semanticweb.elk.proofs.expressions.entries.StructuralEquivalenceChecker;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemmaVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkReflexivePropertyChainLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubClassOfLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubPropertyChainOfLemma;

/**
 * Checks if an expression is a tautology.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TautologyChecker extends
		DummyElkAxiomVisitor<Boolean> implements
		ExpressionVisitor<Void, Boolean>, ElkLemmaVisitor<Void, Boolean> {

	@Override
	protected Boolean defaultLogicalVisit(ElkAxiom axiom) {
		return false;
	}

	@Override
	public Boolean visit(ElkReflexivePropertyChainLemma lemma, Void input) {
		return false;
	}

	@Override
	public Boolean visit(ElkSubClassOfLemma lemma, Void input) {
		return false;
	}

	@Override
	public Boolean visit(ElkSubPropertyChainOfLemma lemma, Void input) {
		return StructuralEquivalenceChecker.equal(lemma.getSubPropertyChain(), lemma.getSuperPropertyChain());
	}

	@Override
	public Boolean visit(AxiomExpression<?> expr, Void input) {
		return expr.getAxiom().accept(this);
	}

	@Override
	public Boolean visit(LemmaExpression<?> expr, Void input) {
		return expr.getLemma().accept(this, input);
	}

	@Override
	public Boolean visit(ElkSubClassOfAxiom ax) {
		return StructuralEquivalenceChecker.equal(ax.getSubClassExpression(), ax.getSuperClassExpression()) || 
				StructuralEquivalenceChecker.equal(ax.getSuperClassExpression(), PredefinedElkClass.OWL_THING) ||
				StructuralEquivalenceChecker.equal(PredefinedElkClass.OWL_NOTHING, ax.getSubClassExpression());
	}

	@Override
	public Boolean visit(ElkSubObjectPropertyOfAxiom ax) {
		return StructuralEquivalenceChecker.equal(ax.getSubObjectPropertyExpression(), ax.getSuperObjectPropertyExpression());
	}
	
}