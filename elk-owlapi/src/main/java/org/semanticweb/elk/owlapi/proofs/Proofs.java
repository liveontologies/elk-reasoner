/**
 * 
 */
package org.semanticweb.elk.owlapi.proofs;
/*
 * #%L
 * ELK OWL API Binding
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

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapitools.proofs.ExplainingOWLReasoner;
import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLAxiomExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpressionVisitor;
import org.semanticweb.owlapitools.proofs.expressions.OWLLemmaExpression;

/**
 * Utilities for working with proofs.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class Proofs {

	/**
	 * Returns all ontology axioms used in all or some proof(s) for the given entailment.
	 * 
	 * @param reasoner
	 * @param entailment
	 * @param allProofs
	 * @return
	 * @throws ProofGenerationException
	 */
	public static Set<OWLAxiom> getUsedAxioms(ExplainingOWLReasoner reasoner, OWLAxiom entailment, boolean allProofs) throws ProofGenerationException {
		final Set<OWLAxiom> usedAxioms = new HashSet<OWLAxiom>();
		
		RecursiveInferenceVisitor.visitInferences(reasoner, entailment, new OWLInferenceVisitor() {
			
			@Override
			public void visit(OWLInference inference) {
				for (OWLExpression premise : inference.getPremises()) {
					premise.accept(new OWLExpressionVisitor<Void>() {

						@Override
						public Void visit(OWLAxiomExpression expression) {
							if (expression.isAsserted()) {
								usedAxioms.add(expression.getAxiom());
							}
							return null;
						}

						@Override
						public Void visit(OWLLemmaExpression expression) {
							return null;
						}
						
					});
				}
			}
			
		}, allProofs);
		
		return usedAxioms;
	}
}
