/**
 * 
 */
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

import java.util.Arrays;
import java.util.HashSet;

import org.semanticweb.elk.owl.AbstractElkAxiomVisitor;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.proofs.ProofReader;
import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.derived.AssertedExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedLemmaExpression;
import org.semanticweb.elk.proofs.expressions.derived.entries.StructuralEquivalenceChecker;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemmaVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkReflexivePropertyChainLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubClassOfLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubPropertyChainOfLemma;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.reasoner.Reasoner;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TestUtils {

	// tests that each derived expression is provably. an expression is provable
	// if it returns at least one inference such that each of the premises is
	// provable.
	public static boolean provabilityTest(Reasoner reasoner, ElkClass sub,
			ElkClass sup) throws ElkException {
		DerivedExpression next = ProofReader.start(reasoner, sub, sup);

		return proved(next, new HashSet<DerivedExpression>(Arrays.asList(next)));
	}

	// TODO recursion is probably OK for short tests but better to re-write
	// using queues and loops
	private static boolean proved(DerivedExpression expr,
			HashSet<DerivedExpression> seen) throws ElkException {
		// check if the expression doesn't require a proof
		if (isTautology(expr)) {
			return true;
		}

		for (Inference inf : expr.getInferences()) {
			// see if this inference proves the expression
			boolean proves = true;

			for (DerivedExpression premise : inf.getPremises()) {
				if (seen.add(premise)) {
					proves &= proved(premise, seen);
				}
			}

			if (proves) {
				return true;
			}
		}

		return false;
	}

	private static boolean isTautology(DerivedExpression expr) {
		return expr.accept(new TautologyChecker(), null);
	}

	/**
	 * Checks if an expression is a tautology.
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private static class TautologyChecker extends
			AbstractElkAxiomVisitor<Boolean> implements
			ExpressionVisitor<Void, Boolean>, ElkLemmaVisitor<Void, Boolean> {

		@Override
		protected Boolean defaultLogicalVisit(ElkAxiom axiom) {
			return false;
		}

		@Override
		public Boolean visit(
				ElkReflexivePropertyChainLemma lemma, Void input) {
			return false;
		}

		@Override
		public Boolean visit(ElkSubClassOfLemma lemma, Void input) {
			return false;
		}

		@Override
		public Boolean visit(ElkSubPropertyChainOfLemma lemma,
				Void input) {
			return StructuralEquivalenceChecker.equal(lemma.getSubPropertyChain(), lemma.getSuperPropertyChain());
		}

		@Override
		public Boolean visit(DerivedAxiomExpression expr,
				Void input) {
			return expr.getAxiom().accept(this);
		}

		@Override
		public Boolean visit(DerivedLemmaExpression expr,
				Void input) {
			return expr.getLemma().accept(this, input);
		}

		@Override
		public Boolean visit(ElkSubClassOfAxiom ax) {
			return StructuralEquivalenceChecker.equal(ax.getSubClassExpression(), ax.getSuperClassExpression());
		}

		@Override
		public Boolean visit(
				ElkSubObjectPropertyOfAxiom ax) {
			return StructuralEquivalenceChecker.equal(ax.getSubObjectPropertyExpression(), ax.getSuperObjectPropertyExpression());
		}
		
	}

}
