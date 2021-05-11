package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

import org.semanticweb.elk.Reference;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;

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

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.ClassConclusionSet;

/**
 * A {@link ClassConclusion.Visitor} that removes the visited
 * {@link ClassConclusion} from the {@link ClassConclusionSet} of the given
 * {@link Reference}. The visit method returns {@code true} if the
 * {@link ClassConclusionSet} was modified as the result of this operation,
 * i.e., the {@link ClassConclusion} was contained in the
 * {@link ClassConclusionSet}.
 * 
 * @see ClassConclusionInsertionVisitor
 * @see ClassConclusionOccurrenceCheckingVisitor
 * 
 * @author "Yevgeny Kazakov"
 */
public class ClassConclusionDeletionVisitor
		extends DummyClassConclusionVisitor<Boolean> {

	private final SaturationStateWriter<?> writer_;

	public ClassConclusionDeletionVisitor(SaturationStateWriter<?> writer) {
		this.writer_ = writer;
	}

	@Override
	protected Boolean defaultVisit(ClassConclusion conclusion) {
		return writer_.removeConclusion(conclusion);
	}

}
