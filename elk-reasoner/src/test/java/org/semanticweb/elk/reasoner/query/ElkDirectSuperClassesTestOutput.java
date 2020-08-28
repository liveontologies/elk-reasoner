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

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.owl.implementation.ElkObjectBaseFactory;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.completeness.IncompleteResult;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;

public class ElkDirectSuperClassesTestOutput extends
		ElkDirectRelatedEntitiesTestOutput<ElkClass, ElkDirectSuperClassesTestOutput> {

	private static ElkAxiom.Factory FACTORY_ = new ElkObjectBaseFactory();

	private final ElkClassExpression query_;

	ElkDirectSuperClassesTestOutput(ElkClassExpression query,
			IncompleteResult<? extends Collection<? extends Node<ElkClass>>> incompleteDisjointNodes) {
		super(incompleteDisjointNodes);
		this.query_ = query;
	}

	ElkDirectSuperClassesTestOutput(ElkClassExpression query,
			Collection<? extends Node<ElkClass>> disjointNodes) {
		super(disjointNodes);
		this.query_ = query;
	}

	ElkDirectSuperClassesTestOutput(Reasoner reasoner, ElkClassExpression query)
			throws ElkException {
		this(query, reasoner.getSuperClassesQuietly(query, true));
	}

	@Override
	protected ElkDirectRelatedEntitiesDiffable.Listener<ElkClass> adaptListener(
			Listener<ElkAxiom> listener) {
		return new ElkDirectRelatedEntitiesDiffable.Listener<ElkClass>() {

			@Override
			public void missingCanonical(ElkClass canonical) {
				listener.missing(
						FACTORY_.getSubClassOfAxiom(query_, canonical));
			}

			@Override
			public void missingMember(ElkClass canonical, ElkClass member) {
				listener.missing(
						FACTORY_.getEquivalentClassesAxiom(canonical, member));
			}

		};
	}

}
