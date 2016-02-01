package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.Reference;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ClassConclusion.Visitor} that checks if the {@link Context} for the
 * origin root f the {@link ClassConclusion} is not saturated, and reports an
 * error otherwise. Should be used for debugging.
 * 
 * @see ClassConclusion#getTraceRoot()
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ClassConclusionOriginContextNotSaturatedCheckingVisitor extends
		AbstractClassConclusionVisitor<Boolean> implements Reference<Context> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ClassConclusionOriginContextNotSaturatedCheckingVisitor.class);

	private final Reference<Context> contextRef_;
	
	private final SaturationState<?> state_;

	public ClassConclusionOriginContextNotSaturatedCheckingVisitor(
			Reference<Context> context,
			SaturationState<?> state) {
		this.contextRef_ = context;
		this.state_ = state;
	}

	@Override
	public Context get() {
		return contextRef_.get();
	}
	
	@Override
	Boolean defaultVisit(SubClassConclusion subConclusion) {
		// ignore sub-conclusions
		return true;
	}

	@Override
	protected Boolean defaultVisit(ClassConclusion conclusion) {
		Context originContext = state_.getContext(conclusion.getTraceRoot());
		if (originContext.isInitialized() && originContext.isSaturated()) {
			LOGGER_.error(
					"{}: adding conclusion {} to saturated context {}",
					contextRef_,
					conclusion,
					get().containsConclusion(conclusion) ? "(it is already there)"
							: "");
		}
		return true;
	}

}
