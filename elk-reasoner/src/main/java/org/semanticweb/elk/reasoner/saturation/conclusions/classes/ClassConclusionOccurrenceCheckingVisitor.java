package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.ClassConclusionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ClassConclusion.Visitor} that checks if visited
 * {@link ClassConclusion} is contained the {@link ClassConclusionSet} value of
 * the given {@link Reference}. The visit method returns {@code true} if the
 * {@link ClassConclusion} occurs in the {@link ClassConclusionSet} and
 * {@code false} otherwise.
 * 
 * @see ClassConclusionInsertionVisitor
 * @see ClassConclusionDeletionVisitor
 * @see ClassConclusionOccurrenceCheckingVisitor
 * 
 * @author "Yevgeny Kazakov"
 */
public class ClassConclusionOccurrenceCheckingVisitor extends
		DummyClassConclusionVisitor<Boolean> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ClassConclusionOccurrenceCheckingVisitor.class);
	
	private final Reference<? extends ClassConclusionSet> conclusionsRef_;
	
	public ClassConclusionOccurrenceCheckingVisitor(Reference<? extends ClassConclusionSet> conclusions) {
		this.conclusionsRef_ = conclusions;
	}

	// TODO: make this by combining the visitor in order to avoid overheads when
	// logging is switched off
	@Override
	protected Boolean defaultVisit(ClassConclusion conclusion) {
		ClassConclusionSet conclusions = conclusionsRef_.get();		
		boolean result = conclusions == null
				? false
				: conclusions.containsConclusion(conclusion);
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("{}: check occurrence of {}: {}", conclusions,
					conclusion, result ? "success" : "failure");
		}
		return result;
	}

}
