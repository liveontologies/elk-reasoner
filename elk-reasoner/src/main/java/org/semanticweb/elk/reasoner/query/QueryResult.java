package org.semanticweb.elk.reasoner.query;

/*-
 * #%L
 * ELK Reasoner Core
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

import org.semanticweb.elk.Lock;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;

/**
 * Represents the result of an entailment test for {@link ElkAxiom} (the query).
 * 
 * @author Peter Skocovsky
 * @author Yevgeny Kazakov
 * 
 *         TODO: manage locks using weak references
 */
public interface QueryResult extends Lock {

	/**
	 * @return The query that is tested for the entailment.
	 */
	ElkAxiom getQuery();

	/**
	 * @return {@code true} if the reasoner has determined that the query is
	 *         entailed by the ontology and {@code false} otherwise. If
	 *         {@code false} is returned this does not necessarily mean that the
	 *         query is not entailed since the reasoning results may be
	 *         incomplete. The latter can be verified using
	 *         {@link #entailmentDisproved()}.
	 * @throws ElkQueryException
	 *             if this {@link QueryResult} has not been computed yet
	 */
	boolean entailmentProved() throws ElkQueryException;

	/**
	 * @return {@code true} if the reasoner has determined that the query cannot
	 *         be entailed by the ontology and {@code false} otherwise. If
	 *         {@code false} is returned, this does not necessarily mean that
	 *         the query is entailed since the reasoning results may be
	 *         incomplete. The latter can be verified using
	 *         {@link #entailmentProved()}.
	 * @throws ElkQueryException
	 *             if this {@link QueryResult} has not been computed yet
	 */
	boolean entailmentDisproved() throws ElkQueryException;

}
