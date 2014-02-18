package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.SubConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ConclusionVisitor} that checks if the source {@link Context} of the
 * {@link Conclusion} is not saturated, and reports an error otherwise. Should
 * be used for debugging.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ConclusionSourceContextNotSaturatedCheckingVisitor extends
		AbstractConclusionVisitor<Context, Boolean> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ConclusionSourceContextNotSaturatedCheckingVisitor.class);

	private final SaturationState state_;

	public ConclusionSourceContextNotSaturatedCheckingVisitor(
			SaturationState state) {
		this.state_ = state;
	}

	@Override
	Boolean defaultVisit(SubConclusion subConclusion, Context context) {
		// ignore sub-conclusions
		return true;
	}

	@Override
	protected Boolean defaultVisit(Conclusion conclusion, Context context) {
		IndexedClassExpression sourceRoot = conclusion.getSourceRoot(context
				.getRoot());
		Context sourceContext = state_.getContext(sourceRoot);
		if (sourceContext.isSaturated()) {
			LOGGER_.error(
					"{}: adding conclusion {} to saturated context {}",
					context,
					conclusion,
					context.containsConclusion(conclusion) ? "(it is already there)"
							: "");
		}
		return true;
	}
}
