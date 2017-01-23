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

import org.semanticweb.elk.reasoner.entailments.impl.EntailedClassInclusionCycleEntailsEquivalentClassesAxiomImpl;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.entailments.model.EquivalentClassesAxiomEntailment;
import org.semanticweb.elk.reasoner.entailments.model.SubClassOfAxiomEntailment;

/**
 * Query whether an
 * {@link org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom
 * ElkEquivalentClassesAxiom} is entailed.
 * <p>
 * Premises are {@link SubClassOfEntailmentQuery}-ies over all the classes from
 * the query, such that superclass of one is the subclass of the next one and
 * superclass of the last one is the subclass of the first one.
 * 
 * @author Peter Skocovsky
 */
public class EquivalentClassesEntailmentQuery extends
		AbstractEntailmentQueryWithPremises<EquivalentClassesAxiomEntailment, SubClassOfEntailmentQuery> {

	/**
	 * @param query
	 *            What entailment is queried.
	 * @param subsumptionCycle
	 *            {@link SubClassOfEntailmentQuery}-ies over all the classes
	 *            from the query, such that superclass of one is the subclass of
	 *            the next one and superclass of the last one is the subclass of
	 *            the first one.
	 */
	public EquivalentClassesEntailmentQuery(
			final EquivalentClassesAxiomEntailment query,
			final List<SubClassOfEntailmentQuery> subsumptionCycle) {
		super(query, subsumptionCycle);
	}

	@Override
	protected Collection<? extends EntailmentInference> getEntailmentInference() {

		final List<SubClassOfAxiomEntailment> premises = new ArrayList<SubClassOfAxiomEntailment>();

		for (final SubClassOfEntailmentQuery subsumption : getPremises()) {
			premises.add(subsumption.getQuery());
		}

		return Collections.singleton(
				new EntailedClassInclusionCycleEntailsEquivalentClassesAxiomImpl(
						getQuery(), premises));
	}

}
