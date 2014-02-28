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

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ConclusionVisitor} that checks if visited {@link Conclusion} is
 * contained the given {@link Context}. The visit method returns {@link true} if
 * the {@link Context} is occurs in the {@link Context} and {@link false}
 * otherwise.
 * 
 * @see ConclusionInsertionVisitor
 * @see ConclusionDeletionVisitor
 * @see MirrorConclusionOccurrenceCheckingVisitor
 * 
 * @author "Yevgeny Kazakov"
 */
public class ConclusionOccurrenceCheckingVisitor extends
		AbstractConclusionVisitor<Context, Boolean> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ConclusionOccurrenceCheckingVisitor.class);

	// TODO: make this by combining the visitor in order to avoid overheads when
	// logging is switched off
	@Override
	protected Boolean defaultVisit(Conclusion conclusion, Context context) {
		boolean result = context.containsConclusion(conclusion);
		LOGGER_.trace("{}: check occurrence of {}: {}", context, conclusion,
				result ? "success" : "failure");
		return result;
	}

}
