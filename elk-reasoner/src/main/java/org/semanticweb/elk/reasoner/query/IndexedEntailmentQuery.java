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

import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInferenceSet;
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
	Collection<? extends IndexedContextRoot> getNegativelyIndexed();

	/**
	 * Explains why the queried entailment is entailed. If it is not entailed,
	 * the returned inference set is empty.
	 * 
	 * @param atMostOne
	 *            Whether at most one explanation should be returned.
	 * @param saturationState
	 * @param conclusionFactory
	 * @return An evidence that the queried entailment is entailed.
	 * @throws ElkQueryException
	 */
	<C extends Context> EntailmentInferenceSet getEvidence(boolean atMostOne,
			SaturationState<C> saturationState,
			SaturationConclusion.Factory conclusionFactory)
			throws ElkQueryException;

}
