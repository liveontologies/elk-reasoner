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

import java.util.Collection;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

/**
 * The base interface of inferences. The inference is an elementary step of a
 * proof. It is an object which has the conclusion (the derived expression), a
 * set of premises (expressions from which the conclusion is derived), and a set
 * of side conditions (boolean expressions which have to hold true for the
 * inference to apply).
 * 
 * Both the conclusion and the premises are modeled using {@link OWLAxiom} while
 * conditions can take arbitrary arguments. One common form of side conditions is
 * to check presence of axioms in the input ontology.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface OWLInference {

	/**
	 * 
	 * @return
	 */
	public OWLExpression getConclusion();

	/**
	 * 
	 * @return
	 */
	public Collection<OWLExpression> getPremises();

}
