package org.semanticweb.elk.owlapi.proofs;

/*
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.inferences.ElkInference;
import org.semanticweb.elk.owl.inferences.ElkInferenceSet;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owlapi.wrapper.ElkAxiomWrap;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLAxiomExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpressionVisitor;
import org.semanticweb.owlapitools.proofs.util.Operations;
import org.semanticweb.owlapitools.proofs.util.Operations.Transformation;

public class OwlAxiomExpressionWrap implements OWLAxiomExpression {

	private final ElkAxiom axiom_;

	private final ElkInferenceSet elkInferences_;

	private final OWLOntology owlOntology_;

	private final ElkObjectFactory elkFactory_;

	public OwlAxiomExpressionWrap(ElkAxiom axiom, ElkInferenceSet elkInferences,
			OWLOntology ontology, ElkObjectFactory elkFactory) {
		super();
		this.axiom_ = axiom;
		this.elkInferences_ = elkInferences;
		this.owlOntology_ = ontology;
		this.elkFactory_ = elkFactory;
	}

	@Override
	public int hashCode() {
		return axiom_.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OwlAxiomExpressionWrap) {
			return axiom_.equals(((OwlAxiomExpressionWrap) obj).axiom_);
		}
		// else
		return false;
	}

	@Override
	public String toString() {
		return axiom_.toString();
	}

	@Override
	public Iterable<? extends OWLInference> getInferences()
			throws ProofGenerationException {
		return Operations.map(elkInferences_.get(axiom_),
				new Transformation<ElkInference, OWLInference>() {

					@Override
					public OWLInference transform(ElkInference element) {
						return new OWLInferenceWrap(element, elkInferences_,
								owlOntology_, elkFactory_);
					}
				});
	}

	@SuppressWarnings("unchecked")
	@Override
	public OWLAxiom getAxiom() {
		return ((ElkAxiomWrap<OWLAxiom>) axiom_).getOWLAxiom();
	}

	@Override
	public boolean isAsserted() {
		return owlOntology_.containsAxiom(getAxiom());
	}

	@Override
	public <O> O accept(OWLExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
