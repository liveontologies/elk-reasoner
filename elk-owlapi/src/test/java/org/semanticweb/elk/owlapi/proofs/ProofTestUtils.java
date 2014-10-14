/**
 * 
 */
package org.semanticweb.elk.owlapi.proofs;
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

import org.semanticweb.elk.proofs.utils.TestUtils;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapitools.proofs.ExplainingOWLReasoner;
import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLAxiomExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpressionVisitor;
import org.semanticweb.owlapitools.proofs.expressions.OWLLemmaExpression;

/**
 * TODO this is adapted from {@link TestUtils}, see if we can get rid of copy-paste.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ProofTestUtils {

	// tests that each derived expression is provable. an expression is provable
	// if either it doesn't require a proof (i.e. is a tautology or asserted) or
	// returns at least one inference such that each of the premises is
	// provable.
	public static boolean provabilityTest(ExplainingOWLReasoner reasoner, OWLSubClassOfAxiom axiom) throws ProofGenerationException {
		OWLExpression next = reasoner.getDerivedExpression(axiom);

		return proved(next, new HashSet<OWLExpression>(Arrays.asList(next)));
	}

	private static boolean proved(OWLExpression expr, HashSet<OWLExpression> seen) throws ProofGenerationException {
		// check if the expression doesn't require a proof
		if (isAsserted(expr)) {
			return true;
		}

		for (OWLInference inf : expr.getInferences()) {
			// see if this inference proves the expression
			boolean proves = true;
			boolean newPremise = false;
			
			if (inf.getConclusion().equals(expr) && isAsserted(inf.getConclusion())) {
				return true;
			}

			for (OWLExpression premise : inf.getPremises()) {
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

	private static boolean isAsserted(OWLExpression expr) {
		return expr.accept(new OWLExpressionVisitor<Boolean>() {

			@Override
			public Boolean visit(OWLAxiomExpression expr) {
				return expr.isAsserted();
			}

			@Override
			public Boolean visit(OWLLemmaExpression arg0) {
				return false;
			}
			
		});
	}

}
