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

class CompositionClosure {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(CompositionClosure.class);

	protected final Collection<IndexedPropertyChain> compositions;

	CompositionClosure(IndexedBinaryPropertyChain composition) {
		if (SaturatedPropertyChain.isRelevant(composition))
			compositions = Collections
					.<IndexedPropertyChain> singleton(composition);
		else {
			compositions = new LinkedList<IndexedPropertyChain>();
			for (IndexedPropertyChain relevantSuperProperty : SuperPropertyExplorer
					.getRelevantSuperProperties(composition))
				compositions.add(relevantSuperProperty);
		}
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("Composition closure: " + compositions);
	}

	public void applyTo(Collection<IndexedPropertyChain> currentCompositions) {
		currentCompositions.addAll(compositions);
	}

}
