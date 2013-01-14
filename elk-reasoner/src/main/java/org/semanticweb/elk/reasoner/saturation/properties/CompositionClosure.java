package org.semanticweb.elk.reasoner.saturation.properties;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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
import java.util.Collections;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;

/**
 * This object is used to apply a new entailed composition to the set of
 * computed compositions of {@link IndexedPropertyChain}s. Depending on the
 * value of the parameters
 * {@link SaturatedPropertyChain#REPLACE_CHAINS_BY_TOLD_SUPER_PROPERTIES} a set
 * of relevant compositions is computed for the given root composition, which
 * can be used to extend several sets of compositions. Caching the set of
 * relevant compositions can be useful since the given compositions can
 * potentially be added to several pairs of {@link IndexedPropertyChain}s.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class CompositionClosure {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(CompositionClosure.class);

	protected final Collection<IndexedPropertyChain> relevantCompositions;

	CompositionClosure(IndexedBinaryPropertyChain root) {
		if (SaturatedPropertyChain.isRelevant(root))
			relevantCompositions = Collections
					.<IndexedPropertyChain> singleton(root);
		else {
			relevantCompositions = new LinkedList<IndexedPropertyChain>();
			relevantCompositions.addAll(root.getToldSuperProperties());
		}
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(root + " relevant compositions: "
					+ relevantCompositions);
	}

	/**
	 * Extends the given {@link Collection} of {@link IndexedPropertyChain} with
	 * the relevant compositions of this {@link CompositionClosure} to the
	 * 
	 * 
	 * @param currentCompositions
	 *            the {@link Collection} of {@link IndexedPropertyChain} to be
	 *            extended
	 */
	public void applyTo(Collection<IndexedPropertyChain> currentCompositions) {
		currentCompositions.addAll(relevantCompositions);
	}

}
