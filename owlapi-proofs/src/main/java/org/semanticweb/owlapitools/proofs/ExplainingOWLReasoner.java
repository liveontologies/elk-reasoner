/**
 * 
 */
package org.semanticweb.owlapitools.proofs;
/*
 * #%L
 * OWL API Proofs Model
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

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLAxiomExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

/**
 * An extension of the OWL API's {@link OWLReasoner} for reasoners which can
 * reconstruct proofs for entailments.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface ExplainingOWLReasoner extends OWLReasoner {

	/**
	 * Returns an {@link OWLExpression} object which represents the entailed
	 * axiom, or {@code null} if the entailment does not hold. All proofs can be
	 * unwound recursively by calling {@link OWLExpression#getInferences()} on
	 * each premise.
	 * 
	 * @param entailment
	 * @return
	 */
	public OWLAxiomExpression getDerivedExpression(OWLAxiom entailment) throws ProofGenerationException, UnsupportedEntailmentTypeException;
	
}
