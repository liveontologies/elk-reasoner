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
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ClassConclusion.Visitor} that checks if the {@link Context} for the
 * root returned by {@link ClassConclusion#getTraceRoot()} for the visited
 * {@link ClassConclusion}s is not saturated, and reports an error otherwise.
 * Should be used for debugging.
 * 
 * @see ClassConclusion#getTraceRoot()
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ClassConclusionTracingContextNotSaturatedCheckingVisitor
		extends
			DummyClassConclusionVisitor<Boolean>
		implements Reference<Context> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory.getLogger(
			ClassConclusionTracingContextNotSaturatedCheckingVisitor.class);

	private final Reference<Context> contextRef_;

	private final SaturationState<?> state_;

	public ClassConclusionTracingContextNotSaturatedCheckingVisitor(
			Reference<Context> context, SaturationState<?> state) {
		this.contextRef_ = context;
		this.state_ = state;
	}

	@Override
	public Context get() {
		return contextRef_.get();
	}

	@Override
	protected Boolean defaultVisit(SubClassConclusion subConclusion) {
		// ignore sub-conclusions
		return true;
	}

	@Override
	protected Boolean defaultVisit(ClassConclusion conclusion) {
		Context originContext = state_.getContext(conclusion.getTraceRoot());
		if (originContext.isInitialized() && originContext.isSaturated()
				&& !(conclusion instanceof ContextInitialization)) { // (*)
			LOGGER_.error("{}: adding conclusion {} to saturated context {}",
					contextRef_, conclusion,
					get().containsConclusion(conclusion)
							? "(it is already there)"
							: "");
			// (*): Since saturated contexts are not set straight away, it is
			// possible that after a saturation job for a context
			// is processed but not yet marked as saturated, another job for the
			// same context is submitted and the insertion attempt happens 
			// after the context for the first job is marked as saturated
		}
		return true;
	}

}
