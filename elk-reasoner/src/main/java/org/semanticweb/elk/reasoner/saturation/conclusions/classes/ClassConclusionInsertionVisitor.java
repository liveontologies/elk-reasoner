package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

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

import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ClassConclusion.Visitor} that adds the visited {@link ClassConclusion} into the
 * given {@link Context}. The visit method returns {@link true} if the
 * {@link Context} was modified as the result of this operation, i.e., the
 * {@link ClassConclusion} was not contained in the {@link Context}. Additionally,
 * when inserting {@link ContextInitialization} the {@link Context} is marked as
 * non-saturated using the provided {@link SaturationStateWriter}.
 * 
 * @see ClassConclusionDeletionVisitor
 * @see ClassConclusionOccurrenceCheckingVisitor
 * 
 * @author "Yevgeny Kazakov"
 */
public class ClassConclusionInsertionVisitor extends
		AbstractClassConclusionVisitor<Context, Boolean> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ClassConclusionInsertionVisitor.class);

	private final SaturationStateWriter<?> writer_;

	public ClassConclusionInsertionVisitor(SaturationStateWriter<?> writer) {
		this.writer_ = writer;
	}

	// TODO: make this by combining the visitor in order to avoid overheads when
	// logging is switched off
	@Override
	protected Boolean defaultVisit(ClassConclusion conclusion, Context context) {
		boolean result = context.addConclusion(conclusion);
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("{}: inserting {}: {}", context, conclusion,
					result ? "success" : "failure");
		}
		return result;
	}

	@Override
	public Boolean visit(ContextInitialization conclusion, Context input) {
		if (input.containsConclusion(conclusion))
			return false;
		// else
		// Mark context as non-saturated if conclusion was not already
		// initialized. It is important to mark before we insert, otherwise
		// the context can be found initialized and saturated when it is not.
		writer_.markAsNotSaturated(input.getRoot());
		return defaultVisit(conclusion, input);
	}
}
