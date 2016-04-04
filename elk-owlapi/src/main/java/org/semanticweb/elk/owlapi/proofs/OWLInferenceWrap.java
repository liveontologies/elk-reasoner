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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.semanticweb.elk.owl.inferences.ElkInference;
import org.semanticweb.elk.owl.inferences.ElkInferenceSet;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

public class OWLInferenceWrap implements OWLInference {

	private final ElkInference elkInference_;

	private final ElkInferenceSet elkInferences_;

	private final OWLOntology owlOntology_;

	private final ElkObject.Factory elkFactory_;

	OWLInferenceWrap(ElkInference elkInference, ElkInferenceSet elkInferences,
			OWLOntology owlOntology, ElkObject.Factory elkFactory) {
		this.elkInference_ = elkInference;
		this.owlOntology_ = owlOntology;
		this.elkFactory_ = elkFactory;
		this.elkInferences_ = elkInferences;
	}

	@Override
	public OWLExpression getConclusion() {
		return new OwlAxiomExpressionWrap(
				elkInference_.getConclusion(elkFactory_), elkInferences_,
				owlOntology_, elkFactory_);
	}

	@Override
	public Collection<? extends OWLExpression> getPremises() {
		List<OWLExpression> result = new ArrayList<OWLExpression>();
		for (int i = 0; i < elkInference_.getPremiseCount(); i++) {
			result.add(new OwlAxiomExpressionWrap(
					elkInference_.getPremise(i, elkFactory_), elkInferences_,
					owlOntology_, elkFactory_));
		}
		return result;
	}

	@Override
	public String getName() {
		return elkInference_.getName();
	}

	@Override
	public String toString() {
		return elkInference_.toString();
	}

}
