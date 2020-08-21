package org.semanticweb.elk.owlapi.query;

/*-
 * #%L
 * ELK OWL API v.4 Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2020 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.elk.reasoner.completeness.IncompleteResult;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.Node;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

public class OwlDirectInstancesTestOutput extends
		OwlDirectRelatedEntitiesTestOutput<OWLNamedIndividual, OwlDirectInstancesTestOutput> {

	private static OWLDataFactory FACTORY_ = new OWLDataFactoryImpl();

	private final OWLClassExpression query_;

	OwlDirectInstancesTestOutput(OWLClassExpression query,
			Collection<? extends Node<OWLNamedIndividual>> directInstanceNodes,
			boolean isComplete) {
		super(directInstanceNodes, isComplete);
		this.query_ = query;
	}

	OwlDirectInstancesTestOutput(ElkReasoner reasoner,
			OWLClassExpression query) {
		// TODO: completeness
		this(query, reasoner.getInstances(query, true).getNodes(), true);
	}

	@Override
	protected OwlDirectRelatedEntitiesDiffable.Listener<OWLNamedIndividual> adaptListener(
			Listener<OWLAxiom> listener) {
		return new OwlDirectRelatedEntitiesDiffable.Listener<OWLNamedIndividual>() {

			@Override
			public void missingCanonical(OWLNamedIndividual canonical) {
				listener.missing(
						FACTORY_.getOWLClassAssertionAxiom(query_, canonical));
			}

			@Override
			public void missingMember(OWLNamedIndividual canonical,
					OWLNamedIndividual member) {
				listener.missing(
						FACTORY_.getOWLSameIndividualAxiom(canonical, member));
			}

		};
	}

}
