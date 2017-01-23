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

import java.util.Collection;
import java.util.Collections;

import org.semanticweb.elk.reasoner.entailments.impl.EntailedEquivalentClassesEntailsSameIndividualAxiomImpl;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.entailments.model.SameIndividualAxiomEntailment;

/**
 * Query whether an
 * {@link org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom
 * ElkSameIndividualAxiom} is entailed.
 * <p>
 * Premises contain one {@link EquivalentClassesEntailmentQuery} querying
 * whether nominals of individuals from the queried axiom are equivalent.
 * 
 * @author Peter Skocovsky
 */
public class SameIndividualEntailmentQuery extends
		AbstractEntailmentQueryWithPremises<SameIndividualAxiomEntailment, EquivalentClassesEntailmentQuery> {

	/**
	 * @param query
	 *            What entailment is queried.
	 * @param equivalence
	 *            {@link EquivalentClassesEntailmentQuery} querying whether
	 *            nominals of individuals from the queried axiom are equivalent.
	 */
	public SameIndividualEntailmentQuery(
			final SameIndividualAxiomEntailment query,
			final EquivalentClassesEntailmentQuery equivalence) {
		super(query, Collections.singletonList(equivalence));
	}

	@Override
	protected Collection<? extends EntailmentInference> getEntailmentInference() {

		return Collections.singleton(
				new EntailedEquivalentClassesEntailsSameIndividualAxiomImpl(
						getQuery(), getPremises().get(0).getQuery()));
	}

}
