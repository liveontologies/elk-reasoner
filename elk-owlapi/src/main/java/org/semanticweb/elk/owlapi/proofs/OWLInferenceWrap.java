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
import org.semanticweb.elk.owl.inferences.ElkInferenceConclusionVisitor;
import org.semanticweb.elk.owl.inferences.ElkInferencePremiseVisitor;
import org.semanticweb.elk.owl.inferences.ElkInferenceSet;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.visitors.DummyElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.util.Operations;
import org.semanticweb.owlapitools.proofs.util.Operations.Transformation;

public class OWLInferenceWrap implements OWLInference {

	private final static ElkAxiomVisitor<ElkAxiom> TRIVIAL_AXIOM_VISITOR_ = new DummyElkAxiomVisitor<ElkAxiom>() {

		@Override
		protected ElkAxiom defaultLogicalVisit(ElkAxiom axiom) {
			return axiom;
		}
	};

	private final ElkInference elkInference_;

	private final ElkInferenceSet elkInferences_;

	private final OWLOntology owlOntology_;

	private final ElkObjectFactory elkFactory_;

	OWLInferenceWrap(ElkInference elkInference, ElkInferenceSet elkInferences,
			OWLOntology owlOntology, ElkObjectFactory elkFactory) {
		this.elkInference_ = elkInference;
		this.owlOntology_ = owlOntology;
		this.elkFactory_ = elkFactory;
		this.elkInferences_ = elkInferences;
	}

	@Override
	public OWLExpression getConclusion() {
		ElkAxiom elkConclusion = elkInference_
				.accept(new ElkInferenceConclusionVisitor<ElkAxiom>(elkFactory_,
						TRIVIAL_AXIOM_VISITOR_));
		return new OwlAxiomExpressionWrap(elkConclusion, elkInferences_,
				owlOntology_, elkFactory_);
	}

	@Override
	public Collection<? extends OWLExpression> getPremises() {
		AxiomSavingVisitor elkPremiseSaver = new AxiomSavingVisitor();
		elkInference_.accept(new ElkInferencePremiseVisitor<ElkAxiom>(
				elkFactory_, elkPremiseSaver));
		return Operations.map(elkPremiseSaver.elkAxioms_,
				new Transformation<ElkAxiom, OWLExpression>() {

					@Override
					public OWLExpression transform(ElkAxiom premise) {
						return new OwlAxiomExpressionWrap(premise,
								elkInferences_, owlOntology_, elkFactory_);
					}

				});
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return elkInference_.toString();
	}

	private static class AxiomSavingVisitor extends DummyElkAxiomVisitor<Void> {

		private final List<ElkAxiom> elkAxioms_ = new ArrayList<ElkAxiom>();

		@Override
		protected Void defaultLogicalVisit(ElkAxiom axiom) {
			elkAxioms_.add(axiom);
			return null;
		}

	};

}
