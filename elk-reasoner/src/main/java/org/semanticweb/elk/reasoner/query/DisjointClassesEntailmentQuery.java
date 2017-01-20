/*-
 * #%L
 * ELK Reasoner Core
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
package org.semanticweb.elk.reasoner.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.semanticweb.elk.reasoner.entailments.impl.DerivedIntersectionInconsistencyEntailsDisjointClassesAxiomImpl;
import org.semanticweb.elk.reasoner.entailments.model.DisjointClassesAxiomEntailment;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.entailments.model.SubClassOfAxiomEntailment;

/**
 * Query whether an
 * {@link org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom
 * ElkDisjointClassesAxiom} is entailed.
 * <p>
 * Premises are {@link SubClassOfEntailmentQuery}-ies where subclasses are all
 * pairwise intersections of classes from the query and superclasses are
 * {@code owl:Nothing}.
 * 
 * @author Peter Skocovsky
 */
public class DisjointClassesEntailmentQuery extends
		AbstractEntailmentQueryWithPremises<DisjointClassesAxiomEntailment, SubClassOfEntailmentQuery> {

	/**
	 * @param query
	 *            What entailment is queried.
	 * @param premises
	 *            {@link SubClassOfEntailmentQuery}-ies where subclasses are all
	 *            pairwise intersections of classes from the query and
	 *            superclasses are {@code owl:Nothing}.
	 */
	public DisjointClassesEntailmentQuery(
			final DisjointClassesAxiomEntailment query,
			final List<SubClassOfEntailmentQuery> premises) {
		super(query, premises);
	}

	@Override
	protected Collection<? extends EntailmentInference> getEntailmentInference() {

		final List<SubClassOfAxiomEntailment> premises = new ArrayList<SubClassOfAxiomEntailment>();

		for (final SubClassOfEntailmentQuery subsumption : getPremises()) {
			premises.add(subsumption.getQuery());
		}

		return Collections.singleton(
				new DerivedIntersectionInconsistencyEntailsDisjointClassesAxiomImpl(
						getQuery(), premises));
	}

}
