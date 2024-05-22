package org.semanticweb.elk.reasoner.query;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2024 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.proof.ReasonerProof;

/**
 * A {@link QueryResult} which can be verified using a proof.
 * 
 * @author Peter Skocovsky
 * @author Yevgeny Kazakov
 */
public interface VerifiableQueryResult extends QueryResult {

	/**
	 * Explains why the queried entailment is entailed. If it is not entailed,
	 * the returned proof is empty.
	 * 
	 * @param atMostOne
	 *            Whether at most one explanation should be returned.
	 * @return An evidence that the queried entailment is entailed.
	 * @throws ElkQueryException
	 * 
	 *             TODO: change to a direct proof of the query, e.g., using
	 *             InternalProof
	 */
	ReasonerProof<EntailmentInference> getEvidence(boolean atMostOne)
			throws ElkQueryException;

	/**
	 * Returns the entailment of the query. This should be the root of
	 * {@link #getEvidence(boolean)}.
	 * 
	 * @return The entailment of the query.
	 * @throws ElkQueryException
	 * 
	 *             TODO: remove
	 */
	Entailment getEntailment() throws ElkQueryException;

}
