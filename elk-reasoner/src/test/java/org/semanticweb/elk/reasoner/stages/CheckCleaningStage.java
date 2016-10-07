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

import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.SubContextPremises;
import org.semanticweb.elk.util.collections.ArrayHashSet;
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
		Set<IndexedContextRoot> cleanedContexts = new ArrayHashSet<IndexedContextRoot>(1024);
		// checking subsumers of cleaned contexts
		for (Context context : reasoner_.saturationState
				.getNotSaturatedContexts()) {
			cleanedContexts.add(context.getRoot());
			if (!context.getComposedSubsumers().isEmpty()) {
				LOGGER_.error(
						"{}: context not cleaned: there are {} subsumers: {}",
						context, context.getComposedSubsumers().size(),
						context.getComposedSubsumers());
			}
			if (!context.getLocalReflexiveObjectProperties().isEmpty()) {
				LOGGER_.error(
						"{}: context not cleaned: there are {} backward reflexive properties: {}"
								+ context, context
								.getLocalReflexiveObjectProperties().size(),
						context.getLocalReflexiveObjectProperties());
			}
		}
		// checking sub contexts
		for (IndexedClassExpression ice : reasoner_
				.getIndexedClassExpressions()) {
			Context context = reasoner_.saturationState.getContext(ice);
			if (context == null)
				continue;
			Map<IndexedObjectProperty, ? extends SubContextPremises> subContextMap = context
					.getSubContextPremisesByObjectProperty();
			for (IndexedPropertyChain ipc : subContextMap.keySet()) {
				for (IndexedContextRoot target : subContextMap.get(ipc)
						.getLinkedRoots())
					if (cleanedContexts.contains(target))
						LOGGER_.error(
								"{}: backward link via {} to cleaned target {}",
								context, ipc, target);
			}
		}
	}
}
