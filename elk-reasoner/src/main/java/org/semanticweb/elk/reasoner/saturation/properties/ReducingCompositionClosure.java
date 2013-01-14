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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;

/**
 * This object is used to apply a new entailed composition to the set of
 * computed compositions of {@link IndexedPropertyChain}s in case the parameter
 * {@link SaturatedPropertyChain#ELIMINATE_IMPLIED_COMPOSITIONS} is set. In this
 * case the set of relevant compositions for the given root composition is
 * reduced according to sub-property relation.
 * 
 * @see CompositionClosure
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class ReducingCompositionClosure extends CompositionClosure {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(SubPropertyExplorer.class);

	final Set<IndexedPropertyChain> rootSubProperties;
	final Set<IndexedPropertyChain> toRemove;

	ReducingCompositionClosure(IndexedBinaryPropertyChain root) {
		super(root);
		this.rootSubProperties = SubPropertyExplorer
				.getSetRelevantSubProperties(root);
		toRemove = new HashSet<IndexedPropertyChain>();
		Iterator<? extends IndexedPropertyChain> compositionIterator = relevantCompositions
				.iterator();
		while (compositionIterator.hasNext()) {
			IndexedPropertyChain next = compositionIterator.next();
			if (toRemove.contains(next)) {
				compositionIterator.remove();
			} else {
				Set<IndexedPropertyChain> candidateSupers = SuperPropertyExplorer
						.getRelevantSuperProperties(next);
				candidateSupers.remove(next);
				toRemove.addAll(candidateSupers);
			}
		}
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("To remove: " + toRemove);
			LOGGER_.trace("Reduced composition closure: "
					+ relevantCompositions);
		}
	}

	@Override
	public void applyTo(Collection<IndexedPropertyChain> currentCompositions) {
		if (!rootSubProperties.isEmpty())
			for (IndexedPropertyChain current : currentCompositions) {
				if (rootSubProperties.contains(current))
					// the root is already implied by some current compositions
					return;
			}
		super.applyTo(currentCompositions);
		currentCompositions.removeAll(toRemove);
	}
}
