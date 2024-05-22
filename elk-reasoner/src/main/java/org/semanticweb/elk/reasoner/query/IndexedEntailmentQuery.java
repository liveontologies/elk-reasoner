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

import org.liveontologies.puli.Proof;
import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SaturationConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Peter Skocovsky
 *
 * @param <E>
 *            Type of the entailment that is queried.
 */
public interface IndexedEntailmentQuery<E extends Entailment> {

	/**
	 * @return The entailment that is queried.
	 */
	E getQuery();

	/**
	 * @return negatively indexed {@link IndexedContextRoot}s obtained by
	 *         indexing this query.
	 */
	Collection<? extends IndexedContextRoot> getPositivelyIndexed();

	/**
	 * Explains why the queried entailment is entailed. If it is not entailed,
	 * the resulting proof is empty.
	 * 
	 * @param atMostOne
	 *            {@code true} to return at most one explanation and
	 *            {@code false} to return all explanations
	 * @param saturationState
	 *            the {@linkplain SaturationState} from which the
	 *            {@link SaturationConclusion}s are obtained
	 * @param conclusionFactory
	 *            a {@link SaturationConclusion.Factory} to create conclusions
	 *            used in the {@link Proof}
	 * @return A {@link Proof} for this query.
	 * @throws ElkQueryException
	 *             if the explanation process fails
	 */
	Proof<EntailmentInference> getEvidence(boolean atMostOne,
			SaturationState<?> saturationState,
			SaturationConclusion.Factory conclusionFactory)
			throws ElkQueryException;

}
