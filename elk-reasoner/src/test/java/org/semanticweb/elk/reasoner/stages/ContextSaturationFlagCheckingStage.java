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

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ContextSaturationFlagCheckingStage.class);

	private final Collection<? extends IndexedClassExpression> classes_;
	private final Set<Context> nonSaturated_;
	private final SaturationState<?> saturationState_;

	public ContextSaturationFlagCheckingStage(AbstractReasonerState reasoner) {
		classes_ = reasoner.ontologyIndex.getClassExpressions();
		nonSaturated_ = new ArrayHashSet<Context>(reasoner.saturationState
				.getNotSaturatedContexts().size());
		nonSaturated_
				.addAll(reasoner.saturationState.getNotSaturatedContexts());
		saturationState_ = reasoner.saturationState;
	}

	@Override
	public String getName() {
		return "Checking context saturation flag";
	}

	@Override
	public void execute() throws ElkException {

		for (IndexedClassExpression ice : classes_) {
			Context context = saturationState_.getContext(ice);

			if (context == null) {
				continue;
			}

			if (context.isSaturated() && nonSaturated_.contains(context)) {
				LOGGER_.error("{}: context IS saturated but contained in the not saturated queue"
						+ context);
			}

			if (!context.isSaturated() && !nonSaturated_.contains(context)) {
				LOGGER_.error(
						"{}: context  is NOT saturated and NOT contained in the not saturated queue",
						context);
			}
		}
	}
}
