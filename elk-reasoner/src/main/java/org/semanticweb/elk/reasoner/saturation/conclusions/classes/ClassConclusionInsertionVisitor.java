package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

import org.semanticweb.elk.Reference;

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

/**
 * A {@link ClassConclusion.Visitor} that adds the visited
 * {@link ClassConclusion} to the {@link Context} value of the provided
 * {@link Reference}. The visit method returns {@code true} if the
 * {@link Context} was modified as the result of this operation, i.e., the
 * {@link ClassConclusion} was not contained in the {@link Context}.
 * Additionally, when inserting {@link ContextInitialization} the
 * {@link Context} is marked as non-saturated using the provided
 * {@link SaturationStateWriter}.
 * 
 * @see ClassConclusionDeletionVisitor
 * @see ClassConclusionOccurrenceCheckingVisitor
 * 
 * @author "Yevgeny Kazakov"
 */
public class ClassConclusionInsertionVisitor
		extends
			DummyClassConclusionVisitor<Boolean> {

	private final SaturationStateWriter<?> writer_;

	public ClassConclusionInsertionVisitor(SaturationStateWriter<?> writer) {

		this.writer_ = writer;
	}
		
	@Override
	protected Boolean defaultVisit(ClassConclusion conclusion) {
		return writer_.addConclusion(conclusion);
	}

}
