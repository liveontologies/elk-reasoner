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

import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.util.collections.ArrayHashSet;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
/**
 * Used to check that non-saturated contexts are those and only those which are
 * marked as not saturated
 */
public class ContextSaturationFlagCheckingStage extends BasePostProcessingStage {

	private static final Logger LOGGER_ = Logger
			.getLogger(ContextSaturationFlagCheckingStage.class);

	private final Collection<IndexedClassExpression> classes_;
	private final Set<IndexedClassExpression> nonSaturated_;

	public ContextSaturationFlagCheckingStage(AbstractReasonerState reasoner) {
		classes_ = reasoner.ontologyIndex.getIndexedClassExpressions();
		nonSaturated_ = new ArrayHashSet<IndexedClassExpression>(
				reasoner.saturationState.getNotSaturatedContexts().size());
		nonSaturated_
				.addAll(reasoner.saturationState.getNotSaturatedContexts());
	}

	@Override
	public String getName() {
		return "Checking context saturation flag";
	}

	@Override
	public void execute() throws ElkException {

		for (IndexedClassExpression ice : classes_) {

			if (ice.getContext() == null) {
				continue;
			}

			if (ice.getContext().isSaturated() && nonSaturated_.contains(ice)) {
				LOGGER_.error("Context "
						+ ice
						+ " IS saturated but contained in the not saturated queue");
			}

			if (!ice.getContext().isSaturated() && !nonSaturated_.contains(ice)) {
				LOGGER_.error("Context "
						+ ice
						+ " is NOT saturated and NOT contained in the not saturated queue");
			}
		}
	}
}
