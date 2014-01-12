/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

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

import java.util.Set;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class CheckCleaningStage extends BasePostProcessingStage {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(CheckCleaningStage.class);

	private final AbstractReasonerState reasoner_;

	public CheckCleaningStage(AbstractReasonerState reasoner) {
		reasoner_ = reasoner;
	}

	@Override
	public String getName() {
		return "Checking that unsaturated contexts are clean";
	}

	@Override
	public void execute() throws ElkException {
		Set<Context> cleanedContexts = new ArrayHashSet<Context>(1024);
		// checking subsumers of cleaned contexts
		for (IndexedClassExpression ice : reasoner_.saturationState
				.getNotSaturatedContexts()) {
			Context context = reasoner_.saturationState.getContext(ice);
			if (context == null) {
				LOGGER_.error("Context removed for " + ice);
				continue;
			}
			cleanedContexts.add(context);
			if (context.getSubsumers().size() > 0) {
				LOGGER_.error("Context not cleaned: " + ice.toString() + "\n"
						+ context.getSubsumers().size() + " subsumers: "
						+ context.getSubsumers());
			}
		}
		// checking backward links
		for (IndexedClassExpression ice : reasoner_
				.getIndexedClassExpressions()) {
			Context context = reasoner_.saturationState.getContext(ice);
			if (context == null)
				continue;
			Multimap<IndexedPropertyChain, Context> backwardLinks = context
					.getBackwardLinksByObjectProperty();
			for (IndexedPropertyChain ipc : backwardLinks.keySet()) {
				for (Context target : backwardLinks.get(ipc))
					if (cleanedContexts.contains(target))
						LOGGER_.error("Backward link in " + context
								+ " via property " + ipc
								+ " to cleaned context " + target);
			}
		}
	}
}
