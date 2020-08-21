package org.semanticweb.elk.reasoner.query;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2018 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.implementation.ElkObjectBaseFactory;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.completeness.IncompleteResult;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;

public class ElkDirectInstancesTestOutput extends
		ElkDirectRelatedEntitiesTestOutput<ElkNamedIndividual, ElkDirectInstancesTestOutput> {

	private static ElkAxiom.Factory FACTORY_ = new ElkObjectBaseFactory();

	private final ElkClassExpression query_;

	ElkDirectInstancesTestOutput(ElkClassExpression query,
			Collection<? extends Node<ElkNamedIndividual>> disjointNodes,
			boolean isComplete) {
		super(disjointNodes, isComplete);
		this.query_ = query;
	}

	ElkDirectInstancesTestOutput(ElkClassExpression query,
			IncompleteResult<? extends Collection<? extends Node<ElkNamedIndividual>>> disjointNodes) {
		this(query, disjointNodes.getValue(), disjointNodes.isComplete());
	}

	@Override
	protected ElkDirectRelatedEntitiesDiffable.Listener<ElkNamedIndividual> adaptListener(
			Listener<ElkAxiom> listener) {
		return new ElkDirectRelatedEntitiesDiffable.Listener<ElkNamedIndividual>() {

			@Override
			public void missingCanonical(ElkNamedIndividual canonical) {
				listener.missing(
						FACTORY_.getClassAssertionAxiom(query_, canonical));
			}

			@Override
			public void missingMember(ElkNamedIndividual canonical,
					ElkNamedIndividual member) {
				listener.missing(
						FACTORY_.getSameIndividualAxiom(canonical, member));
			}

		};
	}

}
