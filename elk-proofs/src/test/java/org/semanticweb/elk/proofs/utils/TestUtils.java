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

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.proofs.ProofReader;
import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedLemmaExpression;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.reasoner.Reasoner;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TestUtils {

	// tests that each derived expression is provable. an expression is provable
	// if either it doesn't require a proof (i.e. is a tautology or asserted) or
	// returns at least one inference such that each of the premises is
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
		if (isTautology(expr) || isAsserted(expr)) {
			return true;
		}

		for (Inference inf : expr.getInferences()) {
			// see if this inference proves the expression. it does if there
			// exists a not yet visited premise which is provable.
			boolean proves = true;
			boolean newPremise = false;

			for (DerivedExpression premise : inf.getPremises()) {
				
				if (seen.add(premise)) {
					newPremise = true;
					proves &= proved(premise, seen);
				}
			}

			if (proves && newPremise) {
				return true;
			}
		}

		return false;
	}

	private static boolean isAsserted(DerivedExpression expr) {
		return expr.accept(new ExpressionVisitor<Void, Boolean>() {

			@Override
			public Boolean visit(DerivedAxiomExpression<?> expr, Void input) {
				return expr.isAsserted();
			}

			@Override
			public Boolean visit(DerivedLemmaExpression expr, Void input) {
				// lemmas can not be asserted
				return false;
			}
			
		}, null);
	}

	private static boolean isTautology(DerivedExpression expr) {
		return expr.accept(new TautologyChecker(), null);
	}

}
