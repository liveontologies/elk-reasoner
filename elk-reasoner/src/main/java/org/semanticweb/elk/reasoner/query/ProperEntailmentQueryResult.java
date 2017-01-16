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

import org.semanticweb.elk.Lock;
import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInferenceSet;

/**
 * An {@link EntailmentQueryResult} of a successfully computed query.
 * <p>
 * A {@link ProperEntailmentQueryResult} is initially locked. It should be
 * released by calling {@link #unlock()} after processed. The query results are
 * guaranteed to be available while the result is locked. After unlocked,
 * methods {@link #isEntailed()} and {@link #getEvidence(boolean)} may not
 * return the specified results.
 * 
 * @author Peter Skocovsky
 */
public interface ProperEntailmentQueryResult
		extends EntailmentQueryResult, Lock {

	/**
	 * @return Whether the queried entailment is entailed.
	 * @throws ElkQueryException
	 */
	boolean isEntailed() throws ElkQueryException;

	/**
	 * Explains why the queried entailment is entailed. If it is not entailed,
	 * the returned inference set is empty.
	 * 
	 * @param atMostOne
	 *            Whether at most one explanation should be returned.
	 * @return An evidence that the queried entailment is entailed.
	 * @throws ElkQueryException
	 */
	EntailmentInferenceSet getEvidence(boolean atMostOne)
			throws ElkQueryException;

	/**
	 * Returns the entailment of the query. This should be the root of
	 * {@link #getEvidence(boolean)}.
	 * 
	 * @return The entailment of the query.
	 * @throws ElkQueryException
	 */
	Entailment getEntailment() throws ElkQueryException;

	<O, T extends Throwable> O accept(Visitor<O, T> visitor) throws T;

	public static interface Visitor<O, T extends Throwable> {
		O visit(ProperEntailmentQueryResult properEntailmentQueryResult)
				throws T;
	}

}
